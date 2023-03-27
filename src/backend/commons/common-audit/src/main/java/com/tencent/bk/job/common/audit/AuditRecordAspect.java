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
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


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


    // 声明审计 AOP 切入点
    @Pointcut("@annotation(AuditRecord)")
    public void audit() {
    }

    @Before("audit()")
    public void startAudit(JoinPoint jp) {
        log.info("Start audit");
        try {
            Method method = ((MethodSignature) jp.getSignature()).getMethod();
            AuditRecord record = method.getAnnotation(AuditRecord.class);
            startAudit(jp, method, record);
        } catch (Throwable e) {
            // 忽略审计错误，避免影响业务代码执行
            log.error("Start audit caught exception", e);
        }
    }

    @AfterReturning(value = "audit()", returning = "result")
    public void afterReturning(JoinPoint jp, Object result) {
        AuditEvent auditEvent = auditManager.currentAuditEvent();
        if (auditEvent == null) {
            return;
        }

        log.info("AfterReturning");

        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        AuditRecord record = method.getAnnotation(AuditRecord.class);

        EvaluationContext context = buildEvaluationContext(jp, method, result);

        if (StringUtils.isEmpty(auditEvent.getInstanceId()) && StringUtils.isNotBlank(record.instanceId())) {
            auditEvent.setInstanceId(parseBySpel(context, record.instanceId()).toString());
        }
        if (StringUtils.isEmpty(auditEvent.getInstanceName()) && StringUtils.isNotBlank(record.instanceName())) {
            auditEvent.setInstanceName(parseBySpel(context, record.instanceName()).toString());
        }
        if (StringUtils.isEmpty(auditEvent.getContent()) && StringUtils.isNotBlank(record.logContent())) {
            auditEvent.setContent(parseBySpel(context, record.logContent()).toString());
        }
    }

    @AfterThrowing(value = "audit()", throwing = "throwable")
    public void afterThrowing(JoinPoint jp, Throwable throwable) {
        log.info("AfterThrowing");
        recordException(auditManager.currentAuditEvent(), throwable);
    }

    @After(value = "audit()")
    public void after(JoinPoint jp) {
        log.info("After");
        stopAudit();
    }

    private void startAudit(JoinPoint jp, Method method, AuditRecord record) {
        HttpServletRequest request = JobContextUtil.getRequest();
        AuditEvent auditEvent = auditManager.startAudit();
        auditEvent.setActionId(record.actionId());
        auditEvent.setInstanceSensitivity(record.sensitivity());
        auditEvent.setUsername(JobContextUtil.getUsername());
        auditEvent.setAccessType(getAccessType(request).getValue());
        auditEvent.setAccessSourceIp(getClientIp(request));
        auditEvent.setUserIdentifyType(UserIdentifyTypeEnum.PERSONAL.getValue());
        auditEvent.setBkAppCode(request.getHeader(JobCommonHeaders.APP_CODE));
        auditEvent.setRequestId(JobContextUtil.getRequestId());
        auditEvent.setAccessUserAgent(getUserAgent(request));
        if (StringUtils.isNotBlank(record.resourceType())) {
            auditEvent.setResourceTypeId(record.resourceType());
        }
        recordRequest(jp, method, auditEvent, request);
    }

    private void recordRequest(JoinPoint jp, Method method, AuditEvent auditEvent, HttpServletRequest request) {
        AuditHttpRequest auditHttpRequest = new AuditHttpRequest(request.getRequestURI(),
            request.getQueryString(), null);
        Object[] args = jp.getArgs();
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            Object arg = args[i];
            Annotation[] argAnnotations = annotations[i];
            if (argAnnotations == null || argAnnotations.length == 0) {
                continue;
            }
            for (Annotation annotation : argAnnotations) {
                if (annotation.annotationType().equals(RequestBody.class)) {
                    auditHttpRequest.setBody(arg);
                    return;
                }
            }
        }
        Map<String, Object> extendData = new HashMap<>();
        extendData.put("request", auditHttpRequest);
        auditEvent.setExtendData(extendData);
    }

    private Object parseBySpel(EvaluationContext context, String spel) {
        // SpEL表达式解析
        return spelExpressionParser.parseExpression(spel).getValue(context);
    }

    private EvaluationContext buildEvaluationContext(JoinPoint jp, Method method, Object result) {
        EvaluationContext context = new StandardEvaluationContext();
        // 获取方法参数名，并设置方法参数到EvaluationContext
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        if (parameterNames != null && parameterNames.length > 0) {
            Object[] args = jp.getArgs();
            for (int i = 0; i < args.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }

        if (result != null) {
            context.setVariable("$", result);
        }

        return context;
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
