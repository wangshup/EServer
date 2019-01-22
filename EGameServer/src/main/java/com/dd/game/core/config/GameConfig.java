package com.dd.game.core.config;

public class GameConfig {
    private static GameConfig instance = new GameConfig();
    private GameConfigXmlParser parser;

    private GameConfig() {
    }

    public static GameConfig getInstance() {
        return instance;
    }

    public void init(String path) throws Exception {
        parser = new GameConfigXmlParser(path);
        parser.parse();
    }

    public <T> T getConfig(Class<? extends IConfigParser<T>> clazz) {
        return parser.getConfig(clazz);
    }
}
