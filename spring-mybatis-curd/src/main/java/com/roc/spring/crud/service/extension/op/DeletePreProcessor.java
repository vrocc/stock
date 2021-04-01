package com.roc.spring.crud.service.extension.op;

import com.roc.spring.crud.service.extension.strategy.DeletePreHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author roc
 */
@Service
public class DeletePreProcessor {

    @Autowired(required = false)
    private List<DeletePreHandler> preHandlers;

    /**
     * 执行策略
     *
     * @param entity 实体
     * @param key    主键
     */
    public void process(String entity, Long key) {
        if (CollectionUtils.isEmpty(preHandlers)) {
            return;
        }
        for (DeletePreHandler handler : preHandlers) {
            if (handler.match(entity)) {
                handler.invoke(entity, key);
            }
        }
    }
}