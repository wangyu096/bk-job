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

package com.tencent.bk.job.common.service.feature.strategy;

import com.tencent.bk.job.common.service.feature.config.ToggleStrategyConfig;
import com.tencent.bk.job.common.util.feature.ToggleStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

@Slf4j
public class ToggleStrategyBuilder {
    private static final Set<ToggleStrategyFactory> factories = new HashSet<>();

    static {
        ServiceLoader<ToggleStrategyFactory> toggleStrategyFactories = ServiceLoader.load(ToggleStrategyFactory.class);
        toggleStrategyFactories.forEach(factories::add);
    }

    public static ToggleStrategy build(ToggleStrategyConfig strategyConfig) {
        ToggleStrategy toggleStrategy = null;
        for (ToggleStrategyFactory factory : factories) {
            toggleStrategy = factory.create(strategyConfig);
            if (toggleStrategy != null) {
                break;
            }
        }
        if (toggleStrategy == null) {
            log.error("Unsupported toggle strategy: {}", strategyConfig.getId());
            throw new FeatureConfigParseException("Unsupported toggle strategy " + strategyConfig.getId());
        }
        return toggleStrategy;
    }
}
