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

package com.tencent.bk.audit;

import com.tencent.bk.audit.constants.AccessTypeEnum;
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class DefaultAuditRequestProvider implements AuditRequestProvider {
    public static final String HEADER_USERNAME = "X-Username";
    public static final String HEADER_USER_IDENTIFY_TENANT_ID = "X-User-Identify-Tenant-Id";
    public static final String HEADER_USER_IDENTIFY_TYPE = "X-User-Identify-Type";
    public static final String HEADER_ACCESS_TYPE = "X-Access-Type";
    public static final String HEADER_REQUEST_ID = "X-Request-Id";
    public static final String HEADER_BK_APP_CODE = "X-Bk-App-Code";

    @Override
    public HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    @Override
    public String getUsername() {
        HttpServletRequest httpServletRequest = getRequest();
        return httpServletRequest.getHeader(HEADER_USERNAME);
    }

    @Override
    public UserIdentifyTypeEnum getUserIdentifyType() {
        HttpServletRequest httpServletRequest = getRequest();
        return UserIdentifyTypeEnum.valOf(httpServletRequest.getHeader(HEADER_USER_IDENTIFY_TYPE));
    }

    @Override
    public String getUserIdentifyTenantId() {
        HttpServletRequest httpServletRequest = getRequest();
        return httpServletRequest.getHeader(HEADER_USER_IDENTIFY_TENANT_ID);
    }

    @Override
    public AccessTypeEnum getAccessType() {
        HttpServletRequest httpServletRequest = getRequest();
        return AccessTypeEnum.valOf(httpServletRequest.getHeader(HEADER_ACCESS_TYPE));
    }

    @Override
    public String getRequestId() {
        HttpServletRequest httpServletRequest = getRequest();
        return httpServletRequest.getHeader(HEADER_REQUEST_ID);
    }

    @Override
    public String getBkAppCode() {
        HttpServletRequest httpServletRequest = getRequest();
        return httpServletRequest.getHeader(HEADER_BK_APP_CODE);
    }

    @Override
    public String getClientIp() {
        HttpServletRequest request = getRequest();
        String xff = request.getHeader("X-Forwarded-For");
        if (xff == null) {
            return request.getRemoteAddr();
        } else {
            return xff.contains(",") ? xff.split(",")[0] : xff;
        }
    }

    @Override
    public String getUserAgent() {
        HttpServletRequest request = getRequest();
        return request.getHeader("User-Agent");
    }
}
