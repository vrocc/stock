package com.roc.spring.crud.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author roc
 */
@NoArgsConstructor
@Data
public class FilterItem {
    private String key;
    private Object value;
    private ConditionEnum operation = ConditionEnum.EQ;

    public FilterItem(String k, Object v) {
        this.key = k;
        this.value = v;
    }
}