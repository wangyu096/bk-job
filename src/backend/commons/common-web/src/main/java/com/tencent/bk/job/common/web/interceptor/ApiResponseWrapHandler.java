package com.tencent.bk.job.common.web.interceptor;

import com.tencent.bk.job.common.annotation.EsbAPI;
import com.tencent.bk.job.common.annotation.InternalAPI;
import com.tencent.bk.job.common.annotation.WebAPI;
import com.tencent.bk.job.common.esb.model.EsbResp;
import com.tencent.bk.job.common.model.InternalResponse;
import com.tencent.bk.job.common.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * API 响应结果封装
 */
@ControllerAdvice
@Slf4j
public class ApiResponseWrapHandler implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return false;
        }
        HttpServletRequest request = attrs.getRequest();
        Object apiAnnotation = request.getAttribute(ApiResponseWrapInterceptor.API_RESPONSE_ANNOTATION);
        return (apiAnnotation instanceof EsbAPI
            || apiAnnotation instanceof WebAPI
            || apiAnnotation instanceof InternalAPI);
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return body;
        }

        HttpServletRequest httpServletRequest = attrs.getRequest();
        Object apiAnnotation = httpServletRequest.getAttribute(ApiResponseWrapInterceptor.API_RESPONSE_ANNOTATION);
        if (apiAnnotation instanceof EsbAPI) {
            if (body instanceof EsbResp) {
                return body;
            } else {
                return EsbResp.buildSuccessResp(body);
            }
        } else if (apiAnnotation instanceof WebAPI) {
            if (body instanceof Response) {
                return body;
            } else {
                return Response.buildSuccessResp(body);
            }
        } else if (apiAnnotation instanceof InternalAPI) {
            if (body instanceof InternalResponse) {
                return body;
            } else {
                return InternalResponse.buildSuccessResp(body);
            }
        } else {
            return body;
        }

    }
}
