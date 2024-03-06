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
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;

/**
 * ServiceException 构造器
 *
 * @param <T> 异常类型 Class
 */
@Slf4j
public class ServiceExceptionBuilder<T extends ServiceException> {
    private String internalErrorMessage;
    private Throwable cause;
    private BkErrorCodeEnum errorCode;
    private SubErrorCode subErrorCode;
    private ErrorPayloadDTO errorPayload;
    private final Class<T> exceptionClass;

    private ServiceExceptionBuilder(Class<T> exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public static <T extends ServiceException> ServiceExceptionBuilder<T> builder(Class<T> exceptionClass) {
        return new ServiceExceptionBuilder<>(exceptionClass);
    }

    public ServiceExceptionBuilder<T> internalErrorMessage(String internalErrorMessage) {
        this.internalErrorMessage = internalErrorMessage;
        return this;
    }

    public ServiceExceptionBuilder<T> cause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    public ServiceExceptionBuilder<T> errorCode(BkErrorCodeEnum errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public ServiceExceptionBuilder<T> subErrorCode(SubErrorCode subErrorCode) {
        this.subErrorCode = subErrorCode;
        return this;
    }

    public ServiceExceptionBuilder<T> errorPayload(ErrorPayloadDTO errorPayload) {
        this.errorPayload = errorPayload;
        return this;
    }

    public T build() {
        T result;
        try {
            // 获取构造方法
            Constructor<T> constructor = exceptionClass.getConstructor(String.class, Throwable.class);
            result = constructor.newInstance(this.internalErrorMessage, this.cause);
            result.setErrorCode(this.errorCode);
            result.setSubErrorCode(this.subErrorCode);
            result.setErrorPayload(this.errorPayload);
            return result;
        } catch (Throwable e) {
            throw new InternalException("Build ServiceException error", e);
        }
    }
}
