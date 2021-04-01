package com.roc.spring.crud.controller;

import com.roc.spring.component.dsc.RepeatedSubmitCheck;
import com.roc.spring.crud.model.BaseEntity;
import com.roc.spring.crud.model.request.CommonSortRequest;
import com.roc.spring.crud.service.CommonEntityService;
import com.roc.spring.crud.service.CommonOpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author roc
 */
@RestController
@RequestMapping("${basic.path.basic:basic}")
@Validated
@CrossOrigin
public class CommonPostController {

    @Autowired
    private CommonOpService commonService;

    @Autowired
    private CommonEntityService commonEntityService;

    /**
     * 增加&更新 通用方法
     *
     * @param entity           goods, cost card, material, etc
     * @param baseEntityString 实体本身
     */
    @PostMapping("{entity}/post")
    @RepeatedSubmitCheck
    public BaseEntity saveOrUpdate(@PathVariable String entity, @RequestBody String baseEntityString) {
        BaseEntity baseEntity = commonEntityService.transformStringToObject(entity, baseEntityString);
        commonService.saveOrUpdate(entity, baseEntity);
        return baseEntity;
    }

    @PostMapping("{entity}/sort")
    @RepeatedSubmitCheck
    public Integer sort(@PathVariable String entity, @Valid @RequestBody CommonSortRequest sortRequest) {
        return commonService.sort(entity, sortRequest);
    }

}