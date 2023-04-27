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

import com.tencent.bk.audit.annotations.AuditEntry;
import com.tencent.bk.audit.annotations.AuditRequestBody;
import com.tencent.bk.audit.model.AuditContext;
import com.tencent.bk.audit.model.AuditHttpRequest;
import com.tencent.bk.audit.model.ErrorInfo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 审计事件入口切入点。
 * <p>
 * 使用@Order(Ordered.LOWEST_PRECEDENCE - 1) 保证 AuditAspect 比 ActionAuditAspect 先执行
 */
@Aspect
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class AuditAspect {
    private final Audit audit;
    private final AuditRequestProvider auditRequestProvider;
    private final AuditExceptionResolver auditExceptionResolver;


    public AuditAspect(Audit audit,
                       AuditRequestProvider auditRequestProvider,
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
        AuditContext auditContext = AuditContext.builder(record.actionId())
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
            .build();
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
}
