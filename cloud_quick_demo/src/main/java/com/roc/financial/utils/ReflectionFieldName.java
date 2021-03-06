package com.roc.financial.utils;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * @author chenpeng57
 */
public class ReflectionFieldName {

    public static <T> String getFieldName(FieldGetter<T, ?> func) {
        try {
            // 通过获取对象方法，判断是否存在该方法
            Method method = func.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            // 利用jdk的SerializedLambda 解析方法引用
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(func);
            String getter = serializedLambda.getImplMethodName();
            return resolveFieldName(getter);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> String getFieldName(FieldGetter<T, ?> func, CaseFormat caseFormat) {
        String fieldName = getFieldName(func);
        return CaseFormat.UPPER_CAMEL.converterTo(caseFormat).convert(fieldName);
    }

    public static <T> String getLowerUnderscoreFieldName(FieldGetter<T, ?> func) {
        String fieldName = getFieldName(func);
        return CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE).convert(fieldName);
    }

    public static <T> String getLowerCamelFieldName(FieldGetter<T, ?> func) {
        String fieldName = getFieldName(func);
        return CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_CAMEL).convert(fieldName);
    }

    private static String resolveFieldName(String getMethodName) {
        if (getMethodName.startsWith("get")) {
            getMethodName = getMethodName.substring(3);
        } else if (getMethodName.startsWith("is")) {
            getMethodName = getMethodName.substring(2);
        }
        // 小写第一个字母
        return firstToLowerCase(getMethodName);
    }

    private static String firstToLowerCase(String param) {
        if (StringUtils.isBlank(param)) {
            return "";
        }
        return param.substring(0, 1).toLowerCase() + param.substring(1);
    }
}