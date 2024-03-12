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

import com.tencent.bk.job.common.error.payload.BadRequestPayloadDTO;
import com.tencent.bk.job.common.error.payload.FieldViolationDTO;
import com.tencent.bk.job.common.exception.base.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

public class ExceptionControllerAdviceBase extends ResponseEntityExceptionHandler {

    protected BadRequestPayloadDTO buildBadRequestPayloadDTO(BindException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        BadRequestPayloadDTO payload = new BadRequestPayloadDTO();
        if (bindingResult.hasFieldErrors()) {
            bindingResult.getFieldErrors().forEach(fieldError ->
                payload.addFieldViolation(
                    new FieldViolationDTO(fieldError.getField(), fieldError.getDefaultMessage())));
        }
        return payload;
    }

    protected BadRequestPayloadDTO buildBadRequestPayloadDTO(ConstraintViolationException ex) {
        BadRequestPayloadDTO payload = new BadRequestPayloadDTO();
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            payload.addFieldViolation(new FieldViolationDTO(
                constraintViolation.getPropertyPath().toString(),
                constraintViolation.getMessage()));
        }
        return payload;
    }

    protected HttpStatus getHttpStatusByServiceException(ServiceException ex) {
        HttpStatus httpStatus = HttpStatus.resolve(ex.getErrorCode().getStatusCode());
        if (httpStatus == null) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return httpStatus;
    }
}
