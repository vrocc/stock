package com.roc.financial.utils;


import java.io.Serializable;
import java.util.function.Function;

/**
 * @author chenpeng57
 */
@FunctionalInterface
public interface FieldGetter<T, R> extends Function<T, R>, Serializable {
}