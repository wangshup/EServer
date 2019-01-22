package com.dd.game.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class CommonUtils {
    private static final Logger logger = LoggerFactory.getLogger(Http.class);

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
        Set<K> set = new HashSet<K>();
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
}
