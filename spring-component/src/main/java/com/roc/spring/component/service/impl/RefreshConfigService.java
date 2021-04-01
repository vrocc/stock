package com.roc.spring.component.service.impl;

import com.roc.spring.component.annotation.Config;
import com.roc.spring.component.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * @author chenpeng
 * @version 1.0
 * @date 2018/1/9
 */
@Slf4j
public abstract class RefreshConfigService<T> implements ConfigService<T> {

    private volatile List<T> configList = Collections.emptyList();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * use {@link Scheduled} to execute refresh
     */
    protected abstract void scheduleReload();

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void reloadConfigs() {
        Config configLocation = this.getConfigLocation();
        if (configLocation == null) {
            log.error("Config Annotation error");
            return;
        }
        String sql = configLocation.sql();
        String query;
        String tableName = configLocation.tableName();
        if (StringUtils.isNoneBlank(sql)) {
            query = sql;
        } else {
            query = "SELECT * FROM " + tableName;
        }
        RowMapper<T> rowMapper = this.getRowMapper();
        List<T> currentConfigList = jdbcTemplate.query(query, rowMapper);
        configList = Collections.unmodifiableList(currentConfigList);

        this.afterReload();
    }

    protected void afterReload() {
        // do the after things
    }

    protected RowMapper<T> getRowMapper() {
        Class<T> clazz = this.getGenericType();
        return BeanPropertyRowMapper.newInstance(clazz);
    }

    /**
     * 获取泛型返回值具体类型
     *
     * @return
     */
    private Class<T> getGenericType() {
        ParameterizedType t = (ParameterizedType) this.getClass().getGenericSuperclass();
        Type[] types = t.getActualTypeArguments();
        Type type = types[0];
        if (type instanceof Class) {
            return (Class<T>) type;
        }
        log.error("MUST SET GenericType");
        return null;
    }

    /**
     * CGLIB AOP
     *
     * @return
     */
    private Config getConfigLocation() {
        Class<?> clazz = this.getClass();
        Config configLocation = AnnotationUtils.getAnnotation(clazz, Config.class);
        while (configLocation == null) {
            clazz = clazz.getSuperclass();
            if (clazz == Object.class) {
                return null;
            }
            configLocation = AnnotationUtils.getAnnotation(clazz, Config.class);
        }
        return configLocation;
    }

    @Override
    public List<T> getConfigList() {
        return configList;
    }
}
