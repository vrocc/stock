package com.roc.spring.component.advice;

import com.roc.spring.component.advice.bean.CommonStatus;
import com.roc.spring.component.advice.bean.Status;
import com.roc.spring.component.advice.bean.ValidatorResult;
import com.roc.spring.component.advice.exception.BizRuntimeException;
import com.roc.spring.component.advice.vo.ResultVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenpeng
 * @version 1.0
 */
@ControllerAdvice
@ResponseBody
@Order
public class GlobalExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionAdvice.class);

    private static String doParseObjectErrors(List<ObjectError> objectErrors) {
        List<String> kvs = new ArrayList<>();
        for (ObjectError error : objectErrors) {
            String innerStr = error.getObjectName();
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                innerStr += "." + fieldError.getField();
            }
            innerStr += error.getDefaultMessage();
            kvs.add(innerStr);
        }
        return "有" + objectErrors.size() + "处问题: " + StringUtils.join(kvs, ", ");
    }

    /**
     * Spring
     * 1. 参数提交格式错误
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(HttpMessageConversionException.class)
    public ResultVo<String> handleHttpMessageNotReadableException(HttpMessageConversionException exception) {
        logger.error("参数解析失败: ", exception);
        CommonStatus parameterError = CommonStatus.PARAMETER_ERROR;
        return ResultVo.<String>exceptionBuilder(parameterError, exception).build();
    }

    /**
     * Spring Validation错误处理
     * 通过了Spring的参数绑定校验, 但未通过Validation
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVo<List<ValidatorResult>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception,
                                                                                 HandlerMethod handlerMethod) {
        logger.error("参数校验失败: ", exception);
        System.out.println(handlerMethod.toString());
        BindingResult bindingResult = exception.getBindingResult();
        // 获取数据校验的结果集
        String msg = doParseObjectErrors(bindingResult.getAllErrors());
        CommonStatus parameterError = CommonStatus.PARAMETER_ERROR;
        return ResultVo.<List<ValidatorResult>>statusBuilder(parameterError).msg(msg).build();
    }

    /**
     * 业务异常处理
     *
     * @param e 运行时业务异常
     * @return 相应的错误返回
     */
    @ExceptionHandler(BizRuntimeException.class)
    public ResultVo<String> handleBizRuntimeException(BizRuntimeException e) {
        logger.error("业务错误", e);
        return ResultVo.<String>statusBuilder(e).build();
    }

    /**
     * 兜底处理
     */
    @ExceptionHandler(Exception.class)
    public ResultVo<String> handleException(Exception e) {
        logger.error("业务错误", e);
        Status status = CommonStatus.SYSTEM_ERROR;
        return ResultVo.<String>exceptionBuilder(status, e).build();
    }

}
