package com.dd.server.utils;

public class StringBuilderHelper {
    private static ThreadLocal<StringBuilder> stringBuilderLocal = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(1024);
        }
    };

    public static StringBuilder getBuilder() {
        StringBuilder sb = stringBuilderLocal.get();
        sb.setLength(0);
        return sb;
    }
}
