package com.roc.spring.component.advice.bean;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenpeng
 * @version 1.0
 * @date 2017/12/8
 */
public class ListWrapper<E> {

    @Size(min = 1, max = 10)
    @Valid
    private List<E> list;

    public ListWrapper() {
        list = new ArrayList<>();
    }

    public List<E> getList() {
        return list;
    }

    public void setList(List<E> list) {
        this.list = list;
    }

}
