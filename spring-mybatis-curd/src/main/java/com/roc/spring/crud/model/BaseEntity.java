package com.roc.spring.crud.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * @author roc
 */
@Getter
@Setter
public abstract class BaseEntity implements Serializable {
    public abstract Long getId();

    public abstract void setId(Long id);

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
