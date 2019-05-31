package com.dd.game.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public final class CommonUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private static String[] chars = new String[]{"a", "b", "c", "d", "e", "f",//
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",//
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",//
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",//
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",//
            "W", "X", "Y", "Z"};

    private CommonUtils() {
    }

    public static <K, V> Map<K, V> string2map(String content, Class<K> kClass, Class<V> vClass) {
        Map<K, V> ret = new LinkedHashMap<>();
        if (StringUtils.isBlank(content)) {
            return ret;
        }
        String[] entryArray = StringUtils.splitByWholeSeparator(content, "|");
        if (entryArray != null && entryArray.length != 0) {
            for (String entry : entryArray) {
                String[] keyValueArray = StringUtils.splitByWholeSeparator(entry, ";");
                if (keyValueArray.length == 2) {
                    ret.put(convert(kClass, keyValueArray[0]), convert(vClass, keyValueArray[1]));
                }
            }
        }
        return ret;
    }

    /**
     * map转成1;1|2:2类型的串
     *
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> String map2string(Map<K, V> map) {
        if (map == null) {
            return "";
        }
        String seperator = "";
        StringBuffer strBuilder = new StringBuffer();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            strBuilder.append(seperator).append(entry.getKey()).append(";").append(entry.getValue());
            seperator = "|";
        }
        return strBuilder.toString();
    }

    private static <T> T convert(Class<T> clazz, String content) {
        if (clazz.isAssignableFrom(Integer.class)) {
            return clazz.cast(Integer.parseInt(content));
        } else if (clazz.isAssignableFrom(Long.class)) {
            return clazz.cast(Long.parseLong(content));
        } else if (clazz.isAssignableFrom(Short.class)) {
            return clazz.cast(Short.parseShort(content));
        } else if (clazz.isAssignableFrom(Byte.class)) {
            return clazz.cast(Byte.parseByte(content));
        } else if (clazz.isAssignableFrom(Boolean.class)) {
            return clazz.cast(Boolean.parseBoolean(content));
        } else if (clazz.isAssignableFrom(Double.class)) {
            return clazz.cast(Double.parseDouble(content));
        } else if (clazz.isAssignableFrom(Float.class)) {
            return clazz.cast(Float.parseFloat(content));
        } else if (clazz.isAssignableFrom(String.class)) {
            return clazz.cast(content);
        } else {
            throw new RuntimeException("不支持的类型");
        }
    }

    public static <K> List<K> string2list(String content, Class<K> kClass, String separator) {
        List<K> ret = new ArrayList<>();
        if (StringUtils.isBlank(content)) {
            return ret;
        }
        String[] entryArray = StringUtils.splitByWholeSeparator(content, separator);
        for (String entry : entryArray) {
            ret.add(convert(kClass, entry));
        }
        return ret;
    }

    public static <K> Set<K> string2set(String content, Class<K> kClass, String separator) {
        Set<K> set = new HashSet<>();
        if (StringUtils.isBlank(content)) {
            return set;
        }
        String[] entryArray = StringUtils.splitByWholeSeparator(content, separator);
        for (String entry : entryArray) {
            set.add(convert(kClass, entry));
        }
        return set;
    }

    public static <K> String collection2string(Collection<K> collection, String separator) {
        StringBuffer sb = new StringBuffer();
        if (collection == null) {
            return sb.toString();
        }
        String spe = "";
        for (K entry : collection) {
            sb.append(spe).append(entry.toString());
            spe = separator;
        }
        return sb.toString();
    }

    public static boolean isPowerOfTwo(int val) {
        return (val & -val) == val;
    }

    static int ceilingPowerOfTwo(int x) {
        // From Hacker's Delight, Chapter 3, Harry S. Warren Jr.
        return 1 << -Integer.numberOfLeadingZeros(x - 1);
    }

    public static String generateShortUuid() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();
    }

    public final static String getIpAddress(HttpServletRequest request) {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
        String ip = request.getHeader("X-Forwarded-For");

        if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
            if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
                ip = request.getRemoteAddr();
            }
        } else if (ip != null && ip.length() > 15) {
            ip = ip.substring(0, ip.indexOf(","));
        }
        return ip;
    }
}
