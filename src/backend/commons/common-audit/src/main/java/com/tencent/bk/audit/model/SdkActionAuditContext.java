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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作审计上下文
 */
@Slf4j
public class SdkActionAuditContext implements ActionAuditContext {

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
    private List<Object> originInstanceList;

    /**
     * 当前实例列表
     */
    private List<Object> instanceList;

    /**
     * 审计事件描述
     */
    private final String content;

    private final Class<? extends AuditEventBuilder> eventBuilderClass;

    /**
     * 其它通过 ActionAuditRecord.AuditAttribute 设置的属性
     */
    private final Map<String, Object> attributes;

    /**
     * 当前操作产生的审计事件列表
     */
    private final List<AuditEvent> events = new ArrayList<>();

    SdkActionAuditContext(String actionId,
                          String resourceType,
                          List<String> instanceIdList,
                          List<String> instanceNameList,
                          List<Object> originInstanceList,
                          List<Object> instanceList,
                          String content,
                          Class<? extends AuditEventBuilder> eventBuilderClass,
                          Map<String, Object> attributes) {
        this.actionId = actionId;
        this.startTime = System.currentTimeMillis();
        this.resourceType = resourceType;
        this.instanceIdList = instanceIdList;
        this.instanceNameList = instanceNameList;
        this.originInstanceList = originInstanceList;
        this.instanceList = instanceList;
        this.content = content;
        this.eventBuilderClass = eventBuilderClass == null ? DefaultAuditEventBuilder.class : eventBuilderClass;
        this.attributes = (attributes == null ? new HashMap<>() : attributes);
    }

    @Override
    public ActionAuditScope makeCurrent() {
        ActionAuditContext beforeContext = AuditContext.current().currentActionAuditContext();
        AuditContext.current().setCurrentActionAuditContext(this);
        return new ActionAuditScopeImpl(beforeContext, this);
    }

    @Override
    public void addAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    @Override
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

    @Override
    public String getActionId() {
        return actionId;
    }

    @Override
    public Long getStartTime() {
        return startTime;
    }

    @Override
    public Long getEndTime() {
        return endTime;
    }

    @Override
    public String getResourceType() {
        return resourceType;
    }

    @Override
    public List<String> getInstanceIdList() {
        return instanceIdList == null ? null : Collections.unmodifiableList(instanceIdList);
    }

    @Override
    public List<String> getInstanceNameList() {
        return instanceNameList == null ? null : Collections.unmodifiableList(instanceNameList);
    }

    @Override
    public List<Object> getOriginInstanceList() {
        return originInstanceList == null ? null : Collections.unmodifiableList(originInstanceList);
    }

    @Override
    public List<Object> getInstanceList() {
        return instanceList == null ? null : Collections.unmodifiableList(instanceList);
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    @Override
    public List<AuditEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    @Override
    public void setInstanceIdList(List<String> instanceIdList) {
        this.instanceIdList = instanceIdList;
    }

    @Override
    public void setInstanceNameList(List<String> instanceNameList) {
        this.instanceNameList = instanceNameList;
    }

    @Override
    public void setOriginInstanceList(List<Object> originInstanceList) {
        this.originInstanceList = originInstanceList;
    }

    @Override
    public void setInstanceList(List<Object> instanceList) {
        this.instanceList = instanceList;
    }
}
