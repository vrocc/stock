package com.roc.spring.component.service;

import java.util.Map;

/**
 * @author chenpeng
 * @version 1.0
 * @date 2017/11/27
 */
public interface ContextService {
    String getIP();

    /**
     * 获取P00001
     */
    String getTicket();

    /**
     * 获取MsgId
     */
    String getMsgId();

    /**
     * 获取请求参数Map
     * 转换参数Map.value格式, 并去除其中的空串
     * <p>
     * <pre>
     *     Map< String, String[]({"a","","c"})> --> Map< String, String("a,c")>
     * </pre>
     *
     * @return Map, 类型为: [String, String]
     */
    Map<String, String> getRequestParameters();
}
