package com.dd.gate.utils;

public interface FileListener {
    void onChange(Iterable<MonitoredFile> changedList);
}
