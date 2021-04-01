package com.roc.spring.component.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roc.spring.component.advice.bean.CommonStatus;
import com.roc.spring.component.advice.exception.BizRuntimeException;
import com.roc.spring.component.advice.vo.ResultVo;
import com.roc.spring.component.log.interceptor.TraceIdInterceptor;
import com.roc.spring.component.service.ContextService;
import com.roc.spring.component.util.WebUtil;
import feign.FeignException;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author roc
 */
@Slf4j
public class SpringDecoderWrapper extends SpringDecoder {

    private final ObjectMapper objectMapper;
    private final ContextService contextService;

    public SpringDecoderWrapper(ObjectFactory<HttpMessageConverters> messageConverters,
                                ObjectMapper objectMapper,
                                ContextService contextService) {
        super(messageConverters);
        this.objectMapper = objectMapper;
        this.contextService = contextService;
    }

    @Override
    public Object decode(final Response response, Type type)
            throws IOException, FeignException {
        Object decode = super.decode(response, ResultVo.class);
        Object finalResult = decode;
        if (decode instanceof ResultVo) {
            ResultVo<?> result = (ResultVo<?>) decode;
            putMessageId(result);
            finalResult = checkResultCode(decode, result, type);
        }
        return finalResult;
    }

    private Object checkResultCode(Object decode, ResultVo<?> result, Type type) {
        String code = result.getCode();
        if (StringUtils.isNotBlank(code) && !StringUtils.equalsIgnoreCase(code, CommonStatus.SUCCESS.getCode())) {
            throw new BizRuntimeException(code, result.getMsg());
        } else {
            Object data = result.getData();
            try {
                String s = this.objectMapper.writeValueAsString(data);
                if (type instanceof Class) {
                    Class<?> t = (Class<?>) type;
                    return this.objectMapper.readValue(s, t);
                }
            } catch (Exception e) {
                log.error("format json error, data {}", data, e);
            }
        }
        return decode;
    }

    private void putMessageId(ResultVo<?> result) {
        String messageId = StringUtils.defaultIfBlank(result.getMessageId(), "(blank)");
        String msgId = contextService.getMsgId();
        String traceIdKey = TraceIdInterceptor.TRACE_ID_KEY;
        if (!WebUtil.mdcAlreadySet()) {
            if (!StringUtils.equals(msgId, messageId)) {
                String val = msgId + "→" + messageId;
                MDC.put(traceIdKey, val);
            }
        }
    }
}
