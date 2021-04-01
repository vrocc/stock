package com.roc.spring.component.invoke;

import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

@Service
public class ClassCheckService {

    public boolean isAssignedFrom(String className, Object bean) {
        try {
            Class<?> clazz = ClassUtils.forName(className, Thread.currentThread().getContextClassLoader());
            return clazz.isInstance(bean);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public Class<?> getClassByName(String className) {
        try {
            return ClassUtils.forName(className, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}
