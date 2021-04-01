package com.roc.spring.component.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author chenpeng
 * @version 1.0
 * @date 2018/1/3
 */
public class WebUtil {
    private static Logger logger = LoggerFactory.getLogger(WebUtil.class);

    /**
     * 获取客户端IP地址，支持proxy
     *
     * @param req HttpServletRequest
     * @return IP地址
     */
    public static String getRemoteAddr(HttpServletRequest req) {
        String xffIp = req.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(xffIp)) {
            String[] ips = StringUtils.split(xffIp, ',');
            for (String ip : ips) {
                if (StringUtils.isBlank(ip)) {
                    continue;
                }
                ip = ip.trim();
                if (isIPAddr(ip) && !ip.startsWith("10.") && !ip.startsWith("192.168.") && !"127.0.0.1".equals(ip)) {
                    return ip.trim();
                }
            }

        }
        String ip = req.getHeader("x-real-ip");
        if (isIPAddr(ip)) {
            return ip;
        }
        ip = req.getRemoteAddr();
        if (ip.indexOf('.') == -1) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    public static String getTicket(HttpServletRequest request) {
        //先从提交的参数中取P00001，如果没有则从Cookie中取
        String ticket = request.getParameter(Constants.PASSPORT_TICKET);
        if (ticket == null || ticket.trim().length() == 0) {
            ticket = WebUtil.getCookieValue(request, Constants.PASSPORT_TICKET);
        }
        return ticket;
    }

    /**
     * 获取COOKIE的值
     *
     * @param request HttpServletRequest
     * @param name    Cookie的名称
     * @return CookieValue or null
     */
    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie ck : cookies) {
            if (StringUtils.equalsIgnoreCase(name, ck.getName())) {
                return ck.getValue();
            }
        }
        return null;
    }

    /**
     * 判断字符串是否是一个IP地址
     *
     * @param addr 字符串
     * @return true:IP地址，false：非IP地址
     */
    private static boolean isIPAddr(String addr) {
        if (StringUtils.isEmpty(addr)) {
            return false;
        }
        String[] ips = StringUtils.split(addr, '.');
        if (ips.length != 4) {
            return false;
        }
        try {
            int ipa = Integer.parseInt(ips[0]);
            int ipb = Integer.parseInt(ips[1]);
            int ipc = Integer.parseInt(ips[2]);
            int ipd = Integer.parseInt(ips[3]);
            return ipa >= 0 && ipa <= 255 && ipb >= 0 && ipb <= 255 && ipc >= 0
                    && ipc <= 255 && ipd >= 0 && ipd <= 255;
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * @return 获得本机IP内网地址
     */
    public static String getLocalIP() {
        // 本地IP，如果没有配置外网IP则返回它
        String localip = null;
        // 外网IP
        String netip = null;
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            // 是否找到外网IP
            boolean finded = false;
            while (netInterfaces.hasMoreElements() && !finded) {
                NetworkInterface ni = netInterfaces.nextElement();
                if ("eth0".equals(ni.getName())) {
                    Enumeration<InetAddress> address = ni.getInetAddresses();
                    while (address.hasMoreElements()) {
                        ip = address.nextElement();
                        if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                                // 外网IP
                                && !ip.getHostAddress().contains(":")) {
                            netip = ip.getHostAddress();
                            finded = true;
                            break;
                        } else if (ip.isSiteLocalAddress()
                                && !ip.isLoopbackAddress()
                                // 内网IP
                                && !ip.getHostAddress().contains(":")) {
                            localip = ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[module:localip] [action:RequestUtils] [step:getLocalIP] ", e);
        }
        if (netip != null && !"".equals(netip)) {
            return netip;
        } else {
            return localip;
        }
    }

    /**
     * 转换参数Map.value格式, 并去除其中的空串
     * <p>
     * <pre>
     *     Map< String, String[]({"a","","c"})> --> Map< String, String("a,c")>
     * </pre>
     *
     * @return Map, 类型为: [String, String]
     */
    public static Map<String, String> genMapByRequestParams(Map<String, String[]> requestParams) {
        Map<String, String> params = new HashMap<>();

        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            Set<String> valueSet = new HashSet<>();
            for (String value : values) {
                if (StringUtils.isNotBlank(value)) {
                    valueSet.add(value);
                }
            }
            String valueStr = StringUtils.join(valueSet, ",");
            params.put(name, valueStr);
        }

        return params;
    }

    public static String getTraceId(ServletRequest request, String msgKey) {
        String traceId = request.getParameter(msgKey);
        if (StringUtils.isBlank(traceId) && request instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = ((HttpServletRequest) request);
            traceId = httpServletRequest.getHeader(msgKey);
        }
        if (StringUtils.isBlank(traceId)) {
            traceId = UuidGernerator.getUuid();
        }
        return traceId;
    }

    public static boolean mdcAlreadySet() {
        return StringUtils.isNotBlank(MDC.get("traceId")) && StringUtils.isNotBlank(MDC.get("spanId"));
    }
}
