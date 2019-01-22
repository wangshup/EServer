package com.dd.server.utils;

public interface FileListener {
    void onChange(Iterable<MonitoredFile> changedList);
}
