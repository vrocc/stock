package com.roc.spring.component.advice.bean;

import lombok.Builder;
import lombok.Data;

/**
 * @author chenpeng
 * @version 1.0
 * @date 2018/1/3
 */
@Data
@Builder
public class CustomStatus implements Status {
    private String code;
    private String message;
}
