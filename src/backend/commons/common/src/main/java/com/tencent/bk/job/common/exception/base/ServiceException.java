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
import lombok.ToString;

/**
 * Job 内部通用服务异常基础类，所有其他类型的异常需要继承该类
 */
@ToString
public class ServiceException extends RuntimeException {
    /**
     * 异常对应的 API 请求错误码
     */
    private BkErrorCodeEnum errorCode = BkErrorCodeEnum.UNKNOWN;

    /**
     * 异常对应的 Job 业务子错误码
     */
    private SubErrorCode subErrorCode = SubErrorCode.INTERNAL_ERROR;

    /**
     * 错误详情负载信息
     */
    private ErrorPayloadDTO errorPayload;

    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message,
                            Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(SubErrorCode subErrorCode) {
        this();
        this.subErrorCode = subErrorCode;
    }

    public ServiceException(String message, SubErrorCode subErrorCode) {
        this(message);
        this.subErrorCode = subErrorCode;
    }

    public ServiceException(String message,
                            Throwable cause,
                            SubErrorCode subErrorCode) {
        this(message, cause);
        this.subErrorCode = subErrorCode;
    }

    public ServiceException(SubErrorCode subErrorCode, ErrorPayloadDTO errorPayload) {
        this(subErrorCode);
        this.errorPayload = errorPayload;
    }

    public ServiceException(String message, SubErrorCode subErrorCode, ErrorPayloadDTO errorPayload) {
        this(message, subErrorCode);
        this.errorPayload = errorPayload;
    }

    public ServiceException(String message,
                            Throwable cause,
                            SubErrorCode subErrorCode,
                            ErrorPayloadDTO errorPayload) {
        this(message, cause, subErrorCode);
        this.errorPayload = errorPayload;
    }


    public SubErrorCode getSubErrorCode() {
        return subErrorCode;
    }

    public ErrorPayloadDTO getErrorPayload() {
        return errorPayload;
    }

    public void setErrorCode(BkErrorCodeEnum errorCode) {
        this.errorCode = errorCode;
    }

    public BkErrorCodeEnum getErrorCode() {
        return errorCode;
    }
}
