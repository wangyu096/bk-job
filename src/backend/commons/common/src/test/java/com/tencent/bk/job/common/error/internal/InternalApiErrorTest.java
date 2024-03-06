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

package com.tencent.bk.job.common.error.internal;

import com.tencent.bk.job.common.error.BkErrorCodeEnum;
import com.tencent.bk.job.common.model.iam.AuthResultDTO;
import com.tencent.bk.job.common.model.iam.PermissionActionResourceDTO;
import com.tencent.bk.job.common.util.json.JsonUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class InternalApiErrorTest {

    @Test
    @DisplayName("测试json反序列不同错误详情Payload对象")
    void testJsonDeserializer() {
        InternalApiError error = new InternalApiError();
        error.setCode(BkErrorCodeEnum.IAM_NO_PERMISSION.getErrorCode());
        error.setMessage("User no permission");

        AuthResultDTO authResult = new AuthResultDTO();
        authResult.setApplyUrl("http://iam.com/apply");
        authResult.setPass(false);
        PermissionActionResourceDTO permissionAction = new PermissionActionResourceDTO();
        permissionAction.setActionId("fast_execute_script");
        authResult.setRequiredActionResources(Collections.singletonList(permissionAction));
        error.setData(authResult);

        String jsonText = JsonUtils.toJson(error);

        InternalApiError deserializeObject = JsonUtils.fromJson(jsonText, InternalApiError.class);
        assertThat(deserializeObject).isNotNull();
        assertThat(deserializeObject.getCode()).isEqualTo(BkErrorCodeEnum.IAM_NO_PERMISSION.getErrorCode());
        assertThat(deserializeObject.getMessage()).isEqualTo("User no permission");
        assertThat(deserializeObject.getData()).isNotNull();
        assertThat(deserializeObject.getData()).isInstanceOf(AuthResultDTO.class);
        AuthResultDTO deserializeAuthResultDTO = (AuthResultDTO) deserializeObject.getData();
        assertThat(deserializeAuthResultDTO.isPass()).isEqualTo(false);
        assertThat(deserializeAuthResultDTO.getApplyUrl()).isEqualTo("http://iam.com/apply");
        assertThat(deserializeAuthResultDTO.getRequiredActionResources()).hasSize(1);
        assertThat(deserializeAuthResultDTO.getRequiredActionResources().get(0).getActionId())
            .isEqualTo("fast_execute_script");

    }
}
