package com.dd.game.utils.hotswap;

import com.dd.agent.HotSwapAgent;
import com.dd.game.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Map;
import java.util.Map.Entry;

public final class HotSwapV2 {
    private static final Logger logger = LoggerFactory.getLogger(HotSwapV2.class);

    private HotSwapV2() {
    }

    public static String reloadClass() {
        StringBuilder sb = new StringBuilder();
        try {
            // full class name -> file
            Map<String, File> files = HotSwapAgent.getFullClassNameFiles();
            if (files.isEmpty()) {
                String msg = "can't find class files in patches";
                logger.error(msg);
                return msg;
            } else {
                for (Entry<String, File> en : files.entrySet()) {
                    String className = en.getKey();
                    if (en.getValue().exists()) {
                        sb.append(reload(en.getValue(), className));
                        sb.append("\r\n");
                    }
                }
            }
        } catch (Exception e) {
            sb.append(e.getMessage());
        }
        logger.info("{} ", sb);
        return sb.toString();
    }

    private static String reload(File file, String className) {
        String msg;
        try {
            Class<?> cls = Class.forName(className);
            return HotSwapAgent.reload(cls, file, Constants.SERVER_ID);
        } catch (IOException e) {
            msg = "<br>[hot swap v2] load class file error : " + file.getName();
            logger.error(msg, e);
        } catch (UnmodifiableClassException e) {
            msg = "<br>[hot swap v2] class is unmodifiable :" + className;
            logger.error(msg, e);
        } catch (ClassNotFoundException e) {
            msg = "<br>[hot swap v2] class can't found :" + className;
            logger.error(msg, e);
        }

        return msg + " server " + Constants.SERVER_ID;
    }
}
