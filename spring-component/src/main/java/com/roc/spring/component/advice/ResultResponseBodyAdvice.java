package com.roc.spring.component.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.google.common.collect.Maps;
import com.roc.spring.component.advice.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author roc
 * todo: 对String的处理
 */
@ControllerAdvice
@Slf4j
public class ResultResponseBodyAdvice implements ResponseBodyAdvice {

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${project.response.ignoreFields:}")
    private List<String> ignoreFields;

    @Value("${project.response.fullMessageKey:}")
    private String fullMessageKey;

    @Value("${spring.application.name:(请设置项目名称🤷)}")
    private String projectName;

    @Value("${management.endpoints.web.base-path:/actuator}")
    private String actuatorPath;

    @Value("${project.response.pageClazz:}")
    private List<String> pageClazz;

    @Value("${project.response.jsonStringMethod:}")
    private List<String> jsonStringMethod;

    private static boolean supportPageInfo = ClassUtils.isPresent("com.github.pagehelper.PageInfo", null);


    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        if (isResponseBody(returnType)) {
            return true;
        }
        return !ResultVo.class.isAssignableFrom(returnType.getParameterType())
                && !StringHttpMessageConverter.class.isAssignableFrom(converterType)
                && !ByteArrayHttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {


        Method method = returnType.getMethod();
        String text = String.valueOf(body);
        if (method != null) {
            String methodClassName = method.getDeclaringClass().getCanonicalName();
            String name = method.getName();
            if (jsonStringMethod.contains(methodClassName + "." + name) && body instanceof String) {
                try {
                    OutputStream outputStream = response.getBody();
                    outputStream.write(text.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    log.error("write error", e);
                }
                return text;
            }
        }


        Class<?> parameterType = returnType.getParameterType();
        if (ResultVo.class.isAssignableFrom(parameterType)) {
            return body;
        }

        if (!CollectionUtils.isEmpty(pageClazz)) {
            String canonicalName = parameterType.getCanonicalName();
            if (pageClazz.contains(canonicalName)) {
                return body;
            }
        }

        if (isActuator(body, returnType, request)) {
            return body;
        }

        String bodyStr;
        try {
            bodyStr = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            bodyStr = text;
        }
        log.info("返回:{}", bodyStr);

        if (canWrapperWithResultVO(body, returnType)) {
            if (supportPageInfo && body instanceof Page) {
                Page<Object> pageInfo = (Page<Object>) body;
                body = pageInfo.toPageInfo();
            }

            ResultVo<Object> build = ResultVo.successBuilder().data(body).build();
            Map<String, ?> hashMap = Maps.newHashMap(BeanMap.create(build));
            boolean showFullMessage = checkShowFullMessage(request);
            if (!CollectionUtils.isEmpty(ignoreFields) && !showFullMessage) {
                for (String ignoreField : ignoreFields) {
                    hashMap.remove(ignoreField);
                }
            }
            body = hashMap;
        }
        return body;
    }

    private boolean canWrapperWithResultVO(Object body, MethodParameter returnType) {
        return !(body instanceof ResultVo || body instanceof String) || isResponseBody(returnType);
    }

    private boolean isResponseBody(MethodParameter returnType) {
        return returnType.getAnnotatedElement().getAnnotation(ResponseBody.class) != null;
    }

    private boolean isActuator(Object body, MethodParameter returnType, ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            return StringUtils.startsWith(servletRequest.getServletPath(), actuatorPath);
        }
        return body instanceof Map
                && ((Map<?, ?>) body).keySet().size() == 1
                && ((Map<?, ?>) body).containsKey("_links");
    }

    private boolean checkShowFullMessage(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            String fullMessageKey = servletRequest.getParameter("fullMessageKey");
            return StringUtils.equals(fullMessageKey, projectName);
        }
        return false;
    }
}