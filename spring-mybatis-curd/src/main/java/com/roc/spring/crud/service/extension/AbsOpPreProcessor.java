package com.roc.spring.crud.service.extension;

import com.roc.spring.crud.model.BaseEntity;
import com.roc.spring.crud.service.extension.strategy.OpPreHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author roc
 */
@Service
public abstract class AbsOpPreProcessor<Handler extends OpPreHandler> {

    @Autowired(required = false)
    private List<Handler> preHandlers;

    /**
     * 执行策略
     *
     * @param entity     实体
     * @param baseEntity 实体类
     */
    public void process(String entity, BaseEntity baseEntity) {
        if (CollectionUtils.isEmpty(preHandlers)) {
            return;
        }
        for (Handler handler : preHandlers) {
            if (handler.match(entity)) {
                handler.invoke(entity, baseEntity);
            }
        }
    }
}
