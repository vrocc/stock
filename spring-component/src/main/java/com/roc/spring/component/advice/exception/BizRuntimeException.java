package com.roc.spring.component.advice.exception;

import com.roc.spring.component.advice.bean.Status;

/**
 * @author chenpeng
 * @version 1.0
 * @date 2017/11/27
 */
public class BizRuntimeException extends RuntimeException implements Status {
    private String code;

    public BizRuntimeException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public BizRuntimeException(Status status, String msg) {
        super(msg);
        this.code = status.getCode();
    }

    public BizRuntimeException(Status status) {
        super(status.getMessage());
        this.code = status.getCode();
    }

    @Override
    public String getCode() {
        return code;
    }
}
