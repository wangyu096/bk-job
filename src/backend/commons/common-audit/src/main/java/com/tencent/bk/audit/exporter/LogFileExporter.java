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

package com.tencent.bk.audit.exporter;

import com.tencent.bk.audit.constants.Constants;
import com.tencent.bk.audit.model.AuditEvent;
import com.tencent.bk.audit.utils.EventIdGenerator;
import com.tencent.bk.audit.utils.json.JsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * 审计日志文件导出
 */
public class LogFileExporter implements EventExporter {
    private final Logger LOGGER;

    public LogFileExporter() {
        this.LOGGER = LoggerFactory.getLogger(Constants.AUDIT_LOGGER_NAME);
    }

    @Override
    public void export(AuditEvent event) {
        if (StringUtils.isBlank(event.getId())) {
            event.setId(EventIdGenerator.generateId());
        }
        LOGGER.info(JsonUtils.toJson(event));
    }

    @Override
    public void export(Collection<AuditEvent> events) {
        if (CollectionUtils.isEmpty(events)) {
            return;
        }
        events.forEach(event -> LOGGER.info(JsonUtils.toJson(event)));
    }
}
