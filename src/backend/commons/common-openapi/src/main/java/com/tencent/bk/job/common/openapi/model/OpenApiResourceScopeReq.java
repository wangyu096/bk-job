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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.bk.job.common.constant.ResourceScopeTypeEnum;
import com.tencent.bk.job.common.model.dto.AppResourceScope;
import com.tencent.bk.job.common.service.AppScopeMappingService;
import com.tencent.bk.job.common.validation.CheckEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@ToString
public class OpenApiResourceScopeReq extends OpenApiReq {

    /**
     * 资源管理空间类型
     */
    @JsonProperty("bk_scope_type")
    @CheckEnum(enumClass = ResourceScopeTypeEnum.class, enumMethod = "isValid",
        message = "{validation.constraints.InvalidBkScopeType.message}")
    private String scopeType;

    /**
     * 资源管理空间ID
     */
    @JsonProperty("bk_scope_id")
    @NotBlank(message = "{validation.constraints.InvalidBkScopeId.message}")
    private String scopeId;

    /**
     * Job 业务ID
     */
    @JsonIgnore
    private Long appId;

    public void fillAppResourceScope(AppScopeMappingService appScopeMappingService) {
        this.appId = appScopeMappingService.getAppIdByScope(this.scopeType, this.scopeId);
    }

    @JsonIgnore
    public AppResourceScope getAppResourceScope() {
        return new AppResourceScope(this.scopeType, this.scopeId, this.appId);
    }
}
