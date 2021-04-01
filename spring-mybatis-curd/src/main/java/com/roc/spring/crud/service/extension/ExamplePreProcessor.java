package com.roc.spring.crud.service.extension;

import com.roc.spring.crud.model.BaseEntity;
import com.roc.spring.crud.model.request.ConditionEnum;
import com.roc.spring.crud.model.request.FilterItem;
import com.roc.spring.crud.service.extension.strategy.ExamplePreHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.List;

/**
 * @author roc
 */
@Service
public class ExamplePreProcessor {

    @Autowired(required = false)
    private List<ExamplePreHandler> examplePreHandlers;

    public void process(Class<? extends BaseEntity> entityClass, List<FilterItem> filterItems) {
        Iterator<FilterItem> iterator = filterItems.iterator();
        while (iterator.hasNext()) {
            FilterItem filterItem = iterator.next();
            String fieldName = filterItem.getKey();
            Object value = filterItem.getValue();
            ConditionEnum condition = filterItem.getOperation();

            if (StringUtils.isBlank(fieldName)) {
                iterator.remove();
                continue;
            }

            // 过滤掉 ""(空串)、LIKE(%%) etc...
            if (value instanceof String) {
                String str = (String) value;
                if (StringUtils.isBlank(str)) {
                    iterator.remove();
                    continue;
                }
                if (StringUtils.equals(str.trim(), "%%") && condition == ConditionEnum.LIKE) {
                    iterator.remove();
                    continue;
                }
            }

            // change eq null to ISNULL
            if (StringUtils.isNotBlank(fieldName) && condition == ConditionEnum.EQ && value == null) {
                filterItem.setOperation(ConditionEnum.ISNULL);
            }
        }

        if (CollectionUtils.isEmpty(examplePreHandlers)) {
            return;
        }
        for (ExamplePreHandler handler : examplePreHandlers) {
            handler.preHandle(entityClass, filterItems);
        }
    }
}
