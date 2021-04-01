package com.roc.spring.component.service;

import java.util.List;

/**
 * @author chenpeng
 * @version 1.0
 * @date 2018/1/9
 */
public interface ConfigService<T> {
    List<T> getConfigList();
}
