package com.roc.spring.crud.service.extension.strategy;

import com.roc.spring.crud.model.BaseEntity;

/**
 * TODO: Add 泛型 support
 * @author roc
 */
public interface OpPreHandler<T extends BaseEntity> {

    /**
     * 匹配
     *
     * @param entity 待匹配DO
     * @return 是否匹配
     */
    boolean match(String entity);


    /**
     * 执行策略
     *
     * @param entity     实体
     * @param baseEntity 实体类
     */
    void invoke(String entity, T baseEntity);
}