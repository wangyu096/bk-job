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

import com.tencent.bk.audit.constants.AuditAttributeNames;
import com.tencent.bk.audit.model.ActionAuditContext;
import com.tencent.bk.audit.model.AuditEvent;
import com.tencent.bk.audit.utils.EventIdGenerator;
import com.tencent.bk.job.common.constant.ErrorCode;
import com.tencent.bk.job.common.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DefaultAuditEventBuilder implements AuditEventBuilder {
    private final ActionAuditContext actionAuditContext;
    private final Map<String, Object> attributes;

    public DefaultAuditEventBuilder(ActionAuditContext actionAuditContext) {
        this.actionAuditContext = actionAuditContext;
        this.attributes = actionAuditContext.getAttributes();
    }

    @Override
    public List<AuditEvent> build() {
        List<AuditEvent> events = new ArrayList<>();
        if (hasMultiResourceInstance()) {
            List<Object> instanceIdList = safeCastList(attributes.get(AuditAttributeNames.INSTANCE_ID_LIST));
            List<Object> instanceNameList = safeCastList(attributes.get(AuditAttributeNames.INSTANCE_NAME_LIST));
            List<Object> originInstanceList = safeCastList(attributes.get(AuditAttributeNames.ORIGIN_INSTANCE_LIST));
            List<Object> instanceList = safeCastList(attributes.get(AuditAttributeNames.INSTANCE_LIST));
            Map<String, Object> eventAttributes = new HashMap<>();

            for (int index = 0; index < instanceIdList.size(); index++) {
                Object instanceId = safeGetElement(instanceIdList, index);
                Object instanceName = safeGetElement(instanceNameList, index);
                Object originInstance = safeGetElement(originInstanceList, index);
                Object instance = safeGetElement(instanceList, index);
                AuditEvent auditEvent = buildAuditEvent(instanceId, instanceName, originInstance, instance,
                    eventAttributes);
                events.add(auditEvent);
            }
        } else {
            Object instanceId = attributes.get(AuditAttributeNames.INSTANCE_ID);
            Object instanceName = attributes.get(AuditAttributeNames.INSTANCE_NAME);
            Object originInstance = attributes.get(AuditAttributeNames.ORIGIN_INSTANCE);
            Object instance = attributes.get(AuditAttributeNames.INSTANCE);
            Map<String, Object> eventAttributes = new HashMap<>(attributes);
            AuditEvent auditEvent = buildAuditEvent(instanceId, instanceName, originInstance, instance,
                eventAttributes);
            events.add(auditEvent);
        }

        return events;
    }

    private String safeToString(Object object) {
        return Objects.toString(object, null);
    }

    private List<Object> safeCastList(Object listObj) {
        return listObj == null ? null : (listObj instanceof List ? (List<Object>) listObj : null);
    }

    private Object safeGetElement(List<Object> list, int index) {
        return list != null && list.size() > index ? list.get(index) : null;
    }

    private AuditEvent buildAuditEvent(Object instanceId,
                                       Object instanceName,
                                       Object originInstance,
                                       Object instance,
                                       Map<String, Object> eventAttributes) {
        AuditEvent auditEvent = new AuditEvent();
        auditEvent.setId(EventIdGenerator.generateId());
        auditEvent.setActionId(actionAuditContext.getActionAuditRecord().actionId());
        auditEvent.setResourceTypeId(actionAuditContext.getResourceType());
        auditEvent.setStartTime(actionAuditContext.getStartTime());
        auditEvent.setEndTime(actionAuditContext.getEndTime());
        auditEvent.setResultCode(ErrorCode.RESULT_OK);
        auditEvent.setResultContent("Success");

        // 审计记录 - 原始数据
        auditEvent.setInstanceOriginData(originInstance);
        // 审计记录 - 更新后数据
        auditEvent.setInstanceData(instance);

        auditEvent.setInstanceId(safeToString(instanceId));
        auditEvent.setInstanceName(safeToString(instanceName));
        auditEvent.setContent(resolveContent(actionAuditContext.getActionAuditRecord().content(),
            eventAttributes));
        return auditEvent;
    }

    boolean hasMultiResourceInstance() {
        return attributes.containsKey(AuditAttributeNames.INSTANCE_ID_LIST);
    }

    private String resolveContent(String contentTemplate, Map<String, Object> eventAttributes) {
        Map<String, String> vars = new HashMap<>();
        eventAttributes.forEach((k, v) -> vars.put(k, v == null ? "" : v.toString()));
        String pattern = "(\\{\\{(.*?)\\}\\})";
        return StringUtil.replaceByRegex(contentTemplate, pattern, vars);
    }
}
