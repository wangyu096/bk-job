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

import com.tencent.bk.job.backup.archive.DbDataNode;
import com.tencent.bk.job.backup.archive.TimeAndIdBasedArchiveProcess;
import com.tencent.bk.job.backup.archive.dao.ArchiveTaskDAO;
import com.tencent.bk.job.backup.archive.model.HourArchiveTask;
import com.tencent.bk.job.backup.constant.ArchiveTaskStatusEnum;
import com.tencent.bk.job.backup.constant.ArchiveTaskTypeEnum;
import com.tencent.bk.job.backup.model.tables.ArchiveTask;
import com.tencent.bk.job.backup.model.tables.records.ArchiveTaskRecord;
import com.tencent.bk.job.common.mysql.jooq.JooqDataTypeUtil;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.TableField;
import org.jooq.UpdateSetMoreStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ArchiveTaskDAOImpl implements ArchiveTaskDAO {

    private final DSLContext ctx;
    private static final ArchiveTask T = ArchiveTask.ARCHIVE_TASK;

    private static final TableField<?, ?>[] ALL_FIELDS = {
        T.TASK_TYPE,
        T.DATA_NODE,
        T.DAY,
        T.HOUR,
        T.FROM_TIMESTAMP,
        T.TO_TIMESTAMP,
        T.TASK_DESC,
        T.PROCESS,
        T.STATUS,
        T.CREATE_TIME,
        T.LAST_UPDATE_TIME
    };

    @Autowired
    public ArchiveTaskDAOImpl(@Qualifier("job-backup-dsl-context") DSLContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public HourArchiveTask getLatestArchiveTask(ArchiveTaskTypeEnum taskType) {
        Record record = ctx.select(ALL_FIELDS)
            .from(T)
            .where(T.TASK_TYPE.eq(JooqDataTypeUtil.toByte(taskType.getType())))
            .orderBy(T.DAY.desc(), T.HOUR.desc())
            .fetchOne();

        return extract(record);
    }

    private HourArchiveTask extract(Record record) {
        if (record == null) {
            return null;
        }
        HourArchiveTask archiveTask = new HourArchiveTask();
        archiveTask.setTaskType(ArchiveTaskTypeEnum.valOf(record.get(T.TASK_TYPE)));
        archiveTask.setDbDataNode(DbDataNode.fromDataNodeId(record.get(T.DATA_NODE)));
        archiveTask.setDay(record.get(T.DAY));
        archiveTask.setHour(JooqDataTypeUtil.toInteger(record.get(T.HOUR)));
        archiveTask.setFromTimestamp(record.get(T.FROM_TIMESTAMP));
        archiveTask.setToTimestamp(record.get(T.TO_TIMESTAMP));
        archiveTask.setTaskDesc(record.get(T.TASK_DESC));
        archiveTask.setProcess(TimeAndIdBasedArchiveProcess.fromPersistentProcess(record.get(T.PROCESS)));
        archiveTask.setStatus(ArchiveTaskStatusEnum.valOf(record.get(T.STATUS)));
        archiveTask.setCreateTime(record.get(T.CREATE_TIME));
        archiveTask.setLastUpdateTime(record.get(T.LAST_UPDATE_TIME));
        return archiveTask;
    }

    @Override
    public void saveArchiveTask(HourArchiveTask hourArchiveTask) {
        long createTime = System.currentTimeMillis();
        ctx.insertInto(
                T,
                T.TASK_TYPE,
                T.DATA_NODE,
                T.DAY,
                T.HOUR,
                T.FROM_TIMESTAMP,
                T.TO_TIMESTAMP,
                T.TASK_DESC,
                T.PROCESS,
                T.STATUS,
                T.CREATE_TIME,
                T.LAST_UPDATE_TIME)
            .values(
                JooqDataTypeUtil.toByte(hourArchiveTask.getTaskType().getType()),
                hourArchiveTask.getDbDataNode().toDataNodeId(),
                hourArchiveTask.getDay(),
                JooqDataTypeUtil.toByte(hourArchiveTask.getHour()),
                hourArchiveTask.getFromTimestamp(),
                hourArchiveTask.getToTimestamp(),
                hourArchiveTask.getTaskDesc(),
                hourArchiveTask.getProcess() != null ? hourArchiveTask.getProcess().toPersistentProcess() : null,
                JooqDataTypeUtil.toByte(hourArchiveTask.getStatus().getStatus()),
                createTime,
                createTime
            )
            .execute();
    }

    @Override
    public List<HourArchiveTask> listRunningTasks(ArchiveTaskTypeEnum taskType) {
        Result<Record> result = ctx.select(ALL_FIELDS)
            .from(T)
            .where(T.TASK_TYPE.eq(JooqDataTypeUtil.toByte(taskType.getType())))
            .and(T.STATUS.eq(JooqDataTypeUtil.toByte(ArchiveTaskStatusEnum.RUNNING.getStatus())))
            .fetch();

        List<HourArchiveTask> tasks = new ArrayList<>(result.size());
        result.forEach(record -> tasks.add(extract(record)));
        return tasks;
    }

    @Override
    public List<HourArchiveTask> listScheduleTasks(ArchiveTaskTypeEnum taskType, int limit) {
        Result<Record> result = ctx.select(ALL_FIELDS)
            .from(T)
            .where(T.TASK_TYPE.eq(JooqDataTypeUtil.toByte(taskType.getType())))
            .and(T.STATUS.in(
                JooqDataTypeUtil.toByte(ArchiveTaskStatusEnum.PENDING.getStatus()),
                JooqDataTypeUtil.toByte(ArchiveTaskStatusEnum.SUSPENDED.getStatus()),
                JooqDataTypeUtil.toByte(ArchiveTaskStatusEnum.FAIL.getStatus())))
            .orderBy(T.DAY.desc(), T.HOUR.desc())
            .limit(limit)
            .fetch();

        List<HourArchiveTask> tasks = new ArrayList<>(result.size());
        result.forEach(record -> tasks.add(extract(record)));
        return tasks;
    }

    @Override
    public void updateTask(HourArchiveTask archiveTask) {
        if (archiveTask.getStatus() == null && archiveTask.getProcess() == null) {
            // 无需更新
            return;
        }
        UpdateSetMoreStep<ArchiveTaskRecord> updateSetMoreStep;
        updateSetMoreStep = ctx
            .update(T)
            .set(T.LAST_UPDATE_TIME, System.currentTimeMillis());
        if (archiveTask.getStatus() != null) {
            updateSetMoreStep
                .set(T.STATUS, JooqDataTypeUtil.toByte(archiveTask.getStatus().getStatus()));
        }
        if (archiveTask.getProcess() != null) {
            updateSetMoreStep
                .set(T.PROCESS, archiveTask.getProcess().toPersistentProcess());
        }
        updateSetMoreStep
            .where(T.TASK_TYPE.eq(JooqDataTypeUtil.toByte(archiveTask.getTaskType().getType())))
            .and(T.DATA_NODE.eq(archiveTask.getDbDataNode().toDataNodeId()))
            .and(T.DAY.eq(archiveTask.getDay()))
            .and(T.HOUR.eq(archiveTask.getHour().byteValue()))
            .execute();
    }
}
