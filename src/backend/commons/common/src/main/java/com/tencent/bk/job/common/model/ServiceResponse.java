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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.bk.job.common.exception.ServiceException;
import com.tencent.bk.job.common.i18n.service.MessageI18nService;
import com.tencent.bk.job.common.model.error.ErrorDetail;
import com.tencent.bk.job.common.model.error.JobError;
import com.tencent.bk.job.common.model.permission.AuthResultVO;
import com.tencent.bk.job.common.util.ApplicationContextRegister;
import com.tencent.bk.job.common.util.JobContextUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ApiModel("服务调用通用返回结构")
@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponse<T> {
    public static final Integer SUCCESS_CODE = 0;
    public static final Integer COMMON_FAIL_CODE = 1;
    @JsonIgnore
    private static MessageI18nService i18nService;

    @ApiModelProperty("是否成功")
    private boolean success;

    @ApiModelProperty("返回码")
    private Integer code;

    /**
     * @see com.tencent.bk.job.common.model.error.ErrorType
     */
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
    private AuthResultVO authResult;

    @ApiModelProperty("错误详情")
    @JsonProperty("errorDetail")
    private ErrorDetail errorDetail;

    public ServiceResponse(JobError error, String errorMsg, T data) {
        this.errorType = error.getErrorType();
        this.code = error.getErrorCode();
        this.errorMsg = errorMsg;
        this.data = data;
        this.requestId = JobContextUtil.getRequestId();
    }

    public static <T> ServiceResponse<T> buildSuccessResp(T data) {
        ServiceResponse<T> resp = new ServiceResponse<>(JobError.OK, null, data);
        resp.success = true;
        return resp;
    }

    public static <T> ServiceResponse<T> buildAuthFailResp(AuthResultVO authResultVO) {
        ServiceResponse<T> resp = new ServiceResponse<>(JobError.PERMISSION_DENIED,
            buildErrorMsg(JobError.PERMISSION_DENIED.getErrorCode()), null);
        resp.success = false;
        resp.authResult = authResultVO;
        return resp;
    }

    public static <T> ServiceResponse<T> buildCommonFailResp(String msg) {
        ServiceResponse<T> resp = new ServiceResponse<>(JobError.COMMON_ERROR, msg, null);
        resp.success = false;
        return resp;
    }

    public static <T> ServiceResponse<T> buildCommonFailResp(JobError error, String msg) {
        ServiceResponse<T> resp = new ServiceResponse<>(error, msg, null);
        resp.success = false;
        return resp;
    }

    public static <T> ServiceResponse<T> buildCommonFailResp(JobError error) {
        ServiceResponse<T> resp = new ServiceResponse<>(error, buildErrorMsg(error.getErrorCode()), null);
        resp.success = false;
        return resp;
    }

    public static <T> ServiceResponse<T> buildCommonFailResp(JobError error, Object[] params) {
        ServiceResponse<T> resp = new ServiceResponse<>(error, buildErrorMsg(error.getErrorCode(), params), null);
        resp.success = false;
        return resp;
    }

    public static <T> ServiceResponse<T> buildCommonFailResp(ServiceException e) {
        int errorCode = e.getError().getErrorCode();
        String errorMsg = buildErrorMsg(errorCode);
        return new ServiceResponse<>(e.getError(), errorMsg, null);
    }

    public static <T> ServiceResponse<T> buildValidateFailResp(ValidateResult validateResult) {
        return new ServiceResponse<>(validateResult.getError(),
            buildErrorMsg(validateResult.getError().getErrorCode(), validateResult.getErrorParams()), null);
    }

    public static <T> ServiceResponse<T> buildCommonFailResp(JobError error, ErrorDetail errorDetail) {
        ServiceResponse<T> esbResp = new ServiceResponse<>(error, buildErrorMsg(error.getErrorCode()), null);
        esbResp.setErrorDetail(errorDetail);
        return esbResp;
    }

    private static String buildErrorMsg(int errorCode) {
        initI18nService();
        if (i18nService == null) {
            log.warn("Can not find available i18nService");
            return "";
        }
        return i18nService.getI18n(String.valueOf(errorCode));
    }

    private static String buildErrorMsg(int errorCode, Object[] errorParams) {
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
