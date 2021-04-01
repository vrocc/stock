package com.roc.spring.crud.service.extension.strategy;

import com.roc.spring.crud.model.BaseEntity;
import com.roc.spring.crud.model.request.FilterItem;

import java.util.List;

/**
 * @author roc
 */
public interface ExamplePreHandler {
    /**
     * 预处理查询条件
     *
     * @param entityClass 实体类型
     * @param filterItems 条件类目
     */
    void preHandle(Class<? extends BaseEntity> entityClass, List<FilterItem> filterItems);
}
