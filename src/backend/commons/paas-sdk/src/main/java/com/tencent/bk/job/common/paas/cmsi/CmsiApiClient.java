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

package com.tencent.bk.job.common.paas.cmsi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.bk.job.common.constant.HttpMethodEnum;
import com.tencent.bk.job.common.esb.config.EsbProperties;
import com.tencent.bk.job.common.exception.InternalCmsiException;
import com.tencent.bk.job.common.metrics.CommonMetricNames;
import com.tencent.bk.job.common.openapi.config.AppProperties;
import com.tencent.bk.job.common.openapi.job.v3.EsbResp;
import com.tencent.bk.job.common.openapi.metrics.OpenApiMetricTags;
import com.tencent.bk.job.common.openapi.model.BkApiAuthorization;
import com.tencent.bk.job.common.openapi.model.OpenApiReq;
import com.tencent.bk.job.common.openapi.model.OpenApiRequestInfo;
import com.tencent.bk.job.common.openapi.sdk.BkApiClient;
import com.tencent.bk.job.common.paas.model.EsbNotifyChannelDTO;
import com.tencent.bk.job.common.paas.model.PostSendMsgReq;
import com.tencent.bk.job.common.util.http.HttpHelperFactory;
import com.tencent.bk.job.common.util.http.HttpMetricUtil;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;

import java.util.List;
import java.util.Set;

import static com.tencent.bk.job.common.metrics.CommonMetricNames.ESB_CMSI_API;

/**
 * 消息通知 API 客户端
 */
@Slf4j
public class CmsiApiClient extends BkApiClient {

    private static final String API_GET_NOTIFY_CHANNEL_LIST = "/api/c/compapi/cmsi/get_msg_type/";
    private static final String API_POST_SEND_MSG = "/api/c/compapi/cmsi/send_msg/";

    private final BkApiAuthorization authorization;

    private static final String CLIENT_NAME = "bk-notice";

    public CmsiApiClient(EsbProperties esbProperties,
                         AppProperties appProperties,
                         MeterRegistry meterRegistry) {
        super(meterRegistry, ESB_CMSI_API, esbProperties.getService().getUrl(),
            HttpHelperFactory.getDefaultHttpHelper(), CLIENT_NAME);
        this.authorization = BkApiAuthorization.appAuthorization(appProperties.getCode(),
            appProperties.getSecret(), "admin");
    }

    public List<EsbNotifyChannelDTO> getNotifyChannelList() {
        try {
            HttpMetricUtil.setHttpMetricName(CommonMetricNames.ESB_CMSI_API_HTTP);
            HttpMetricUtil.addTagForCurrentMetric(
                Tag.of(OpenApiMetricTags.KEY_API_NAME, API_GET_NOTIFY_CHANNEL_LIST)
            );
            EsbResp<List<EsbNotifyChannelDTO>> esbResp = doRequest(
                OpenApiRequestInfo.builder()
                    .method(HttpMethodEnum.GET)
                    .uri(API_GET_NOTIFY_CHANNEL_LIST)
                    .authorization(authorization)
                    .build(),
                new TypeReference<EsbResp<List<EsbNotifyChannelDTO>>>() {
                }
            );
            return esbResp.getData();
        } catch (Exception e) {
            String errorMsg = "Get " + API_GET_NOTIFY_CHANNEL_LIST + " error";
            log.error(errorMsg, e);
            throw new InternalCmsiException(errorMsg, e);
        } finally {
            HttpMetricUtil.clearHttpMetric();
        }
    }

    public void sendMsg(String msgType,
                        String sender,
                        Set<String> receivers,
                        String title,
                        String content) {
        PostSendMsgReq req = buildSendMsgReq(msgType, sender, receivers, title, content);
        String uri = API_POST_SEND_MSG;
        try {
            HttpMetricUtil.setHttpMetricName(CommonMetricNames.ESB_CMSI_API_HTTP);
            HttpMetricUtil.addTagForCurrentMetric(Tag.of(OpenApiMetricTags.KEY_API_NAME, uri));
            EsbResp<Object> esbResp = doRequest(
                OpenApiRequestInfo.builder()
                    .method(HttpMethodEnum.POST)
                    .uri(uri)
                    .body(req)
                    .authorization(authorization)
                    .build(),
                new TypeReference<EsbResp<Object>>() {
                }
            );

            if (esbResp.getResult() == null || !esbResp.getResult() || esbResp.getCode() != 0) {
                throw new InternalCmsiException("CMSI fail to send message, reason: " + esbResp.getMessage());
            }
        } catch (Exception e) {
            String msg = MessageFormatter.format(
                "Fail to request {}",
                uri
            ).getMessage();
            log.error(msg, e);
            throw new InternalCmsiException(msg, e);
        } finally {
            HttpMetricUtil.clearHttpMetric();
        }
    }

    private PostSendMsgReq buildSendMsgReq(String msgType,
                                           String sender,
                                           Set<String> receivers,
                                           String title,
                                           String content) {
        PostSendMsgReq req = OpenApiReq.buildRequest(PostSendMsgReq.class, "superadmin");
        if (title == null || title.isEmpty()) {
            title = "Default Title";
        }
        req.setMsgType(msgType);
        req.setSender(sender);
        req.setReceiverUsername(String.join(",", receivers));
        req.setTitle(title);
        req.setContent(content);
        return req;
    }
}
