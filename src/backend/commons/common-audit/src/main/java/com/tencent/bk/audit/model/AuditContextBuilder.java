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
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;

import java.util.List;

public class AuditContextBuilder {

    private String requestId;

    private String username;

    private UserIdentifyTypeEnum userIdentifyType;

    private String userIdentifyTenantId;

    private String bkAppCode;

    private AccessTypeEnum accessType;

    private String accessSourceIp;

    private String accessUserAgent;

    /**
     * 操作ID
     */
    private final String actionId;

    private AuditHttpRequest httpRequest;

    /**
     * 子操作
     */
    private List<String> subActionIds;


    public static AuditContextBuilder builder(String actionId) {
        return new AuditContextBuilder(actionId);
    }

    private AuditContextBuilder(String actionId) {
        this.actionId = actionId;
    }

    public AuditContextBuilder setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public AuditContextBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public AuditContextBuilder setUserIdentifyType(UserIdentifyTypeEnum userIdentifyType) {
        this.userIdentifyType = userIdentifyType;
        return this;
    }

    public AuditContextBuilder setUserIdentifyTenantId(String userIdentifyTenantId) {
        this.userIdentifyTenantId = userIdentifyTenantId;
        return this;
    }

    public AuditContextBuilder setBkAppCode(String bkAppCode) {
        this.bkAppCode = bkAppCode;
        return this;
    }

    public AuditContextBuilder setAccessType(AccessTypeEnum accessType) {
        this.accessType = accessType;
        return this;
    }

    public AuditContextBuilder setAccessSourceIp(String accessSourceIp) {
        this.accessSourceIp = accessSourceIp;
        return this;
    }

    public AuditContextBuilder setAccessUserAgent(String accessUserAgent) {
        this.accessUserAgent = accessUserAgent;
        return this;
    }

    public AuditContextBuilder setHttpRequest(AuditHttpRequest httpRequest) {
        this.httpRequest = httpRequest;
        return this;
    }

    public AuditContextBuilder setSubActionIds(List<String> subActionIds) {
        this.subActionIds = subActionIds;
        return this;
    }

    public SdkAuditContext start() {
        return SdkAuditContext.start(actionId, requestId, username, userIdentifyType, userIdentifyTenantId,
            bkAppCode, accessType, accessSourceIp, accessUserAgent, httpRequest, subActionIds);

    }
}
