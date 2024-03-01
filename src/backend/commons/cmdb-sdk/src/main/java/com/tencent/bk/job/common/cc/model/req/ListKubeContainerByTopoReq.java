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

package com.tencent.bk.job.common.cc.model.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.bk.job.common.cc.model.PropertyFilterDTO;
import com.tencent.bk.job.common.cc.model.container.KubeNodeID;
import com.tencent.bk.job.common.openapi.model.OpenApiReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * CMDB API 请求 - 根据容器拓扑获取container信息
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListKubeContainerByTopoReq extends OpenApiReq {
    @JsonProperty("bk_biz_id")
    private Long bizId;

    /**
     * 容器拓扑节点信息，数组限制条件为200
     */
    @JsonProperty("bk_kube_nodes")
    private List<KubeNodeID> nodeIdList;

    @JsonProperty("container_filter")
    private PropertyFilterDTO containerFilter;

    @JsonProperty("pod_filter")
    private PropertyFilterDTO podFilter;

    @JsonProperty("container_fields")
    private List<String> containerFields;

    @JsonProperty("pod_fields")
    private List<String> podFields;

    @JsonProperty("page")
    private Page page;

}
