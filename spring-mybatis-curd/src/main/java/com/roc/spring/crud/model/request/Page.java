package com.roc.spring.crud.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author apple
 */
@NoArgsConstructor
@Data
public class Page {
    private Integer pageSize = 10;
    private Integer pageNo = 1;
    private String orderBy = "id";
    private String order = "DESC";
}
