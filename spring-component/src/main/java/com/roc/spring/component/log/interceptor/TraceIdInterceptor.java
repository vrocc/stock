package com.roc.spring.component.log.interceptor;

import com.roc.spring.component.util.WebUtil;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chenpeng
 * @version 1.0
 * @date 2017/12/4
 */
@Component
public class TraceIdInterceptor extends HandlerInterceptorAdapter {

    // [traceId, spanId, spanExportable, X-Span-Export, X-B3-SpanId, X-B3-ParentSpanId, X-B3-TraceId, parentId]
    public static String TRACE_ID_KEY = "traceId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (WebUtil.mdcAlreadySet()) {
            return false;
        } else {
            String messageId = WebUtil.getTraceId(request, TRACE_ID_KEY);
            MDC.put(TRACE_ID_KEY, messageId);
            return true;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.clear();
    }

    public String getTraceIdKey() {
        return TRACE_ID_KEY;
    }

    public void setTraceIdKey(String traceIdKey) {
        TRACE_ID_KEY = traceIdKey;
    }

}
