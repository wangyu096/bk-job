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

package com.tencent.bk.job.gateway.filter.openapi;

import com.tencent.bk.job.common.constant.JobCommonHeaders;
import com.tencent.bk.job.common.i18n.locale.LocaleUtils;
import com.tencent.bk.job.common.util.RequestUtil;
import com.tencent.bk.job.gateway.common.consts.BkApiGatewayLangHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import static com.tencent.bk.job.common.i18n.locale.LocaleUtils.COMMON_LANG_HEADER;

/**
 * 蓝鲸 API 网关/ESB 请求国际化处理
 */
@Slf4j
@Component
public class AddBkApiGatewayLangHeaderGatewayFilterFactory
    extends AbstractGatewayFilterFactory<AddBkApiGatewayLangHeaderGatewayFilterFactory.Config> {

    @Autowired
    public AddBkApiGatewayLangHeaderGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            // 从header中获取API 网关传过来的客户端lang
            String langHeaderValue = RequestUtil.getHeaderValue(request, JobCommonHeaders.BK_GATEWAY_LANG);
            String commonLang = LocaleUtils.LANG_ZH_CN;
            if (!StringUtils.isEmpty(langHeaderValue)) {
                if (langHeaderValue.equalsIgnoreCase(BkApiGatewayLangHeader.EN)) {
                    commonLang = LocaleUtils.LANG_EN;
                } else if (langHeaderValue.equalsIgnoreCase(BkApiGatewayLangHeader.ZH_CN)) {
                    commonLang = LocaleUtils.LANG_ZH_CN;
                } else {
                    commonLang = LocaleUtils.LANG_ZH_CN;
                }
            }
            request.mutate().header(COMMON_LANG_HEADER, new String[]{commonLang}).build();
            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    static class Config {

    }

}
