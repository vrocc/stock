package com.roc.spring.crud.service.extension.strategy;

import com.roc.spring.crud.model.BaseEntity;

/**
 * @author roc
 */
public interface ResultPostHandler {

    /**
     * 匹配
     *
     * @param entity 待匹配DO
     * @return 是否匹配
     */
    <T extends BaseEntity> boolean match(T entity);

    <T extends BaseEntity> T handle(T entity);
}
