package com.roc.spring.crud.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.roc.spring.crud.model.BaseEntity;
import com.roc.spring.crud.model.request.CommonQueryRequest;
import com.roc.spring.crud.model.request.ConditionEnum;
import com.roc.spring.crud.model.request.FilterItem;
import com.roc.spring.crud.model.request.Page;
import com.roc.spring.crud.service.CommonEntityService;
import com.roc.spring.crud.service.extension.ExamplePreProcessor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author roc
 */
@Service
public class ExampleService {

    @Autowired
    private CommonEntityService commonEntityService;

    @Autowired
    private ExamplePreProcessor examplePreProcessor;

    public <T> Example getExample(String entity, CommonQueryRequest queryRequest) {
        Class<? extends BaseEntity> entityClass = commonEntityService.getBaseEntityClass(entity);

        List<FilterItem> filterItems = queryRequest.getFilter();
        examplePreProcessor.process(entityClass, filterItems);

        Example.Builder builder = Example.builder(entityClass);

        Sqls custom = Sqls.custom();
        Map<ConditionEnum, BiFunction<String, T, Sqls>> functionMap = getConditionMap(custom);

        for (FilterItem filterItem : filterItems) {
            String field = filterItem.getKey();
            ConditionEnum condition = filterItem.getOperation();
            Object value = filterItem.getValue();
            if (!(value instanceof Iterable) && condition == ConditionEnum.IN) {
                value = Collections.singletonList(value);
            }
            custom = functionMap.get(condition).apply(field, (T) value);
        }

        builder = builder.andWhere(custom);

        Page page = queryRequest.getPage();
        if (page == null) {
            return builder.build();
        }
        String order = page.getOrder();
        String orderBy = page.getOrderBy();
        MDC.put("order", order);
        MDC.put("orderBy", orderBy);

        if (StringUtils.equalsIgnoreCase(order, "ASC")) {
            builder = builder.orderByAsc(orderBy);
        } else {
            builder = builder.orderByDesc(orderBy);
        }

        PageHelper.startPage(page.getPageNo(), page.getPageSize());
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private <AnyType> Map<ConditionEnum, BiFunction<String, AnyType, Sqls>> getConditionMap(Sqls custom) {
        Map<ConditionEnum, BiFunction<String, AnyType, Sqls>> functionMap = Maps.newHashMap();
        BiFunction<String, Object, Sqls> equal = custom::andEqualTo;
        BiFunction<String, Object, Sqls> notEqualTo = custom::andNotEqualTo;
        BiFunction<String, String, Sqls> like = custom::andLike;
        BiFunction<String, Iterable, Sqls> andIn = custom::andIn;
        BiFunction<String, AnyType, Sqls> andIsNull = (property, anyType) -> custom.andIsNull(property);

        functionMap.put(ConditionEnum.EQ, (BiFunction<String, AnyType, Sqls>) equal);
        functionMap.put(ConditionEnum.NE, (BiFunction<String, AnyType, Sqls>) notEqualTo);
        functionMap.put(ConditionEnum.LIKE, (BiFunction<String, AnyType, Sqls>) like);
        functionMap.put(ConditionEnum.IN, (BiFunction<String, AnyType, Sqls>) andIn);
        functionMap.put(ConditionEnum.ISNULL, andIsNull);
        return functionMap;
    }
}
