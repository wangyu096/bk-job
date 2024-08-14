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

package com.tencent.bk.job.backup.archive.dao.impl;

import com.tencent.bk.job.execute.model.tables.StepInstance;
import com.tencent.bk.job.execute.model.tables.records.StepInstanceRecord;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Table;
import org.jooq.TableField;

import static org.jooq.impl.DSL.max;

/**
 * step_instance DAO
 */
public class StepInstanceRecordDAO extends AbstractJobInstanceHotRecordDAO<StepInstanceRecord> {

    private static final StepInstance TABLE = StepInstance.STEP_INSTANCE;

    public StepInstanceRecordDAO(DSLContext context) {
        super(context);
    }

    @Override
    public Table<StepInstanceRecord> getTable() {
        return TABLE;
    }

    /**
     * 获取作业实例ID范围内的步骤实例ID最大值
     *
     * @param taskInstanceId 作业实例ID
     * @return 步骤实例ID 最大值
     */
    public Long getMaxId(Long taskInstanceId) {
        Record1<Long> record =
            context.select(max(TABLE.ID))
                .from(TABLE)
                .where(TABLE.TASK_INSTANCE_ID.lessOrEqual(taskInstanceId))
                .fetchOne();
        if (record != null) {
            Long maxId = (Long) record.get(0);
            if (maxId != null) {
                return maxId;
            }
        }
        return 0L;
    }

    @Override
    public TableField<StepInstanceRecord, Long> getJobInstanceIdField() {
        return TABLE.ID;
    }
}
