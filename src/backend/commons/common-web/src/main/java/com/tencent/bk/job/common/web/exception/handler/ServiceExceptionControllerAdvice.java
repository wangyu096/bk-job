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
import com.tencent.bk.job.common.exception.BadRequestException;
import com.tencent.bk.job.common.exception.BusinessException;
import com.tencent.bk.job.common.exception.ServiceException;
import com.tencent.bk.job.common.exception.SystemException;
import com.tencent.bk.job.common.iam.exception.InSufficientPermissionException;
import com.tencent.bk.job.common.model.ServiceResponse;
import com.tencent.bk.job.common.model.error.JobError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice(annotations = {InternalAPI.class})
@Slf4j
public class ServiceExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    public ServiceExceptionControllerAdvice() {
    }

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ServiceResponse<?> handleException(HttpServletRequest request, Throwable ex) {
        String exceptionInfo = "Handle Exception, uri: " + request.getRequestURI();
        log.error(exceptionInfo, ex);
        return ServiceResponse.buildCommonFailResp(JobError.INTERNAL_ERROR);
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    ResponseEntity<?> handleServiceException(HttpServletRequest request, ServiceException ex) {
        String exceptionInfo = "Handle ServiceException, uri: " + request.getRequestURI();
        log.warn(exceptionInfo, ex);
        return new ResponseEntity<>(ServiceResponse.buildCommonFailResp(ex), HttpStatus.OK);
    }

    @ExceptionHandler(InSufficientPermissionException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ServiceResponse<?> handleInSufficientPermissionException(HttpServletRequest request,
                                                             InSufficientPermissionException ex) {
        log.info("Handle InSufficientPermissionException, uri: {}, authResult: {}",
            request.getRequestURI(), ex.getAuthResult());
        return ServiceResponse.buildCommonFailResp(JobError.PERMISSION_DENIED);
    }

    @ExceptionHandler(SystemException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ServiceResponse<?> handleControllerSystemException(HttpServletRequest request, SystemException ex) {
        String errorMsg = "Handle SystemException, uri: " + request.getRequestURI();
        log.error(errorMsg, ex);
        return ServiceResponse.buildCommonFailResp(ex.getError());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ServiceResponse<?> handleBadRequestException(HttpServletRequest request, BadRequestException ex) {
        String errorMsg = "Handle BadRequestException, uri: " + request.getRequestURI();
        log.error(errorMsg, ex);
        return ServiceResponse.buildCommonFailResp(ex.getError());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    ServiceResponse<?> handleBusinessException(HttpServletRequest request, BusinessException ex) {
        String errorMsg = "Handle BusinessException, uri: " + request.getRequestURI();
        log.info(errorMsg, ex);
        return ServiceResponse.buildCommonFailResp(ex.getError());
    }
}
