package com.dd.game.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.game.core.GameEngine;

/**
 * 用于获取指定包名下的所有类名.<br/>
 * 并可设置是否遍历该包名下的子包的类名.<br/>
 * 并可通过Annotation(内注)来过滤，避免一些内部类的干扰.<br/>
 */
public class ClassUtil {
    private static final Logger logger = LoggerFactory.getLogger(ClassUtil.class);

    private ClassUtil() {
    }

    public static List<Class<?>> getClassList(String pkgName, boolean isRecursive,
            Class<? extends Annotation> annotation) {
        return getClassList(pkgName, isRecursive, annotation, Thread.currentThread().getContextClassLoader());
    }

    public static List<Class<?>> getClassList(String pkgName, boolean isRecursive,
            Class<? extends Annotation> annotation, ClassLoader cl) {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        try {
            // 按文件的形式去查找
            String strFile = pkgName.replaceAll("\\.", "/");
            Enumeration<URL> urls = cl.getResources(strFile);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol();
                    String pkgPath = url.getPath();
                    logger.debug("protocol:" + protocol + " path:" + pkgPath);
                    if ("file".equals(protocol)) {
                        // 本地自己可见的代码
                        findClassName(classList, pkgName, pkgPath, isRecursive, annotation);
                    } else if ("jar".equals(protocol)) {
                        // 引用第三方jar的代码
                        findClassName(classList, pkgName, url, isRecursive, annotation);
                    }
                }
            }
        } catch (IOException e) {
            logger.error(String.format("Scan Class error, pkgName %s, annotation %s, recursive %s", pkgName, annotation,
                    isRecursive), e);
        }
        return classList;
    }

    public static void findClassName(List<Class<?>> clazzList, String pkgName, String pkgPath, boolean isRecursive,
            Class<? extends Annotation> annotation) {
        if (clazzList == null) {
            return;
        }
        File[] files = filterClassFiles(pkgPath);// 过滤出.class文件及文件夹
        logger.debug("files:" + ((files == null) ? "null" : "length=" + files.length));
        if (files != null) {
            for (File f : files) {
                String fileName = f.getName();
                if (f.isFile()) {
                    // .class 文件的情况
                    String clazzName = getClassName(pkgName, fileName);
                    addClassName(clazzList, clazzName, annotation);
                } else {
                    // 文件夹的情况
                    if (isRecursive) {
                        // 需要继续查找该文件夹/包名下的类
                        String subPkgName = pkgName + "." + fileName;
                        String subPkgPath = pkgPath + "/" + fileName;
                        findClassName(clazzList, subPkgName, subPkgPath, true, annotation);
                    }
                }
            }
        }
    }

    /**
     * 第三方Jar类库的引用。<br/>
     * 
     * @throws IOException
     */
    public static void findClassName(List<Class<?>> clazzList, String pkgName, URL url, boolean isRecursive,
            Class<? extends Annotation> annotation) throws IOException {
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        JarFile jarFile = jarURLConnection.getJarFile();
        logger.debug("jarFile:" + jarFile.getName());
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarEntryName = jarEntry.getName(); // 类似：sun/security/internal/interfaces/TlsMasterSecret.class
            String clazzName = jarEntryName.replace("/", ".");
            int endIndex = clazzName.lastIndexOf(".");
            String prefix = null;
            if (endIndex > 0) {
                String prefix_name = clazzName.substring(0, endIndex);
                endIndex = prefix_name.lastIndexOf(".");
                if (endIndex > 0) {
                    prefix = prefix_name.substring(0, endIndex);
                }
            }
            if (prefix != null && jarEntryName.endsWith(".class")) {
                if (prefix.equals(pkgName)) {
                    logger.debug("jar entryName:" + jarEntryName);
                    addClassName(clazzList, clazzName, annotation);
                } else if (isRecursive && prefix.startsWith(pkgName)) {
                    // 遍历子包名：子类
                    logger.debug("jar entryName:" + jarEntryName + " isRecursive:" + isRecursive);
                    addClassName(clazzList, clazzName, annotation);
                }
            }
        }
    }

    private static File[] filterClassFiles(String pkgPath) {
        if (pkgPath == null) {
            return null;
        }
        // 接收 .class 文件 或 类文件夹
        return new File(pkgPath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
            }
        });
    }

    private static String getClassName(String pkgName, String fileName) {
        int endIndex = fileName.lastIndexOf(".");
        String clazz = null;
        if (endIndex >= 0) {
            clazz = fileName.substring(0, endIndex);
        }
        String clazzName = null;
        if (clazz != null) {
            clazzName = pkgName + "." + clazz;
        }
        return clazzName;
    }

    private static void addClassName(List<Class<?>> clazzList, String clazzName,
            Class<? extends Annotation> annotation) {
        if (clazzList != null && clazzName != null) {
            if (clazzName.endsWith(".class")) {
                clazzName = clazzName.substring(0, clazzName.length() - 6);
            }
            Class<?> clazz = null;
            ClassLoader clazzLoader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(GameEngine.class.getClassLoader());
                clazz = Class.forName(clazzName);
            } catch (ClassNotFoundException e) {
            } finally {
                Thread.currentThread().setContextClassLoader(clazzLoader);
            }
            if (clazz != null) {
                if (annotation == null) {
                    clazzList.add(clazz);
                    logger.debug("add:" + clazz);
                } else if (clazz.isAnnotationPresent(annotation)) {
                    clazzList.add(clazz);
                    logger.debug("add annotation:" + clazz);
                }
            }
        }
    }
}