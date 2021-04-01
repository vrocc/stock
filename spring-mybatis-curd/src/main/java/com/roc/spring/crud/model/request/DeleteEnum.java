package com.roc.spring.crud.model.request;

import lombok.Getter;

@Getter
public enum DeleteEnum {
    UNDELETE(0),
    DELETE(1);

    private int status;

    DeleteEnum(int i) {
        status = i;
    }
}
