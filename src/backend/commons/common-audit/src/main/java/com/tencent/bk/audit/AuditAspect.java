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

import com.tencent.bk.audit.annotations.ActionAuditRecord;
import com.tencent.bk.audit.annotations.AuditAttribute;
import com.tencent.bk.audit.annotations.AuditEntry;
import com.tencent.bk.audit.annotations.AuditRequestBody;
import com.tencent.bk.audit.model.ActionAuditContext;
import com.tencent.bk.audit.model.ActionAuditContextBuilder;
import com.tencent.bk.audit.model.ActionAuditScope;
import com.tencent.bk.audit.model.AuditContextBuilder;
import com.tencent.bk.audit.model.AuditHttpRequest;
import com.tencent.bk.audit.model.ErrorInfo;
import com.tencent.bk.audit.model.SdkActionAuditContext;
import com.tencent.bk.audit.model.SdkAuditContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Aspect
@Slf4j
public class AuditAspect {
    private final Audit audit;
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

    public AuditAspect(Audit audit, AuditRequestProvider auditRequestProvider,
                       AuditExceptionResolver auditExceptionResolver) {
        this.audit = audit;
        this.auditRequestProvider = auditRequestProvider;
        this.auditExceptionResolver = auditExceptionResolver;
        log.info("Init AuditAspect success");
    }


    // 声明审计事件入口切入点
    @Pointcut("@annotation(com.tencent.bk.audit.annotations.AuditEntry)")
    public void auditEntry() {
    }

    // 声明操作审计事件记录切入点
    @Pointcut("@annotation(com.tencent.bk.audit.annotations.ActionAuditRecord)")
    public void actionAuditRecord() {
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
        SdkAuditContext auditContext = AuditContextBuilder.builder(record.actionId())
            .setSubActionIds(record.subActionIds().length == 0 ? null : Arrays.asList(record.subActionIds()))
            .setUsername(auditRequestProvider.getUsername())
            .setAccessType(auditRequestProvider.getAccessType())
            .setAccessSourceIp(auditRequestProvider.getClientIp())
            .setUserIdentifyType(auditRequestProvider.getUserIdentifyType())
            .setUserIdentifyTenantId(auditRequestProvider.getUserIdentifyTenantId())
            .setBkAppCode(auditRequestProvider.getBkAppCode())
            .setRequestId(auditRequestProvider.getRequestId())
            .setAccessUserAgent(auditRequestProvider.getUserAgent())
            .setHttpRequest(parseRequest(jp, method, auditRequestProvider.getRequest()))
            .start();
        audit.startAudit(auditContext);
    }

    private AuditHttpRequest parseRequest(JoinPoint jp, Method method, HttpServletRequest request) {
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
        return auditHttpRequest;
    }

    @After(value = "auditEntry()")
    public void stopAudit(JoinPoint jp) {
        if (log.isInfoEnabled()) {
            log.info("Stop audit");
        }
        long start = System.currentTimeMillis();

        try {
            audit.stopAudit();
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
            recordException(throwable);
        } catch (Throwable e) {
            // 忽略审计错误，避免影响业务代码执行
            log.error("Audit exception caught exception", e);
        } finally {
            if (log.isInfoEnabled()) {
                log.info("Audit exception, cost: {}", System.currentTimeMillis() - start);
            }
        }
    }

    private void recordException(Throwable e) {
        ErrorInfo errorInfo = auditExceptionResolver.resolveException(e);
        audit.currentAuditContext().error(errorInfo.getErrorCode(), errorInfo.getErrorMessage());
    }

    @Around("actionAuditRecord()")
    public Object actionAuditRecord(ProceedingJoinPoint pjp) throws Throwable {
        Object result = null;
        long start = System.currentTimeMillis();
        Method method = null;
        ActionAuditRecord record = null;
        ActionAuditScope scope = null;
        boolean isAuditRecording = audit.isRecording();
        if (isAuditRecording) {
            method = ((MethodSignature) pjp.getSignature()).getMethod();
            record = method.getAnnotation(ActionAuditRecord.class);
            SdkActionAuditContext startActionAuditContext =
                ActionAuditContextBuilder.builder(record.actionId())
                    .setResourceType(record.instance().resourceType())
                    .setEventBuilder(record.builder())
                    .setContent(record.content())
                    .start();
            scope = startActionAuditContext.makeCurrent();
        }
        try {
            result = pjp.proceed();
            return result;
        } finally {
            if (isAuditRecording) {
                try {
                    ActionAuditContext currentActionAuditContext = ActionAuditContext.current();
                    parseActionAuditRecordSpEL(pjp, record, method, result, currentActionAuditContext);
                    currentActionAuditContext.end();
                    if (log.isInfoEnabled()) {
                        log.info("Audit action {}, cost: {}", record.actionId(), System.currentTimeMillis() - start);
                    }
                } catch (Throwable e) {
                    // 忽略审计错误，避免影响业务代码执行
                    log.error("Audit action caught exception", e);
                } finally {
                    scope.close();
                }
            }
        }


    }

    private void parseActionAuditRecordSpEL(ProceedingJoinPoint pjp,
                                            ActionAuditRecord record,
                                            Method method,
                                            Object result,
                                            ActionAuditContext auditActionContext) {
        EvaluationContext evaluationContext = buildEvaluationContext(pjp, method, result);
        if (StringUtils.isNotBlank(record.instance().resourceType())) {
            parseInstanceIdList(record, auditActionContext, evaluationContext);
            parseInstanceNameList(record, auditActionContext, evaluationContext);
            parseInstanceList(record, auditActionContext, evaluationContext);
            parseOriginInstanceList(record, auditActionContext, evaluationContext);
        }
        if (record.attributes().length > 0) {
            for (AuditAttribute auditAttribute : record.attributes()) {
                Object value = parseBySpel(evaluationContext, auditAttribute.value());
                auditActionContext.addAttribute(auditAttribute.name(), value);
            }
        }
    }

    private void parseInstanceIdList(ActionAuditRecord record,
                                     ActionAuditContext auditActionContext,
                                     EvaluationContext evaluationContext) {
        if (CollectionUtils.isEmpty(auditActionContext.getInstanceIdList()) &&
            StringUtils.isNotBlank(record.instance().instanceIds())) {
            Object object = parseBySpel(evaluationContext, record.instance().instanceIds());
            auditActionContext.setInstanceIdList(evaluateStringList(object));
        }
    }

    private List<String> evaluateStringList(Object object) {
        if (object == null) {
            return null;
        }
        List<String> list = new ArrayList<>();
        if (object instanceof Collection) {
            Collection<?> collection = (Collection<?>) object;
            list.addAll(collection.stream().map(Object::toString).collect(Collectors.toList()));
        } else {
            list.add(object.toString());
        }
        return list;
    }

    private void parseInstanceNameList(ActionAuditRecord record,
                                       ActionAuditContext auditActionContext,
                                       EvaluationContext evaluationContext) {
        if (CollectionUtils.isEmpty(auditActionContext.getInstanceNameList()) &&
            StringUtils.isNotBlank(record.instance().instanceNames())) {
            Object object = parseBySpel(evaluationContext, record.instance().instanceNames());
            auditActionContext.setInstanceNameList(evaluateStringList(object));
        }
    }

    private void parseInstanceList(ActionAuditRecord record,
                                   ActionAuditContext auditActionContext,
                                   EvaluationContext evaluationContext) {
        if (CollectionUtils.isEmpty(auditActionContext.getInstanceList()) &&
            StringUtils.isNotBlank(record.instance().instances())) {
            Object object = parseBySpel(evaluationContext, record.instance().instances());
            auditActionContext.setInstanceList(evaluateAuditInstanceList(object));
        }
    }

    private List<Object> evaluateAuditInstanceList(Object object) {
        if (object == null) {
            return null;
        }
        List<Object> list = new ArrayList<>();
        if (object instanceof Collection) {
            Collection<?> collection = (Collection<?>) object;
            list.addAll(collection);
        } else {
            list.add(object);
        }
        return list;
    }

    private void parseOriginInstanceList(ActionAuditRecord record,
                                         ActionAuditContext auditActionContext,
                                         EvaluationContext evaluationContext) {
        if (CollectionUtils.isEmpty(auditActionContext.getOriginInstanceList()) &&
            StringUtils.isNotBlank(record.instance().originInstances())) {
            Object object = parseBySpel(evaluationContext, record.instance().originInstances());
            auditActionContext.setOriginInstanceList(evaluateAuditInstanceList(object));
        }
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
}
