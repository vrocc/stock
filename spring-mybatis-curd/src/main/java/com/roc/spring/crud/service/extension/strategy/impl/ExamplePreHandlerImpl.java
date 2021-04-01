package com.roc.spring.crud.service.extension.strategy.impl;

import com.roc.spring.crud.model.BaseEntity;
import com.roc.spring.crud.model.request.ConditionEnum;
import com.roc.spring.crud.model.request.FilterItem;
import com.roc.spring.crud.service.extension.strategy.ExamplePreHandler;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author roc
 */
@Service
public class ExamplePreHandlerImpl implements ExamplePreHandler {
    @Override
    public void preHandle(Class<? extends BaseEntity> entityClass, List<FilterItem> filterItems) {
        Set<String> fieldNames = Stream.of(entityClass.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());
        String fieldName = "delete";
        if (!fieldNames.contains(fieldName)) {
            return;
        }
        if (!getFilterKey(filterItems).contains(fieldName)) {
            FilterItem item = new FilterItem();
            item.setKey(fieldName);
            item.setOperation(ConditionEnum.EQ);
            item.setValue(0);
            filterItems.add(item);
        }
    }

    private Set<String> getFilterKey(List<FilterItem> filterItems) {
        return filterItems.stream().map(FilterItem::getKey).collect(Collectors.toSet());
    }
}
