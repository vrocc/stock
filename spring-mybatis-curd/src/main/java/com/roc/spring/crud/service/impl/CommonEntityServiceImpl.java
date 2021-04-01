package com.roc.spring.crud.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.CaseFormat;
import com.roc.spring.component.advice.exception.BizRuntimeException;
import com.roc.spring.crud.mapper.BaseMapper;
import com.roc.spring.crud.mapper.BaseViewMapper;
import com.roc.spring.crud.model.BaseEntity;
import com.roc.spring.crud.service.CommonEntityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.roc.spring.component.advice.bean.CommonStatus.PARAMETER_ERROR;

/**
 * TODO Cache
 *
 * @author roc
 */
@Service
public class CommonEntityServiceImpl implements CommonEntityService {

    private static final String MAPPER_SUFFIX = "Mapper";

    private static final Set<Class<? extends BaseEntity>> CLAZZ_SET = new HashSet<>();
    private static final Set<Class<? extends BaseEntity>> VIEW_CLAZZ_SET = new HashSet<>();

    @Autowired
    private Map<String, BaseMapper> commonMappers;

    @Autowired
    private Map<String, BaseViewMapper> baseViewMappers;

    @Override
    public BaseEntity transformStringToObject(String entity, String baseEntityString) {
        Class<? extends BaseEntity> entityClazz = getBaseEntityClass(entity);
        return JSON.parseObject(baseEntityString, entityClazz);
    }


    @Override
    public Class<? extends BaseEntity> getBaseEntityClass(String entity) {
        String finalEntity = formatEntityName(entity);
        return CLAZZ_SET.stream()
                .filter(clazz -> clazz.getSimpleName().equalsIgnoreCase(finalEntity))
                .findFirst()
                .orElseThrow(() ->
                        new BizRuntimeException(PARAMETER_ERROR, "can`t find mapper for entity: " + finalEntity));
    }

    private String formatEntityName(String entity) {
        if (StringUtils.contains(entity, "_")) {
            entity = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL).convert(entity);
        }
        return entity;
    }

    @Override
    public BaseEntity getBaseEntity(String entityName) {
        try {
            return this.getBaseEntityClass(entityName).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public BaseMapper<BaseEntity> getBaseMapper(String entity) {
        entity = formatEntityName(entity);
        BaseMapper<BaseEntity> mapper = this.commonMappers.get(entity + MAPPER_SUFFIX);
        Objects.requireNonNull(mapper, "can`t find any matched mapper, entity is " + entity);
        return mapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public BaseViewMapper<BaseEntity> getBaseViewMapper(String entity) {
        entity = formatEntityName(entity);
        BaseViewMapper<BaseEntity> viewMapper = this.baseViewMappers.get(entity + MAPPER_SUFFIX);
        Objects.requireNonNull(viewMapper, "can`t find any matched mapper, entity is " + entity);
        return viewMapper;
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void initModelClassSet() {
        Collection<BaseViewMapper> values = commonMappers.values().stream()
                .map((baseMapper) -> (BaseViewMapper) baseMapper)
                .collect(Collectors.toList());
        Set<Class<? extends BaseEntity>> clazzSet = CLAZZ_SET;
        initClassSet(values, clazzSet);
        Collection<BaseViewMapper> baseViewMappers = this.baseViewMappers.values();
        initClassSet(baseViewMappers, clazzSet);
    }

    private void initClassSet(Collection<BaseViewMapper> values, Set<Class<? extends BaseEntity>> clazzSet) {
        for (BaseViewMapper baseMapper : values) {
            Type type = Arrays.stream(baseMapper.getClass().getInterfaces()).filter(
                    clazz -> !StringUtils.startsWithIgnoreCase(clazz.getName(), "org.springframework.")
            ).findFirst().map(Class::getGenericInterfaces).map(
                    arr -> arr[0]
            ).get();
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type argument = parameterizedType.getActualTypeArguments()[0];
            Class<? extends BaseEntity> clazz = (Class<? extends BaseEntity>) argument;
            clazzSet.add(clazz);
        }
    }

}
