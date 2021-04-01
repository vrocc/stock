package com.roc.spring.component.advice;

import com.github.pagehelper.Page;
import lombok.Data;
import org.slf4j.MDC;

import java.util.List;

/**
 * @author roc
 */
public class PageInfoHelper {

    @SuppressWarnings("unchecked")
    public static SimplePageInfo simplifyPageInfo(Page<Object> pageInfo) {
        SimplePageInfo simplePageInfo = new SimplePageInfo();
        simplePageInfo.setPageNo(pageInfo.getPageNum());
        simplePageInfo.setPageSize(pageInfo.getPageSize());
        simplePageInfo.setTotalCount(pageInfo.getTotal());
        simplePageInfo.setTotalPage(pageInfo.getPages());
        simplePageInfo.setList(pageInfo.getResult());
        simplePageInfo.setOrder(MDC.get("order"));
        simplePageInfo.setOrderBy(MDC.get("orderBy"));
        return simplePageInfo;
    }

    public static Page<Object> rawPageInfo(SimplePageInfo simplePageInfo) {
        Page<Object> pageInfo = new Page<>();
        pageInfo.setPageNum(simplePageInfo.getPageNo());
        pageInfo.setPageSize(simplePageInfo.getPageSize());
        pageInfo.setTotal(simplePageInfo.getTotalCount());
        pageInfo.setPages(simplePageInfo.getPageNo());
        pageInfo.addAll(simplePageInfo.getList());
        pageInfo.setOrderBy(MDC.get("orderBy"));
        return pageInfo;
    }

    @Data
    public static class SimplePageInfo {
        private String order;
        private String orderBy;
        //当前页
        private int pageNo;
        //每页的数量
        private int pageSize;
        //总记录数
        protected long totalCount;
        //总页数
        private int totalPage;
        //结果集
        protected List<Object> list;
    }
}
