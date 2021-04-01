package com.roc.spring.component.util;

import java.util.UUID;

/**
 * @author chenpeng
 * @version 1.0
 * @date 2018/1/3
 */
public class UuidGernerator {
    public static String getUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
