package com.dd.server.services;

import com.dd.server.Server;
import com.dd.server.annotation.ServiceStart;
import com.dd.server.entities.GameZone;
import com.dd.server.entities.IUser;
import com.dd.server.entities.IZone;
import com.dd.server.exceptions.*;
import com.dd.server.extensions.ExtensionReloadMode;
import com.dd.server.extensions.ExtensionSetting;
import com.dd.server.extensions.ExtensionState;
import com.dd.server.extensions.IExtension;
import com.dd.server.session.ISession;
import com.dd.server.utils.FileMonitor;
import com.dd.server.utils.JarLoader;
import com.dd.server.utils.MonitoredFile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ServiceStart
public class ExtensionService extends AbstractService {
    private static final Logger logger = LoggerFactory.getLogger(ExtensionService.class);
    private JarLoader jarLoader = new JarLoader();
    private Map<IZone, IExtension> extensions = new ConcurrentHashMap<>();
    private Map<String, IZone> zones = new ConcurrentHashMap<>();
    private FileMonitor jarMonitor = new FileMonitor(true);

    public ExtensionService() {
        super(ServiceType.EXTENSION);
        jarMonitor.addListener((changedList) -> {
            MonitoredFile file = changedList.iterator().next();
            logger.info("jar file changed for {} ", file.getGroup());
            reloadExtension(file.getGroup());
        });
    }

    public FileMonitor getJarMonitor() {
        return jarMonitor;
    }

    public void reloadExtension(String zoneName) {
        if (StringUtils.isBlank(zoneName)) {
            return;
        }
        for (IZone zone : extensions.keySet()) {
            if (StringUtils.equals(zoneName, zone.getName())) {
                reloadExtension(zone.getExtension());
            }
        }
    }

    public void reloadAllExtensions() {
        for (IExtension extension : extensions.values()) {
            reloadExtension(extension);
        }
    }

    public void reloadExtension(IExtension extension) {
        if (extension.getReloadMode() == ExtensionReloadMode.DISABLE) {
            logger.warn("extension {} disabled reload", extension.getName());
            return;
        }
        ExtensionSetting setting = new ExtensionSetting();
        setting.file = extension.getExtensionFileName();
        setting.name = extension.getName();
        setting.propertiesFile = extension.getPropertiesFileName();
        setting.jarLoader = new JarLoader();
        try {
            logger.info("start reload extension {}", extension.getName());
            try {
                extension.setState(ExtensionState.DESTROYING);
                extension.destroy();
            } finally {
                extension.setState(ExtensionState.DESTROYED);
            }
            try {
                createExtension(setting, extension.getParentZone());
                // 删除掉拷贝的jar
                jarLoader.clean();
                jarLoader = setting.jarLoader;
                logger.info("end reload extension {}", extension.getName());
            } catch (Exception ex) {
                // 使用新的代码加载失败，尝试使用旧的重新加载一次
                setting.jarLoader = null;
                createExtension(setting, extension.getParentZone());
            }

        } catch (Throwable e) {
            logger.error(String.format("reload extension %s error", extension.getName()), e);
        }
    }

    public IZone createExtension(ExtensionSetting setting) throws ExtensionException {
        return createExtension(setting, null);
    }

    public IZone createExtension(ExtensionSetting setting, IZone parentZone) throws ExtensionException {
        if (setting.file == null || setting.file.isEmpty()) {
            throw new ExtensionException("missing extension main class");
        }
        IExtension serverExtension = this.createJavaExtension(setting);
        serverExtension.setExtensionFileName(setting.file);
        serverExtension.setName(setting.name);
        serverExtension.setReloadMode(setting.reloadMode);

        if (StringUtils.isNotBlank(setting.propertiesFile) && (setting.propertiesFile.startsWith("../") || setting.propertiesFile.startsWith("/"))) {
            throw new ExtensionException("Illegal path for Extension property file. File path outside the extensions/ folder is not valid: " + setting.propertiesFile);
        }
        try {
            serverExtension.setPropertiesFileName(setting.propertiesFile);
            IZone zone;
            if (parentZone == null) {
                // create new zone for the extension
                zone = new GameZone(setting.name, serverExtension);
                zones.put(zone.getName(), zone);
            } else {
                // zone exists, this will happen when reload extension
                zone = parentZone;
            }
            serverExtension.setZone(zone);
            serverExtension.setState(ExtensionState.INITIALIZING);
            serverExtension.init();
            serverExtension.setState(ExtensionState.RUNNING);
            extensions.put(zone, serverExtension);
            if (parentZone != null) {
                // change the zone extension when reload extension success
                parentZone.setExtension(serverExtension);
            }
            zone.signalExtensionReady();
            return zone;
        } catch (Exception ex) {
            throw new ExtensionException("init extension fail", ex);
        }
    }

    private IExtension createJavaExtension(ExtensionSetting settings) throws ExtensionException {
        try {
            String extensionPath = "extensions/" + settings.name;
            if (settings.reloadMode == ExtensionReloadMode.AUTO) {
                jarMonitor.addFolder(extensionPath, "jar", false, settings.name);
            }
            ClassLoader extensionClassLoader;
            if (settings.jarLoader != null) {
                extensionClassLoader = settings.jarLoader.loadClasses(new String[]{extensionPath}, getClass().getClassLoader());
            } else {
                extensionClassLoader = jarLoader.loadClasses(new String[]{extensionPath}, getClass().getClassLoader());
            }
            Class<?> extensionClass = extensionClassLoader.loadClass(settings.file);
            if (!IExtension.class.isAssignableFrom(extensionClass)) {
                throw new ExtensionException("Extension does not implement ISFSExtension interface: " + settings.name);
            }
            return (IExtension) extensionClass.newInstance();
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException ex) {
            throw new ExtensionException("create extension fail for class : " + settings.file, ex);
        } catch (BootException b) {
            throw new ExtensionException(b);
        }
    }

    public void destroyAllExtension() {
        for (IExtension extension : extensions.values()) {
            try {
                extension.destroy();
            } catch (Exception ex) {
                logger.error("destroy extension {} fail", extension.getName(), ex);
            }
        }
    }

    @Override
    protected void initService() throws ServiceInitException {
        try {
            initExtensions();
        } catch (ExtensionException e) {
            throw new ServiceInitException(e.getMessage());
        }
    }

    @Override
    protected void startService() throws ServiceStartException {
    }

    @Override
    protected void stopService() throws ServiceStopException {
        destroyAllExtension();
    }

    protected void initExtensions() throws ExtensionException {
        List<String> zoneList = ZoneUtil.getZoneNames();
        for (String zone : zoneList) {
            initZone(zone);
        }
    }

    private void initZone(String zone) throws ExtensionException {
        Server server = Server.getInstance();
        ExtensionSetting setting = new ExtensionSetting();
        setting.name = zone;
        setting.file = server.getConfiguration().getString("extension.class");
        setting.propertiesFile = server.getConfiguration().getString("extension.propertyFileName");
        setting.reloadMode = ExtensionReloadMode.valueOf(server.getConfiguration().getInt("extension.reload_mode", 0));
        createExtension(setting);
    }

    public Collection<IZone> getZones() {
        return Collections.unmodifiableCollection(zones.values());
    }

    public IZone getZone(String name) {
        return zones.get(name);
    }

    public IZone getZone(int zoneId) {
        for (IZone zone : zones.values()) {
            if (zone.getZoneId() == zoneId) return zone;
        }
        return null;
    }

    public IUser getUserBySession(ISession session) {
        for (IZone zone : zones.values()) {
            if (zone.containsSession(session)) {
                return zone.getUserBySession(session);
            }
        }
        return null;
    }

    public void checkTimeoutUsers(boolean closeChannel) {
        for (IZone zone : zones.values()) {
            zone.checkTimeoutUsers(closeChannel);
        }
    }

    static class ZoneUtil {
        private static final FileFilter zoneFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        };

        public static List<String> getZoneNames() {
            List<String> zoneNames = new ArrayList<>();
            File zonePath = new File("extensions");
            if (zonePath.exists()) {
                File[] files = zonePath.listFiles(zoneFilter);
                for (File f : files) {
                    zoneNames.add(f.getName());
                }
            }
            return zoneNames;
        }
    }
}
