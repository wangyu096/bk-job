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

package com.tencent.bk.job.manage.api.web.impl;

import com.tencent.bk.job.common.model.PageData;
import com.tencent.bk.job.common.model.WebResponse;
import com.tencent.bk.job.common.model.vo.HostInfoVO;
import com.tencent.bk.job.manage.api.web.WebIndexResource;
import com.tencent.bk.job.manage.model.web.vo.index.AgentStatistics;
import com.tencent.bk.job.manage.model.web.vo.index.GreetingVO;
import com.tencent.bk.job.manage.model.web.vo.index.JobAndScriptStatistics;
import com.tencent.bk.job.manage.model.web.vo.task.TaskTemplateVO;
import com.tencent.bk.job.manage.service.IndexService;
import com.tencent.bk.job.manage.service.auth.TaskTemplateAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class WebIndexResourceImpl implements WebIndexResource {

    private final IndexService indexService;
    private final TaskTemplateAuthService taskTemplateAuthService;

    @Autowired
    public WebIndexResourceImpl(IndexService indexService, TaskTemplateAuthService taskTemplateAuthService) {
        this.indexService = indexService;
        this.taskTemplateAuthService = taskTemplateAuthService;
    }

    @Override
    public WebResponse<List<GreetingVO>> listGreeting(String username, Long appId) {
        return WebResponse.buildSuccessResp(indexService.listGreeting(username));
    }

    @Override
    public WebResponse<AgentStatistics> getAgentStatistics(String username, Long appId) {
        return WebResponse.buildSuccessResp(indexService.getAgentStatistics(username, appId));
    }

    @Override
    public WebResponse<PageData<HostInfoVO>> listHostsByAgentStatus(String username, Long appId,
                                                                    Integer agentStatus, Long start,
                                                                    Long pageSize) {
        return WebResponse.buildSuccessResp(indexService.listHostsByAgentStatus(username, appId, agentStatus,
            start, pageSize));
    }

    @Override
    public WebResponse<PageData<String>> listIPsByAgentStatus(String username, Long appId, Integer agentStatus,
                                                              Long start, Long pageSize) {
        return WebResponse.buildSuccessResp(indexService.listIPsByAgentStatus(username, appId, agentStatus, start
            , pageSize));
    }

    @Override
    public WebResponse<JobAndScriptStatistics> getJobAndScriptStatistics(String username, Long appId) {
        return WebResponse.buildSuccessResp(indexService.getJobAndScriptStatistics(username, appId));
    }

    @Override
    public WebResponse<List<TaskTemplateVO>> listMyFavorTasks(String username, Long appId, Long limit) {
        List<TaskTemplateVO> resultList = indexService.listMyFavorTasks(username, appId, limit);
        taskTemplateAuthService.processTemplatePermission(username, appId, resultList);
        return WebResponse.buildSuccessResp(resultList);
    }
}
