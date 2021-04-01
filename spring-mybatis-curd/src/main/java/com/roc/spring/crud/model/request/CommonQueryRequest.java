package com.roc.spring.crud.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author apple
 */
@NoArgsConstructor
@Data
public class CommonQueryRequest {
    @NotNull
    private List<FilterItem> filter;
    private Page page;
    private Integer pageSize = 10;
    private Integer pageNo = 1;
    private String orderBy = "id";
    private String order = "DESC";

    /**
     * FIXME: 2018/12/21 因为前端只能分开传page属性，所以暂时先转换一下，以后就直接封装page
     */
    public Page getPage() {
        if (page != null) {
            return page;
        }
        page = new Page();
        page.setPageSize(pageSize);
        page.setPageNo(pageNo);
        page.setOrder(order);
        page.setOrderBy(orderBy);
        return page;
    }

}
