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

import com.tencent.bk.job.common.model.WebResponse;
import com.tencent.bk.job.manage.api.web.WebGlobalSettingsResource;
import com.tencent.bk.job.manage.model.web.request.globalsetting.AccountNameRulesReq;
import com.tencent.bk.job.manage.model.web.request.globalsetting.FileUploadSettingReq;
import com.tencent.bk.job.manage.model.web.request.globalsetting.HistoryExpireReq;
import com.tencent.bk.job.manage.model.web.request.globalsetting.SetTitleFooterReq;
import com.tencent.bk.job.manage.model.web.request.notify.ChannelTemplatePreviewReq;
import com.tencent.bk.job.manage.model.web.request.notify.ChannelTemplateReq;
import com.tencent.bk.job.manage.model.web.request.notify.NotifyBlackUsersReq;
import com.tencent.bk.job.manage.model.web.request.notify.SetAvailableNotifyChannelReq;
import com.tencent.bk.job.manage.model.web.vo.globalsetting.AccountNameRulesWithDefaultVO;
import com.tencent.bk.job.manage.model.web.vo.globalsetting.FileUploadSettingVO;
import com.tencent.bk.job.manage.model.web.vo.globalsetting.NotifyChannelWithIconVO;
import com.tencent.bk.job.manage.model.web.vo.globalsetting.TitleFooterWithDefaultVO;
import com.tencent.bk.job.manage.model.web.vo.notify.ChannelTemplateDetailWithDefaultVO;
import com.tencent.bk.job.manage.model.web.vo.notify.ChannelTemplateStatusVO;
import com.tencent.bk.job.manage.model.web.vo.notify.NotifyBlackUserInfoVO;
import com.tencent.bk.job.manage.model.web.vo.notify.UserVO;
import com.tencent.bk.job.manage.service.GlobalSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description
 * @Date 2020/2/27
 * @Version 1.0
 */

@RestController
@Slf4j
public class WebGlobalSettingsResourceImpl implements WebGlobalSettingsResource {

    private GlobalSettingsService globalSettingsService;

    @Autowired
    public WebGlobalSettingsResourceImpl(GlobalSettingsService globalSettingsService) {
        this.globalSettingsService = globalSettingsService;
    }

    @Override
    public WebResponse<List<NotifyChannelWithIconVO>> listNotifyChannel(String username) {
        return WebResponse.buildSuccessResp(globalSettingsService.listNotifyChannel(username));
    }

    @Override
    public WebResponse<Integer> setAvailableNotifyChannel(String username, SetAvailableNotifyChannelReq req) {
        return WebResponse.buildSuccessResp(globalSettingsService.setAvailableNotifyChannel(username, req));
    }

    @Override
    public WebResponse<Integer> saveChannelTemplate(String username, ChannelTemplateReq req) {
        return WebResponse.buildSuccessResp(globalSettingsService.saveChannelTemplate(username, req));
    }

    @Override
    public WebResponse<Integer> sendChannelTemplate(String username, ChannelTemplatePreviewReq req) {
        return WebResponse.buildSuccessResp(globalSettingsService.sendChannelTemplate(username, req));
    }

    @Override
    public WebResponse<ChannelTemplateDetailWithDefaultVO> getChannelTemplateDetail(String username,
                                                                                    String channelCode,
                                                                                    String messageTypeCode) {
        return WebResponse.buildSuccessResp(globalSettingsService.getChannelTemplateDetail(username, channelCode,
            messageTypeCode));
    }

    @Override
    public WebResponse<List<ChannelTemplateStatusVO>> listChannelTemplateStatus(String username) {
        return WebResponse.buildSuccessResp(globalSettingsService.listChannelTemplateStatus(username));
    }

    @Override
    public WebResponse<List<UserVO>> listUsers(String username, String prefixStr, Long offset, Long limit) {
        return WebResponse.buildSuccessResp(globalSettingsService.listUsers(username, prefixStr, offset, limit));
    }

    @Override
    public WebResponse<List<NotifyBlackUserInfoVO>> listNotifyBlackUsers(String username, Integer start,
                                                                         Integer pageSize) {
        return WebResponse.buildSuccessResp(globalSettingsService.listNotifyBlackUsers(username, start, pageSize));
    }

    @Override
    public WebResponse<List<String>> saveNotifyBlackUsers(String username, NotifyBlackUsersReq req) {
        return WebResponse.buildSuccessResp(globalSettingsService.saveNotifyBlackUsers(username, req));
    }

    @Override
    public WebResponse<Long> getHistoryExpireTime(String username) {
        return WebResponse.buildSuccessResp(globalSettingsService.getHistoryExpireTime(username));
    }

    @Override
    public WebResponse<Integer> setHistoryExpireTime(String username, HistoryExpireReq req) {
        return WebResponse.buildSuccessResp(globalSettingsService.setHistoryExpireTime(username, req));
    }

    @Override
    public WebResponse<AccountNameRulesWithDefaultVO> getAccountNameRules(String username) {
        return WebResponse.buildSuccessResp(globalSettingsService.getAccountNameRules());
    }

    @Override
    public WebResponse<Boolean> setAccountNameRules(String username, AccountNameRulesReq req) {
        return WebResponse.buildSuccessResp(globalSettingsService.setAccountNameRules(username, req));
    }

    @Override
    public WebResponse<Boolean> saveFileUploadSettings(String username, FileUploadSettingReq req) {
        return WebResponse.buildSuccessResp(globalSettingsService.saveFileUploadSettings(username, req));
    }

    @Override
    public WebResponse<FileUploadSettingVO> getFileUploadSettings(String username) {
        return WebResponse.buildSuccessResp(globalSettingsService.getFileUploadSettings(username));
    }

    @Override
    public WebResponse<Boolean> setTitleFooter(String username, SetTitleFooterReq req) {
        return WebResponse.buildSuccessResp(globalSettingsService.setTitleFooter(username, req));
    }

    @Override
    public WebResponse<TitleFooterWithDefaultVO> getTitleFooterWithDefault(String username) {
        return WebResponse.buildSuccessResp(globalSettingsService.getTitleFooterWithDefault(username));
    }
}
