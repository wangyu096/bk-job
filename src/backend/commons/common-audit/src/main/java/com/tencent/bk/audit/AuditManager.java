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

import com.tencent.bk.audit.constants.AuditKey;
import com.tencent.bk.audit.exporter.EventExporter;
import com.tencent.bk.audit.model.ActionAuditContext;
import com.tencent.bk.audit.model.AuditContext;
import com.tencent.bk.audit.model.AuditEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class AuditManager {

    private final ThreadLocalAuditContextHolder auditContextHolder = new ThreadLocalAuditContextHolder();
    private final EventExporter eventExporter;

    @Autowired
    public AuditManager(EventExporter eventExporter) {
        this.eventExporter = eventExporter;
        AuditManagerRegistry.register(this);
    }

    public AuditContext startAudit(String actionId) {
        if (auditContextHolder.current() != null) {
            log.error("Current audit context is already exist! ");
            return null;
        }
        AuditContext auditContext = new AuditContext(actionId);
        auditContext.setStartTime(System.currentTimeMillis());
        auditContextHolder.set(auditContext);
        return auditContext;
    }

    public AuditContext current() {
        return auditContextHolder.current();
    }

    public void stopAudit() {
        try {
            AuditContext auditContext = auditContextHolder.current();
            if (auditContext == null) {
                log.error("Current audit context is empty! Skip stop audit event");
                return;
            }
            auditContext.setEndTime(System.currentTimeMillis());
            Map<AuditKey, AuditEvent> auditEvents = new HashMap<>();
            List<ActionAuditContext> actionAuditContexts = auditContext.getActionAuditContexts();
            if (auditContext.isRecordSubEvent()) {
                actionAuditContexts = actionAuditContexts.stream()
                    .filter(actionAuditContext -> actionAuditContext.getActionId().equals(auditContext.getActionId()))
                    .collect(Collectors.toList());
            }
            actionAuditContexts.forEach(actionAuditContext ->
                actionAuditContext.getEvents().forEach(
                    auditEvent -> auditEvents.put(auditEvent.toAuditKey(), auditEvent)
                )
            );
            eventExporter.export(auditEvents.values());
        } finally {
            auditContextHolder.reset();
        }
    }
}
