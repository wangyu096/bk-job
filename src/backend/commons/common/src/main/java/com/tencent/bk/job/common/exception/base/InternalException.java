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

package com.tencent.bk.job.common.exception.base;

import com.tencent.bk.job.common.error.BkErrorCodeEnum;
import com.tencent.bk.job.common.error.SubErrorCode;
import com.tencent.bk.job.common.error.payload.ErrorPayloadDTO;
import lombok.Getter;
import lombok.ToString;

/**
 * 内部服务异常
 */
@Getter
@ToString
public class InternalException extends ServiceException {

    public InternalException() {
        setErrorCode(BkErrorCodeEnum.INTERNAL);
    }

    public InternalException(String message) {
        super(message);
        setErrorCode(BkErrorCodeEnum.INTERNAL);
    }

    public InternalException(String message, Throwable cause) {
        super(message, cause);
        setErrorCode(BkErrorCodeEnum.INTERNAL);
    }

    public InternalException(Throwable cause) {
        super(cause);
        setErrorCode(BkErrorCodeEnum.INTERNAL);
    }

    public InternalException(SubErrorCode subErrorCode) {
        super(subErrorCode);
        setErrorCode(BkErrorCodeEnum.INTERNAL);
    }

    public InternalException(String message, SubErrorCode subErrorCode) {
        super(message, subErrorCode);
        setErrorCode(BkErrorCodeEnum.INTERNAL);
    }

    public InternalException(String message, Throwable cause, SubErrorCode subErrorCode) {
        super(message, cause, subErrorCode);
        setErrorCode(BkErrorCodeEnum.INTERNAL);
    }

    public InternalException(SubErrorCode subErrorCode,
                             ErrorPayloadDTO errorPayload) {
        super(subErrorCode, errorPayload);
        setErrorCode(BkErrorCodeEnum.INTERNAL);
    }

    public InternalException(String message,
                             SubErrorCode subErrorCode,
                             ErrorPayloadDTO errorPayload) {
        super(message, subErrorCode, errorPayload);
        setErrorCode(BkErrorCodeEnum.INTERNAL);
    }

    public InternalException(String message,
                             Throwable cause,
                             SubErrorCode subErrorCode,
                             ErrorPayloadDTO errorPayload) {
        super(message, cause, subErrorCode, errorPayload);
        setErrorCode(BkErrorCodeEnum.INTERNAL);
    }

    public InternalException(String message,
                             Throwable cause,
                             ErrorPayloadDTO errorPayload) {
        super(message, cause, SubErrorCode.INTERNAL_ERROR, errorPayload);
        setErrorCode(BkErrorCodeEnum.INTERNAL);
    }

    public InternalException(String message,
                             ErrorPayloadDTO errorPayload) {
        super(message, SubErrorCode.INTERNAL_ERROR, errorPayload);
        setErrorCode(BkErrorCodeEnum.INTERNAL);
    }

    public InternalException(Throwable cause,
                             ErrorPayloadDTO errorPayload) {
        super(null, cause, SubErrorCode.INTERNAL_ERROR, errorPayload);
        setErrorCode(BkErrorCodeEnum.INTERNAL);
    }

    public InternalException(Throwable cause,
                             SubErrorCode subErrorCode,
                             ErrorPayloadDTO errorPayload) {
        super(null, cause, subErrorCode, errorPayload);
        setErrorCode(BkErrorCodeEnum.INTERNAL);
    }
}
