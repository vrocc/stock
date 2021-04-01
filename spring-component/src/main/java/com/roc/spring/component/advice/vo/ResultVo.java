package com.roc.spring.component.advice.vo;

import com.roc.spring.component.advice.bean.CommonStatus;
import com.roc.spring.component.advice.bean.Status;
import com.roc.spring.component.service.ContextService;
import com.roc.spring.component.util.ApplicationContextUtil;
import com.roc.spring.component.util.InetAddressUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultVo<T> {

    private static final ContextService contextService = ApplicationContextUtil.getBean(ContextService.class);
    private static String hostAddress = "";
    private String messageId;
    private String host;
    private String code;
    private String msg;
    private T data;

    public static <Type> ResultVoBuilder<Type> successBuilder() {
        return statusBuilder(CommonStatus.SUCCESS);
    }

    public static <Type> ResultVoBuilder<Type> statusBuilder(Status status) {
        return ResultVo.<Type>builder()
                .code(status.getCode())
                .msg(status.getMessage())
                .messageId(contextService.getMsgId())
                .host(getHostAddress());
    }

    public static <Type> ResultVoBuilder<Type> exceptionBuilder(Status status, Exception e) {
        ResultVoBuilder<Type> initBuilder = statusBuilder(status);
        return initBuilder.msg(StringUtils.defaultIfBlank(e.getMessage(), status.getMessage()));
    }

    private static String getHostAddress() {
        if (StringUtils.isBlank(hostAddress)) {
            try {
                hostAddress = InetAddressUtil.getLocalHostLANAddress().getHostAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return hostAddress;
    }
}
