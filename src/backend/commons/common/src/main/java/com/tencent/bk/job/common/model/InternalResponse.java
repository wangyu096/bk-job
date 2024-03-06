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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.bk.job.common.annotation.CompatibleImplementation;
import com.tencent.bk.job.common.constant.CompatibleType;
import com.tencent.bk.job.common.constant.ErrorCode;
import com.tencent.bk.job.common.error.BkErrorCodeEnum;
import com.tencent.bk.job.common.error.SubErrorCode;
import com.tencent.bk.job.common.error.internal.InternalApiError;
import com.tencent.bk.job.common.error.payload.ErrorPayloadDTO;
import com.tencent.bk.job.common.exception.ServiceException;
import com.tencent.bk.job.common.model.error.ErrorType;
import com.tencent.bk.job.common.model.iam.AuthResultDTO;
import com.tencent.bk.job.common.util.JobContextUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@ToString
@ApiModel("job内部微服务间RPC调用通用响应")
public class InternalResponse<T> {

    @Deprecated
    @CompatibleImplementation(name = "openapi", type = CompatibleType.DEPLOY, explain = "发布完成后可删除")
    private boolean success;

    @Deprecated
    @CompatibleImplementation(name = "openapi", type = CompatibleType.DEPLOY, explain = "发布完成后可删除")
    private Integer code;

    @Deprecated
    @CompatibleImplementation(name = "openapi", type = CompatibleType.DEPLOY, explain = "发布完成后可删除")
    private Integer errorType;

    @Deprecated
    @CompatibleImplementation(name = "openapi", type = CompatibleType.DEPLOY, explain = "发布完成后可删除")
    private String errorMsg;

    @ApiModelProperty("请求成功返回的数据")
    private T data;

    @ApiModelProperty("请求 ID")
    private String requestId;

    @JsonProperty("authResult")
    @Deprecated
    @CompatibleImplementation(name = "openapi", type = CompatibleType.DEPLOY, explain = "发布完成后可删除")
    private AuthResultDTO authResult;

    /**
     * API 错误信息
     */
    private InternalApiError error;

    public InternalResponse() {

    }

    private InternalResponse(BkErrorCodeEnum errorCode,
                             SubErrorCode subErrorCode) {
        this(errorCode, subErrorCode, null);
    }

    private InternalResponse(BkErrorCodeEnum errorCode,
                             SubErrorCode subErrorCode,
                             ErrorPayloadDTO errorPayload) {
        // 兼容处理
        this.code = subErrorCode.getCode();
        this.success = false;
        this.errorMsg = subErrorCode.getI18nMessage();
        this.errorType = convertToErrorType(errorCode);
        this.requestId = JobContextUtil.getRequestId();

        // 标准错误处理
        InternalApiError error = new InternalApiError();
        error.setCode(errorCode.getErrorCode());
        error.setMessage(subErrorCode.getI18nMessage());
        error.setSubCode(subErrorCode.getCode());
        error.setData(errorPayload);
        this.error = error;
    }


    @CompatibleImplementation(name = "openapi", type = CompatibleType.DEPLOY, explain = "发布完成后可删除")
    private Integer convertToErrorType(BkErrorCodeEnum errorCode) {
        switch (errorCode) {
            case IAM_NO_PERMISSION:
                return ErrorType.PERMISSION_DENIED.getType();
            case INVALID_ARGUMENT:
            case OUT_OF_RANGE:
            case INVALID_REQUEST:
                return ErrorType.INVALID_PARAM.getType();
            case FAILED_PRECONDITION:
                return ErrorType.FAILED_PRECONDITION.getType();
            case UNAUTHENTICATED:
                return ErrorType.UNAUTHENTICATED.getType();
            case NOT_FOUND:
                return ErrorType.NOT_FOUND.getType();
            case ALREADY_EXISTS:
                return ErrorType.ALREADY_EXISTS.getType();
            case ABORTED:
                return ErrorType.ABORTED.getType();
            case RESOURCE_EXHAUSTED:
            case RATELIMIT_EXCEED:
                return ErrorType.RESOURCE_EXHAUSTED.getType();
            case NOT_IMPLEMENTED:
                return ErrorType.UNIMPLEMENTED.getType();
            case INTERNAL:
            case UNKNOWN:
                return ErrorType.INTERNAL.getType();
            case UNAVAILABLE:
                return ErrorType.UNAVAILABLE.getType();
            default:
                return ErrorType.INTERNAL.getType();
        }
    }

    public static <T> InternalResponse<T> buildSuccessResp(T data) {
        InternalResponse<T> response = new InternalResponse<>();
        response.code = ErrorCode.RESULT_OK;
        response.success = true;
        response.data = data;
        response.errorType = ErrorType.OK.getType();
        response.requestId = JobContextUtil.getRequestId();
        return response;
    }

    public static <T> InternalResponse<T> buildCommonFailResp(BkErrorCodeEnum errorCode,
                                                              SubErrorCode subErrorCode) {
        return new InternalResponse<>(errorCode, subErrorCode);
    }

    public static <T> InternalResponse<T> buildCommonFailResp(ServiceException e) {
        return new InternalResponse<>(e.getErrorCode(), e.getSubErrorCode(), e.getErrorPayload());
    }

}
