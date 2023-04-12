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

import com.tencent.bk.audit.AuditManagerRegistry;
import com.tencent.bk.audit.annotations.ActionAuditRecord;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ActionAuditContext {

    private String actionId;

    private Long startTime;

    private Long endTime;

    private String resourceType;

    private List<String> instanceIdList;

    private List<String> instanceNameList;

    private List<Object> originInstanceList;

    private List<Object> instanceList;

    private Map<String, Object> attributes = new HashMap<>();

    private List<AuditEvent> events = new ArrayList<>();

    private ActionAuditRecord actionAuditRecord;

    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }

//    public void addEvent(AuditKey key, AuditEvent value) {
//        events.put(key, value);
//    }
//
//    public AuditEvent findAuditEvent(AuditKey auditKey) {
//        return events.get(auditKey);
//    }

    public void addAuditEvent(AuditEvent auditEvent) {
        events.add(auditEvent);
    }

    public void clearAllAuditEvent() {
        events.clear();
    }

    public static ActionAuditContext current() {
        return AuditManagerRegistry.get().current().currentActionAuditContext();
    }


}
