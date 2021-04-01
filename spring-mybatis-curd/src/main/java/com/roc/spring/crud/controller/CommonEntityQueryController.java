package com.roc.spring.crud.controller;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.roc.spring.crud.model.BaseEntity;
import com.roc.spring.crud.model.request.CommonQueryRequest;
import com.roc.spring.crud.model.request.FilterItem;
import com.roc.spring.crud.model.request.Page;
import com.roc.spring.crud.service.CommonSelectService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author roc
 */
@RestController
@RequestMapping("${basic.path.basic:basic}")
@Validated
@CrossOrigin
public class CommonEntityQueryController {

    @Autowired
    private CommonSelectService commonService;

    @GetMapping("{entity}/batch")
    public List<BaseEntity> batchEntitiesById(@PathVariable String entity,
                                              @NotNull @Pattern(regexp = "(\\d+[,])*(\\d+)") String ids) {
        return commonService.selectByIds(entity, ids);
    }

    /**
     * 通用查询方法
     *
     * @param entity       goods, cost card, material, etc
     * @param queryRequest 查询请求
     */
    @RequestMapping("{entity}/find")
    public PageInfo<BaseEntity> find(@PathVariable String entity, @Valid @RequestBody CommonQueryRequest queryRequest) {
        List<BaseEntity> entities = commonService.find(entity, queryRequest);
        return new PageInfo<>(entities);
    }

    @RequestMapping("${basic.path.findOne:\\{entity}/findOne}")
    public BaseEntity findOne(@PathVariable String entity, @RequestBody CommonQueryRequest queryRequest) {
        return commonService.findOne(entity, queryRequest);
    }

    @RequestMapping("{entity}/query")
    public PageInfo<BaseEntity> basicQuery(@PathVariable String entity,
                                           @RequestParam(required = false) Map<String, String> queryParam,
                                           Page page) {
        CommonQueryRequest queryRequest = getCommonQueryRequest(queryParam, page);
        List<BaseEntity> entities = commonService.find(entity, queryRequest);
        return new PageInfo<>(entities);
    }

    @RequestMapping("${basic.path.queryOne:\\{entity}/queryOne}")
    public BaseEntity basicQueryOne(@PathVariable String entity,
                                    @RequestParam(required = false) Map<String, String> queryParam) {
        CommonQueryRequest queryRequest = getCommonQueryRequest(queryParam, null);
        return commonService.findOne(entity, queryRequest);
    }

    private CommonQueryRequest getCommonQueryRequest(Map<String, String> queryParam, Page page) {
        if (!CollectionUtils.isEmpty(queryParam)) {
            Field[] declaredFields = Page.class.getDeclaredFields();
            Stream.of(declaredFields).map(Field::getName).forEach(queryParam::remove);
        } else {
            queryParam = Collections.emptyMap();
        }
        CommonQueryRequest queryRequest = new CommonQueryRequest();
        List<FilterItem> filter = queryParam.entrySet().stream()
                .map((entry) -> new FilterItem(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        queryRequest.setFilter(filter);
        queryRequest.setPage(page);
        return queryRequest;
    }

    @GetMapping("{entity}/{id}")
    public BaseEntity findOneWithPrimaryKey(@PathVariable String entity, @PathVariable Long id) {
        return commonService.findOneWithPrimaryKey(entity, id);
    }

    @RequestMapping("{entity}/tree")
    public Map<String, Object> tree(@PathVariable String entity, @RequestBody TreeQueryRequest request) {
        return commonService.tree(entity, request);
    }

    @Data
    @NoArgsConstructor
    public static class TreeQueryRequest {
        private String relationField = "parentId";
        private String subItemsFiled = "subItems";
        private String sortField = "menuOrder";
        private List<FilterItem> filter = Lists.newArrayList();
    }
}