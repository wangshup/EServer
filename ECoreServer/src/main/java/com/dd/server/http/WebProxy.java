package com.dd.server.http;

import com.dd.server.utils.GameActionStatistics;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface WebProxy {

    Object doRepair(HttpServletRequest req);

    String hotswap();

    Map<String, GameActionStatistics.GameActionStatistic> gmActionStatistics();
}
