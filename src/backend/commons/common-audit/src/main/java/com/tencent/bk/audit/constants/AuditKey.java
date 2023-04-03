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

package com.tencent.bk.audit.constants;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class AuditKey {
    private final String actionId;
    private final String resourceType;
    private final String resourceId;

    private AuditKey(String actionId, String resourceType, String resourceId) {
        this.actionId = actionId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public static AuditKey build(String actionId, String resourceType, String resourceId) {
        return new AuditKey(actionId, resourceType, resourceId);
    }

    public static AuditKey build(String actionId) {
        return new AuditKey(actionId, null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditKey auditKey = (AuditKey) o;
        return Objects.equals(actionId, auditKey.actionId) &&
            Objects.equals(resourceType, auditKey.resourceType) &&
            Objects.equals(resourceId, auditKey.resourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionId, resourceType, resourceId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(actionId);
        if (StringUtils.isNotBlank(resourceType)) {
            sb.append(":").append(resourceType);
        }
        if (StringUtils.isNotBlank(resourceId)) {
            sb.append(":").append(resourceId);
        }
        return sb.toString();
    }
}
