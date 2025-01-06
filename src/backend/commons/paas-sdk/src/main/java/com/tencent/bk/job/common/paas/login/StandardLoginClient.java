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

package com.tencent.bk.job.common.paas.login;

import com.tencent.bk.job.common.model.dto.BkUserDTO;
import com.tencent.bk.job.common.paas.login.v3.BkLoginApiClient;
import com.tencent.bk.job.common.paas.login.v3.OpenApiBkUser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StandardLoginClient implements ILoginClient {

    private final BkLoginApiClient bkLoginApiClient;

    public StandardLoginClient(BkLoginApiClient bkLoginApiClient) {
        this.bkLoginApiClient = bkLoginApiClient;
    }

    /**
     * 获取指定用户信息
     *
     * @param bkToken 用户登录 token
     * @return 用户信息
     */
    @Override
    public BkUserDTO getUserInfoByToken(String bkToken) {
        OpenApiBkUser bkUser = bkLoginApiClient.getBkUserByToken(bkToken);
        if (bkUser == null) {
            return null;
        }
        BkUserDTO bkUserDTO = new BkUserDTO();
        bkUserDTO.setUsername(bkUser.getUsername());
        bkUserDTO.setDisplayName(bkUser.getDisplayName());
        bkUserDTO.setTimeZone(bkUser.getTimeZone());
        bkUserDTO.setTenantId(bkUser.getTenantId());
        bkUserDTO.setLanguage(bkUser.getLanguage());
        return bkUserDTO;
    }
}
