package com.dd.server.http.controller;

import com.dd.server.Server;
import com.dd.server.entities.IZone;
import com.dd.server.http.WebProxy;
import com.dd.server.utils.GameActionStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
public class HttpController {
    private static final Logger logger = LoggerFactory.getLogger(HttpController.class);
    private static final String ZONE_PREFIX = "RPG";

    @RequestMapping(value = "/hotswap.do")
    public Object doHotswap(String zoneId) {
        IZone zone = getZone(zoneId);
        WebProxy proxy = (WebProxy) zone.getProperty("webproxy");
        return proxy.hotswap();
    }

    @RequestMapping(value = "/actionstat.do")
    public Map<String, GameActionStatistics.GameActionStatistic> getActionStat(String zoneId) {
        IZone zone = getZone(zoneId);
        WebProxy proxy = (WebProxy) zone.getProperty("webproxy");
        return proxy.gmActionStatistics();
    }

    /**
     * 作为临时修复问题使用，添加到这里的代码在下一版本需要移到正式的地方或清理掉
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "/repair.do")
    public Object doRepair(HttpServletRequest req, HttpServletResponse resp) {
        IZone zone = getZone(req.getParameter("zoneId"));
        WebProxy proxy = (WebProxy) zone.getProperty("webproxy");
        return proxy.doRepair(req);
    }

    protected IZone getZone(String zoneId) {
        IZone zone = null;
        if (null == zoneId) {
            List<IZone> zoneList = getGameZoneList();
            if (!zoneList.isEmpty()) {
                zone = zoneList.get(0);
            }
        } else {
            String zoneName = ZONE_PREFIX + (zoneId.equals("0") ? "" : zoneId);
            zone = Server.getInstance().getExtensionService().getZone(zoneName);
        }
        return zone;
    }

    protected List<IZone> getGameZoneList() {
        Collection<IZone> zones = Server.getInstance().getExtensionService().getZones();
        List<IZone> zoneList = new ArrayList<>();
        for (IZone zone : zones) {
            if (zone.getName().startsWith(ZONE_PREFIX)) {
                zoneList.add(zone);
            }
        }
        return zoneList;
    }
}
