package com.roc.spring.component.log.interceptor;

import com.roc.spring.component.util.WebUtil;
import org.slf4j.MDC;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import static com.roc.spring.component.log.interceptor.TraceIdInterceptor.TRACE_ID_KEY;

/**
 * 使用时应注意Filter的顺序，建议在最后
 *
 * @author chenpeng
 * @version 1.0
 * @date 2018/1/5
 */
public class TraceIdFilter implements Filter {

    private String traceIdKey = TRACE_ID_KEY;

    private String xTraceId = "X-B3-TraceId";

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (WebUtil.mdcAlreadySet()) {
            chain.doFilter(request, response);
            return;
        } else {
            String messageId = WebUtil.getTraceId(request, traceIdKey);
            MDC.put(traceIdKey, messageId);
            chain.doFilter(request, response);
            MDC.clear();
        }
    }

    @Override
    public void destroy() {

    }

    public String getTraceIdKey() {
        return traceIdKey;
    }

    public void setTraceIdKey(String traceIdKey) {
        this.traceIdKey = traceIdKey;
        TRACE_ID_KEY = traceIdKey;
    }
}
