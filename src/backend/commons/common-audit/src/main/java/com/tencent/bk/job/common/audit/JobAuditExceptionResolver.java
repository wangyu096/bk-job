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

package com.tencent.bk.job.common.audit;

import com.tencent.bk.audit.AuditExceptionResolver;
import com.tencent.bk.audit.model.ErrorInfo;
import com.tencent.bk.job.common.constant.ErrorCode;
import com.tencent.bk.job.common.error.SubErrorCode;
import com.tencent.bk.job.common.exception.base.ServiceException;
import com.tencent.bk.job.common.util.I18nUtil;

import java.util.Locale;

public class JobAuditExceptionResolver implements AuditExceptionResolver {
    @Override
    public ErrorInfo resolveException(Throwable e) {
        String errorMessage;
        if (e instanceof ServiceException) {
            ServiceException serviceException = (ServiceException) e;
            SubErrorCode subErrorCode = serviceException.getSubErrorCode();
            // 使用英文描述
            if (subErrorCode != null) {
                errorMessage = subErrorCode.getI18nMessage(Locale.ENGLISH);
            } else {
                errorMessage = "Unknown error";
            }
        } else {
            // 使用英文描述
            errorMessage = I18nUtil.getI18nMessage(Locale.ENGLISH, String.valueOf(ErrorCode.INTERNAL_ERROR));
        }
        return new ErrorInfo(ErrorCode.INTERNAL_ERROR, errorMessage);
    }
}
