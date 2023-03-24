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

package com.tencent.bk.job.common.audit;

import com.tencent.bk.audit.AuditManager;
import com.tencent.bk.audit.constants.AccessTypeEnum;
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;
import com.tencent.bk.audit.model.AuditEvent;
import com.tencent.bk.job.common.constant.ErrorCode;
import com.tencent.bk.job.common.constant.JobCommonHeaders;
import com.tencent.bk.job.common.exception.ServiceException;
import com.tencent.bk.job.common.util.I18nUtil;
import com.tencent.bk.job.common.util.JobContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Locale;


@Aspect
@Slf4j
public class AuditRecordAspect {
    private final AuditManager auditManager;
    /**
     * 参数名发现器
     */
    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    // SpEL表达式解析器
    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    public AuditRecordAspect(AuditManager auditManager) {
        this.auditManager = auditManager;
        log.info("Init AuditRecordAspect");
    }


    // 声明AOP切入点
    @Pointcut("@annotation(AuditRecord)")
    public void audit() {
    }

    @Around("audit()")
    public Object record(ProceedingJoinPoint pjp) throws Throwable {
        log.debug("Start audit");
        AuditEvent auditEvent = null;
        try {
            Method method = ((MethodSignature) pjp.getSignature()).getMethod();
            AuditRecord record = method.getAnnotation(AuditRecord.class);
            auditEvent = startAudit(pjp, method, record);
            auditEvent.setResultCode(ErrorCode.RESULT_OK);

//            Object[] args = pjp.getArgs();
//            args[0].getClass().getA
//            // 请求方法参数名称
//            LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
//            String[] paramNames = u.getParameterNames(method);
//            if (args != null && paramNames != null) {
//                String params = "";
//                for (int i = 0; i < args.length; i++) {
//                    params += "  " + paramNames[i] + ": " + args[i];
//                }
//                // 长度超过1000字符串的大参数也不记录
//                if (params.length() <= MAX_LENGTH_TO_RECORD_PARAMS) {
//                    log.setParams(params);
//
//                }
//            }
            return pjp.proceed();
        } catch (Throwable e) {
            recordException(auditEvent, e);
            throw e;
        } finally {
            log.debug("Stop audit");
            stopAudit();
        }
    }

    private AuditEvent startAudit(ProceedingJoinPoint joinPoint, Method method, AuditRecord record) {
        HttpServletRequest request = JobContextUtil.getRequest();
        AuditEvent auditEvent = auditManager.startAudit();
        auditEvent.setActionId(record.actionId());
        auditEvent.setResourceTypeId(record.resourceType());
        auditEvent.setInstanceSensitivity(record.sensitivity());
        auditEvent.setUsername(JobContextUtil.getUsername());
        auditEvent.setAccessType(getAccessType(request).getValue());
        auditEvent.setAccessSourceIp(getClientIp(request));
        auditEvent.setUserIdentifyType(UserIdentifyTypeEnum.PERSONAL.getValue());
        auditEvent.setBkAppCode(request.getHeader(JobCommonHeaders.APP_CODE));
        auditEvent.setRequestId(JobContextUtil.getRequestId());
        auditEvent.setAccessUserAgent(getUserAgent(request));
        if (StringUtils.isNotBlank(record.instanceId())) {
            auditEvent.setInstanceId(resolveInstanceId(joinPoint, method, record.instanceId()));
        }
        return auditEvent;
    }

    private String resolveInstanceId(JoinPoint joinPoint, Method method, String instanceIdExpr) {
        // SpEL表达式解析日志信息
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        if (parameterNames == null || parameterNames.length == 0) {
            return null;
        }

        EvaluationContext context = new StandardEvaluationContext();
        //获取方法参数值
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        Object value = spelExpressionParser.parseExpression(instanceIdExpr)
            .getValue(context);
        return value == null ? null : value.toString();
    }

    public String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff == null) {
            return request.getRemoteAddr();
        } else {
            return xff.contains(",") ? xff.split(",")[0] : xff;
        }
    }

    private AccessTypeEnum getAccessType(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.startsWith("/web/")) {
            return AccessTypeEnum.WEB;
        } else if (uri.startsWith("/esb/")) {
            return AccessTypeEnum.API;
        } else {
            return AccessTypeEnum.OTHER;
        }
    }

    private String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    private void stopAudit() {
        if (auditManager.currentAuditEvent() != null) {
            auditManager.stopAudit();
        }
    }

    private void recordException(AuditEvent auditEvent, Throwable e) {
        if (e instanceof ServiceException) {
            ServiceException serviceException = (ServiceException) e;
            auditEvent.setResultCode(serviceException.getErrorCode());
            // 使用英文描述
            auditEvent.setResultContent(serviceException.getI18nMessage(Locale.ENGLISH));
        } else {
            auditEvent.setResultCode(ErrorCode.INTERNAL_ERROR);
            // 使用英文描述
            auditEvent.setResultContent(I18nUtil.getI18nMessage(Locale.ENGLISH,
                String.valueOf(ErrorCode.INTERNAL_ERROR)));
        }
    }
}
