package com.roc.spring.component.dsc;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.roc.spring.component.advice.bean.CommonStatus;
import com.roc.spring.component.advice.exception.BizRuntimeException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author roc
 */
@Component
@Slf4j
public class RepeatedSubmitInterceptor extends HandlerInterceptorAdapter {

    private static final Cache<UserMethodKey, String> CACHE = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(2, TimeUnit.SECONDS)
            .build();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RepeatedSubmitCheck submitCheck = handlerMethod.getMethodAnnotation(RepeatedSubmitCheck.class);
            if (submitCheck == null) {
                return true;
            }

            String clientId = this.getClientId();
            String uri = request.getMethod() + " " + request.getRequestURI();
            log.info("clientId is {}, uri is {}", clientId, uri);
            UserMethodKey k = this.getUserMethodKey(clientId, uri);

            if (CACHE.getIfPresent(k) == null) {
                CACHE.put(k, k.getClientId());
            } else {
                throw new BizRuntimeException(CommonStatus.REPEATED_SUBMIT);
            }

        }
        return true;
    }

    private UserMethodKey getUserMethodKey(String clientId, String uri) {
        UserMethodKey k = new UserMethodKey();
        k.setClientId(clientId);
        k.setUri(uri);
        return k;
    }

    private String getClientId() {
        return Objects.requireNonNull(RequestContextHolder.getRequestAttributes()).getSessionId();
    }

    @Data
    private static class UserMethodKey {
        private String clientId;
        private String uri;
    }
}