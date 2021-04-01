package com.roc.spring.crud;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.stream.Stream;

/**
 * @author roc
 */
@Configuration
@ComponentScan("com.roc.spring.component")
public class CrudConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] methods = Stream.of(HttpMethod.values()).map(String::valueOf).toArray(String[]::new);
        registry.addMapping("/**")
                .maxAge(3600)
                .allowCredentials(true)
                .allowedMethods(methods);
    }
}