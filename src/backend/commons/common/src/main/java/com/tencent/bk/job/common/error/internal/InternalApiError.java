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

package com.tencent.bk.job.common.error.internal;

import com.tencent.bk.job.common.constant.ErrorCode;
import com.tencent.bk.job.common.error.ApiError;
import com.tencent.bk.job.common.error.payload.BadRequestPayloadDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Job 内部 API 错误统一模型
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InternalApiError extends ApiError {

    /**
     * Job 自定义的子错误码
     */
    private Integer subCode;

    public InternalApiError() {
        super();
    }

    public InternalApiError(ApiError apiError) {
        super();
        setCode(apiError.getCode());
        setMessage(apiError.getMessage());
        setData(apiError.getData());
    }

    public Integer getSubCode() {
        return subCode;
    }

    public void setSubCode(Integer subCode) {
        this.subCode = subCode;
    }

    public static InternalApiError internalError() {
        ApiError error = ApiError.internalError();
        InternalApiError internalApiError = new InternalApiError(error);
        internalApiError.setSubCode(ErrorCode.INTERNAL_ERROR);
        return internalApiError;
    }

    public static InternalApiError invalidArgument(BadRequestPayloadDTO errorPayload) {
        ApiError error = ApiError.invalidArgument(errorPayload);
        InternalApiError internalApiError = new InternalApiError(error);
        internalApiError.setSubCode(ErrorCode.BAD_REQUEST);
        return internalApiError;
    }

    public static InternalApiError invalidRequest() {
        ApiError error = ApiError.invalidRequest();
        InternalApiError internalApiError = new InternalApiError(error);
        internalApiError.setSubCode(ErrorCode.BAD_REQUEST);
        return internalApiError;
    }
}
