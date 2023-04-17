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

import com.tencent.bk.audit.AuditEventBuilder;
import com.tencent.bk.audit.DefaultAuditEventBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作审计上下文
 */
@Slf4j
public class ActionAuditContext {

    /**
     * 操作 ID
     */
    private final String actionId;

    /**
     * 操作开始时间
     */
    private final Long startTime;

    /**
     * 操作结束时间
     */
    private Long endTime;

    /**
     * 资源类型
     */
    private final String resourceType;

    /**
     * 操作实例ID列表
     */
    private List<String> instanceIdList;

    /**
     * 操作实例名称列表，需要与instanceIdList中的ID一一对应
     */
    private List<String> instanceNameList;

    /**
     * 原始实例列表
     */
    private List<AuditInstance> originInstanceList;

    /**
     * 当前实例列表
     */
    private List<AuditInstance> instanceList;

    /**
     * 审计事件描述
     */
    private final String content;

    private final Class<? extends AuditEventBuilder> eventBuilderClass;

    /**
     * 其它通过 ActionAuditRecord.AuditAttribute 设置的属性
     */
    private final Map<String, Object> attributes = new HashMap<>();

    /**
     * 当前操作产生的审计事件列表
     */
    private final List<AuditEvent> events = new ArrayList<>();


    private ActionAuditContext(String actionId,
                               String resourceType,
                               String content,
                               Class<? extends AuditEventBuilder> eventBuilderClass,
                               Long startTime) {
        this.actionId = actionId;
        this.resourceType = resourceType;
        this.content = content;
        this.eventBuilderClass = eventBuilderClass == null ? DefaultAuditEventBuilder.class : eventBuilderClass;
        this.startTime = startTime;
    }

    static ActionAuditContext start(String actionId,
                                    String resourceType,
                                    String content,
                                    Class<? extends AuditEventBuilder> eventBuilderClass) {
        return new ActionAuditContext(actionId, resourceType, content, eventBuilderClass,
            System.currentTimeMillis());
    }

    /**
     * 返回当前操作审计上下文
     */
    public static ActionAuditContext current() {
        return AuditContext.current().currentActionAuditContext();
    }

    public ActionAuditScope makeCurrent() {
        ActionAuditContext beforeContext = current();
        AuditContext.current().setCurrentActionAuditContext(this);
        return new ActionAuditScopeImpl(beforeContext, this);
    }

    public void addAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public boolean hasResource() {
        return StringUtils.isNotBlank(resourceType);
    }

    public void end() {
        endTime = System.currentTimeMillis();
        buildEvents();
        AuditContext.current().addActionAuditContext(this);
    }

    private void buildEvents() {
        try {
            AuditEventBuilder eventBuilder = eventBuilderClass.newInstance();
            events.addAll(eventBuilder.build());
        } catch (Throwable e) {
            log.error("ActionAuditContext - build event caught error", e);
        }
    }

    public String getActionId() {
        return actionId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public String getResourceType() {
        return resourceType;
    }

    public List<String> getInstanceIdList() {
        return instanceIdList == null ? null : Collections.unmodifiableList(instanceIdList);
    }

    public List<String> getInstanceNameList() {
        return instanceNameList == null ? null : Collections.unmodifiableList(instanceNameList);
    }

    public List<AuditInstance> getOriginInstanceList() {
        return originInstanceList == null ? null : Collections.unmodifiableList(originInstanceList);
    }

    public List<AuditInstance> getInstanceList() {
        return instanceList == null ? null : Collections.unmodifiableList(instanceList);
    }

    public String getContent() {
        return content;
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public List<AuditEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public void setInstanceIdList(List<String> instanceIdList) {
        this.instanceIdList = instanceIdList;
    }

    public void setInstanceNameList(List<String> instanceNameList) {
        this.instanceNameList = instanceNameList;
    }

    public void setOriginInstanceList(List<AuditInstance> originInstanceList) {
        this.originInstanceList = originInstanceList;
    }

    public void setInstanceList(List<AuditInstance> instanceList) {
        this.instanceList = instanceList;
    }
}
