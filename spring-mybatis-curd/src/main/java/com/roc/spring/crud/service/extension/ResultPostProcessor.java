package com.roc.spring.crud.service.extension;

import com.roc.spring.crud.model.BaseEntity;
import com.roc.spring.crud.service.extension.strategy.ResultPostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author roc
 */
@Service
public class ResultPostProcessor {

    @Autowired(required = false)
    private List<ResultPostHandler> resultPostHandlers;

    public <T extends BaseEntity> T process(T t) {
        if (CollectionUtils.isEmpty(resultPostHandlers)) {
            return t;
        }
        for (ResultPostHandler handler : resultPostHandlers) {
            if (handler.match(t)) {
                return handler.handle(t);
            }
        }
        return t;
    }
}
