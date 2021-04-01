package com.roc.spring.crud.service.extension;

import com.roc.spring.crud.model.BaseEntity;
import com.roc.spring.crud.service.extension.strategy.ResultsPostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author roc
 */
@Service
public class ResultsPostProcessor {

    @Autowired
    private ResultPostProcessor resultPostProcessor;

    @Autowired(required = false)
    private List<ResultsPostHandler> resultsPostHandlers;

    public <T extends BaseEntity> List<T> process(List<T> resultList) {
        if (!CollectionUtils.isEmpty(resultsPostHandlers)) {

            // 过滤
            for (ResultsPostHandler handler : resultsPostHandlers) {
                handler.handle(resultList);
            }
        }

        // 原地转换, 扩充字段
        for (int i = 0; i < resultList.size(); i++) {
            T t = resultList.get(i);
            T result = resultPostProcessor.process(t);
            resultList.set(i, result);
        }
        return resultList;
    }
}