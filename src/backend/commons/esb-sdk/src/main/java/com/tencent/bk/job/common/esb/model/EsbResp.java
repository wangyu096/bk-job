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

package com.tencent.bk.job.common.esb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.bk.job.common.esb.model.iam.EsbApplyPermissionDTO;
import com.tencent.bk.job.common.exception.ServiceException;
import com.tencent.bk.job.common.i18n.service.MessageI18nService;
import com.tencent.bk.job.common.model.ServiceResponse;
import com.tencent.bk.job.common.model.ValidateResult;
import com.tencent.bk.job.common.model.error.ErrorDetail;
import com.tencent.bk.job.common.model.error.JobError;
import com.tencent.bk.job.common.util.ApplicationContextRegister;
import com.tencent.bk.job.common.util.JobContextUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
public class EsbResp<T> {
    public static final Integer SUCCESS_CODE = 0;

    @JsonIgnore
    private static MessageI18nService i18nService;

    private Integer code;

    private Boolean result;

    @JsonProperty("job_request_id")
    private String requestId;

    private String message;

    private T data;

    @JsonProperty("error_detail")
    private ErrorDetail errorDetail;

    /**
     * 无权限返回数据
     */
    private EsbApplyPermissionDTO permission;

    private EsbResp(JobError error, String message, T data) {
        this.code = error.getErrorCode();
        this.data = data;
        this.message = message;
        this.result = code.equals(SUCCESS_CODE);
        this.requestId = JobContextUtil.getRequestId();
    }

    public static <T> EsbResp<T> buildSuccessResp(T data) {
        EsbResp<T> resp = new EsbResp<>(JobError.OK, null, data);
        resp.result = true;
        return resp;
    }

    public static <T> EsbResp<T> buildCommonFailResp(JobError error, String msg) {
        EsbResp<T> resp = new EsbResp<>(error, msg, null);
        resp.result = false;
        return resp;
    }

    public static <T> EsbResp<T> buildCommonFailResp(ServiceException e) {
        String errorMsg = buildErrorMsg(e.getError().getErrorCode());
        return new EsbResp<>(e.getError(), errorMsg, null);
    }

    public static <T> EsbResp<T> buildCommonFailResp(JobError error) {
        return new EsbResp<>(error, buildErrorMsg(error.getErrorCode()), null);
    }

    public static <T> EsbResp<T> buildCommonFailResp(JobError error, ErrorDetail errorDetail) {
        EsbResp<T> esbResp = buildCommonFailResp(error);
        esbResp.setErrorDetail(errorDetail);
        return esbResp;
    }

    public static <T> EsbResp<T> buildCommonFailResp(JobError error, Object... errorParams) {
        return new EsbResp<>(error, buildErrorMsg(error.getErrorCode(), errorParams), null);
    }

    public static <T> EsbResp<T> buildAuthFailResult(EsbApplyPermissionDTO permission) {
        EsbResp<T> esbResp = new EsbResp<>();
        esbResp.setPermission(permission);
        esbResp.setMessage(buildErrorMsg(JobError.BK_PERMISSION_DENIED.getErrorCode()));
        esbResp.setCode(JobError.BK_PERMISSION_DENIED.getErrorCode());
        return esbResp;
    }

    public static <T> EsbResp<T> buildCommonFailResp(ValidateResult validateResult) {
        return new EsbResp<>(validateResult.getError(),
            buildErrorMsg(validateResult.getError().getErrorCode(), validateResult.getErrorParams()), null);
    }

    public static <T, R> EsbResp<R> convertData(EsbResp<T> esbResp, Function<T, R> converter) {
        EsbResp<R> newEsbResp = new EsbResp<>();
        newEsbResp.setCode(esbResp.getCode());
        newEsbResp.setMessage(esbResp.getMessage());
        newEsbResp.setRequestId(esbResp.getRequestId());
        newEsbResp.setPermission(esbResp.getPermission());
        newEsbResp.setResult(esbResp.getResult());
        newEsbResp.setData(converter.apply(esbResp.getData()));
        return newEsbResp;
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
