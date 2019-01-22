package com.dd.server.utils;

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

import com.dd.server.exceptions.BootException;

public final class JarFilesUtil {
    private static final String JAR_EXT = ".jar";

    public static List<String> scanFolderForJarFiles(String path) throws BootException {
        ArrayList<String> jarFiles = new ArrayList<>();
        File theFolder = new File(path);
        if (!theFolder.isDirectory()) {
            throw new BootException("The provided path is not a directory: " + path);
        }
        for (File fileEntry : theFolder.listFiles()) {
            if (fileEntry.isFile()) {
                String fileName = fileEntry.getName();
                if (hasExtension(fileName, JAR_EXT)) {
                    jarFiles.add(path + "/" + fileName);
                }
            }
        }
        return jarFiles;
    }

    public static List<String> scanClassNamesInJarFile(String jarFilePath) throws BootException {
        ArrayList<String> classNames = new ArrayList<>();
        JarFile jarFile = null;
        ;
        try {
            jarFile = new JarFile(jarFilePath);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if ((!entry.isDirectory()) && (entry.getName().endsWith(".class"))) {
                    String fqcName = entry.getName().replace('/', '.');
                    classNames.add(fqcName.substring(0, fqcName.length() - 6));
                }
            }
            return classNames;
        } catch (IOException e) {
        } finally {
            try {
                if (jarFile != null)
                    jarFile.close();
            } catch (Exception ee) {
            }
        }
        throw new BootException("Cannot access jar file: " + jarFilePath);
    }

    private static boolean hasExtension(String fileName, String expectedExtension) {
        boolean isOk = false;
        if (fileName == null) {
            return isOk;
        }
        int extPos = fileName.lastIndexOf('.');
        if (extPos > 0) {
            String fileExt = fileName.substring(extPos);
            if (expectedExtension.equalsIgnoreCase(fileExt)) {
                isOk = true;
            }
        }
        return isOk;
    }

    public static List<Class<?>> getClassList(String pkgName, boolean isRecursive,
            Class<? extends Annotation> annotation, ClassLoader cl) {
        List<Class<?>> classList = new ArrayList<>();
        ClassLoader loader = cl;// Thread.currentThread().getContextClassLoader();
        try {
            // 按文件的形式去查找
            String strFile = pkgName.replaceAll("\\.", "/");
            Enumeration<URL> urls = loader.getResources(strFile);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol();
                    String pkgPath = url.getPath();
                    if ("file".equals(protocol)) {
                        // 本地自己可见的代码
                        findClassName(classList, pkgName, pkgPath, isRecursive, annotation, cl);
                    } else if ("jar".equals(protocol)) {
                        // 引用第三方jar的代码
                        findClassName(classList, pkgName, url, isRecursive, annotation, cl);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classList;
    }

    public static void findClassName(List<Class<?>> clazzList, String pkgName, String pkgPath, boolean isRecursive,
            Class<? extends Annotation> annotation, ClassLoader cl) {
        if (clazzList == null) {
            return;
        }
        File[] files = filterClassFiles(pkgPath);// 过滤出.class文件及文件夹
        if (files != null) {
            for (File f : files) {
                String fileName = f.getName();
                if (f.isFile()) {
                    // .class 文件的情况
                    String clazzName = getClassName(pkgName, fileName);
                    addClassName(clazzList, clazzName, annotation, cl);
                } else {
                    // 文件夹的情况
                    if (isRecursive) {
                        // 需要继续查找该文件夹/包名下的类
                        String subPkgName = pkgName + "." + fileName;
                        String subPkgPath = pkgPath + "/" + fileName;
                        findClassName(clazzList, subPkgName, subPkgPath, true, annotation, cl);
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
            Class<? extends Annotation> annotation, ClassLoader cl) throws IOException {
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        JarFile jarFile = jarURLConnection.getJarFile();
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            // 类似：sun/security/internal/interfaces/TlsMasterSecret.class
            String jarEntryName = jarEntry.getName();
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
                    addClassName(clazzList, clazzName, annotation, cl);
                } else if (isRecursive && prefix.startsWith(pkgName)) {
                    // 遍历子包名：子类
                    addClassName(clazzList, clazzName, annotation, cl);
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

    private static void addClassName(List<Class<?>> clazzList, String clazzName, Class<? extends Annotation> annotation,
            ClassLoader cl) {
        if (clazzList != null && clazzName != null) {
            Class<?> clazz = null;
            try {
                if (clazzName.endsWith(".class")) {
                    clazzName = clazzName.substring(0, clazzName.lastIndexOf("."));
                }
                clazz = Class.forName(clazzName, true, cl);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (clazz != null) {
                if (annotation == null) {
                    clazzList.add(clazz);
                } else if (clazz.isAnnotationPresent(annotation)) {
                    clazzList.add(clazz);
                }
            }
        }
    }
}