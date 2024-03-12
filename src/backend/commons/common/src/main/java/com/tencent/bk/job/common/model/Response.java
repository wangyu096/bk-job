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

package com.tencent.bk.job.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tencent.bk.job.common.annotation.CompatibleImplementation;
import com.tencent.bk.job.common.constant.CompatibleType;
import com.tencent.bk.job.common.constant.ErrorCode;
import com.tencent.bk.job.common.error.ApiError;
import com.tencent.bk.job.common.error.BkErrorCodeEnum;
import com.tencent.bk.job.common.error.SubErrorCode;
import com.tencent.bk.job.common.exception.base.ServiceException;
import com.tencent.bk.job.common.model.permission.AuthResultVO;
import com.tencent.bk.job.common.util.I18nUtil;
import com.tencent.bk.job.common.util.JobContextUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ApiModel("服务调用通用返回结构")
@Slf4j
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

    @Deprecated
    @CompatibleImplementation(name = "openapi", type = CompatibleType.DEPLOY, explain = "发布完成后可删除")
    @ApiModelProperty("是否成功")
    private boolean success;

    @Deprecated
    @CompatibleImplementation(name = "openapi", type = CompatibleType.DEPLOY, explain = "发布完成后可删除")
    @ApiModelProperty("返回码")
    private Integer code;

    @Deprecated
    @CompatibleImplementation(name = "openapi", type = CompatibleType.DEPLOY, explain = "发布完成后可删除")
    @ApiModelProperty("错误信息")
    private String errorMsg;

    @ApiModelProperty("请求成功/失败返回的数据")
    private T data;

    @ApiModelProperty("请求 ID")
    private String requestId;

    @ApiModelProperty("鉴权结果, 当http code 为 403 的时候，该字段有值")
    @Deprecated
    @CompatibleImplementation(name = "openapi", type = CompatibleType.DEPLOY, explain = "发布完成后可删除")
    private AuthResultVO authResult;

    /**
     * API 错误信息
     */
    private ApiError error;

    public Response() {
    }

    private Response(ApiError apiError,
                     SubErrorCode subErrorCode) {
        Response<T> response = new Response<>();
        // 标准错误处理
        response.setError(apiError);
        response.setRequestId(JobContextUtil.getRequestId());

        // 兼容处理
        response.setSuccess(false);
        response.setCode(subErrorCode.getCode());
        response.setErrorMsg(subErrorCode.getI18nMessage());
    }


    public static <T> Response<T> buildSuccessResp(T data) {
        Response<T> response = new Response<>();
        response.code = ErrorCode.RESULT_OK;
        response.success = true;
        response.data = data;
        response.requestId = JobContextUtil.getRequestId();
        return response;
    }

    public static <T> Response<T> buildCommonFailResp(ApiError apiError, SubErrorCode subErrorCode) {
        return new Response<>(apiError, subErrorCode);
    }

    public static <T> Response<T> buildCommonFailResp(ServiceException e) {
        ApiError apiError = new ApiError();
        apiError.setCode(e.getErrorCode().getErrorCode());
        SubErrorCode subErrorCode = e.getSubErrorCode();
        if (subErrorCode != null) {
            apiError.setMessage(subErrorCode.getI18nMessage());
        } else {
            apiError.setMessage("Unknown error");
        }
        apiError.setData(e.getErrorPayload());
        return buildCommonFailResp(apiError, e.getSubErrorCode());
    }

    public static <T> Response<T> buildAuthFailResp(AuthResultVO authResult) {
        ApiError apiError = new ApiError();
        apiError.setCode(BkErrorCodeEnum.IAM_NO_PERMISSION.getErrorCode());
        apiError.setMessage(I18nUtil.getI18nMessage(String.valueOf(ErrorCode.IAM_PERMISSION_DENIED)));
        apiError.setData(authResult);

        SubErrorCode subErrorCode = SubErrorCode.of(ErrorCode.IAM_PERMISSION_DENIED);

        Response<T> response = new Response<>(apiError, subErrorCode);
        response.setAuthResult(authResult);

        return response;
    }

}
