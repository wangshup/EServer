package com.dd.server.extensions;

import com.dd.server.utils.JarLoader;

public class ExtensionSetting {
    public String file;
    public String name;
    public String propertiesFile;
    public ExtensionReloadMode reloadMode;
    public JarLoader jarLoader;
}
