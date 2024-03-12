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

package com.tencent.bk.job.common.error.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.bk.job.common.error.ValidationDesc;
import lombok.Data;

/**
 * API 参数校验错误
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldViolationDTO {
    /**
     * 错误参数名
     */
    @JsonProperty("field")
    private String field;

    /**
     * 描述参数不合法的原因，需要支持国际化
     */
    @JsonProperty("description")
    private String description;

    public FieldViolationDTO(String field, String description) {
        this.field = field;
        this.description = description;
    }

    public static FieldViolationDTO atLeastOneParamRequired(String param1, String param2) {
        return new FieldViolationDTO(param1 + "/" + param2, ValidationDesc.AT_LEAST_ONE_PARAM_REQUIRED);
    }

    public static FieldViolationDTO atLeastOneParamRequired(String param1, String param2, String param3) {
        return new FieldViolationDTO(param1 + "/" + param2 + "/" + param3,
            ValidationDesc.AT_LEAST_ONE_PARAM_REQUIRED);
    }
}
