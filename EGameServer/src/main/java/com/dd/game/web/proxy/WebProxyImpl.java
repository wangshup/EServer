package com.dd.game.web.proxy;

import com.dd.agent.HotSwapV2;
import com.dd.game.utils.Constants;
import com.dd.server.http.WebProxy;
import com.dd.server.utils.GameActionStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class WebProxyImpl implements WebProxy {

    private static final Logger logger = LoggerFactory.getLogger(WebProxyImpl.class);

    @Override
    public Object doRepair(HttpServletRequest req) {
        return null;
    }

    @Override
    public String hotswap() {
        return HotSwapV2.reloadClass(Constants.SERVER_ID,"com.dd.game");
    }

    @Override
    public Map<String, GameActionStatistics.GameActionStatistic> gmActionStatistics() {
        return GameActionStatistics.getAllActionIdStatistics();
    }
}
