package com.roc.financial.dao.handlers;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.roc.financial.dao.model.StockQueryLog;
import com.roc.financial.utils.ReflectionFieldName;
import com.roc.spring.component.advice.bean.CommonStatus;
import com.roc.spring.component.advice.exception.BizRuntimeException;
import com.roc.spring.crud.model.BaseEntity;
import com.roc.spring.crud.model.request.CommonQueryRequest;
import com.roc.spring.crud.model.request.ConditionEnum;
import com.roc.spring.crud.model.request.FilterItem;
import com.roc.spring.crud.service.CommonSelectService;
import com.roc.spring.crud.service.extension.strategy.InsertPreHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class StockQueryLogInsertPreHandler implements InsertPreHandler<StockQueryLog> {

    @Autowired
    private CommonSelectService commonSelectService;

    @Override
    public boolean match(String entity) {
        String entityUpper = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.UPPER_CAMEL).convert(entity);
        return StringUtils.equals(entityUpper, StockQueryLog.class.getSimpleName());
    }

    @Override
    public void invoke(String entity, StockQueryLog queryLog) {
        CommonQueryRequest queryRequest = new CommonQueryRequest();
        List<FilterItem> filterItems = Lists.newArrayList();
        FilterItem filterItem = new FilterItem();
        String key = ReflectionFieldName.getLowerCamelFieldName(StockQueryLog::getUrl);
        filterItem.setKey(key);
        filterItem.setOperation(ConditionEnum.EQ);
        filterItem.setValue(queryLog.getUrl());
        queryRequest.setFilter(filterItems);
        List<BaseEntity> baseEntities = commonSelectService.find(entity, queryRequest);
        if (!CollectionUtils.isEmpty(baseEntities)) {
            throw new BizRuntimeException(CommonStatus.REPEATED_SUBMIT, "已经有相关的数据");
        }
    }
}