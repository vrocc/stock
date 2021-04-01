package com.roc.spring.crud.controller;

import com.google.common.base.Splitter;
import com.roc.spring.crud.service.CommonOpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${basic.path.basic:basic}")
@Validated
@CrossOrigin
public class CommonDeleteController {

    @Autowired
    private CommonOpService commonOpService;

    /**
     * 删除通用方法（物理）
     *
     * @param entity goods, cost card, material, etc
     * @param id     主键
     */
    @DeleteMapping("{entity}/{id}")
    public Integer delete(@PathVariable String entity, @PathVariable Long id) {
        return commonOpService.deleteByPrimaryKey(entity, id);
    }

    /**
     * 删除通用方法（软）
     *
     * @param entity goods, cost card, material, etc
     * @param id     主键
     */
    @DeleteMapping("soft/{entity}/{id}")
    public Integer softDelete(@PathVariable String entity, @PathVariable Long id) {
        return commonOpService.softDeleteByPrimaryKey(entity, id);
    }

    /**
     * 删除通用方法批量（物理）
     *
     * @param entity goods, cost card, material, etc
     * @param ids    主键列表
     */
    @DeleteMapping("{entity}/batch")
    public Integer deleteList(@PathVariable String entity, @NotNull @Pattern(regexp = "(\\d+[,])*(\\d+)") String ids) {
        List<Long> pkList = Splitter.on(",").omitEmptyStrings().splitToList(ids).stream().map(Long::valueOf).collect(Collectors.toList());
        return commonOpService.deleteByIds(entity, pkList);
    }

    /**
     * 删除通用方法批量（软）
     *
     * @param entity goods, cost card, material, etc
     * @param ids    主键列表
     */
    @DeleteMapping("soft/{entity}/batch")
    public Integer softDeleteList(@PathVariable String entity, @NotNull @Pattern(regexp = "(\\d+[,])*(\\d+)") String ids) {
        List<Long> pkList = Splitter.on(",").omitEmptyStrings().splitToList(ids).stream().map(Long::valueOf).collect(Collectors.toList());
        return commonOpService.softDeleteByIds(entity, pkList);
    }

}
