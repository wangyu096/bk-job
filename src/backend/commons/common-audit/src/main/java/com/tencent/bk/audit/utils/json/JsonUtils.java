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

package com.tencent.bk.audit.utils.json;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * JSON工具
 */
@Slf4j
public class JsonUtils {

    private static final Map<String, JsonMapper> JSON_MAPPERS = new HashMap<>();

    /**
     * 从Json串中解析成bean对象,支持参数泛型
     *
     * @param jsonString    json
     * @param typeReference 类型
     * @param <T>           bean Class 类型
     * @return bean
     */
    public static <T> T fromJson(String jsonString, TypeReference<T> typeReference) throws JsonParseException {
        return JSON_MAPPERS.computeIfAbsent("__all__", s -> JsonMapper.alwaysOutputMapper()).fromJson(jsonString,
                typeReference);
    }

    /**
     * 从Json串中解析成bean对象
     *
     * @param jsonString json
     * @param beanClass  bean Class 类型
     * @param <T>        bean Class 类型
     * @return bean
     */
    public static <T> T fromJson(String jsonString, Class<T> beanClass) throws JsonParseException {
        return JSON_MAPPERS.computeIfAbsent("__all__", s -> JsonMapper.alwaysOutputMapper()).fromJson(jsonString,
                beanClass);
    }

    /**
     * 创建输出所有字段的Json，不管字段值是默认值 还是等于 null 还是空集合的字段，全输出,可用于外部接口协议输出
     *
     * @param bean bean
     * @param <T>  bean
     * @return json
     */
    public static <T> String toJson(T bean) throws JsonParseException {
        return JSON_MAPPERS.computeIfAbsent("__all__", s -> JsonMapper.alwaysOutputMapper()).toJson(bean);
    }

    public static <T> String toNonEmptyJson(T bean) {
        return JSON_MAPPERS.computeIfAbsent("__non_empty__", s -> JsonMapper.nonEmptyMapper()).toJson(bean);
    }

    public static <T> String toNonDefault(T bean) {
        return JSON_MAPPERS.computeIfAbsent("__non_default__", s -> JsonMapper.nonDefaultMapper()).toJson(bean);
    }

}
