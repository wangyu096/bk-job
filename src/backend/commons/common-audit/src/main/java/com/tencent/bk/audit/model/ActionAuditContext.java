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

import java.util.List;
import java.util.Map;

/**
 * 操作审计上下文实现
 */
public interface ActionAuditContext {
    ActionAuditContext INVALID = new InvalidActionAuditContext();

    /**
     * 返回当前操作审计上下文
     */
    static ActionAuditContext current() {
        return AuditContext.current().currentActionAuditContext();
    }

    ActionAuditScope makeCurrent();

    void addAttribute(String name, Object value);

    boolean hasResource();

    void end();

    String getActionId();

    Long getStartTime();

    Long getEndTime();

    String getResourceType();

    List<String> getInstanceIdList();

    List<String> getInstanceNameList();

    List<Object> getOriginInstanceList();

    List<Object> getInstanceList();

    String getContent();

    Map<String, Object> getAttributes();

    List<AuditEvent> getEvents();

    void setInstanceIdList(List<String> instanceIdList);

    void setInstanceNameList(List<String> instanceNameList);

    void setOriginInstanceList(List<Object> originInstanceList);

    void setInstanceList(List<Object> instanceList);
}
