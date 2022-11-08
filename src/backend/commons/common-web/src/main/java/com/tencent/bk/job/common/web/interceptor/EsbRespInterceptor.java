package com.tencent.bk.job.common.web.interceptor;

import com.tencent.bk.job.common.annotation.EsbAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
@Slf4j
public class EsbRespInterceptor implements HandlerInterceptor {
    public static final String ESB_API = "ESB-RESP";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Class<?> cls = handlerMethod.getBeanType();
            Method method = handlerMethod.getMethod();
            if (cls.isAnnotationPresent(EsbAPI.class) || method.isAnnotationPresent(EsbAPI.class)) {
                request.setAttribute(ESB_API, cls.getAnnotation(EsbAPI.class));
            }
        }
        return true;
    }
}
