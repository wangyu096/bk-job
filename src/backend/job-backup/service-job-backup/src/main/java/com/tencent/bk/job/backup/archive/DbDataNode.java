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

package com.tencent.bk.job.backup.archive;

import com.tencent.bk.job.backup.constant.DbDataNodeTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * db 数据节点信息
 */
@Data
@NoArgsConstructor
public class DbDataNode {
    /**
     * db 数据节点类型
     */
    private DbDataNodeTypeEnum type;
    /**
     * db 实例位置索引，从 0 开始。单库单表架构始终为 0。
     */
    private Integer dbIndex;

    /**
     * 表位置索引，从 0 开始。单库单表架构始终为 0。
     */
    private Integer tableIndex;

    public DbDataNode(DbDataNodeTypeEnum type, Integer dbIndex, Integer tableIndex) {
        this.type = type;
        this.dbIndex = dbIndex;
        this.tableIndex = tableIndex;
    }

    public String toDataNodeId() {
        return type + ":" + dbIndex + ":" + tableIndex;
    }

    public static DbDataNode fromDataNodeId(String dataNodeId) {
        String[] dataNodeParts = dataNodeId.split(":");
        return new DbDataNode(
            DbDataNodeTypeEnum.valOf(Integer.parseInt(dataNodeParts[0])),
            Integer.parseInt(dataNodeParts[1]),
            Integer.parseInt(dataNodeParts[2])
        );
    }

    @Override
    public DbDataNode clone() {
        return new DbDataNode(type, dbIndex, tableIndex);
    }
}
