package com.dd.gate.utils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileMonitor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(FileMonitor.class);

    private Map<String, MonitoredFile> monitoredFileMap = new ConcurrentHashMap<>();
    private Set<FileListener> listeners = new HashSet<>();
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private boolean dispatchByGroup = false;

    public FileMonitor(boolean dispatchByGroup) {
        this.dispatchByGroup = dispatchByGroup;
    }

    public FileMonitor(File file, boolean dispatchByGroup, FileListener listener) {
        this.dispatchByGroup = dispatchByGroup;
        this.addFile(file);
        this.addListener(listener);
    }

    public FileMonitor() {
    }

    public void addFile(File file) {
        addFile(file, MonitoredFile.DEFAULT_GROUP);
    }

    public void addFile(File file, String group, String parentFolder) {
        if (!monitoredFileMap.containsKey(file.getPath())) {
            monitoredFileMap.put(file.getPath(), new MonitoredFile(file, group, parentFolder));
        }
    }

    public void addFile(File file, String group) {
        addFile(file, group, "");
    }

    public void addFile(String path) {
        addFile(path, MonitoredFile.DEFAULT_GROUP);
    }

    public void addFile(String path, String group) {
        addFile(new File(path), group);
    }

    public void removeFile(File file) {
        monitoredFileMap.remove(file.getPath(), file);
    }

    public void removeFile(String file) {
        monitoredFileMap.remove(file);
    }

    public void addFolder(String path, String ext, boolean recursive, String group) {
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("provide path not exist or is not a valid directory");
        }
        Collection<File> props = FileUtils.listFiles(dir, new String[]{ext}, recursive);
        for (File f : props) {
            addFile(f, group, path);
        }
    }

    public void addFolder(String path, String ext, boolean recursive) {
        addFolder(path, ext, recursive, MonitoredFile.DEFAULT_GROUP);
    }

    public void addListener(FileListener listener) {
        readWriteLock.writeLock().lock();
        try {
            listeners.add(listener);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void removeListener(FileListener listener) {
        readWriteLock.writeLock().lock();
        try {
            listeners.remove(listener);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private void dispatchEvent(Collection<MonitoredFile> fileList) {
        for (FileListener listener : listeners) {
            try {
                listener.onChange(fileList);
            } catch (Throwable t) {
                logger.error("dispatch file change event fail for file {} on listener {}", fileList.size(), listener, t);
            }
        }
    }

    public Set<String> getMonitoredFiles() {
        return monitoredFileMap.keySet();
    }

    public Collection<MonitoredFile> getMonitored() {
        return monitoredFileMap.values();
    }

    public List<MonitoredFile> getMonitordFileList() {
        return new ArrayList<>(monitoredFileMap.values());
    }

    @Override
    public void run() {
        if (monitoredFileMap.isEmpty()) {
            return;
        }
        try {
            batchDispatch();
        } catch (Throwable t) {
            logger.error("dispatch file change event fail", t);
        }
    }

    private void batchDispatch() {
        if (readWriteLock.readLock().tryLock() == false) {
            return;
        }
        try {
            Map<String, List<MonitoredFile>> changedListMap = null;
            List<MonitoredFile> changedList = null;
            for (MonitoredFile file : monitoredFileMap.values()) {
                if (file.isModified()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("file {} changed", file.getFile().getPath());
                    }
                    if (changedListMap == null) {
                        changedListMap = new HashMap<>();
                        changedList = new ArrayList<>();
                    }
                    List<MonitoredFile> fileList = changedListMap.get(file.getGroup());
                    if (fileList == null) {
                        fileList = new ArrayList<>();
                        changedListMap.put(file.getGroup(), fileList);
                    }
                    fileList.add(file);
                    changedList.add(file);
                    file.update();
                }
            }
            if (changedListMap != null && !changedListMap.isEmpty()) {
                if (dispatchByGroup) {
                    for (Map.Entry<String, List<MonitoredFile>> e : changedListMap.entrySet()) {
                        dispatchEvent(e.getValue());
                    }
                } else {
                    dispatchEvent(changedList);
                }
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public int size() {
        return monitoredFileMap.size();
    }
}
