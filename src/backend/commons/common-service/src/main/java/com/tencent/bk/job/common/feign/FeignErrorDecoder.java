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

package com.tencent.bk.job.common.feign;

import com.tencent.bk.job.common.exception.SystemException;
import com.tencent.bk.job.common.model.ServiceResponse;
import com.tencent.bk.job.common.util.json.JsonUtils;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Slf4j
public class FeignErrorDecoder extends ErrorDecoder.Default {
    @Override
    public Exception decode(String methodKey, Response response) {
        log.info("Decode FeignException, methodKey: {}, response: {}", methodKey, response);
        Exception exception = super.decode(methodKey, response);

        if (exception instanceof RetryableException) {
            return exception;
        }

        try {
            if (exception instanceof FeignException && ((FeignException) exception).responseBody().isPresent()) {
                ByteBuffer responseBody = ((FeignException) exception).responseBody().get();
                String bodyText = StandardCharsets.UTF_8.newDecoder()
                    .decode(responseBody.asReadOnlyBuffer()).toString();
                log.info("Handle FeignException, error: {}", bodyText);
                ServiceResponse<?> serviceResponse = JsonUtils.fromJson(bodyText, ServiceResponse.class);
                Integer errorCode = serviceResponse.getCode();
                return new SystemException(errorCode);
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
        return exception;
    }
}
