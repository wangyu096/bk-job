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

import com.tencent.bk.audit.ActionCallable;
import com.tencent.bk.audit.ActionRunnable;

import java.util.List;
import java.util.Map;

/**
 * 操作审计上下文实现
 */
public interface ActionAuditContext {
    /**
     * 非法的操作审计上下文，用于当前操作审计上下文不存在时返回这个实例（避免返回null导致系统异常)
     */
    ActionAuditContext INVALID = new InvalidActionAuditContext();

    static ActionAuditContextBuilder builder(String actionId) {
        return ActionAuditContextBuilder.builder(actionId);
    }

    /**
     * 返回当前操作审计上下文
     */
    static ActionAuditContext current() {
        return AuditContext.current().currentActionAuditContext();
    }

    /**
     * 设置自身为当前操作审计上下文
     *
     * @return scope
     */
    ActionAuditScope makeCurrent();

    /**
     * 结束操作审计上下文
     */
    void end();

    default <T> ActionCallable<T> wrapActionCallable(ActionCallable<T> callable) {
        return () -> {
            ActionAuditScope scope = null;
            ActionAuditContext current = null;
            try {
                scope = makeCurrent();
                current = current();
            } catch (Throwable ignore) {
                // 保证业务代码正常执行，忽略所有审计错误
            }
            try {
                return callable.call();
            } finally {
                safelyEndActionAuditContext(scope, current);
            }
        };
    }

    default void safelyEndActionAuditContext(ActionAuditScope scope,
                                             ActionAuditContext current) {
        try {
            if (current != null) {
                current.end();
            }
            if (scope != null) {
                scope.close();
            }
        } catch (Throwable ignore) {
            // 保证业务代码正常执行，忽略所有审计错误
        }
    }

    default ActionRunnable wrapActionRunnable(ActionRunnable actionRunnable) {
        return () -> {
            ActionAuditScope scope = null;
            ActionAuditContext current = null;
            try {
                scope = makeCurrent();
                current = current();
            } catch (Throwable ignore) {
                // 保证业务代码正常执行，忽略所有审计错误
            }
            try {
                actionRunnable.run();
            } finally {
                safelyEndActionAuditContext(scope, current);
            }
        };
    }

    /**
     * 获取审计事件
     *
     * @return 生成的审计事件
     */
    List<AuditEvent> getEvents();

    /**
     * 新增属性
     *
     * @param name  属性名称
     * @param value 属性值
     */
    void addAttribute(String name, Object value);

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

    void setInstanceIdList(List<String> instanceIdList);

    void setInstanceNameList(List<String> instanceNameList);

    void setOriginInstanceList(List<Object> originInstanceList);

    void setInstanceList(List<Object> instanceList);

    void setInstanceId(String instanceId);

    void setInstanceName(String instanceName);

    void setOriginInstance(Object originInstance);

    void setInstance(Object instance);
}
