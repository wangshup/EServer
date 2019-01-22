package com.dd.server.utils;

import java.io.*;

public class AsciiArt {
    public static String getAsciiMessage(String messageName) {
        String filePath = "config/ascii/" + messageName + ".txt";
        String asciiMessage = readFileToString(filePath);
        return asciiMessage;
    }

    public static String readFileToString(String fileName) {
        String encoding = "ISO-8859-1";
        File file = new File(fileName);
        Long fileLength = Long.valueOf(file.length());
        byte[] fileContent = new byte[fileLength.intValue()];
        try (FileInputStream in = new FileInputStream(file)) {
            in.read(fileContent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(fileContent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
        }
        return "";
    }
}