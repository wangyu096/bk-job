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

import com.tencent.bk.audit.AuditManagerRegistry;
import com.tencent.bk.audit.constants.AccessTypeEnum;
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;
import lombok.Data;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Data
public class AuditContext {

    private String requestId;

    private String username;

    private UserIdentifyTypeEnum userIdentifyType;

    private String userIdentifyTenantId;

    private Long startTime;

    private Long endTime;

    private String bkAppCode;

    private AccessTypeEnum accessType;

    private String accessSourceIp;

    private String accessUserAgent;

    private int resultCode;

    private String resultContent;

    /**
     * 操作ID
     */
    private String actionId;

    private AuditHttpRequest httpRequest;

    private List<ActionAuditContext> actionAuditContexts = new ArrayList<>();

    private ActionAuditContext currentActionAuditContext;

    private boolean recordSubEvent;


    public AuditContext(String actionId) {
        this.actionId = actionId;
    }

    //    /**
//     * 审计事件
//     */
//    private List<AuditEvent> auditEvents;

    public static AuditContext current() {
        return AuditManagerRegistry.get().current();
    }



    public ActionAuditScope startAuditAction(String actionId) {
        ActionAuditContext actionAuditContext = new ActionAuditContext();
        actionAuditContext.setActionId(actionId);
        return new ActionAuditScopeImpl(ActionAuditContext.current(), actionAuditContext);
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

    public void clearActionAuditContext() {
        actionAuditContexts.clear();
    }

    public void addActionAuditContext(ActionAuditContext actionAuditContext) {
        actionAuditContexts.add(actionAuditContext);
    }

    private static class ActionAuditScopeImpl implements ActionAuditScope {

        @Nullable
        private final ActionAuditContext beforeAttach;
        private final ActionAuditContext toAttach;
        private boolean closed;

        private ActionAuditScopeImpl(@Nullable ActionAuditContext beforeAttach, ActionAuditContext toAttach) {
            this.beforeAttach = beforeAttach;
            this.toAttach = toAttach;
        }

        @Override
        public void close() {
            if (!closed && ActionAuditContext.current() == toAttach) {
                closed = true;
                current().setCurrentActionAuditContext(beforeAttach);
            }
        }
    }


}
