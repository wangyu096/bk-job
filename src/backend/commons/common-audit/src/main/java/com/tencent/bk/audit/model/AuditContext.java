/*
 * Tencent is pleased to support the open source community by making BK-JOB蓝鲸智云作业平台 available.
 *
 * Copyright (C) 2021 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * BK-JOB蓝鲸智云作业平台 is licensed under the MIT License.
 *
 * License for BK-JOB蓝鲸智云作业平台:
 * --------------------------------------------------------------------
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package com.tencent.bk.audit.model;

import com.tencent.bk.audit.GlobalAuditRegistry;
import com.tencent.bk.audit.constants.AccessTypeEnum;
import com.tencent.bk.audit.constants.AuditEventKey;
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 审计上下文
 */
public class AuditContext {

    private final String requestId;

    private final String username;

    private final UserIdentifyTypeEnum userIdentifyType;

    private final String userIdentifyTenantId;

    private final Long startTime;

    private Long endTime;

    private final String bkAppCode;

    private final AccessTypeEnum accessType;

    private final String accessSourceIp;

    private final String accessUserAgent;

    private int resultCode;

    private String resultContent;

    /**
     * 操作ID
     */
    private final String actionId;

    private final AuditHttpRequest httpRequest;

    private List<ActionAuditContext> actionAuditContexts = new ArrayList<>();

    private final List<AuditEvent> events = new ArrayList<>();

    private ActionAuditContext currentActionAuditContext;

    private final boolean recordSubEvent;


    public static AuditContext start(String actionId,
                                     String requestId,
                                     String username,
                                     UserIdentifyTypeEnum userIdentifyType,
                                     String userIdentifyTenantId,
                                     String bkAppCode,
                                     AccessTypeEnum accessType,
                                     String accessSourceIp,
                                     String accessUserAgent,
                                     AuditHttpRequest httpRequest,
                                     boolean recordSubEvent) {
        return new AuditContext(actionId, requestId, username, userIdentifyType, userIdentifyTenantId,
            System.currentTimeMillis(), bkAppCode, accessType, accessSourceIp, accessUserAgent, httpRequest,
            recordSubEvent);
    }

    private AuditContext(String actionId,
                         String requestId,
                         String username,
                         UserIdentifyTypeEnum userIdentifyType,
                         String userIdentifyTenantId,
                         Long startTime,
                         String bkAppCode,
                         AccessTypeEnum accessType,
                         String accessSourceIp,
                         String accessUserAgent,
                         AuditHttpRequest httpRequest,
                         boolean recordSubEvent) {
        this.actionId = actionId;
        this.requestId = requestId;
        this.username = username;
        this.userIdentifyType = userIdentifyType;
        this.userIdentifyTenantId = userIdentifyTenantId;
        this.startTime = startTime;
        this.bkAppCode = bkAppCode;
        this.accessType = accessType;
        this.accessSourceIp = accessSourceIp;
        this.accessUserAgent = accessUserAgent;
        this.httpRequest = httpRequest;
        this.recordSubEvent = recordSubEvent;
    }

    public static AuditContext current() {
        return GlobalAuditRegistry.get().current();
    }

    public void addContextAttributes(AuditEvent auditEvent) {
        auditEvent.setRequestId(requestId);
        auditEvent.setBkAppCode(bkAppCode);

        if (accessType != null) {
            auditEvent.setAccessType(accessType.getValue());
        }
        auditEvent.setAccessUserAgent(accessUserAgent);
        auditEvent.setAccessSourceIp(accessSourceIp);

        auditEvent.setUsername(username);
        if (userIdentifyType != null) {
            auditEvent.setUserIdentifyType(UserIdentifyTypeEnum.PERSONAL.getValue());
        }
        auditEvent.setUserIdentifyTenantId(userIdentifyTenantId);

        if (httpRequest != null) {
            auditEvent.addExtendData("request", httpRequest);
        }
    }

    public ActionAuditContext currentActionAuditContext() {
        return currentActionAuditContext;
    }

    public void addActionAuditContext(ActionAuditContext actionAuditContext) {
        actionAuditContexts.add(actionAuditContext);
    }

    private void buildAuditEvents() {
        Map<AuditEventKey, AuditEvent> auditEvents = new HashMap<>();
        if (recordSubEvent) {
            actionAuditContexts = actionAuditContexts.stream()
                .filter(actionAuditContext -> actionAuditContext.getActionId().equals(actionId))
                .collect(Collectors.toList());
        }
        actionAuditContexts.forEach(actionAuditContext ->
            actionAuditContext.getEvents().forEach(
                auditEvent -> auditEvents.put(auditEvent.toAuditKey(), auditEvent)
            )
        );
        auditEvents.values().forEach(this::addContextAttributes);
    }

    public void end() {
        this.endTime = System.currentTimeMillis();
        buildAuditEvents();
    }

    public void setCurrentActionAuditContext(ActionAuditContext actionAuditContext) {
        this.currentActionAuditContext = actionAuditContext;
    }

    public void error(int resultCode, String resultContent) {
        this.resultCode = resultCode;
        this.resultContent = resultContent;
        this.actionAuditContexts.clear();
        this.events.clear();
        AuditEvent auditEvent = new AuditEvent(actionId);
        addContextAttributes(auditEvent);
        auditEvent.setEndTime(System.currentTimeMillis());
        auditEvent.setResultCode(this.resultCode);
        auditEvent.setResultContent(this.resultContent);
        this.events.add(auditEvent);
    }

    public List<AuditEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }
}
