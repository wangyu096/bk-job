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

package com.tencent.bk.audit;

import com.tencent.bk.audit.constants.AuditEntry;
import com.tencent.bk.audit.constants.AuditEventRecord;
import com.tencent.bk.audit.constants.AuditRequestBody;
import com.tencent.bk.audit.model.AuditContext;
import com.tencent.bk.audit.model.AuditEvent;
import com.tencent.bk.audit.model.ErrorInfo;
import com.tencent.bk.audit.utils.EventIdGenerator;
import com.tencent.bk.job.common.audit.config.AuditEventBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


@Aspect
@Slf4j
public class AuditAspect {
    private final AuditManager auditManager;
    private final AuditRequestProvider auditRequestProvider;
    private final AuditExceptionResolver auditExceptionResolver;
    /**
     * 参数名发现
     */
    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * SpEL表达式解析器
     */
    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    public AuditAspect(AuditManager auditManager, AuditRequestProvider auditRequestProvider,
                       AuditExceptionResolver auditExceptionResolver) {
        this.auditManager = auditManager;
        this.auditRequestProvider = auditRequestProvider;
        this.auditExceptionResolver = auditExceptionResolver;
        log.info("Init AuditAspect success");
    }


    // 声明审计事件入口切入点
    @Pointcut("@annotation(com.tencent.bk.audit.constants.AuditEntry)")
    public void auditEntry() {
    }

    // 声明审计事件记录切入点
    @Pointcut("@annotation(com.tencent.bk.audit.constants.AuditEventRecord)")
    public void auditEventRecord() {
    }

    @Before("auditEntry()")
    public void startAudit(JoinPoint jp) {
        if (log.isInfoEnabled()) {
            log.info("Start audit, entry: {}", jp.getSignature().toShortString());
        }

        long start = System.currentTimeMillis();
        try {
            Method method = ((MethodSignature) jp.getSignature()).getMethod();
            AuditEntry record = method.getAnnotation(AuditEntry.class);
            startAudit(jp, method, record);
        } catch (Throwable e) {
            // 忽略审计错误，避免影响业务代码执行
            log.error("Start audit caught exception", e);
        } finally {
            if (log.isInfoEnabled()) {
                log.info("Audit start, cost: {}", System.currentTimeMillis() - start);
            }
        }
    }

    private void startAudit(JoinPoint jp, Method method, AuditEntry record) {
        AuditContext auditContext = new AuditContext();
        HttpServletRequest request = auditRequestProvider.getRequest();
        auditContext.setActionId(record.actionId());
        auditContext.setUsername(auditRequestProvider.getUsername());
        auditContext.setAccessType(auditRequestProvider.getAccessType());
        auditContext.setAccessSourceIp(auditRequestProvider.getClientIp());
        auditContext.setUserIdentifyType(auditRequestProvider.getUserIdentifyType());
        auditContext.setBkAppCode(auditRequestProvider.getBkAppCode());
        auditContext.setRequestId(auditRequestProvider.getRequestId());
        auditContext.setAccessUserAgent(auditRequestProvider.getUserAgent());
        recordRequest(jp, method, auditContext, request);
        auditManager.startAudit(auditContext);
    }

    private void recordRequest(JoinPoint jp, Method method, AuditContext auditContext, HttpServletRequest request) {
        AuditHttpRequest auditHttpRequest = new AuditHttpRequest(request.getRequestURI(),
            request.getQueryString(), null);
        Object[] args = jp.getArgs();
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            Object arg = args[i];
            boolean found = false;
            Annotation[] argAnnotations = annotations[i];
            if (argAnnotations == null || argAnnotations.length == 0) {
                continue;
            }
            for (Annotation annotation : argAnnotations) {
                if (annotation.annotationType().equals(AuditRequestBody.class)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                auditHttpRequest.setBody(arg);
                break;
            }
        }
        auditContext.setHttpRequest(auditHttpRequest);
    }

    @After(value = "auditEntry()")
    public void stopAudit(JoinPoint jp) {
        if (log.isInfoEnabled()) {
            log.info("Stop audit");
        }
        long start = System.currentTimeMillis();

        try {
            auditManager.stopAudit();
        } catch (Throwable e) {
            // 忽略审计错误，避免影响业务代码执行
            log.error("Audit stop caught exception", e);
        } finally {
            if (log.isInfoEnabled()) {
                log.info("Audit stop, cost: {}", System.currentTimeMillis() - start);
            }
        }
    }

    @AfterThrowing(value = "auditEntry()", throwing = "throwable")
    public void auditException(JoinPoint jp, Throwable throwable) {
        long start = System.currentTimeMillis();
        if (log.isInfoEnabled()) {
            log.info("Audit exception");
        }

        try {
            AuditEvent auditEvent = recordFailAuditEvent(throwable);
            auditManager.current().clearAllAuditEvent();
            auditManager.current().addAuditEvent(auditEvent);
        } catch (Throwable e) {
            // 忽略审计错误，避免影响业务代码执行
            log.error("Audit exception caught exception", e);
        } finally {
            if (log.isInfoEnabled()) {
                log.info("Audit exception, cost: {}", System.currentTimeMillis() - start);
            }
        }
    }

    private AuditEvent recordFailAuditEvent(Throwable throwable) {
        AuditContext auditContext = auditManager.current();

        AuditEvent auditEvent = new AuditEvent(auditContext.getActionId());
        auditContext.addContextAttributes(auditEvent);
        auditEvent.setEndTime(System.currentTimeMillis());
        recordException(auditEvent, throwable);

        return auditEvent;
    }

    private void recordException(AuditEvent auditEvent, Throwable e) {
        ErrorInfo errorInfo = auditExceptionResolver.resolveException(e);
        auditEvent.setResultCode(errorInfo.getErrorCode());
        auditEvent.setResultContent(errorInfo.getErrorMessage());
    }

//    @Before("auditEventRecord()")
//    public void startAuditEvent(JoinPoint jp) {
//        if (auditManager.current() == null) {
//            return;
//        }
//
//        if (log.isInfoEnabled()) {
//            log.info("Start audit event, entry: {}", jp.getSignature().toShortString());
//        }
//
//        long start = System.currentTimeMillis();
//        try {
//            AuditEvent auditEvent = new AuditEvent();
//            Method method = ((MethodSignature) jp.getSignature()).getMethod();
//            AuditEventRecord record = method.getAnnotation(AuditEventRecord.class);
//            startAudit(jp, method, record);
//        } catch (Throwable e) {
//            // 忽略审计错误，避免影响业务代码执行
//            log.error("Start audit event caught exception", e);
//        } finally {
//            if (log.isInfoEnabled()) {
//                log.info("Audit start event, cost: {}", System.currentTimeMillis() - start);
//            }
//        }
//    }
//
//    private void startAuditEvent(JoinPoint jp, Method method, AuditEventRecord record) {
//        String actionId = record.actionId();
//        String resourceType = record.resourceType();
//        String instanceId = record.instanceId();
//        AuditKey auditKey = AuditKey.build(actionId, resourceType, instanceId);
//        AuditEvent auditEvent = auditManager.current().findAuditEvent(auditKey);
//        if (auditEvent == null) {
//            auditEvent = new AuditEvent();
//            auditEvent.setId(EventIdGenerator.generateId());
//            auditEvent.setActionId(actionId);
//            auditEvent.setResourceTypeId(resourceType);
//            auditEvent.setStartTime(System.currentTimeMillis());
//            auditManager.current().addAuditEvent(auditEvent);
//        }
//    }

    @AfterReturning(value = "auditEventRecord()", returning = "result")
    public void auditEventReturning(JoinPoint jp, Object result) {
        long start = System.currentTimeMillis();
        if (log.isInfoEnabled()) {
            log.info("Audit done");
        }

        try {
            AuditContext auditContext = auditManager.current();
            if (auditContext == null) {
                if (log.isInfoEnabled()) {
                    log.info("AuditContext is empty");
                }
                return;
            }

            Method method = ((MethodSignature) jp.getSignature()).getMethod();

            EvaluationContext context = buildEvaluationContext(jp, method, result);
            fillAuditEvent(auditContext, jp, context);
        } catch (Throwable e) {
            // 忽略审计错误，避免影响业务代码执行
            log.error("Audit done caught exception", e);
        } finally {
            if (log.isInfoEnabled()) {
                log.info("Audit done, cost: {}", System.currentTimeMillis() - start);
            }
        }


    }

    private void fillAuditEvent(AuditContext auditContext, JoinPoint jp, EvaluationContext context) {
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        AuditEventRecord record = method.getAnnotation(AuditEventRecord.class);
        if (record == null) {
            return;
        }

        AuditEvent auditEvent = new AuditEvent();
        auditEvent.setId(EventIdGenerator.generateId());

        if (StringUtils.isEmpty(auditEvent.getInstanceId()) && StringUtils.isNotBlank(record.instanceId())) {
            auditEvent.setInstanceId(parseStringBySpel(context, record.instanceId()));
        }
        if (StringUtils.isEmpty(auditEvent.getInstanceName()) && StringUtils.isNotBlank(record.instanceName())) {
            auditEvent.setInstanceName(parseStringBySpel(context, record.instanceName()));
        }
        if (StringUtils.isEmpty(auditEvent.getContent()) && StringUtils.isNotBlank(record.content())) {
            auditEvent.setContent(parseStringBySpel(context, record.content()));
        }

        auditContext.addAuditEvent(auditEvent);
    }


    private String parseStringBySpel(EvaluationContext context, String spel) {
        // SpEL表达式解析
        Object object = parseBySpel(context, spel);
        return object == null ? null : object.toString();
    }

    private Object parseBySpel(EvaluationContext context, String spel) {
        // SpEL表达式解析
        return spelExpressionParser.parseExpression(spel).getValue(context);
    }

    private EvaluationContext buildEvaluationContext(JoinPoint jp, Method method, Object returnValue) {
        EvaluationContext context = new StandardEvaluationContext();
        // 获取方法参数名，并设置方法参数到EvaluationContext
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        if (parameterNames != null && parameterNames.length > 0) {
            Object[] args = jp.getArgs();
            for (int i = 0; i < args.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }

        if (returnValue != null) {
            context.setVariable("$", returnValue);
        }

        return context;
    }

    @After(value = "auditEventRecord()")
    public void auditEventAfter(JoinPoint jp) {
        long start = System.currentTimeMillis();
        if (log.isInfoEnabled()) {
            log.info("Audit done");
        }

        try {
            AuditContext auditContext = auditManager.current();
            if (auditContext == null) {
                if (log.isInfoEnabled()) {
                    log.info("AuditContext is empty");
                }
                return;
            }

            Method method = ((MethodSignature) jp.getSignature()).getMethod();
            AuditEventRecord record = method.getAnnotation(AuditEventRecord.class);
            if (record == null) {
                return;
            }

            Class<? extends AuditEventBuilder> builderClass = record.builder();
            AuditEventBuilder builder = builderClass.newInstance();
            AuditEvent auditEvent = builder.build();
            auditContext.addAuditEvent(auditEvent);
        } catch (Throwable e) {
            // 忽略审计错误，避免影响业务代码执行
            log.error("Audit done caught exception", e);
        } finally {
            if (log.isInfoEnabled()) {
                log.info("Audit done, cost: {}", System.currentTimeMillis() - start);
            }
        }
    }


}
