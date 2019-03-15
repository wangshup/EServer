package com.dd.server.zk;

public interface ZkConstants {

    enum ZKBasePath {
        LINK("/link"),
        GS("/gs"),
        WEB("/web"),
        CONFIG("/config");

        public final String path;

        ZKBasePath(String path) {
            this.path = path;
        }
    }

}
