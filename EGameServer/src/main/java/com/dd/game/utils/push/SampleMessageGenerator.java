package com.dd.game.utils.push;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class SampleMessageGenerator {

    private SampleMessageGenerator() {
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static enum Platform {

        APNS, // Apple Push Notification Service
        APNS_SANDBOX, // Sandbox version of Apple Push Notification Service
        ADM, // Amazon Device Messaging
        GCM, // Google Cloud Messaging
        BAIDU, // Baidu CloudMessaging Service
        WNS, // Windows Notification Service
        MPNS;// Microsoft Push Notificaion Service

        public static Platform getValue(String pushWay) {
            if (pushWay.toUpperCase().equals("GOOGLE")) {
                return Platform.GCM;
            } else if (pushWay.toUpperCase().equals("AMAZON")) {
                return Platform.ADM;
            } else if (pushWay.toUpperCase().equals("IOS")) {
                return Platform.APNS;
            } else if (pushWay.toUpperCase().equals("IOSTEST")) {
                return Platform.APNS_SANDBOX;
            } else if (pushWay.toUpperCase().equals("BAIDU")) {
                return Platform.BAIDU;
            }
            return null;
        }
    }

    public static String jsonify(Object message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw (RuntimeException) e;
        }
    }

    private static Map<String, Object> getData(String msg) {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("message", msg);
        return payload;
    }

    public static String getSampleAppleMessage(String msg) {
        Map<String, Object> appleMessageMap = new HashMap<String, Object>();
        Map<String, Object> appMessageMap = new HashMap<String, Object>();
        appMessageMap.put("alert", msg);
        appMessageMap.put("badge", 1);// iOS接到推送以后APP图标上的数字
        appMessageMap.put("sound", "default");
        appleMessageMap.put("aps", appMessageMap);
        return jsonify(appleMessageMap);
    }

    // wangzongsheng 添加
    public static String getSampleAppleMessage(String msg, int messageType, JSONObject pushData) {
        Map<String, Object> appleMessageMap = new HashMap<String, Object>();
        Map<String, Object> appMessageMap = new HashMap<String, Object>();
        appMessageMap.put("alert", msg);
        appMessageMap.put("messageType", messageType);
        appMessageMap.put("badge", 1);// iOS接到推送以后APP图标上的数字
        int pushtype = 0;
        if (pushData != null) {
            if (pushData.containsKey("pushType")) {
                pushtype = Integer.parseInt(pushData.getString("pushType"));
            }
        }
        appMessageMap.put("sound", getIOSound(pushtype));
        appleMessageMap.put("aps", appMessageMap);
        return jsonify(appleMessageMap);
    }

    public static String getSampleKindleMessage(String msg) {
        Map<String, Object> kindleMessageMap = new HashMap<String, Object>();
        kindleMessageMap.put("data", getData(msg));
        // kindleMessageMap.put("consolidationKey",
        // "Welcome");//分组合并,由于我们自己有处理逻辑,这里不需要
        kindleMessageMap.put("expiresAfter", 86400);
        return jsonify(kindleMessageMap);
    }

    // wangzongsheng 添加
    public static String getSampleKindleMessage(String msg, int messageType) {
        Map<String, Object> kindleMessageMap = new HashMap<String, Object>();
        Map<String, Object> payload = getData(msg);
        payload.put("messageType", messageType);
        kindleMessageMap.put("data", payload);
        kindleMessageMap.put("messageType", messageType);
        // kindleMessageMap.put("consolidationKey",
        // "Welcome");//分组合并,由于我们自己有处理逻辑,这里不需要
        kindleMessageMap.put("expiresAfter", 86400);
        return jsonify(kindleMessageMap);
    }

    public static String getSampleAndroidMessage(String msg) {
        Map<String, Object> androidMessageMap = new HashMap<String, Object>();
        // androidMessageMap.put("collapse_key",
        // "Welcome");//分组合并,由于我们自己有处理逻辑,这里不需要
        androidMessageMap.put("data", getData(msg));
        androidMessageMap.put("delay_while_idle", true);
        androidMessageMap.put("time_to_live", 125);
        androidMessageMap.put("dry_run", false);
        return jsonify(androidMessageMap);
    }

    // wangzongsheng 添加
    public static String getSampleAndroidMessage(String msg, int messageType, JSONObject pushData) {
        Map<String, Object> androidMessageMap = new HashMap<String, Object>();
        // androidMessageMap.put("collapse_key",
        // "Welcome");//分组合并,由于我们自己有处理逻辑,这里不需要
        Map<String, Object> payload = getData(msg);
        payload.put("messageType", messageType);
        if (pushData != null) {
            if (pushData.containsKey("pushType")) {
                payload.put("pushType", pushData.getString("pushType"));
            }
        }
        androidMessageMap.put("data", payload);
        androidMessageMap.put("messageType", messageType);
        androidMessageMap.put("delay_while_idle", true);
        androidMessageMap.put("time_to_live", 125);
        androidMessageMap.put("dry_run", false);
        return jsonify(androidMessageMap);
    }

    public static String getSampleBaiduMessage(String msg) {
        Map<String, Object> baiduMessageMap = new HashMap<String, Object>();
        // baiduMessageMap.put("title", "");
        baiduMessageMap.put("message", msg);// 客户端目前只解析了message
        return jsonify(baiduMessageMap);
    }

    // wangzongsheng 添加
    public static String getSampleBaiduMessage(String msg, int messageType) {
        Map<String, Object> baiduMessageMap = new HashMap<String, Object>();
        // baiduMessageMap.put("title", "");
        baiduMessageMap.put("message", msg);// 客户端目前只解析了message
        baiduMessageMap.put("messageType", messageType);
        return jsonify(baiduMessageMap);
    }

    private static String getIOSound(int messageType) {
        String sound = "default";
        switch (messageType) {
        case 1:
            sound = "cs_push_building_1.caf";
            break;
        case 2:
            sound = "cs_push_dark_knight_2.caf";
            break;
        case 3:
            sound = "cs_push_chat.caf";
            break;
        // case 4:
        // sound = "cs_push_building_1.aac";
        // break;
        case 5:
            sound = "cs_push_chat.caf";
            break;
        case 6:
            sound = "cs_push_reinforcement_6.caf";
            break;
        case 7:
            sound = "cs_push_resouce_complet_7.caf";
            break;
        case 8:
            sound = "cs_push_chat.caf";
            break;
        case 9:
            sound = " cs_push_harbor_9.caf";
            break;
        case 10:
            sound = "cs_push_under_attack_10.caf";
            break;
        case 11:
            sound = "cs_push_troops_return_11.caf";
            break;
        case 12:
            sound = "cs_push_resouce_full.caf";
            break;
        case 14:
            sound = "cs_push_resouce_full.caf";
            break;
        case 15:
            sound = "cs_push_country_sys_15.caf";
            break;
        default:
            break;
        }
        return sound;
    }
}
