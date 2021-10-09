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

package com.tencent.bk.job.common.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.bk.job.common.constant.ErrorCode;
import com.tencent.bk.job.common.exception.ServiceException;
import com.tencent.bk.job.common.i18n.service.MessageI18nService;
import com.tencent.bk.job.common.iam.model.AuthResult;
import com.tencent.bk.job.common.model.ServiceResponse;
import com.tencent.bk.job.common.model.ValidateResult;
import com.tencent.bk.job.common.model.error.ErrorDetail;
import com.tencent.bk.job.common.model.error.ErrorType;
import com.tencent.bk.job.common.model.error.JobError;
import com.tencent.bk.job.common.util.ApplicationContextRegister;
import com.tencent.bk.job.common.util.JobContextUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
@ApiModel("job内部微服务间调用通用返回结构")
public class InternalResponse<T> {
    public static final Integer SUCCESS_CODE = 0;
    public static final Integer COMMON_FAIL_CODE = 1;
    private static volatile MessageI18nService i18nService;

    @ApiModelProperty("是否成功")
    private boolean success;

    @ApiModelProperty("返回码")
    private Integer code;

    @ApiModelProperty("错误类型")
    private Integer errorType;

    @ApiModelProperty("错误信息")
    private String errorMsg;

    @ApiModelProperty("请求成功返回的数据")
    private T data;

    @ApiModelProperty("请求 ID")
    private String requestId;

    @ApiModelProperty("鉴权结果，当返回码为1238001时，该字段有值")
    @JsonProperty("authResult")
    private AuthResult authResult;

    @ApiModelProperty("错误详情")
    @JsonProperty("errorDetail")
    private ErrorDetail errorDetail;

    public InternalResponse(ErrorType errorType, Integer errorCode, T data) {
        this.code = errorCode;
        this.success = SUCCESS_CODE.equals(errorCode);
        this.errorMsg = buildErrorMsg(errorCode);
        this.errorType = errorType.getType();
        this.data = data;
        this.requestId = JobContextUtil.getRequestId();
    }

    public InternalResponse(ErrorType errorType, Integer errorCode, Object[] errorParams, T data) {
        this.code = errorCode;
        this.success = SUCCESS_CODE.equals(errorCode);
        this.errorMsg = buildErrorMsg(errorCode, errorParams);
        this.errorType = errorType.getType();
        this.data = data;
        this.requestId = JobContextUtil.getRequestId();
    }

    public static <T> InternalResponse<T> buildSuccessResp(T data) {
        return new InternalResponse<>(ErrorType.OK, SUCCESS_CODE, data);
    }

    public static <T> InternalResponse<T> buildAuthFailResp(AuthResult authResult) {
        InternalResponse<T> resp = new InternalResponse<>(ErrorType.PERMISSION_DENIED,
            ErrorCode.PERMISSION_DENIED, null);
        resp.authResult = authResult;
        return resp;
    }

    public static <T> InternalResponse<T> buildCommonFailResp(ErrorType errorType, Integer errorCode) {
        return new InternalResponse<>(errorType, errorCode, null);
    }

    public static <T> InternalResponse<T> buildCommonFailResp(ErrorType errorType, Integer errorCode, Object[] params) {
        return new InternalResponse<>(errorType, errorCode, params, null);
    }

    public static <T> InternalResponse<T> buildCommonFailResp(ServiceException e) {
        int errorCode = e.getError().getErrorCode();
        String errorMsg = buildErrorMsg(errorCode);
        return new InternalResponse<>(e.getError(), errorMsg, null);
    }

    public static <T> InternalResponse<T> buildValidateFailResp(ValidateResult validateResult) {
        return new InternalResponse<>(validateResult.getError(),
            buildErrorMsg(validateResult.getError().getErrorCode(), validateResult.getErrorParams()), null);
    }

    public static <T> InternalResponse<T> buildCommonFailResp(JobError error, ErrorDetail errorDetail) {
        String errorMsg = buildErrorMsg(error.getErrorCode());
        InternalResponse<T> esbResp = new InternalResponse<>(error, errorMsg, null);
        esbResp.setErrorDetail(errorDetail);
        return esbResp;
    }

    private static String buildErrorMsg(Integer errorCode) {
        initI18nService();
        if (i18nService == null) {
            log.warn("Can not find available i18nService");
            return "";
        }
        return i18nService.getI18n(String.valueOf(errorCode));
    }

    private static String buildErrorMsg(Integer errorCode, Object[] errorParams) {
        initI18nService();
        if (i18nService == null) {
            log.warn("Can not find available i18nService");
            return "";
        }
        if (errorParams != null && errorParams.length > 0) {
            return i18nService.getI18nWithArgs(String.valueOf(errorCode), errorParams);
        } else {
            return i18nService.getI18n(String.valueOf(errorCode));
        }
    }

    private static void initI18nService() {
        if (i18nService == null) {
            synchronized (ServiceResponse.class) {
                if (i18nService == null) {
                    i18nService = ApplicationContextRegister.getBean(MessageI18nService.class);
                }
            }
        }
    }
}
