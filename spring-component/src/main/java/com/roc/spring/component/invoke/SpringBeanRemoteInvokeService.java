package com.roc.spring.component.invoke;

import com.alibaba.fastjson.JSON;
import com.github.jsonzou.jmockdata.JMockData;
import com.roc.spring.component.annotation.RPC;
import com.roc.spring.component.util.MD5Util;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.web.format.WebConversionService;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author apple
 */
@Service
@Slf4j
public class SpringBeanRemoteInvokeService {

    private static ConfigurableListableBeanFactory context = MyBeanFactoryPostProcessor.listableBeanFactory;
    private static List<BeanInfo> beanIfs = new ArrayList<>();
    private static Map<String, List<SimpleMethodDescription>> beanInfoMap = new HashMap<>();
    private LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private WebConversionService webConversionService = new WebConversionService(dateFormat);
    @Autowired
    private ClassCheckService classCheckService;

    @PostConstruct
    public void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                beanIfs = SpringBeanRemoteInvokeService.this.initSpringBeans();
                for (BeanInfo beanInfo : beanIfs) {
                    String beanName = beanInfo.beanName;
                    List<SimpleMethodDescription> beanMethods = SpringBeanRemoteInvokeService.this.initBeanMethods(beanName);
                    beanInfoMap.put(beanName, beanMethods);
                }
            }
        }).start();
    }

    public List<BeanInfo> getSpringBeans() {
        return beanIfs;
    }

    public List<SimpleMethodDescription> getBeanMethods(String beanName) {
        return beanInfoMap.get(beanName);
    }

    /**
     * todo: 可配置获取Bean的筛选条件
     * 获取spring bean名称、类型、是否为AOP等信息
     */
    private List<BeanInfo> initSpringBeans() {
        List<BeanInfo> beanInfoList = new ArrayList<>();
        for (String beanName : context.getBeanDefinitionNames()) {
            List<SimpleMethodDescription> beanMethods = this.initBeanMethods(beanName);
            if (CollectionUtils.isEmpty(beanMethods)) {
                continue;
            }
            BeanInfo beanInfo = null;
            try {
                beanInfo = this.generateBeanInfo(beanName);
            } catch (Exception e) {
                log.error("get bean error", e);
            }
            if (beanInfo != null) {
                beanInfoList.add(beanInfo);
            }
        }
        return beanInfoList;
    }

    private BeanInfo generateBeanInfo(final String beanName) {
        BeanDefinition beanDefinition = context.getBeanDefinition(beanName);
        Class<?> targetClass = null;
        try {
            targetClass = ClassUtils.getClass(beanDefinition.getBeanClassName());
        } catch (Exception e) {
            return null;
        }
        boolean isProxy = false;
        Class<?> primitiveType = targetClass;
        if (StringUtils.equalsIgnoreCase(targetClass.getCanonicalName(), "tk.mybatis.spring.mapper.MapperFactoryBean")) {
            isProxy = true;
            primitiveType = context.getBean(beanName).getClass().getInterfaces()[0];
        }
        if (Proxy.isProxyClass(targetClass)) {
            isProxy = true;
            while (primitiveType.getName().contains("$")) {
                primitiveType = primitiveType.getSuperclass();
            }
        }
        if (this.isAnnotatedWith(primitiveType, Controller.class, RequestMapping.class)) {
            return null;
        }
        String springPrefix = "org.springframework";
        if (primitiveType.getName().startsWith(springPrefix)) {
            return null;
        }
        return BeanInfo.builder()
                .beanName(beanName)
                .isProxy(isProxy)
                .primitiveType(primitiveType)
                .proxyType(targetClass)
                .build();
    }


    /**
     * 获取某个特定Bean的所有方法
     *
     * @param beanName bean的名称
     * @return 某个特定Bean的所有方法
     */
    private List<SimpleMethodDescription> initBeanMethods(String beanName) {
        List<MethodDescription> methodDescriptions = this.doGetBeanMethods(beanName);

        List<SimpleMethodDescription> simples = new ArrayList<>();
        for (MethodDescription description : methodDescriptions) {
            SimpleMethodDescription simple = SimpleMethodDescription.builder()
                    .methodName(description.getMethodName())
                    .methodSignature(description.getMethodSignature())
                    .paramInfos(description.getParamInfos())
                    .build();
            simples.add(simple);
        }
        return simples;
    }

    /**
     * 调用相关的bean的方法
     *
     * @param bean              bean名称
     * @param method            方法名称
     * @param paramTypeValueMap 参数名称、类型、值Map 【paramName → type@paramValue】
     * @return 调用结果
     */
    public Object invokeBeanMethod(String bean, String method,
                                   Map<String, String> paramTypeValueMap,
                                   String signature) {

        // 解析入参
        Map<String, ParamTypeValue> typeValueMap = new LinkedHashMap<>(8);
        for (Map.Entry<String, String> entry : paramTypeValueMap.entrySet()) {
            String paramName = entry.getKey();
            String value = entry.getValue();
            String[] split = value.split("@", 2);
            Class<?> clazz = classCheckService.getClassByName(split[0]);
            String paramValue = split[1];
            try {
                paramValue = URLDecoder.decode(paramValue, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            ParamTypeValue paramTypeValue = ParamTypeValue.builder()
                    .paramName(paramName)
                    .paramType(clazz)
                    .value(paramValue)
                    .build();
            typeValueMap.put(paramName, paramTypeValue);
        }

        // 匹配方法
        List<MethodDescription> beanMethods = this.doGetBeanMethods(bean);
        MethodDescription chooseMethod = null;
        for (MethodDescription methodDescription : beanMethods) {
            Method beanMethod = methodDescription.getMethod();
            if (StringUtils.equalsIgnoreCase(beanMethod.getName(), method)) {
                String methodSignature = this.getMethodSignature(beanMethod);
                if (StringUtils.equals(methodSignature, signature)) {
                    chooseMethod = methodDescription;
                    chooseMethod.getMethod().setAccessible(true);
                    break;
                }
            }
        }

        if (chooseMethod == null) {
            throw new RuntimeException("没找到...");
        }

        // 反序列化参数
        List<Class<?>> parameterTypes = chooseMethod.getParameterTypes();
        Object[] args = new Object[parameterTypes.size()];
        int i = 0;
        for (Map.Entry<String, ParamTypeValue> entry : typeValueMap.entrySet()) {
            ParamTypeValue paramTypeValue = entry.getValue();
            Class<?> paramType = paramTypeValue.getParamType();
            String value = paramTypeValue.getValue();
            Object convert;
            try {
                convert = webConversionService.convert(value, paramType);
            } catch (Exception e) {
                if (paramType.isArray() || Collection.class.isAssignableFrom(paramType)) {
                    convert = JSON.parseArray(value, paramType);
                } else {
                    convert = JSON.parseObject(value, paramType);
                }
            }
            args[i++] = convert;
        }

        // 调用
        Object invoke;
        try {
            invoke = chooseMethod.getMethod().invoke(chooseMethod.getBean(), args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage());
        }
        return invoke;
    }


    private List<MethodDescription> doGetBeanMethods(String beanName) {
        List<MethodDescription> methodDescriptions = new ArrayList<>();

        BeanInfo beanInfo = null;
        try {
            beanInfo = this.generateBeanInfo(beanName);
        } catch (Exception e) {
            log.error("get bean error", e);
        }
        if (beanInfo == null) {
            return Collections.EMPTY_LIST;
        }

        Class<?> targetClass = beanInfo.getPrimitiveType();
        if (!this.isAnnotatedWith(targetClass, RPC.class)) {
            return Collections.EMPTY_LIST;
        }
        Object bean = context.getBean(beanName);
        do {
            this.handleInheritedPublicMethods(targetClass, methodDescriptions, bean, targetClass == beanInfo.getPrimitiveType());
            targetClass = targetClass.getSuperclass();
        } while (targetClass != Object.class && targetClass != null);


        return methodDescriptions;
    }

    private void handleInheritedPublicMethods(Class<?> targetClass,
                                              List<MethodDescription> methodDescriptions,
                                              Object bean,
                                              boolean isPrimitiveTyp) {
        Method[] declaredMethods = targetClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            int modifiers = declaredMethod.getModifiers();
            if (!Modifier.isPublic(modifiers)) {
                continue;
            }
            if (isPrimitiveTyp) {
                Class<?> declaringClass = declaredMethod.getDeclaringClass();
                if (AnnotationUtils.findAnnotation(declaredMethod, RPC.class) == null && !this.isAnnotatedWith(declaringClass, RPC.class)) {
                    continue;
                }
            }

            String signature = this.getMethodSignature(declaredMethod);
            Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
            String[] parameterNames = discoverer.getParameterNames(declaredMethod);
            boolean emptyParam = ArrayUtils.isEmpty(parameterNames);
            List<ParamInfo> paramInfos = new ArrayList<>();
            if (ArrayUtils.isNotEmpty(parameterTypes)) {
                for (int i = 0; i < parameterTypes.length; i++) {
                    Class<?> type = parameterTypes[i];
                    String paramName;
                    if (emptyParam) {
                        paramName = type.getSimpleName();
                    } else {
                        paramName = parameterNames[i];
                    }
                    Object mock = "";
                    try {
                        mock = JMockData.mock(type);
                    } catch (Exception e) {
                        // ignore
                    }
                    ParamInfo paramInfo = ParamInfo.builder()
                            .paramName(paramName)
                            .paramType(type)
                            .demo(mock)
                            .build();
                    paramInfos.add(paramInfo);
                }
            }
            List<Class<?>> classes = Arrays.asList(parameterTypes);
            MethodDescription methodDescription = MethodDescription.builder()
                    .methodSignature(signature)
                    .paramInfos(paramInfos)
                    .parameterTypes(classes)
                    .bean(bean)
                    .method(declaredMethod)
                    .methodName(declaredMethod.getName())
                    .build();
            methodDescriptions.add(methodDescription);
        }
    }

    private String getMethodSignature(Method declaredMethod) {
        Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
        List<String> parameterTypeList = new ArrayList<>();

        for (Class<?> parameterType : parameterTypes) {
            String parameterTypeName = parameterType.getName();
            parameterTypeList.add(parameterTypeName);
        }
        String clazzName = declaredMethod.getDeclaringClass().getName();
        String methodName = declaredMethod.getName();
        String joins = StringUtils.join(parameterTypeList, ",");
        String fullMethodName = clazzName + "#" + methodName + "(" + joins + ")";
        return MD5Util.getMD5(fullMethodName);
    }

    private boolean isAnnotatedWith(Class<?> primitiveType, Class<? extends Annotation>... annotationTypes) {
        for (Class<? extends Annotation> annotation : annotationTypes) {
            if (AnnotationUtils.getAnnotation(primitiveType, annotation) != null) {
                return true;
            }
        }
        return false;
    }

    @Data
    @Builder
    public static class MethodDescription {
        private transient Object bean;
        private transient Method method;
        private transient List<Class<?>> parameterTypes;
        private String methodName;
        private String methodSignature;
        private List<ParamInfo> paramInfos;
    }

    @Data
    @Builder
    public static class SimpleMethodDescription {
        private String methodName;
        private String methodSignature;
        private List<ParamInfo> paramInfos;
    }

    @Data
    @Builder
    private static class ParamInfo {
        private String paramName;
        private Class<?> paramType;
        private Object demo;
    }

    @Data
    @Builder
    public static class BeanInfo {
        private String beanName;
        private Boolean isProxy;
        private Class primitiveType;
        private Class proxyType;
    }

    @Data
    @Builder
    public static class ParamTypeValue {
        private String paramName;
        private Class<?> paramType;
        private String value;
    }

}