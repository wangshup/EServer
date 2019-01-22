package com.dd.game.utils.hotswap;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.management.ManagementFactory;

public final class HotSwapAgentManager {
    private static final Logger logger = LoggerFactory.getLogger(HotSwapAgentManager.class);
    private static final String AGENT_JAR = "extensions/__lib__/game-agent-1.0.jar";

    private HotSwapAgentManager() {
    }

    public static void reloadAgent() {
        String pid = null;
        VirtualMachine vm = null;
        try {
            pid = getPID();
            vm = VirtualMachine.attach(pid);
        } catch (AttachNotSupportedException e) {
            logger.error("attach " + pid, e);
        } catch (IOException e) {
            logger.error("attach " + pid, e);
        }
        if (vm == null) {
            return;
        }
        try {
            vm.loadAgent(AGENT_JAR);
            vm.detach();
        } catch (AgentLoadException e) {
            logger.error("load agent " + pid, e);
        } catch (AgentInitializationException e) {
            logger.error("load agent " + pid, e);
        } catch (IOException e) {
            logger.error("load agent " + pid, e);
        }
    }

    private static String getPID() {
        String pidAtHost = ManagementFactory.getRuntimeMXBean().getName();
        String pid = StringUtils.split(pidAtHost, '@')[0];
        logger.info("pid is {}", pid);
        return pid;
    }
}
