package com.tencent.bk.job.common.web.interceptor;

import com.tencent.bk.job.common.annotation.EsbAPI;
import com.tencent.bk.job.common.annotation.InternalAPI;
import com.tencent.bk.job.common.annotation.WebAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * API 响应封装
 */
@Component
@Slf4j
public class ApiResponseWrapInterceptor implements HandlerInterceptor {
    public static final String API_RESPONSE_ANNOTATION = "API_RESPONSE_ANNOTATION";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Class<?> cls = handlerMethod.getBeanType();
            Method method = handlerMethod.getMethod();
            if (cls.isAnnotationPresent(EsbAPI.class) || method.isAnnotationPresent(EsbAPI.class)) {
                request.setAttribute(API_RESPONSE_ANNOTATION, cls.getAnnotation(EsbAPI.class));
            } else if (cls.isAnnotationPresent(WebAPI.class) || method.isAnnotationPresent(WebAPI.class)) {
                request.setAttribute(API_RESPONSE_ANNOTATION, cls.getAnnotation(WebAPI.class));
            } else if (cls.isAnnotationPresent(InternalAPI.class) || method.isAnnotationPresent(InternalAPI.class)) {
                request.setAttribute(API_RESPONSE_ANNOTATION, cls.getAnnotation(InternalAPI.class));
            }
        }
        return true;
    }
}
