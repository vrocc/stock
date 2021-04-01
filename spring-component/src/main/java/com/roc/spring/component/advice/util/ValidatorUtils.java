package com.roc.spring.component.advice.util;

import com.roc.spring.component.advice.bean.ValidatorResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

/**
 * Hibernate Validator 数据校验工具类
 */
public class ValidatorUtils {


    /**
     * 一次全部获取数据校验的结果
     *
     * @param bindingResult
     * @return
     */
    public static List<ValidatorResult> getValidatorResult(BindingResult bindingResult) {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        List<ValidatorResult> results = new ArrayList<>();
        for (ObjectError error : allErrors) {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            results.add(ValidatorResult.builder().filed(field).message(message).build());
        }
        return results;
    }
}
