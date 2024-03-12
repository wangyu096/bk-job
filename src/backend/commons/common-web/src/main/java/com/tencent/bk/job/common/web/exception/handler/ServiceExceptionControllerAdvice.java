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

package com.tencent.bk.job.common.web.exception.handler;

import com.tencent.bk.job.common.annotation.InternalAPI;
import com.tencent.bk.job.common.error.SubErrorCode;
import com.tencent.bk.job.common.error.internal.InternalApiError;
import com.tencent.bk.job.common.error.payload.BadRequestPayloadDTO;
import com.tencent.bk.job.common.exception.base.ServiceException;
import com.tencent.bk.job.common.iam.exception.IamPermissionDeniedException;
import com.tencent.bk.job.common.iam.model.AuthResult;
import com.tencent.bk.job.common.model.InternalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

@ControllerAdvice(annotations = {InternalAPI.class})
@Slf4j
public class ServiceExceptionControllerAdvice extends ExceptionControllerAdviceBase {

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    InternalResponse<?> handleException(HttpServletRequest request, Throwable ex) {
        String errorMsg = "Handle Exception, uri: " + request.getRequestURI();
        log.error(errorMsg, ex);

        return InternalResponse.buildCommonFailResp(InternalApiError.internalError(), SubErrorCode.INTERNAL_ERROR);
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    ResponseEntity<?> handleServiceException(HttpServletRequest request, ServiceException ex) {
        String exceptionInfo = "Handle ServiceException, uri: " + request.getRequestURI();
        log.warn(exceptionInfo, ex);
        return new ResponseEntity<>(InternalResponse.buildCommonFailResp(ex), getHttpStatusByServiceException(ex));
    }

    @ExceptionHandler(IamPermissionDeniedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    InternalResponse<?> handlePermissionDeniedException(HttpServletRequest request,
                                                        IamPermissionDeniedException ex) {
        log.info("Handle PermissionDeniedException, uri: {}, authResult: {}",
            request.getRequestURI(), ex.getAuthResult());
        return InternalResponse.buildAuthFailResp(AuthResult.toAuthResultDTO(ex.getAuthResult()));
    }

    @SuppressWarnings("all")
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        log.warn("HandleMethodArgumentNotValid", ex);
        BadRequestPayloadDTO errorPayload = buildBadRequestPayloadDTO(ex);
        InternalApiError apiError = InternalApiError.invalidArgument(errorPayload);
        InternalResponse<?> resp = InternalResponse.buildCommonFailResp(apiError, SubErrorCode.ILLEGAL_PARAM);
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    ResponseEntity<?> handleConstraintViolationException(HttpServletRequest request,
                                                         ConstraintViolationException ex) {
        log.warn("HandleConstraintViolationException", ex);
        BadRequestPayloadDTO errorPayload = buildBadRequestPayloadDTO(ex);
        InternalApiError apiError = InternalApiError.invalidArgument(errorPayload);
        InternalResponse<?> resp = InternalResponse.buildCommonFailResp(apiError, SubErrorCode.ILLEGAL_PARAM);
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

}
