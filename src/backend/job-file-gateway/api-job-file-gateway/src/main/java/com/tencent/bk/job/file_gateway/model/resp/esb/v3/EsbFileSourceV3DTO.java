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

package com.tencent.bk.job.file_gateway.model.resp.esb.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tencent.bk.job.common.esb.model.EsbAppScopeDTO;
import com.tencent.bk.job.common.util.json.LongTimestampSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class EsbFileSourceV3DTO extends EsbAppScopeDTO {
    /**
     * id
     */
    private Integer id;
    /**
     * 文件源标识
     */
    private String code;
    /**
     * 文件源别名
     */
    private String alias;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 类型
     */
    @JsonProperty("file_source_type")
    private Integer fileSourceType;

    /**
     * 是否为公共文件源
     */
    @JsonProperty("is_public")
    private boolean publicFlag;

    /**
     * 凭据Id
     */
    @JsonProperty("credential_id")
    private String credentialId;

    /**
     * 是否启用
     */
    private Boolean enable;

    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
     */
    @JsonProperty("create_time")
    private Long createTime;
    /**
     * 更新人
     */
    @JsonProperty("last_modify_user")
    private String lastModifyUser;

    /**
     * 更新时间
     */
    @JsonSerialize(using = LongTimestampSerializer.class)
    @JsonProperty("last_modify_time")
    private Long lastModifyTime;

}
