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

package com.tencent.bk.job.common.model;

import com.tencent.bk.job.common.model.error.JobError;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 参数校验结果
 */
@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ValidateResult {

    /**
     * 是否通过校验
     */
    private boolean pass;

    /**
     * 错误码
     */
    private JobError error;

    /**
     * 错误信息参数值
     */
    private Object[] errorParams;

    public ValidateResult(boolean isPass, JobError error, Object[] errorParams) {
        this.pass = isPass;
        this.error = error;
        this.errorParams = errorParams;
    }

    public static ValidateResult pass() {
        return new ValidateResult(true, null, null);
    }

    public static ValidateResult fail(JobError error, Object[] errorParams) {
        return new ValidateResult(false, error, errorParams);
    }

    public static ValidateResult fail(JobError error) {
        return new ValidateResult(false, error, null);
    }

    public static ValidateResult fail(JobError error, Object errorParam1) {
        return new ValidateResult(false, error, new Object[]{errorParam1});
    }
}
