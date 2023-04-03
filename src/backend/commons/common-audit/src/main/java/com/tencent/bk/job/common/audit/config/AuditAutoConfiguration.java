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

package com.tencent.bk.job.common.audit.config;

import com.tencent.bk.audit.AuditAspect;
import com.tencent.bk.audit.AuditExceptionResolver;
import com.tencent.bk.audit.AuditManager;
import com.tencent.bk.audit.AuditRequestProvider;
import com.tencent.bk.audit.LogFileExporter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AuditProperties.class)
@ConditionalOnProperty(name = "auditEntry.enabled", havingValue = "true", matchIfMissing = true)
public class AuditAutoConfiguration {
    private static final String EXPORTER_TYPE_LOG_FILE = "log_file";

    @Bean("logFileEventExporter")
    @ConditionalOnProperty(name = "auditEntry.exporter.type", havingValue = EXPORTER_TYPE_LOG_FILE, matchIfMissing =
        true)
    LogFileExporter logFileEventExporter() {
        return new LogFileExporter();
    }

    @Bean("auditManager")
    @ConditionalOnProperty(name = "auditEntry.exporter.type", havingValue = EXPORTER_TYPE_LOG_FILE, matchIfMissing =
        true)
    AuditManager auditManager(LogFileExporter logFileExporter) {
        return new AuditManager(logFileExporter);
    }

    @Bean("auditRequestProvider")
    public AuditRequestProvider auditRequestProvider() {
        return new JobAuditRequestProvider();
    }

    @Bean("auditRecordAspect")
    public AuditAspect auditRecordAspect(AuditManager auditManager,
                                         AuditRequestProvider auditRequestProvider,
                                         AuditExceptionResolver auditExceptionResolver) {
        return new AuditAspect(auditManager, auditRequestProvider, auditExceptionResolver);
    }

    @Bean("auditExceptionResolver")
    public JobAuditExceptionResolver auditExceptionResolver() {
        return new JobAuditExceptionResolver();
    }
}
