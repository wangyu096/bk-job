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

package com.tencent.bk.job.backup.config;

import com.tencent.bk.job.backup.archive.JobExecuteArchiveManage;
import com.tencent.bk.job.backup.dao.ExecuteArchiveDAO;
import com.tencent.bk.job.backup.dao.impl.ExecuteArchiveDAOImpl;
import com.tencent.bk.job.backup.dao.impl.FileSourceTaskRecordDAO;
import com.tencent.bk.job.backup.dao.impl.GseFileAgentTaskRecordDAO;
import com.tencent.bk.job.backup.dao.impl.GseScriptAgentTaskRecordDAO;
import com.tencent.bk.job.backup.dao.impl.GseTaskIpLogRecordDAO;
import com.tencent.bk.job.backup.dao.impl.GseTaskLogRecordDAO;
import com.tencent.bk.job.backup.dao.impl.GseTaskRecordDAO;
import com.tencent.bk.job.backup.dao.impl.OperationLogRecordDAO;
import com.tencent.bk.job.backup.dao.impl.RollingConfigRecordDAO;
import com.tencent.bk.job.backup.dao.impl.StepInstanceConfirmRecordDAO;
import com.tencent.bk.job.backup.dao.impl.StepInstanceFileRecordDAO;
import com.tencent.bk.job.backup.dao.impl.StepInstanceRecordDAO;
import com.tencent.bk.job.backup.dao.impl.StepInstanceRollingTaskRecordDAO;
import com.tencent.bk.job.backup.dao.impl.StepInstanceScriptRecordDAO;
import com.tencent.bk.job.backup.dao.impl.StepInstanceVariableRecordDAO;
import com.tencent.bk.job.backup.dao.impl.TaskInstanceHostRecordDAO;
import com.tencent.bk.job.backup.dao.impl.TaskInstanceRecordDAO;
import com.tencent.bk.job.backup.dao.impl.TaskInstanceVariableRecordDAO;
import com.tencent.bk.job.backup.service.ArchiveProgressService;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;

@Configuration
@EnableScheduling
@Slf4j
@ConditionalOnExpression("${job.backup.archiveDB.execute.enabled:false}")
public class ArchivistAutoConfig {

    @Configuration
    public static class ExecuteDaoAutoConfig {

        @Bean(name = "taskInstanceRecordDAO")
        public TaskInstanceRecordDAO taskInstanceRecordDAO(@Qualifier("job-execute-dsl-context") DSLContext context,
                                                           ArchiveDBProperties archiveDBProperties) {
            log.info("Init TaskInstanceRecordDAO");
            return new TaskInstanceRecordDAO(context, archiveDBProperties);
        }

        @Bean(name = "stepInstanceRecordDAO")
        public StepInstanceRecordDAO stepInstanceRecordDAO(@Qualifier("job-execute-dsl-context") DSLContext context,
                                                           ArchiveDBProperties archiveDBProperties) {
            log.info("Init StepInstanceRecordDAO");
            return new StepInstanceRecordDAO(context, archiveDBProperties);
        }

        @Bean(name = "stepInstanceScriptRecordDAO")
        public StepInstanceScriptRecordDAO stepInstanceScriptRecordDAO(
            @Qualifier("job-execute-dsl-context") DSLContext context,
            ArchiveDBProperties archiveDBProperties) {
            log.info("Init StepInstanceScriptRecordDAO");
            return new StepInstanceScriptRecordDAO(context, archiveDBProperties);
        }

        @Bean(name = "stepInstanceFileRecordDAO")
        public StepInstanceFileRecordDAO stepInstanceFileRecordDAO(
            @Qualifier("job-execute-dsl-context") DSLContext context,
            ArchiveDBProperties archiveDBProperties) {
            log.info("Init StepInstanceFileRecordDAO");
            return new StepInstanceFileRecordDAO(context, archiveDBProperties);
        }

        @Bean(name = "stepInstanceConfirmRecordDAO")
        public StepInstanceConfirmRecordDAO stepInstanceConfirmRecordDAO(
            @Qualifier("job-execute-dsl-context") DSLContext context,
            ArchiveDBProperties archiveDBProperties) {
            log.info("Init StepInstanceConfirmRecordDAO");
            return new StepInstanceConfirmRecordDAO(context, archiveDBProperties);
        }

        @Bean(name = "stepInstanceVariableRecordDAO")
        public StepInstanceVariableRecordDAO stepInstanceVariableRecordDAO(
            @Qualifier("job-execute-dsl-context") DSLContext context,
            ArchiveDBProperties archiveDBProperties) {
            log.info("Init StepInstanceVariableRecordDAO");
            return new StepInstanceVariableRecordDAO(context, archiveDBProperties);
        }

        @Bean(name = "taskInstanceVariableRecordDAO")
        public TaskInstanceVariableRecordDAO taskInstanceVariableRecordDAO(
            @Qualifier("job-execute-dsl-context") DSLContext context,
            ArchiveDBProperties archiveDBProperties) {
            log.info("Init TaskInstanceVariableRecordDAO");
            return new TaskInstanceVariableRecordDAO(context, archiveDBProperties);
        }

        @Bean(name = "operationLogRecordDAO")
        public OperationLogRecordDAO operationLogRecordDAO(
            @Qualifier("job-execute-dsl-context") DSLContext context,
            ArchiveDBProperties archiveDBProperties) {
            log.info("Init OperationLogRecordDAO");
            return new OperationLogRecordDAO(context, archiveDBProperties);
        }

        @Bean(name = "gseTaskLogRecordDAO")
        public GseTaskLogRecordDAO gseTaskLogRecordDAO(
            @Qualifier("job-execute-dsl-context") DSLContext context,
            ArchiveDBProperties archiveDBProperties) {
            log.info("Init GseTaskLogRecordDAO");
            return new GseTaskLogRecordDAO(context, archiveDBProperties);
        }

        @Bean(name = "gseTaskIpLogRecordDAO")
        public GseTaskIpLogRecordDAO gseTaskIpLogRecordDAO(
            @Qualifier("job-execute-dsl-context") DSLContext context,
            ArchiveDBProperties archiveDBProperties) {
            log.info("Init GseTaskIpLogRecordDAO");
            return new GseTaskIpLogRecordDAO(context, archiveDBProperties);
        }

        @Bean(name = "fileSourceTaskRecordDAO")
        public FileSourceTaskRecordDAO fileSourceTaskRecordDAO(
            @Qualifier("job-execute-dsl-context") DSLContext context,
            ArchiveDBProperties archiveDBProperties) {
            log.info("Init FileSourceTaskRecordDAO");
            return new FileSourceTaskRecordDAO(context, archiveDBProperties);
        }

        @Bean(name = "gseTaskRecordDAO")
        public GseTaskRecordDAO gseTaskRecordDAO(
            @Qualifier("job-execute-dsl-context") DSLContext context,
            ArchiveDBProperties archiveDBProperties) {
            log.info("Init GseTaskRecordDAO");
            return new GseTaskRecordDAO(context, archiveDBProperties);
        }

        @Bean(name = "gseScriptAgentTaskRecordDAO")
        public GseScriptAgentTaskRecordDAO gseScriptAgentTaskRecordDAO(
            @Qualifier("job-execute-dsl-context") DSLContext context,
            ArchiveDBProperties archiveDBProperties) {
            log.info("Init GseScriptAgentTaskRecordDAO");
            return new GseScriptAgentTaskRecordDAO(context, archiveDBProperties);
        }

        @Bean(name = "gseFileAgentTaskRecordDAO")
        public GseFileAgentTaskRecordDAO gseFileAgentTaskRecordDAO(
            @Qualifier("job-execute-dsl-context") DSLContext context,
            ArchiveDBProperties archiveDBProperties) {
            log.info("Init GseFileAgentTaskRecordDAO");
            return new GseFileAgentTaskRecordDAO(context, archiveDBProperties);
        }

        @Bean(name = "stepInstanceRollingTaskRecordDAO")
        public StepInstanceRollingTaskRecordDAO stepInstanceRollingTaskRecordDAO(
            @Qualifier("job-execute-dsl-context") DSLContext context,
            ArchiveDBProperties archiveDBProperties) {
            log.info("Init StepInstanceRollingTaskRecordDAO");
            return new StepInstanceRollingTaskRecordDAO(context, archiveDBProperties);
        }

        @Bean(name = "rollingConfigRecordDAO")
        public RollingConfigRecordDAO rollingConfigRecordDAO(
            @Qualifier("job-execute-dsl-context") DSLContext context,
            ArchiveDBProperties archiveDBProperties) {
            log.info("Init RollingConfigRecordDAO");
            return new RollingConfigRecordDAO(context, archiveDBProperties);
        }

        @Bean(name = "taskInstanceHostRecordDAO")
        public TaskInstanceHostRecordDAO taskInstanceHostRecordDAO(
            @Qualifier("job-execute-dsl-context") DSLContext context,
            ArchiveDBProperties archiveDBProperties) {
            log.info("Init TaskInstanceHostRecordDAO");
            return new TaskInstanceHostRecordDAO(context, archiveDBProperties);
        }

    }

    @Bean(name = "execute-archive-dao")
    @ConditionalOnExpression("${job.execute.archiveDB.execute.backup.enabled:false}")
    public ExecuteArchiveDAO executeArchiveDAO(@Qualifier("job-execute-archive-dsl-context") DSLContext context) {
        log.info("Init ExecuteArchiveDAO");
        return new ExecuteArchiveDAOImpl(context);
    }

    @Bean
    public JobExecuteArchiveManage jobExecuteArchiveManage(
        TaskInstanceRecordDAO taskInstanceRecordDAO,
        StepInstanceRecordDAO stepInstanceRecordDAO,
        StepInstanceScriptRecordDAO stepInstanceScriptRecordDAO,
        StepInstanceFileRecordDAO stepInstanceFileRecordDAO,
        StepInstanceConfirmRecordDAO stepInstanceConfirmRecordDAO,
        StepInstanceVariableRecordDAO stepInstanceVariableRecordDAO,
        TaskInstanceVariableRecordDAO taskInstanceVariableRecordDAO,
        OperationLogRecordDAO operationLogRecordDAO,
        GseTaskLogRecordDAO gseTaskLogRecordDAO,
        GseTaskIpLogRecordDAO gseTaskIpLogRecordDAO,
        GseTaskRecordDAO gseTaskRecordDAO,
        GseScriptAgentTaskRecordDAO gseScriptAgentTaskRecordDAO,
        GseFileAgentTaskRecordDAO gseFileAgentTaskRecordDAO,
        StepInstanceRollingTaskRecordDAO stepInstanceRollingTaskRecordDAO,
        RollingConfigRecordDAO rollingConfigRecordDAO,
        TaskInstanceHostRecordDAO taskInstanceHostRecordDAO,
        ObjectProvider<ExecuteArchiveDAO> executeArchiveDAOObjectProvider,
        ArchiveProgressService archiveProgressService,
        @Qualifier("archiveExecutor") ExecutorService archiveExecutor,
        ArchiveDBProperties archiveDBProperties) {

        log.info("Init JobExecuteArchiveManage");
        return new JobExecuteArchiveManage(
            taskInstanceRecordDAO,
            stepInstanceRecordDAO,
            stepInstanceScriptRecordDAO,
            stepInstanceFileRecordDAO,
            stepInstanceConfirmRecordDAO,
            stepInstanceVariableRecordDAO,
            taskInstanceVariableRecordDAO,
            operationLogRecordDAO,
            gseTaskLogRecordDAO,
            gseTaskIpLogRecordDAO,
            gseTaskRecordDAO,
            gseScriptAgentTaskRecordDAO,
            gseFileAgentTaskRecordDAO,
            stepInstanceRollingTaskRecordDAO,
            rollingConfigRecordDAO,
            taskInstanceHostRecordDAO,
            executeArchiveDAOObjectProvider.getIfAvailable(),
            archiveProgressService,
            archiveDBProperties,
            archiveExecutor);
    }
}
