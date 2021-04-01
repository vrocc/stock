package com.roc.spring.crud.mapper;

import tk.mybatis.mapper.common.base.BaseSelectMapper;
import tk.mybatis.mapper.common.example.SelectByExampleMapper;
import tk.mybatis.mapper.common.example.SelectCountByExampleMapper;
import tk.mybatis.mapper.common.example.SelectOneByExampleMapper;

/**
 * View(Read Only Mapper)
 *
 * @author apple
 */
public interface BaseViewMapper<T> extends SelectByExampleMapper<T>,
        SelectOneByExampleMapper<T>,
        SelectCountByExampleMapper<T>,
        BaseSelectMapper<T> {
}