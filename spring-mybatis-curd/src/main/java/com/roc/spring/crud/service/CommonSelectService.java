package com.roc.spring.crud.service;

import com.roc.spring.crud.controller.CommonEntityQueryController.TreeQueryRequest;
import com.roc.spring.crud.model.BaseEntity;
import com.roc.spring.crud.model.request.CommonQueryRequest;

import java.util.List;
import java.util.Map;

/**
 * @author roc
 */
public interface CommonSelectService {

    /**
     * 分页条件查询
     * 支持设置是否删除条件
     *
     * @param entity       实体名称
     * @param queryRequest 查询请求
     * @param <T>          查询类别
     * @return 查询分页列表
     */
    <T extends BaseEntity> List<T> find(String entity, CommonQueryRequest queryRequest);

    /**
     * 条件查询，只返回一个
     * 支持设置是否删除条件
     *
     * @param entity       实体名称
     * @param queryRequest 查询请求
     * @param <T>          查询类别
     * @return 实体
     */
    <T extends BaseEntity> T findOne(String entity, CommonQueryRequest queryRequest);

    /**
     * 根据主键查询实体
     *
     * @param entity 实体名称
     * @param key    主键
     * @param <T>    查询类别
     * @return
     */
    <T extends BaseEntity> T findOneWithPrimaryKey(String entity, Long key);

    /**
     * 根据主键字符串进行查询，类中只有存在一个带有@Id注解的字段
     *
     * @param entity 实体名称
     * @param ids    如 "1,2,3,4"
     * @param <T>    实体类型标识
     * @return 符合条件的集合
     */
    <T extends BaseEntity> List<T> selectByIds(String entity, String ids);

    /**
     * 获取树状结构的信息
     *
     * @param entity  实体名称
     * @param request 树状请求条件
     * @return 树🌲
     */
    Map<String, Object> tree(String entity, TreeQueryRequest request);

    /**
     * 通过实体类查
     *
     * @param entity 实体类名称
     * @param pojo   实体类
     * @return 结果集
     */
    List<BaseEntity> findByPojo(String entity, String pojo);

    /**
     * 通过实体类查一个
     *
     * @param entity 实体类名称
     * @param pojo   实体类
     * @return 结果集
     */
    BaseEntity findOneByPojo(String entity, String pojo);
}
