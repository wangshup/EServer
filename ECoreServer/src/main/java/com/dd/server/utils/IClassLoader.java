package com.dd.server.utils;

import com.dd.server.exceptions.BootException;

public interface IClassLoader {
    ClassLoader loadClasses(String[] paramArrayOfString, ClassLoader paramClassLoader) throws BootException;
}