package com.roc.spring.component.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author chenpeng
 * @version 1.0
 * @date 2017/10/16
 */
@Service
@Lazy(false)
public class ApplicationContextUtil implements ApplicationContextAware {
    private static ApplicationContext context = null;

    private ApplicationContextUtil() {
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static <T> T getBean(Class<T> requiredType) {
        return context.getBean(requiredType);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
