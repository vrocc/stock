package com.roc.spring.crud.service.impl;

import com.roc.spring.crud.mapper.BaseMapper;
import com.roc.spring.crud.model.BaseEntity;
import com.roc.spring.crud.model.request.CommonQueryRequest;
import com.roc.spring.crud.model.request.CommonSortRequest;
import com.roc.spring.crud.service.CommonEntityService;
import com.roc.spring.crud.service.CommonOpService;
import com.roc.spring.crud.service.CommonSelectService;
import com.roc.spring.crud.service.extension.op.DeletePreProcessor;
import com.roc.spring.crud.service.extension.op.InsertPreProcessor;
import com.roc.spring.crud.service.extension.op.SavePreProcessor;
import com.roc.spring.crud.service.extension.op.UpdatePreProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author roc
 */
@Service
@Slf4j
public class CommonOpServiceImpl implements CommonOpService {

    @Autowired
    private CommonEntityService commonEntityService;

    @Autowired
    private CommonSelectService commonSelectService;

    @Autowired
    private SavePreProcessor savePreProcessor;
    @Autowired
    private InsertPreProcessor insertPreProcessor;
    @Autowired
    private UpdatePreProcessor updatePreProcessor;
    @Autowired
    private DeletePreProcessor deletePreProcessor;

    @Override
    @SuppressWarnings("unchecked")
    public int saveOrUpdate(String entity, BaseEntity baseEntity) {
        BaseMapper baseMapper = commonEntityService.getBaseMapper(entity);
        savePreProcessor.process(entity, baseEntity);
        if (baseEntity.getId() != null) {
            updatePreProcessor.process(entity, baseEntity);
            int rows = baseMapper.updateByPrimaryKeySelective(baseEntity);
            log.info("update rows is {}", rows);
            return rows;
        } else {
            insertPreProcessor.process(entity, baseEntity);
            int rows = baseMapper.insertSelective(baseEntity);
            log.info("insert rows is {}", rows);
            return rows;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKey(String entity, Long key) {
        deletePreProcessor.process(entity, key);
        BaseMapper baseMapper = commonEntityService.getBaseMapper(entity);
        return baseMapper.deleteByPrimaryKey(key);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int softDeleteByPrimaryKey(String entity, Long key) {
        deletePreProcessor.process(entity, key);
        BaseMapper<BaseEntity> baseMapper = commonEntityService.getBaseMapper(entity);
        BaseEntity baseEntity = commonEntityService.getBaseEntity(entity);
        baseEntity.setId(key);
        return baseMapper.updateByPrimaryKeySelective(baseEntity);
    }

    @Override
    public int deleteByIds(String entity, List<Long> ids) {
        log.info("即将执行{}个批量删除操作，entity：{}, ids: {}", ids.size(), entity, StringUtils.join(ids, ","));
        int count = 0;
        for (Long id : ids) {
            try {
                deleteByPrimaryKey(entity, id);
                log.info("删除第{}个成功，entity：{}, id: {}", entity, id);
            } catch (Exception e) {
                log.warn("删除第{}个失败！！entity：{}, id: {}", entity, id);
            }
            count++;
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int softDeleteByIds(String entity, List<Long> ids) {
        log.info("即将执行{}个软批量删除操作，entity：{}, ids: {}", ids.size(), entity, StringUtils.join(ids, ","));
        int count = 0;
        for (Long id : ids) {
            try {
                softDeleteByPrimaryKey(entity, id);
                log.info("软删除第{}个成功，entity：{}, id: {}", entity, id);
            } catch (Exception e) {
                log.warn("软删除第{}个失败！！entity：{}, id: {}", entity, id);
            }
            count++;
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int sort(String entity, CommonSortRequest sortRequest) {
        CommonQueryRequest commonQueryRequest = new CommonQueryRequest();
        commonQueryRequest.setFilter(sortRequest.getFilter());
        commonQueryRequest.setPageSize(10000);
        List<BaseEntity> baseEntities = commonSelectService.find(entity, commonQueryRequest);
        List<Long> keys = sortRequest.getKeys();
        Set<Long> keysFromDb = baseEntities.stream().mapToLong(BaseEntity::getId).boxed().collect(Collectors.toSet());
        if (keys.retainAll(keysFromDb)) {
            throw new IllegalArgumentException("编辑内容不一致");
        }

        int size = keys.size();
        for (int i = 0; i < size; i++) {
            Long pk = keys.get(i);
            Integer newOrder = i;
            baseEntities.stream()
                    .filter(baseEntity -> Objects.equals(baseEntity.getId(), pk))
                    .findFirst()
                    .ifPresent(baseEntity -> {
                        // todo 支持Map
                        try {
                            FieldUtils.writeField(baseEntity, entity + "Order", newOrder, true);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
        for (BaseEntity baseEntity : baseEntities) {
            this.saveOrUpdate(entity, baseEntity);
        }
        return size;
    }
}