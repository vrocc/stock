package com.roc.financial.dao.model;

import com.roc.spring.crud.model.BaseEntity;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

@Getter
@Setter
@Table(name = "stock_query_log")
@Repository
public class StockQueryLog extends BaseEntity {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 名称列表
     */
    private String names;

    /**
     * 分享链接
     */
    private String url;
}