package com.roc.spring.component;

import com.roc.spring.component.log.interceptor.TraceIdFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * @author roc
 */
@Configuration
@ComponentScan("com.roc.spring.component.**")
public class SpringComponentConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<Filter> traceFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TraceIdFilter());
        registration.addUrlPatterns("/*");
        registration.setName("TraceIdFilter");
        registration.setOrder(1);
        return registration;
    }
}
