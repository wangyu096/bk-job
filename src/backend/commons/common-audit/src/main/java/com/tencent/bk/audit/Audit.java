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

import com.tencent.bk.audit.exporter.EventExporter;
import com.tencent.bk.audit.model.AuditContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 审计SDK 入口
 */
@Slf4j
public class Audit {

    private final ThreadLocalAuditContextHolder auditContextHolder = new ThreadLocalAuditContextHolder();
    private final EventExporter eventExporter;

    @Autowired
    public Audit(EventExporter eventExporter) {
        this.eventExporter = eventExporter;
        GlobalAuditRegistry.register(this);
    }

    /**
     * 开始审计
     *
     * @param auditContext 审计上下文
     */
    public void startAudit(AuditContext auditContext) {
        if (auditContextHolder.current() != null) {
            log.error("Current audit context is already exist! ");
            return;
        }
        auditContextHolder.set(auditContext);
    }

    /**
     * 返回当前审计上下文
     *
     * @return 当前审计上下文
     */
    public AuditContext currentAuditContext() {
        return auditContextHolder.current();
    }

    /**
     * 是否正在记录审计事件
     */
    public boolean isRecording() {
        return currentAuditContext() != null;
    }

    /**
     * 结束审计
     */
    public void stopAudit() {
        try {
            AuditContext auditContext = auditContextHolder.current();
            if (auditContext == null) {
                log.error("Current audit context is empty!");
                return;
            }
            auditContext.end();
            eventExporter.export(auditContext.getEvents());
        } catch (Throwable e) {
            // 忽略审计错误，避免影响业务代码执行
            log.error("Audit stop caught exception", e);
        } finally {
            auditContextHolder.reset();
        }
    }
}
