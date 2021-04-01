package com.roc.spring.component.service.impl;

import com.roc.spring.component.log.interceptor.TraceIdInterceptor;
import com.roc.spring.component.util.WebUtil;
import com.roc.spring.component.service.ContextService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author chenpeng
 * @version 1.0
 * @date 2017/11/27
 */
@Component
public class ContextServiceImpl implements ContextService {

    @Autowired
    private TraceIdInterceptor traceIdInterceptor;

    @Override
    public String getIP() {
        HttpServletRequest request = this.getRequest();
        return WebUtil.getRemoteAddr(request);
    }

    @Override
    public Map<String, String> getRequestParameters() {
        HttpServletRequest request = this.getRequest();
        @SuppressWarnings("unchecked")
        Map<String, String[]> parameterMap = (Map<String, String[]>) request.getParameterMap();
        return WebUtil.genMapByRequestParams(parameterMap);
    }

    @Override
    public String getTicket() {
        HttpServletRequest request = this.getRequest();
        return WebUtil.getTicket(request);
    }

    @Override
    public String getMsgId() {
        List<String> traceIds = Arrays.asList("X-B3-TraceId", "traceId");
        for (String traceId : traceIds) {
            String cs = MDC.get(traceId);
            if (StringUtils.isNotBlank(cs)) {
                return cs;
            }
        }
        return MDC.get(traceIdInterceptor.getTraceIdKey());
    }

    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }
}
