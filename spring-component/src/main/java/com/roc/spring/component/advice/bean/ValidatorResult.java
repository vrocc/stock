package com.roc.spring.component.advice.bean;

import lombok.Builder;
import lombok.Data;

/**
 * 参数校验信息封装类
 */
@Data
@Builder
public class ValidatorResult {

    /**
     * 字段名
     */
    private String filed;

    /**
     * 参数校验提示信息
     */
    private String message;
}
