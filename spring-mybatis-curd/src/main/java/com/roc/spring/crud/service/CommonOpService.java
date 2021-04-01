package com.roc.spring.crud.service;

import com.roc.spring.crud.model.BaseEntity;
import com.roc.spring.crud.model.request.CommonSortRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author roc
 */
public interface CommonOpService {

    /**
     * 插入or更新记录
     *
     * @param entity     实体名称
     * @param baseEntity 实体本身
     * @return 受影响的行数
     */
    int saveOrUpdate(String entity, BaseEntity baseEntity);

    /**
     * 删除主键为key的记录
     *
     * @param entity 实体
     * @param key    主键
     * @return 受影响的行数
     */
    int deleteByPrimaryKey(String entity, Long key);

    int deleteByIds(String entity, List<Long> ids);

    /**
     * 软删除
     *
     * @param entity 实体
     * @param key    主键
     * @return 受影响的行数
     */
    int softDeleteByPrimaryKey(String entity, Long key);

    @Transactional(rollbackFor = Exception.class)
    int softDeleteByIds(String entity, List<Long> ids);

    /**
     * 排序 指定查询条件的记录，默认正序
     *
     * @param entity      实体名称
     * @param sortRequest 排序请求
     * @return 变动了多少行
     */
    int sort(String entity, CommonSortRequest sortRequest);
}
