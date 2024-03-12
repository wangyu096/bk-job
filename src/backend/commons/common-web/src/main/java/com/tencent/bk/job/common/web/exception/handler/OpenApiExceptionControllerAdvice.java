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

import com.tencent.bk.job.common.annotation.OpenAPI;
import com.tencent.bk.job.common.error.ApiError;
import com.tencent.bk.job.common.error.payload.BadRequestPayloadDTO;
import com.tencent.bk.job.common.exception.base.ServiceException;
import com.tencent.bk.job.common.iam.exception.IamPermissionDeniedException;
import com.tencent.bk.job.common.iam.service.AuthService;
import com.tencent.bk.job.common.openapi.model.OpenApiResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

/**
 * Open API 异常全局处理
 */
@ControllerAdvice(annotations = {OpenAPI.class})
@Slf4j
public class OpenApiExceptionControllerAdvice extends ExceptionControllerAdviceBase {
    private final AuthService authService;

    @Autowired
    public OpenApiExceptionControllerAdvice(AuthService authService) {
        this.authService = authService;
    }

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    ResponseEntity<?> handleException(HttpServletRequest request, Throwable ex) {
        log.error("Handle exception", ex);
        ApiError error = ApiError.internalError();
        return new ResponseEntity<>(OpenApiResp.fail(error), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    ResponseEntity<?> handleServiceException(HttpServletRequest request, ServiceException ex) {
        log.error("Handle ServiceException", ex);

        return new ResponseEntity<>(OpenApiResp.fail(ex), getHttpStatusByServiceException(ex));
    }

    @ExceptionHandler(IamPermissionDeniedException.class)
    @ResponseBody
    ResponseEntity<?> handlePermissionDeniedException(HttpServletRequest request, IamPermissionDeniedException ex) {
        log.info("Handle PermissionDeniedException", ex);
        // esb请求错误统一返回200，具体的错误信息放在返回数据里边
        return new ResponseEntity<>(authService.buildEsbAuthFailResp(ex), HttpStatus.OK);
    }

    @Override
    @SuppressWarnings("all")
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        log.warn("HandleMethodArgumentNotValid", ex);
        BadRequestPayloadDTO errorPayload = buildBadRequestPayloadDTO(ex);
        ApiError apiError = ApiError.invalidArgument(errorPayload);
        OpenApiResp<?> resp = OpenApiResp.fail(apiError);
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    ResponseEntity<?> handleConstraintViolationException(HttpServletRequest request, ConstraintViolationException ex) {
        log.warn("HandleConstraintViolationException", ex);
        BadRequestPayloadDTO errorPayload = buildBadRequestPayloadDTO(ex);
        ApiError apiError = ApiError.invalidArgument(errorPayload);
        OpenApiResp<?> resp = OpenApiResp.fail(apiError);
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }


    @Override
    @SuppressWarnings("all")
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         HttpHeaders headers,
                                                                         HttpStatus status,
                                                                         WebRequest request) {
        log.warn("Handle HttpRequestMethodNotSupportedException", ex);
        OpenApiResp<?> resp = OpenApiResp.fail(ApiError.invalidRequest());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @Override
    @SuppressWarnings("all")
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                     HttpHeaders headers, HttpStatus status,
                                                                     WebRequest request) {
        log.warn("Handle HttpMediaTypeNotSupportedException", ex);
        OpenApiResp<?> resp = OpenApiResp.fail(ApiError.invalidRequest());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @Override
    @SuppressWarnings("all")
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
                                                                      HttpHeaders headers,
                                                                      HttpStatus status,
                                                                      WebRequest request) {
        log.warn("Handle HttpMediaTypeNotAcceptableException", ex);
        OpenApiResp<?> resp = OpenApiResp.fail(ApiError.invalidRequest());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @Override
    @SuppressWarnings("all")
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex,
                                                               HttpHeaders headers,
                                                               HttpStatus status,
                                                               WebRequest request) {
        log.warn("Handle MissingPathVariableException", ex);
        OpenApiResp<?> resp = OpenApiResp.fail(ApiError.invalidRequest());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @Override
    @SuppressWarnings("all")
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatus status,
                                                                          WebRequest request) {
        log.warn("Handle MissingServletRequestParameterException", ex);
        OpenApiResp<?> resp = OpenApiResp.fail(ApiError.invalidRequest());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @Override
    @SuppressWarnings("all")
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatus status,
                                                                          WebRequest request) {
        log.warn("Handle ServletRequestBindingException", ex);
        OpenApiResp<?> resp = OpenApiResp.fail(ApiError.invalidRequest());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @Override
    @SuppressWarnings("all")
    protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        log.warn("Handle ConversionNotSupportedException", ex);
        OpenApiResp<?> resp = OpenApiResp.fail(ApiError.invalidRequest());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @Override
    @SuppressWarnings("all")
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex,
                                                        HttpHeaders headers,
                                                        HttpStatus status,
                                                        WebRequest request) {
        log.warn("Handle TypeMismatchException", ex);
        OpenApiResp<?> resp = OpenApiResp.fail(ApiError.invalidRequest());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @Override
    @SuppressWarnings("all")
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        log.warn("Handle HttpMessageNotReadableException", ex);
        OpenApiResp<?> resp = OpenApiResp.fail(ApiError.invalidRequest());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @Override
    @SuppressWarnings("all")
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        log.warn("Handle HttpMessageNotWritableException", ex);
        OpenApiResp<?> resp = OpenApiResp.fail(ApiError.invalidRequest());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @Override
    @SuppressWarnings("all")
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
                                                                     HttpHeaders headers,
                                                                     HttpStatus status,
                                                                     WebRequest request) {
        log.warn("Handle MissingServletRequestPartException", ex);
        OpenApiResp resp = OpenApiResp.fail(ApiError.invalidRequest());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @Override
    @SuppressWarnings("all")
    protected ResponseEntity<Object> handleBindException(BindException ex,
                                                         HttpHeaders headers,
                                                         HttpStatus status,
                                                         WebRequest request) {
        log.warn("HandleBindException", ex);
        BadRequestPayloadDTO errorPayload = buildBadRequestPayloadDTO(ex);
        ApiError apiError = ApiError.invalidArgument(errorPayload);
        OpenApiResp<?> resp = OpenApiResp.fail(apiError);
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @Override
    @SuppressWarnings("all")
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,
                                                                   HttpHeaders headers,
                                                                   HttpStatus status,
                                                                   WebRequest request) {
        log.warn("Handle NoHandlerFoundException", ex);
        OpenApiResp<?> resp = OpenApiResp.fail(ApiError.invalidRequest());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @Override
    @SuppressWarnings("all")
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex,
                                                                        HttpHeaders headers, HttpStatus status,
                                                                        WebRequest webRequest) {
        log.error("Handle AsyncRequestTimeoutException", ex);
        OpenApiResp<?> resp = OpenApiResp.fail(ApiError.internalError());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}
