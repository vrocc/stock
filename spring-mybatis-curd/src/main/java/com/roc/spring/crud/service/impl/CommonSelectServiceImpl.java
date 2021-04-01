package com.roc.spring.crud.service.impl;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.roc.spring.crud.controller.CommonEntityQueryController.TreeQueryRequest;
import com.roc.spring.crud.mapper.BaseMapper;
import com.roc.spring.crud.mapper.BaseViewMapper;
import com.roc.spring.crud.model.BaseEntity;
import com.roc.spring.crud.model.request.CommonQueryRequest;
import com.roc.spring.crud.service.CommonEntityService;
import com.roc.spring.crud.service.CommonSelectService;
import com.roc.spring.crud.service.extension.ResultPostProcessor;
import com.roc.spring.crud.service.extension.ResultsPostProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.common.base.BaseSelectMapper;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author roc
 */
@Service
@Slf4j
public class CommonSelectServiceImpl implements CommonSelectService {

    @Autowired
    private CommonEntityService commonEntityService;

    @Autowired
    private ExampleService exampleService;

    @Autowired
    private ResultsPostProcessor resultsPostProcessor;

    @Autowired
    private ResultPostProcessor resultPostProcessor;

    @Autowired
    private ConversionService conversionService;

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> List<T> find(String entity, CommonQueryRequest queryRequest) {
        BaseViewMapper baseMapper = commonEntityService.getBaseViewMapper(entity);
        logForQuery(baseMapper);
        Example example = exampleService.getExample(entity, queryRequest);
        List<T> resultList = baseMapper.selectByExample(example);
        return resultsPostProcessor.process(resultList);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> T findOne(String entity, CommonQueryRequest queryRequest) {
        BaseMapper baseMapper = commonEntityService.getBaseMapper(entity);
        logForQuery(baseMapper);
        Example example = exampleService.getExample(entity, queryRequest);
        T result = (T) baseMapper.selectOneByExample(example);
        return resultPostProcessor.process(result);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> T findOneWithPrimaryKey(String entity, Long key) {
        BaseSelectMapper baseMapper = commonEntityService.getBaseViewMapper(entity);
        logForQuery(baseMapper);
        T result = (T) baseMapper.selectByPrimaryKey(key);
        return resultPostProcessor.process(result);
    }

    private void logForQuery(BaseSelectMapper baseMapper) {
        Arrays.stream(baseMapper.getClass().getInterfaces()).filter(
                clazz -> !StringUtils.startsWithIgnoreCase(clazz.getName(), "org.springframework.")
        ).findFirst().ifPresent(type -> log.info("match this mapper: {}", type.getName()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> List<T> selectByIds(String entity, String ids) {
        BaseMapper baseMapper = commonEntityService.getBaseMapper(entity);
        return (List<T>) baseMapper.selectByIds(ids);
    }

    @Override
    public Map<String, Object> tree(String entity, TreeQueryRequest request) {
        CommonQueryRequest commonQueryRequest = new CommonQueryRequest();
        commonQueryRequest.setFilter(request.getFilter());
        commonQueryRequest.setPageSize(10000);
        List<BaseEntity> baseEntities = this.find(entity, commonQueryRequest);
        if (CollectionUtils.isEmpty(baseEntities)) {
            return null;
        }
        String relationField = request.getRelationField();
        // todo 支持Map BEAN
        Optional<BaseEntity> first = baseEntities.stream()
                .filter(baseEntity -> this.readField(baseEntity, relationField, Number.class) == null)
                .findFirst();
        if (!first.isPresent()) {
            return null;
        }
        Map<String, Object> rootBean = first.map(CommonSelectServiceImpl::beanToMap).orElseThrow(RuntimeException::new);

        Multimap<String, Map<String, Object>> treeMultiMap = LinkedHashMultimap.create();
        baseEntities.forEach(baseEntity -> treeMultiMap.put(this.readField(baseEntity, relationField, String.class), beanToMap(baseEntity)));

        treeifyBeans(rootBean, treeMultiMap, request.getSubItemsFiled(), request.getSortField());
        return rootBean;
    }

    @SuppressWarnings("unchecked")
    private static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = Maps.newLinkedHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            Set set = beanMap.keySet();
            ArrayList list = Lists.newArrayList(set);
            Collections.sort(list);
            for (Object key : list) {
                map.put(String.valueOf(key), beanMap.get(key));
            }
        }
        return map;
    }

    private void treeifyBeans(Map<String, Object> rootBean,
                              Multimap<String, Map<String, Object>> treeMultiMap,
                              String subItemsFiled,
                              String sortField) {
        List<Map<String, Object>> nextSubLevelItems = Lists.newArrayList(treeMultiMap.get(String.valueOf(rootBean.get("id"))).stream()
                .sorted(Comparator.comparingInt(map -> conversionService.convert(
                        Optional.ofNullable(map).map(m -> m.getOrDefault(sortField,0L)).orElse(0)
                        , Integer.class)))
                .collect(Collectors.toList()));
        rootBean.put(subItemsFiled, nextSubLevelItems);
        for (Map<String, Object> rb : nextSubLevelItems) {
            treeifyBeans(rb, treeMultiMap, subItemsFiled, sortField);
        }
    }

    private <T> T readField(Object target, String fieldName, Class<T> targetType) {
        Object convert;
        try {
            convert = FieldUtils.readField(target, fieldName, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return conversionService.convert(convert, targetType);
    }

    @Override
    public List<BaseEntity> findByPojo(String entity, String pojo) {
        BaseMapper baseMapper = commonEntityService.getBaseMapper(entity);
        logForQuery(baseMapper);
        BaseEntity baseEntity = commonEntityService.transformStringToObject(entity, pojo);
        Class<? extends BaseEntity> entityClass = commonEntityService.getBaseEntityClass(entity);
        Example example = Example.builder(entityClass).build();
        baseMapper.selectByExample(example);
        // todo
        return null;
    }

    @Override
    public BaseEntity findOneByPojo(String entity, String pojo) {
        return null;
    }
}