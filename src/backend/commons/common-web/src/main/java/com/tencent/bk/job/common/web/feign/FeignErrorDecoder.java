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

package com.tencent.bk.job.common.web.feign;

import com.tencent.bk.job.common.annotation.CompatibleImplementation;
import com.tencent.bk.job.common.constant.CompatibleType;
import com.tencent.bk.job.common.error.BkErrorCodeEnum;
import com.tencent.bk.job.common.error.SubErrorCode;
import com.tencent.bk.job.common.error.internal.InternalApiError;
import com.tencent.bk.job.common.error.payload.ErrorPayloadDTO;
import com.tencent.bk.job.common.error.payload.ResourceInfoPayloadDTO;
import com.tencent.bk.job.common.exception.base.AlreadyExistsException;
import com.tencent.bk.job.common.exception.base.FailedPreconditionException;
import com.tencent.bk.job.common.exception.base.InternalException;
import com.tencent.bk.job.common.exception.base.NotFoundException;
import com.tencent.bk.job.common.iam.exception.IamPermissionDeniedException;
import com.tencent.bk.job.common.iam.model.AuthResult;
import com.tencent.bk.job.common.model.InternalResponse;
import com.tencent.bk.job.common.model.error.ErrorType;
import com.tencent.bk.job.common.model.iam.AuthResultDTO;
import com.tencent.bk.job.common.util.json.JsonUtils;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class FeignErrorDecoder extends ErrorDecoder.Default {

    @Override
    public Exception decode(String methodKey, Response response) {
        Exception exception = super.decode(methodKey, response);
        log.debug("Decode feign error, methodKey: {}, response: {}", methodKey, response);

        if (exception instanceof RetryableException) {
            return exception;
        }

        try {
            if (exception instanceof FeignException) {
                FeignException feignException = (FeignException) exception;
                String responseBody = feignException.contentUTF8();

                if (StringUtils.isNotEmpty(responseBody)) {
                    InternalResponse<?> internalResponse = JsonUtils.fromJson(responseBody, InternalResponse.class);
                    if (internalResponse.getError() != null) {
                        return decodeByErrorEntity(feignException, internalResponse);
                    } else {
                        // 兼容：解析 errorCode
                        return decodeErrorCodeCompatibly(feignException, internalResponse);
                    }
                }
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
        return exception;
    }

    private Exception decodeByErrorEntity(FeignException exception, InternalResponse<?> response) {
        InternalApiError error = response.getError();

        if (log.isDebugEnabled()) {
            log.debug("Decode error code, error: {}", JsonUtils.toJson(error));
        }

        BkErrorCodeEnum type =
            BkErrorCodeEnum.valOf(error.getCode());
        ErrorPayloadDTO errorPayload = error.getData();
        switch (type) {
            case INVALID_ARGUMENT:
            case INVALID_REQUEST:
            case OUT_OF_RANGE:
            case UNAUTHENTICATED:
            case NO_PERMISSION:
            case ABORTED:
            case RESOURCE_EXHAUSTED:
            case RATELIMIT_EXCEED:
            case NOT_IMPLEMENTED:
            case INTERNAL:
            case UNKNOWN:
                // 微服务内部调用错误，应该转换为外部的错误;例如，从另一个服务接收 INVALID_ARGUMENT 错误,应该将 INTERNAL_ERROR 传播给它自己的调用者。
                return new InternalException(exception, new SubErrorCode(error.getSubCode(), error.getMessage()), null);
            case IAM_NO_PERMISSION:
                return new IamPermissionDeniedException(AuthResult.fromAuthResultDTO((AuthResultDTO) error.getData()));
            case NOT_FOUND:
                return new NotFoundException(exception, new SubErrorCode(error.getSubCode(), error.getMessage()),
                    errorPayload == null ? null : (ResourceInfoPayloadDTO) errorPayload);
            case ALREADY_EXISTS:
                return new AlreadyExistsException(exception, new SubErrorCode(error.getSubCode(), error.getMessage()),
                    errorPayload == null ? null : (ResourceInfoPayloadDTO) errorPayload);
            case FAILED_PRECONDITION:
                return new FailedPreconditionException(exception, new SubErrorCode(error.getSubCode(),
                    error.getMessage()));
            default:
                // 无法识别的错误，统一转换为系统内部异常
                return new InternalException(exception);
        }

    }

    /**
     * 兼容模式解析错误信息
     *
     * @param exception 异常
     * @param response  请求响应数据
     */
    @Deprecated
    @CompatibleImplementation(name = "openapi", type = CompatibleType.DEPLOY, explain = "发布完成后可删除")
    private Exception decodeErrorCodeCompatibly(FeignException exception, InternalResponse<?> response) {
        Integer errorType = response.getErrorType();
        Integer errorCode = response.getCode();
        String errorMsg = response.getErrorMsg();
        if (errorType == null || errorCode == null) {
            return exception;
        }

        log.debug("Decode error code, errorType: {}, errorCode: {}, errorMsg: {}", errorType, errorCode, errorMsg);

        ErrorType type = ErrorType.valOf(errorType);
        switch (type) {
            case INVALID_PARAM:
            case UNAUTHENTICATED:
            case ABORTED:
            case RESOURCE_EXHAUSTED:
            case UNIMPLEMENTED:
            case INTERNAL:
            case UNAVAILABLE:
            case TIMEOUT:
                // 微服务内部调用错误，应该转换为外部的错误;例如，从另一个服务接收 INVALID_PARAM 错误,应该将 INTERNAL_ERROR 传播给它自己的调用者。
                return new InternalException(errorMsg, exception);
            case PERMISSION_DENIED:
                return new IamPermissionDeniedException(AuthResult.fromAuthResultDTO(response.getAuthResult()));
            case NOT_FOUND:
                return new NotFoundException(exception, new SubErrorCode(errorCode, errorMsg), null);
            case ALREADY_EXISTS:
                return new AlreadyExistsException(exception, new SubErrorCode(errorCode, errorMsg), null);
            case FAILED_PRECONDITION:
                return new FailedPreconditionException(exception, new SubErrorCode(errorCode, errorMsg));
            default:
                // 无法识别的错误，统一转换为系统内部异常
                return new InternalException(exception);
        }

    }
}
