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

import com.tencent.bk.audit.constants.AccessTypeEnum;
import com.tencent.bk.audit.constants.AuditEventKey;
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;
import com.tencent.bk.audit.utils.EventIdGenerator;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 审计上下文实现
 */
public class SdkAuditContext implements AuditContext {

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

    /**
     * 操作ID
     */
    private String actionId;

    private final AuditHttpRequest httpRequest;

    private final List<ActionAuditContext> actionAuditContexts = new ArrayList<>();

    private final List<AuditEvent> events = new ArrayList<>();

    private ActionAuditContext currentActionAuditContext;

    private final List<String> subActionIds;

    SdkAuditContext(String actionId,
                    String requestId,
                    String username,
                    UserIdentifyTypeEnum userIdentifyType,
                    String userIdentifyTenantId,
                    String bkAppCode,
                    AccessTypeEnum accessType,
                    String accessSourceIp,
                    String accessUserAgent,
                    AuditHttpRequest httpRequest,
                    List<String> subActionIds) {
        this.actionId = actionId;
        this.requestId = requestId;
        this.username = username;
        this.userIdentifyType = userIdentifyType;
        this.userIdentifyTenantId = userIdentifyTenantId;
        this.startTime = System.currentTimeMillis();
        this.bkAppCode = bkAppCode;
        this.accessType = accessType;
        this.accessSourceIp = accessSourceIp;
        this.accessUserAgent = accessUserAgent;
        this.httpRequest = httpRequest;
        this.subActionIds = subActionIds == null ? Collections.emptyList() : subActionIds;
    }

    @Override
    public ActionAuditContext currentActionAuditContext() {
        return currentActionAuditContext != null ? currentActionAuditContext : ActionAuditContext.INVALID;
    }

    @Override
    public void addActionAuditContext(ActionAuditContext actionAuditContext) {
        actionAuditContexts.add(actionAuditContext);
    }

    private void buildAuditEvents() {
        if (StringUtils.isEmpty(actionId)) {
            return;
        }

        Map<AuditEventKey, AuditEvent> auditEvents = new HashMap<>();
        actionAuditContexts.stream()
            .filter(actionAuditContext -> isActionRecordable(actionAuditContext.getActionId()))
            .forEach(actionAuditContext ->
                actionAuditContext.getEvents().forEach(
                    auditEvent -> auditEvents.put(auditEvent.toAuditKey(), auditEvent)
                )
            );
        if (!auditEvents.isEmpty()) {
            auditEvents.values().forEach(this::addContextAttributes);
            this.events.addAll(auditEvents.values());
        }
    }

    private void addContextAttributes(AuditEvent auditEvent) {
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

    private boolean isActionRecordable(String actionId) {
        return StringUtils.isNotEmpty(this.actionId) &&
            (this.actionId.equals(actionId) || this.subActionIds.contains(actionId));
    }

    @Override
    public void end() {
        this.endTime = System.currentTimeMillis();
        buildAuditEvents();
    }

    @Override
    public void setCurrentActionAuditContext(ActionAuditContext actionAuditContext) {
        this.currentActionAuditContext = actionAuditContext;
    }

    @Override
    public void error(int resultCode, String resultContent) {
        this.endTime = System.currentTimeMillis();
        this.actionAuditContexts.clear();
        this.events.clear();
        AuditEvent auditEvent = new AuditEvent(actionId);
        auditEvent.setId(EventIdGenerator.generateId());
        addContextAttributes(auditEvent);
        auditEvent.setStartTime(startTime);
        auditEvent.setEndTime(endTime);
        auditEvent.setResultCode(resultCode);
        auditEvent.setResultContent(resultContent);
        this.events.add(auditEvent);
    }

    @Override
    public List<AuditEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    @Override
    public void updateActionId(String actionId) {
        if (StringUtils.isEmpty(this.actionId)) {
            this.actionId = actionId;
        }
    }
}
