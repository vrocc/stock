package com.roc.spring.component.advice.bean;

/**
 * @author chenpeng
 * @version 1.0
 * @date 2018/1/3
 */
public enum CommonStatus implements Status {
    SUCCESS("A00000", "成功"),
    PARAMETER_ERROR("Q00301", "参数错误"),
    NOT_FOUND("Q00302", "无资源"),
    SIGN_ERROR("Q00303", "签名错误"),
    COMMON_ERROR_ILLEGALUSER("Q00304", "非法用户"),
    SYSTEM_ERROR("Q00332", "系统错误"),
    REPEATED_SUBMIT("Q00346", "重复提交"),
    NO_ACCESS("Q00347", "无权访问"),
    INVALID_COOKIE("Q00348", "无效cookie"),
    //
    ;
    private String code;
    private String message;

    CommonStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
