package com.dd.game.utils;

import com.dd.game.core.GameEngine;

public final class Constants {

    /*
     * 服务器配置信息
     */
    public static final int SERVER_ID = GameEngine.ZONE_ID;
    public static final int GLOBAL_USER_TTL = 30 * 24 * 3600;
    public static final String SERVER_ID_STR = Integer.toString(GameEngine.ZONE_ID);
    public static final int SERVER_ID_MAX = 6;
    public static final String LOG_SEPARATOR = "|";
    public static final String LOG_SEPARATOR_REPLACE = "vertical";
    public static final String XML_ELEMENT_NAME = "Data";
    public static final String XML_GROUP_NAME = "group";
    public static final String GAME_CONFIG_PATH = "config";
    public static final String GAME_CONFIG_PARSER_PACKAGE = "com.dd.game.core.config.parser";
    private Constants() {
    }
}
