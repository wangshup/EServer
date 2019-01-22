package com.dd.server.utils;

import com.dd.server.exceptions.BootException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class JarLoader implements IClassLoader {
    private static final Logger logger = LoggerFactory.getLogger(JarLoader.class);
    private Map<String, File> copiedJarMap = new ConcurrentHashMap<>();

    private boolean copyExtensionJar = true;
    private boolean overrideExistFile = false;
    private File copyTargetPath = null;
    private String tmpFileBase = "/tmp/jar_cache/";

    public JarLoader() {
        this(true);
    }

    public JarLoader(boolean copy) {
        this.copyExtensionJar = copy;
    }

    public boolean isOverrideExistFile() {
        return overrideExistFile;
    }

    public void setOverrideExistFile(boolean overrideExistFile) {
        this.overrideExistFile = overrideExistFile;
    }

    public boolean isCopyExtensionJar() {
        return copyExtensionJar;
    }

    public void setCopyExtensionJar(boolean copyExtensionJar) {
        this.copyExtensionJar = copyExtensionJar;
    }

    public String getTmpFileBase() {
        return tmpFileBase;
    }

    public void setTmpFileBase(String tmpFileBase) {
        this.tmpFileBase = tmpFileBase;
    }

    protected void createTmpDir() {
        if (this.copyExtensionJar && copyTargetPath == null) {
            String tmpDirName = Encoder.MD5(String.valueOf(System.currentTimeMillis())).substring(0, 8);
            copyTargetPath = new File(tmpFileBase, tmpDirName);
            if (!copyTargetPath.exists()) {
                copyTargetPath.mkdirs();
            }
        }
    }

    public ClassLoader loadClasses(String[] paths, ClassLoader parentClassLoader) throws BootException {
        createTmpDir();
        List<URL> locations = new ArrayList<>();
        for (int i = 0; i < paths.length; i++) {
            String classPath = paths[i];
            List<String> jarFiles = JarFilesUtil.scanFolderForJarFiles(classPath);
            for (String jarFilePath : jarFiles) {
                try {
                    addPathURL(locations, jarFilePath);
                } catch (MalformedURLException var13) {
                    throw new BootException("Malformed URL: " + jarFilePath);
                }
            }
            if (locations.size() == 0) {
                throw new BootException("Unexpected: no jars were located!");
            }
        }

        URL[] urls = new URL[locations.size()];
        locations.toArray(urls);
        ClassLoader classLoader = AccessController.doPrivileged((PrivilegedAction<URLClassLoader>) () -> new URLClassLoader(urls, parentClassLoader));
        if (logger.isDebugEnabled()) {
            for (URL url : urls) {
                logger.debug("{} be loaded by {}", url, classLoader.toString());
            }
        }
        return classLoader;
    }

    public void clean() {
        if (copyExtensionJar) {
            FileUtils.deleteQuietly(copyTargetPath);
            copyTargetPath = null;
            copiedJarMap.clear();
        }
    }

    private void addPathURL(List<URL> locations, String jarFilePath) throws MalformedURLException {
        File e = new File(jarFilePath);
        if (!copyExtensionJar) {
            locations.add(e.toURI().toURL());
            return;
        }
        String fileName = copyTargetPath.getPath() + File.separator + jarFilePath;
        File destFile = new File(fileName);
        try {
            if (!destFile.exists() || isOverrideExistFile()) {
                FileUtils.copyFile(e, destFile, true);
            }
            locations.add(destFile.toURI().toURL());
            copiedJarMap.put(jarFilePath, destFile);
        } catch (IOException e1) {
            locations.add(e.toURI().toURL());
        }
    }
}