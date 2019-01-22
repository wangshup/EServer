package com.dd.server.utils;

import java.net.URL;
import java.net.URLClassLoader;

public class ServerClassLoader extends URLClassLoader {

    public ServerClassLoader(URL[] urls, ClassLoader parent) {
        super(urls);
    }

    @Override
    public String toString() {
        return "ServerClassLoader";
    }
}
