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

package com.tencent.bk.job.common.error;

/**
 * 系统子错误码
 */
public class SubErrorCode {
    /**
     * 子错误码
     */
    private Long code;

    /**
     * 错误提示信息引用的参数
     */
    private Object[] params;

    /**
     * 错误提示信息
     */
    private String message;

    public static SubErrorCode of(Long code, Object... params) {
        SubErrorCode subErrorCode = new SubErrorCode();
        subErrorCode.code = code;
        if (params != null && params.length > 0) {
            subErrorCode.params = new Object[params.length];
            System.arraycopy(params, 0, subErrorCode.params, 0, params.length);
        }
        return subErrorCode;
    }

    public Long getCode() {
        return code;
    }

    public Object[] getParams() {
        return params;
    }

    public String getMessage() {
        return message;
    }
}
