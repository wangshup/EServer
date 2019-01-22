package com.dd.game.core.config.tools;

public interface Output {
    public void outputFile(String data, String fileName) throws Exception;

    public void outputFile(String data, String fileName, String strPackageName) throws Exception;
}
