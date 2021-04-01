package com.roc.spring.crud.service.extension.strategy;

/**
 * @author apple
 */
public interface DeletePreHandler {
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
     * @param entity 实体名称
     * @param key    实体主键
     */
    void invoke(String entity, Long key);
}