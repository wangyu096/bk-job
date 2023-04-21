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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultAuditEventBuilder implements AuditEventBuilder {
    private final ActionAuditContext actionAuditContext;

    public DefaultAuditEventBuilder() {
        this.actionAuditContext = ActionAuditContext.current();
    }

    @Override
    public List<AuditEvent> build() {
        List<AuditEvent> events = new ArrayList<>();
        Map<String, Object> eventAttributes = new HashMap<>(actionAuditContext.getAttributes());

        List<String> instanceIdList = actionAuditContext.getInstanceIdList();
        if (StringUtils.isNotBlank(actionAuditContext.getResourceType())) {
            if (CollectionUtils.isNotEmpty(instanceIdList)) {
                List<String> instanceNameList = actionAuditContext.getInstanceNameList();
                List<Object> originInstanceList = actionAuditContext.getOriginInstanceList();
                List<Object> instanceList = actionAuditContext.getInstanceList();

                for (int index = 0; index < instanceIdList.size(); index++) {
                    String instanceId = safeGetElement(instanceIdList, index);
                    String instanceName = safeGetElement(instanceNameList, index);
                    Object originInstance = safeGetElement(originInstanceList, index);
                    Object instance = safeGetElement(instanceList, index);
                    eventAttributes.put(AuditAttributeNames.INSTANCE_ID, instanceId);
                    eventAttributes.put(AuditAttributeNames.INSTANCE_NAME, instanceName);
                    AuditEvent auditEvent = buildAuditEvent(instanceId, instanceName, originInstance, instance,
                        eventAttributes);
                    events.add(auditEvent);
                }
            }
        } else {
            AuditEvent auditEvent = buildAuditEvent(null, null, null, null,
                eventAttributes);
            events.add(auditEvent);
        }

        return events;
    }

    protected <T> T safeGetElement(List<T> list, int index) {
        return list != null && list.size() > index ? list.get(index) : null;
    }

    protected AuditEvent buildAuditEvent(String instanceId,
                                         String instanceName,
                                         Object originInstance,
                                         Object instance,
                                         Map<String, Object> eventAttributes) {
        AuditEvent auditEvent = buildBasicAuditEvent();

        // 审计记录 - 原始数据
        auditEvent.setInstanceOriginData(originInstance);
        // 审计记录 - 更新后数据
        auditEvent.setInstanceData(instance);

        auditEvent.setInstanceId(instanceId);
        auditEvent.setInstanceName(instanceName);
        auditEvent.setContent(resolveAttributes(actionAuditContext.getContent(),
            eventAttributes));
        return auditEvent;
    }

    protected AuditEvent buildBasicAuditEvent() {
        AuditEvent auditEvent = new AuditEvent();
        auditEvent.setId(EventIdGenerator.generateId());
        auditEvent.setActionId(actionAuditContext.getActionId());
        auditEvent.setResourceTypeId(actionAuditContext.getResourceType());
        auditEvent.setStartTime(actionAuditContext.getStartTime());
        auditEvent.setEndTime(actionAuditContext.getEndTime());
        auditEvent.setResultCode(ErrorCode.RESULT_OK);
        auditEvent.setResultContent("Success");
        return auditEvent;
    }

    /**
     * 根据事件属性解析内容
     *
     * @param contentTemplate 内容模板
     * @param eventAttributes 事件属性
     * @return 解析之后的值
     */
    protected String resolveAttributes(String contentTemplate, Map<String, Object> eventAttributes) {
        Map<String, String> vars = new HashMap<>();
        eventAttributes.forEach((k, v) -> vars.put(k, v == null ? "" : v.toString()));
        String pattern = "(\\{\\{(.*?)\\}\\})";
        return StringUtil.replaceByRegex(contentTemplate, pattern, vars);
    }
}
