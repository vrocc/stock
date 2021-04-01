package com.roc.spring.crud.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author roc
 */
@NoArgsConstructor
@Data
public class CommonSortRequest {
    @NotNull
    private List<FilterItem> filter;
    @NotEmpty
    @NotNull
    private List<Long> keys;
}