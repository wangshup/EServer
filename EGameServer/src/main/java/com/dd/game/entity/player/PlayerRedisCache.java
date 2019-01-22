package com.dd.game.entity.player;

import java.util.Map;

public class PlayerRedisCache {
    public static final String KEY_NAME = "name";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_COUNTRY = "country";
    public static final String KEY_LOGINTIME = "loginTime";

    private long id;
    private Map<String, String> data;

    public PlayerRedisCache(long id, Map<String, String> data) {
        this.id = id;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return data.getOrDefault(KEY_NAME, "");
    }

    public String getCountry() {
        return data.getOrDefault(KEY_COUNTRY, "");
    }

    public int getLevel() {
        return Integer.parseInt(data.getOrDefault(KEY_LEVEL, "1"));
    }
}
