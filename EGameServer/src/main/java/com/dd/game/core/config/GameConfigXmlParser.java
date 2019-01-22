package com.dd.game.core.config;

import com.dd.game.core.ThreadPoolManager;
import com.dd.game.utils.ClassUtil;
import com.dd.server.utils.FileMonitor;
import com.dd.server.utils.MonitoredFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.dd.game.utils.Constants.GAME_CONFIG_PARSER_PACKAGE;

public class GameConfigXmlParser {
    private static final Logger logger = LoggerFactory.getLogger(GameConfigXmlParser.class);
    private final String configBaseDir;
    private final FileMonitor configMonitor = new FileMonitor(true);
    private Map<String, Class<?>> parserClass = new LinkedHashMap<>();
    private Exception parseError;
    private Map<Class<?>, Object> configs = new ConcurrentHashMap<>();

    public GameConfigXmlParser(String configBaseDir) {
        this.configBaseDir = configBaseDir;
        configMonitor.addListener(changedList -> {
            for (MonitoredFile file : changedList) {
                String filePath = file.getFile().getPath();
                try {
                    logger.info("config file {} changed", filePath);
                    Class<?> clazz = parserClass.get(filePath);
                    if (clazz != null) {
                        IConfigParser<?> parser = (IConfigParser<?>) clazz.newInstance();
                        parser.setResourceBaseDir(configBaseDir);
                        parser.parse("changed");
                        configs.put(parser.getClass(), parser.getConfig());
                    } else {
                        logger.info("parser not found for config file {}", filePath);
                    }
                } catch (Exception e) {
                    logger.error("parsed  config file {} error!", filePath, e);
                }
            }

        });
        ThreadPoolManager.scheduleAtFixedRate(configMonitor, 120, 60, TimeUnit.SECONDS);
    }

    private void addParser(IConfigParser<?> parser) {
        parserClass.put(parser.getConfigFile(), parser.getClass());
        configMonitor.addFile(parser.getConfigFile());
    }

    public void parse() throws Exception {
        List<IConfigParser<?>> parsers = new ArrayList<>();
        List<Class<?>> list = ClassUtil.getClassList(GAME_CONFIG_PARSER_PACKAGE, true, null, getClass().getClassLoader());
        for (Class<?> clazz : list) {
            if (!AbstractConfigParser.class.isAssignableFrom(clazz) || clazz == AbstractConfigParser.class) continue;
            try {
                IConfigParser<?> parser = (IConfigParser<?>) clazz.newInstance();
                parser.setResourceBaseDir(configBaseDir);
                addParser(parser);
                parser.parse("init");
                parsers.add(parser);
                configs.put(parser.getClass(), parser.getConfig());
            } catch (Exception e) {
                parseError = e;
                logger.error("parse config {} error!", clazz, e);
            }
        }

        for (IConfigParser<?> parser : parsers) {
            parser.validate();
        }
        if (parseError != null) {
            throw parseError;
        }
    }

    public <T> T getConfig(Class<?> clazz) {
        if (configs.containsKey(clazz)) return (T) configs.get(clazz);
        return null;
    }
}
