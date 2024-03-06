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

package com.tencent.bk.job.common.openapi.job.v4;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

/**
 * 执行对象定义
 */
@Data
public class OpenApiExecuteObjectsV4DTO {
    /**
     * 全局变量名
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyDescription("Variable name")
    private String variable;

    /**
     * 主机列表
     */
    @JsonProperty("host_list")
    @JsonPropertyDescription("Hosts")
    @Valid
    private List<OpenApiHostV4DTO> hosts;

    /**
     * 容器列表
     */
    @JsonProperty("container_list")
    @JsonPropertyDescription("Containers")
    @Valid
    private List<OpenApiContainerV4DTO> containers;


    /**
     * 主机动态分组列表
     */
    @JsonProperty("host_dynamic_group_list")
    @JsonPropertyDescription("Cmdb host dynamic groups")
    @Valid
    private List<OpenApiDynamicGroupV4DTO> hostDynamicGroups;

    /**
     * 主机拓扑节点列表
     */
    @JsonProperty("host_topo_node_list")
    @JsonPropertyDescription("Cmdb host topo nodes")
    @Valid
    private List<OpenApiHostTopoNodeV4DTO> hostTopoNodes;

    /**
     * 容器拓扑节点列表
     */
    @JsonProperty("container_topo_node_list")
    @JsonPropertyDescription("Cmdb container topo nodes")
    @Valid
    private List<OpenApiHostTopoNodeV4DTO> containerTopoNodes;
}
