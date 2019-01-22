package com.dd.game.core.config.tools;

public class XmlCodeGenerator {
    // xml文件目录
    private static final String XML_RESOURCE_PATH = "E:/work/others/EServer/runtime/resource";
    // java文件生成目录
    private static final String OUTPUT_PATH = "E:/work/others/EServer/EGameServer/src/main/java";

    public static void main(String[] args) {
        XmlConfigParser parser = new XmlConfigParser(XML_RESOURCE_PATH, OUTPUT_PATH);
        parser.parse();
    }
}
