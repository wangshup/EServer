package com.dd.game.core.config;

public interface IConfigParser<T> {

    String getConfigFile();

    void setResourceBaseDir(String resourceBaseDir);

    void parse(Object param);

    void validate() throws Exception;

    T getConfig();
}
