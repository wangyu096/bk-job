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

package com.tencent.bk.job.manage.api.tmp;

import com.tencent.bk.job.common.model.WebResponse;
import com.tencent.bk.job.manage.model.tmp.TmpAccountCreateUpdateReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = {"job-manage:tmp:App_Account"})
@RequestMapping("/tmp/account/app/{appId}")
public interface TmpAppAccountResource {

    @ApiOperation(value = "新增账号（不校验alias）", produces = "application/json")
    @PostMapping("/account")
    WebResponse saveAccount(
        @ApiParam(value = "用户名，网关自动传入", required = true) @RequestHeader("username") String username,
        @ApiParam(value = "业务 ID", required = true) @PathVariable("appId") Long appId,
        @ApiParam("创建时间") @RequestHeader(value = "X-Create-Time", required = false) Long createTime,
        @ApiParam("修改时间") @RequestHeader(value = "X-Update-Time", required = false) Long lastModifyTime,
        @ApiParam("最后修改人") @RequestHeader(value = "X-Update-User", required = false) String lastModifyUser,
        @ApiParam("是否使用当前时间") @RequestHeader(value = "X-Use-Current-Time", required = false) Boolean useCurrentTime,
        @ApiParam(value = "创建账号请求") @RequestBody TmpAccountCreateUpdateReq accountCreateUpdateReq);

}
