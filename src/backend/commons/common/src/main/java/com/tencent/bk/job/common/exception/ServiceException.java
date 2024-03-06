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

package com.tencent.bk.job.common.exception;

import com.tencent.bk.job.common.error.BkErrorCodeEnum;
import com.tencent.bk.job.common.error.SubErrorCode;
import com.tencent.bk.job.common.error.payload.ErrorPayloadDTO;
import lombok.ToString;

/**
 * Job 内部通用服务异常基础类，所有其他类型的异常需要继承该类
 */
@ToString
public class ServiceException extends RuntimeException {
    private BkErrorCodeEnum errorCode;
    private SubErrorCode subErrorCode;
    private ErrorPayloadDTO errorPayload;

    public ServiceException(String internalErrorMessage,
                            Throwable cause) {
        super(internalErrorMessage, cause);
    }

    public ServiceException(String internalErrorMessage) {
        super(internalErrorMessage);
    }

    public void setErrorCode(BkErrorCodeEnum errorCode) {
        this.errorCode = errorCode;
    }

    public void setSubErrorCode(SubErrorCode subErrorCode) {
        this.subErrorCode = subErrorCode;
    }

    public void setErrorPayload(ErrorPayloadDTO errorPayload) {
        this.errorPayload = errorPayload;
    }

    public BkErrorCodeEnum getErrorCode() {
        return errorCode;
    }

    public SubErrorCode getSubErrorCode() {
        return subErrorCode;
    }

    public ErrorPayloadDTO getErrorPayload() {
        return errorPayload;
    }
}
