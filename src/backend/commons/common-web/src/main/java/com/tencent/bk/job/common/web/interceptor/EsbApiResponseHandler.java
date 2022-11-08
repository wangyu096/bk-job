package com.tencent.bk.job.common.web.interceptor;

import com.tencent.bk.job.common.annotation.EsbAPI;
import com.tencent.bk.job.common.esb.model.EsbResp;
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
import javax.validation.constraints.Positive;

@ControllerAdvice
@Slf4j
public class EsbApiResponseHandler implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return false;
        }
        HttpServletRequest request = attrs.getRequest();
        EsbAPI esbApiAnnotation = (EsbAPI) request.getAttribute(EsbRespInterceptor.ESB_API);
        return esbApiAnnotation != null;
    }

    @Override
    public Object beforeBodyWrite(Object body,@Positive
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        if (body instanceof EsbResp) {
            return body;
        } else {
            return EsbResp.buildSuccessResp(body);
        }
    }
}
