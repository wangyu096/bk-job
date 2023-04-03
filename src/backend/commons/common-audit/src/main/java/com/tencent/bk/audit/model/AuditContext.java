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

import com.tencent.bk.audit.AuditHttpRequest;
import com.tencent.bk.audit.constants.AccessTypeEnum;
import com.tencent.bk.audit.constants.AuditKey;
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

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

    private Map<AuditKey, AuditEvent> events = new HashMap<>();

    private AuditHttpRequest httpRequest;


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

    public AuditEvent findAuditEvent(AuditKey auditKey) {
        return events.get(auditKey);
    }

    public void addAuditEvent(AuditEvent auditEvent) {
        events.put(AuditKey.build(auditEvent.getActionId(), auditEvent.getResourceTypeId(),
            auditEvent.getInstanceId()), auditEvent);
    }

    public void clearAllAuditEvent() {
        events.clear();
    }
}
