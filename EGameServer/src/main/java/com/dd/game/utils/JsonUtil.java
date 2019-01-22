package com.dd.game.utils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonUtil() {
    }

    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error(String.format("Json 序列化出错 %s ", object), e);
        }
        return null;
    }

    public static Object toObject(String json, Class<?> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            logger.error(String.format("Json 反序列化出错 %s, %s", json, clazz), e);
        }
        return null;
    }

    public static <K, V> String map2String(Map<K, V> map) {
        JSONObject jSObject = new JSONObject();
        for (Map.Entry e : map.entrySet()) {
            jSObject.put(String.valueOf(e.getKey()), e.getValue());
        }
        return jSObject.toString();
    }

    public static <K, V> Map<K, V> string2Map(Class<K> kClass, Class<V> vClass, String value) {
        Map<K, V> map = new HashMap<>();
        if (value != null && value.trim().length() > 0) {
            JSONObject json = JSONObject.parseObject(value);
            Iterator<?> iter = json.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                map.put(convert(kClass, key), convert(vClass, json.getString(key)));
            }
        }
        return map;
    }

    public static boolean containIgnoreCase(JSONObject jsonObject, String key) {
        for (Object keyObj : jsonObject.keySet()) {
            if (key.compareToIgnoreCase(String.valueOf(keyObj)) == 0) {
                return true;
            }
        }
        return false;
    }

    private static <T> T convert(Class<T> clazz, String str) {
        if (clazz.isAssignableFrom(Long.class)) {
            return clazz.cast(Long.parseLong(str));
        } else if (clazz.isAssignableFrom(Integer.class)) {
            return clazz.cast(Integer.parseInt(str));
        } else if (clazz.isAssignableFrom(Short.class)) {
            return clazz.cast(Short.parseShort(str));
        } else if (clazz.isAssignableFrom(Byte.class)) {
            return clazz.cast(Byte.parseByte(str));
        } else if (clazz.isAssignableFrom(String.class)) {
            return clazz.cast(str);
        } else {
            throw new RuntimeException("convert error");
        }
    }

    public static void main(String[] args) {
        //Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        //map.put(1, 13);
        //map.put(2, 432);
        //map.put(3, 990);
        //String str = map2String(map);
        //System.out.println(str);
        //Map<Integer, Integer> converMap = string2Map(Integer.class, Integer.class, "");
        //System.out.println(converMap.size());
    }
}
