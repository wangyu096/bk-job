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

package com.tencent.bk.job.backup.archive;

import com.tencent.bk.job.backup.archive.dao.JobInstanceColdDAO;
import com.tencent.bk.job.backup.archive.dao.JobInstanceHotRecordDAO;
import com.tencent.bk.job.backup.archive.model.ArchiveTaskSummary;
import com.tencent.bk.job.backup.archive.model.DbDataNode;
import com.tencent.bk.job.backup.archive.model.JobInstanceArchiveTaskInfo;
import com.tencent.bk.job.backup.archive.model.TimeAndIdBasedArchiveProcess;
import com.tencent.bk.job.backup.config.ArchiveProperties;
import com.tencent.bk.job.backup.constant.ArchiveModeEnum;
import com.tencent.bk.job.backup.constant.ArchiveTaskStatusEnum;
import com.tencent.bk.job.backup.metrics.ArchiveErrorTaskCounter;
import com.tencent.bk.job.common.util.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.TableRecord;
import org.slf4j.helpers.MessageFormatter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 作业实例数据归档任务基础实现
 *
 * @param <T> 表记录
 */
@Slf4j
public abstract class AbstractJobInstanceArchiveTask<T extends TableRecord<?>> implements JobInstanceArchiveTask {
    protected JobInstanceHotRecordDAO<T> jobInstanceHotRecordDAO;
    protected JobInstanceColdDAO jobInstanceColdDAO;
    protected final ArchiveProperties archiveProperties;
    private final ArchiveTaskLock archiveTaskLock;
    protected final ArchiveErrorTaskCounter archiveErrorTaskCounter;
    protected final ArchiveTaskService archiveTaskService;
    protected final ArchiveTablePropsStorage archiveTablePropsStorage;

    protected String taskId;
    protected DbDataNode dbDataNode;
    protected JobInstanceArchiveTaskInfo archiveTask;

    /**
     * 归档进度
     */
    private TimeAndIdBasedArchiveProcess progress;

    private final ArchiveTaskSummary archiveTaskSummary;

    private boolean isAcquireLock;


    public AbstractJobInstanceArchiveTask(JobInstanceHotRecordDAO<T> jobInstanceHotRecordDAO,
                                          JobInstanceColdDAO jobInstanceColdDAO,
                                          ArchiveProperties archiveProperties,
                                          ArchiveTaskLock archiveTaskLock,
                                          ArchiveErrorTaskCounter archiveErrorTaskCounter,
                                          JobInstanceArchiveTaskInfo archiveTask,
                                          ArchiveTaskService archiveTaskService,
                                          ArchiveTablePropsStorage archiveTablePropsStorage) {
        this.jobInstanceHotRecordDAO = jobInstanceHotRecordDAO;
        this.jobInstanceColdDAO = jobInstanceColdDAO;
        this.archiveProperties = archiveProperties;
        this.archiveTaskLock = archiveTaskLock;
        this.archiveErrorTaskCounter = archiveErrorTaskCounter;
        this.archiveTask = archiveTask;
        this.archiveTaskService = archiveTaskService;
        this.archiveTablePropsStorage = archiveTablePropsStorage;
        this.progress = archiveTask.getProcess();
        this.taskId = buildTaskId(archiveTask);
        this.archiveTaskSummary = new ArchiveTaskSummary(archiveTask, archiveProperties.getMode());
    }

    private String buildTaskId(JobInstanceArchiveTaskInfo archiveTask) {
        return archiveTask.getTaskType() + ":" + archiveTask.getDbDataNode().toDataNodeId()
            + ":" + archiveTask.getDay() + ":" + archiveTask.getHour();
    }


    @Override
    public void execute() {
        archive();
    }

    @Override
    public void stop() {

    }

    private void archive() {
        try {
            if (!acquireLock()) {
                archiveTaskSummary.setSkip(!isAcquireLock);
                return;
            }

            log.info("[{}] Start archive task", taskId);
            // 归档
            backupAndDelete();
        } catch (Throwable e) {
            String msg = MessageFormatter.format(
                "Error while execute archive task : {}",
                taskId
            ).getMessage();
            log.error(msg, e);
            archiveTaskSummary.setMessage(e.getMessage());

            archiveErrorTaskCounter.increment();

            // 更新归档任务状态
            updateArchiveProgress(ArchiveTaskStatusEnum.FAIL, null);
        } finally {
            if (this.isAcquireLock) {
                archiveTaskLock.unlock(taskId);
            }
            log.info(
                "[{}] Archive finished, result: {}",
                taskId,
                JsonUtils.toJson(archiveTaskSummary)
            );
        }
    }

    private void backupAndDelete() {
        boolean backupEnabled = isBackupEnable();
        boolean deleteEnabled = isDeleteEnable();

        int readLimit = 1000;
        long archivedJobInstanceCount = 0;

        long startTime = System.currentTimeMillis();
        log.info("[{}] Archive task mode: {}, backupEnabled: {}, deleteEnabled: {}",
            taskId, archiveProperties.getMode(), backupEnabled, deleteEnabled);
        try {
            List<T> jobInstanceRecords;
            do {
                if (progress != null) {
                    jobInstanceRecords = readSortedJobInstanceFromHotDB(progress.getTimestamp(),
                        archiveTask.getToTimestamp(), progress.getId(), readLimit);
                } else {
                    jobInstanceRecords = readSortedJobInstanceFromHotDB(archiveTask.getFromTimestamp(),
                        archiveTask.getToTimestamp(), null, readLimit);
                }

                if (CollectionUtils.isEmpty(jobInstanceRecords)) {
                    updateArchiveProgress(ArchiveTaskStatusEnum.SUCCESS, null);
                    return;
                }
                archivedJobInstanceCount++;

                List<Long> jobInstanceIds =
                    jobInstanceRecords.stream().map(this::extractJobInstanceId).collect(Collectors.toList());

                // 写入数据到冷 db
                if (backupEnabled) {
                    backupJobInstanceToColdDb(jobInstanceRecords);
                }
                // 从热 db 删除数据
                if (deleteEnabled) {
                    deleteJobInstanceHotData(jobInstanceIds);
                }

                // 更新归档进度
                T lastRecord = jobInstanceRecords.get(jobInstanceRecords.size() - 1);
                Long lastTimestamp = extractJobInstanceCreateTime(lastRecord);
                Long lastJobInstanceId = extractJobInstanceId(lastRecord);
                progress = new TimeAndIdBasedArchiveProcess(lastTimestamp, lastJobInstanceId);
                boolean isFinished = jobInstanceRecords.size() < readLimit;
                updateArchiveProgress(isFinished ? ArchiveTaskStatusEnum.SUCCESS : ArchiveTaskStatusEnum.RUNNING,
                    progress);
            } while (jobInstanceRecords.size() == readLimit);
        } finally {
            long archiveCost = System.currentTimeMillis() - startTime;
            archiveTaskSummary.setArchivedRecordSize(archivedJobInstanceCount);
            archiveTaskSummary.setArchiveCost(archiveCost);
        }
    }

    /**
     * 备份作业实例数据到冷存储
     *
     * @param jobInstances 作业实例列表
     */
    protected abstract void backupJobInstanceToColdDb(List<T> jobInstances);

    /**
     * 删除作业实例热数据
     *
     * @param jobInstanceIds 作业实例 ID 列表
     */
    protected abstract void deleteJobInstanceHotData(List<Long> jobInstanceIds);

    protected boolean isBackupEnable() {
        ArchiveModeEnum archiveMode = ArchiveModeEnum.valOf(archiveProperties.getMode());
        return archiveProperties.isEnabled()
            && (ArchiveModeEnum.BACKUP_THEN_DELETE == archiveMode || ArchiveModeEnum.BACKUP_ONLY == archiveMode);
    }

    protected boolean isDeleteEnable() {
        ArchiveModeEnum archiveMode = ArchiveModeEnum.valOf(archiveProperties.getMode());
        return archiveProperties.isEnabled()
            && (ArchiveModeEnum.BACKUP_THEN_DELETE == archiveMode || ArchiveModeEnum.DELETE_ONLY == archiveMode);
    }

    private boolean acquireLock() {
        this.isAcquireLock = archiveTaskLock.lock(taskId);
        if (!isAcquireLock) {
            log.info("[{}] Acquire lock fail", taskId);
        }
        return isAcquireLock;
    }

    private void updateArchiveProgress(ArchiveTaskStatusEnum taskStatus, TimeAndIdBasedArchiveProcess progress) {
        archiveTask.setStatus(taskStatus);
        archiveTask.setProcess(progress);
        archiveTaskService.updateTask(archiveTask);
    }

    /**
     * 从热 db 读取作业实例熟悉，按照时间+ID 的顺序排序
     *
     * @param fromTimestamp     时间范围-起始-作业实例创建时间（include)
     * @param endTimestamp      时间范围-起始-作业实例创建时间（exclude)
     * @param fromJobInstanceId 作业实例 ID-起始 (exclude)
     * @param limit             读取记录最大数量
     * @return 作业实例记录
     */
    protected abstract List<T> readSortedJobInstanceFromHotDB(Long fromTimestamp,
                                                              Long endTimestamp,
                                                              Long fromJobInstanceId,
                                                              int limit);

    /**
     * 从作业实例记录中提取作业实例 ID
     *
     * @param record 作业实例记录
     */
    protected abstract Long extractJobInstanceId(T record);

    /**
     * 从作业实例记录中提取作业实例创建时间
     *
     * @param record 作业实例记录
     */
    protected abstract Long extractJobInstanceCreateTime(T record);
}
