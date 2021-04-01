package com.roc.spring.crud.service;

import com.roc.spring.crud.mapper.BaseMapper;
import com.roc.spring.crud.mapper.BaseViewMapper;
import com.roc.spring.crud.model.BaseEntity;

/**
 * 通用接口 entity 相关服务
 *
 * @author apple
 */
public interface CommonEntityService {

    /**
     * 对象变换
     *
     * @param entity           实体名称
     * @param baseEntityString 实体本身JSON
     * @return 实体对象形式
     */
    BaseEntity transformStringToObject(String entity, String baseEntityString);

    /**
     * 获取实体类型
     *
     * @param entity 实体名称
     * @return 类型
     */
    Class<? extends BaseEntity> getBaseEntityClass(String entity);

    /**
     * 获取实体初始化实例
     *
     * @param entity 实体名称
     * @return 实例
     */
    BaseEntity getBaseEntity(String entity);

    /**
     * 获取实体DAO view mapper
     *
     * @param entity 实体名称
     * @return 响应Mapper
     */
    BaseViewMapper<BaseEntity> getBaseViewMapper(String entity);

    /**
     * 获取实体DAO mapper
     *
     * @param entity 实体名称
     * @return 响应Mapper
     */
    BaseMapper<BaseEntity> getBaseMapper(String entity);
}