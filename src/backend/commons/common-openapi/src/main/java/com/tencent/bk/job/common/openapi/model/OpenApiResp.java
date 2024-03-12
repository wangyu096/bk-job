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

package com.tencent.bk.job.common.openapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.bk.job.common.error.ApiError;
import com.tencent.bk.job.common.exception.base.ServiceException;
import com.tencent.bk.job.common.util.JobContextUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
public class OpenApiResp<T> {

    private T data;

    @JsonProperty("job_request_id")
    private String requestId;

    /**
     * 错误信息
     */
    private ApiError error;

    public OpenApiResp() {
        this.requestId = JobContextUtil.getRequestId();
    }

    private OpenApiResp(T data) {
        this.data = data;
        this.requestId = JobContextUtil.getRequestId();
    }

    private OpenApiResp(ApiError error) {
        this.error = error;
        this.requestId = JobContextUtil.getRequestId();
    }


    public static <T> OpenApiResp<T> success(T data) {
        return new OpenApiResp<>(data);
    }

    public static <T> OpenApiResp<T> success() {
        return new OpenApiResp<>(null);
    }

    public static <T> OpenApiResp<T> fail(ApiError error) {
        return new OpenApiResp<>(error);
    }

    public static <T> OpenApiResp<T> fail(ServiceException e) {
        OpenApiResp<T> resp = new OpenApiResp<>();
        ApiError error = new ApiError(e);
        resp.setError(error);
        return resp;
    }
}
