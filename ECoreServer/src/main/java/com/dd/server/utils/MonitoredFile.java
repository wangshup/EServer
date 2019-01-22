package com.dd.server.utils;

import java.io.File;

public class MonitoredFile {
    public static final String DEFAULT_GROUP = "def";
    private File file;
    private long lastModified = -1;
    private String group;
    private String parent;

    MonitoredFile(File file) {
        this(file, DEFAULT_GROUP);
    }

    public MonitoredFile(File file, String group) {
        this(file, group, "");

    }

    public MonitoredFile(File file, String group, String parent) {
        this.file = file;
        this.group = group;
        this.parent = parent;
        update();
    }

    public String getGroup() {
        if (group == null || group.isEmpty()) {
            return DEFAULT_GROUP;
        }
        return group;
    }

    public MonitoredFile(String fileName) {
        this(new File(fileName));
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public File getFile() {
        return file;
    }

    public void update() {
        this.lastModified = file.lastModified();
    }

    public boolean isModified() {
        return this.lastModified == -1 || this.lastModified < file.lastModified();
    }
}
