package common.utils;

import common.constant.CommonConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP 工具类
 */
public class IpUtil {

    private static final Logger logger = LoggerFactory.getLogger(IpUtil.class);


    /**
     * 获取客户端真实 IP 地址
     * <p>
     * 使用 Nginx 等反向代理软件，不能直接通过 request.getRemoteAddr() 获取 IP 地址
     * 如果使用了多级代理，X-Forwarded-For 的值可能包含多个 IP，
     * 此时取第一个非 unknown 的有效 IP 即为客户端真实 IP
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = null;
        try {
            // 常见的代理头顺序
            String[] headers = {
                    "x-forwarded-for",
                    "Proxy-Client-IP",
                    "WL-Proxy-Client-IP",
                    "HTTP_CLIENT_IP",
                    "HTTP_X_FORWARDED_FOR"
            };
            for (String header : headers) {
                ip = request.getHeader(header);
                if (StringUtils.isNotBlank(ip) && !CommonConstant.UNKNOWN.equalsIgnoreCase(ip)) {
                    break;
                }
            }

            // 如果没有从头里取到，直接取 remoteAddr
            if (StringUtils.isBlank(ip) || CommonConstant.UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
            logger.error("IPUtils ERROR ", e);
        }

        // 多级代理情况，取第一个有效 IP
        if (StringUtils.isNotBlank(ip) && ip.contains(",")) {
            for (String ipAddress : ip.split(",")) {
                ipAddress = ipAddress.trim();
                if (isValidIpAddress(ipAddress)) {
                    return ipAddress;
                }
            }
        }

        // 处理回环地址
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return getServerIp();
        }

        return ip;
    }

    /**
     * 判断是否是有效的 IP 地址（支持 IPv4 和 IPv6）
     *
     * @param ipAddress 输入 IP
     * @return true=有效，false=无效
     */
    public static boolean isValidIpAddress(String ipAddress) {
        if (StringUtils.isBlank(ipAddress)) {
            return false;
        }
        try {
            InetAddress.getByName(ipAddress);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    /**
     * 获取本机服务器 IP 地址
     *
     * @return IP 字符串，获取失败返回空串
     */
    public static String getServerIp() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            logger.error("获取服务器 IP 地址失败", e);
        }
        return "";
    }

    /**
     * 判断是否为 IPv4 地址
     */
    public static boolean isIPv4(String ipAddress) {
        try {
            InetAddress inet = InetAddress.getByName(ipAddress);
            return inet.getHostAddress().equals(ipAddress) && inet instanceof java.net.Inet4Address;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否为 IPv6 地址
     */
    public static boolean isIPv6(String ipAddress) {
        try {
            InetAddress inet = InetAddress.getByName(ipAddress);
            return inet.getHostAddress().equals(ipAddress) && inet instanceof java.net.Inet6Address;
        } catch (Exception e) {
            return false;
        }
    }
}
