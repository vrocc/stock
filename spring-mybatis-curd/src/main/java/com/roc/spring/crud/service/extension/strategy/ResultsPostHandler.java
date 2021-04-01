package com.roc.spring.crud.service.extension.strategy;

import com.roc.spring.crud.model.BaseEntity;

import java.util.List;

/**
 * @author roc
 */
public interface ResultsPostHandler {
    /**
     * 处理查询结果
     *
     * @param resultList 处理集合
     * @param <T>        处理后的结果集
     */
    <T extends BaseEntity> void handle(List<T> resultList);
}
