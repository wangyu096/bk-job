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

package com.tencent.bk.job.common.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tencent.bk.job.common.constant.ErrorCode;
import com.tencent.bk.job.common.error.payload.BadRequestPayloadDTO;
import com.tencent.bk.job.common.error.payload.ErrorPayloadDTO;
import com.tencent.bk.job.common.exception.base.ServiceException;
import com.tencent.bk.job.common.util.I18nUtil;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 蓝鲸 Open API 错误统一模型
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    /**
     * 蓝鲸错误码
     * <p>
     * 作用: 上游编码基于这个做代码层面的逻辑判断(所以必须是确定的枚举)不允许各系统自定义。
     */
    private String code;

    /**
     * 提供给用户的错误信息。需要支持国际化
     */
    private String message;

    /**
     * 错误 Payload。根据 code 字段值不同对应不同的 schema
     */
    private ErrorPayloadDTO data;

    public ApiError() {

    }

    public ApiError(ServiceException e) {
        this.code = e.getErrorCode().getErrorCode();
        SubErrorCode subErrorCode = e.getSubErrorCode();
        if (subErrorCode != null) {
            this.message = e.getSubErrorCode().getI18nMessage();
        } else {
            this.message = I18nUtil.getI18nMessage(String.valueOf(ErrorCode.INTERNAL_ERROR));
        }
        this.data = e.getErrorPayload();
    }

    public static ApiError internalError() {
        ApiError error = new ApiError();
        error.setCode(BkErrorCodeEnum.INTERNAL.getErrorCode());
        error.setMessage(I18nUtil.getI18nMessage(String.valueOf(ErrorCode.INTERNAL_ERROR)));
        return error;
    }

    public static ApiError invalidArgument(BadRequestPayloadDTO errorPayload) {
        ApiError error = new ApiError();
        error.setCode(BkErrorCodeEnum.INVALID_ARGUMENT.getErrorCode());

        String localizedMessage = null;
        if (CollectionUtils.isNotEmpty(errorPayload.getFieldViolations())) {
            localizedMessage = errorPayload.getFieldViolations().get(0).getDescription();
        }
        if (StringUtils.isEmpty(localizedMessage)) {
            localizedMessage = I18nUtil.getI18nMessage(String.valueOf(ErrorCode.BAD_REQUEST));
        }
        error.setMessage(localizedMessage);

        error.setData(errorPayload);
        return error;
    }

    public static ApiError invalidRequest() {
        ApiError error = new ApiError();
        error.setCode(BkErrorCodeEnum.INVALID_REQUEST.getErrorCode());
        error.setMessage(I18nUtil.getI18nMessage(String.valueOf(ErrorCode.BAD_REQUEST)));
        return error;
    }

}
