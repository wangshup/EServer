package com.dd.game.core.world;

import com.dd.edata.EData;
import com.dd.edata.db.DBWhere;
import com.dd.game.core.GameEngine;
import com.dd.game.core.ThreadPoolManager;
import com.dd.game.core.world.map.MapRectRange;
import com.dd.game.core.world.map.Point;
import com.dd.game.core.world.march.AbstractWorldMarch;
import com.dd.game.core.world.march.WorldMarchManager;
import com.dd.game.entity.IEntity;
import com.dd.game.entity.model.IModel;
import com.dd.game.entity.model.WorldPointModel;
import com.dd.game.exceptions.GameExceptionCode;
import com.dd.game.exceptions.WorldException;
import com.dd.game.utils.ClassUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.dd.game.core.world.WorldMapManager.Area.*;

/**
 * @program: server
 * @description: 世界地图管理类
 * @author: wangshupeng
 * @create: 2018-11-12 16:13
 **/
public class WorldMapManager {

    private static final Logger logger = LoggerFactory.getLogger(WorldMapManager.class);

    public static final int WORLD_W = 3672;
    public static final int WORLD_H = 2232;

    private static WorldPoint[] points = new WorldPoint[WORLD_H * WORLD_W];
    private Map<Integer, Area> areas = new HashMap<>(WORLD_W * WORLD_H / (AREA_W * AREA_H));
    private static Map<WorldPoint.PointObjType, WorldEntityRefresher> refreshers = new EnumMap<>(WorldPoint.PointObjType.class);

    public static int getPointIdx(int x, int y) {
        return x + y * WORLD_W;
    }

    public static int getPointIdx(Point p) {
        return p.getX() + p.getY() * WORLD_W;
    }

    public static int[] getPointXY(int id) {
        int[] xy = new int[2];
        xy[1] = id / WORLD_W;
        xy[0] = id - xy[1] * WORLD_W;
        return xy;
    }

    public static Point getPoint(int id) {
        int[] xy = getPointXY(id);
        return new Point(xy[0], xy[1]);
    }

    public static int tileType2Bits(WorldPoint.TileType... types) {
        int bits = 0;
        for (WorldPoint.TileType type : types) {
            bits |= (1 << type.getType());
        }
        return bits;
    }

    public void init() throws Exception {
        long now = System.currentTimeMillis();
        initRefreshers();
        initAreas();
        initWorldPoint();
        logger.info("init world point cost {} ms!", System.currentTimeMillis() - now);
    }

    private void initRefreshers() throws Exception {
        for (Class<?> clazz : ClassUtil.getClassList("com.dd.game.core.world", true, null)) {
            if (WorldEntityRefresher.class.isAssignableFrom(clazz) && WorldEntityRefresher.class != clazz) {
                Constructor<? extends WorldEntityRefresher> constructor = ((Class<? extends WorldEntityRefresher>) clazz).getDeclaredConstructor();
                constructor.setAccessible(true);
                WorldEntityRefresher refresher = constructor.newInstance();
                refreshers.put(refresher.getType(), refresher);
                logger.info(" add world entity refresher {}", clazz.getSimpleName());
            }
        }
    }

    private void initAreas() {
        int w = WORLD_W / AREA_W;
        int h = WORLD_H / AREA_H;
        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                int areaId = j + i * w;
                areas.put(areaId, new Area(areaId));
            }
        }
    }

    private void initWorldPoint() throws Exception {
        if (!hasInit2DB()) {
            initWorldPointsFromMapData();
            initWorldEntities();
            WorldEntityRefresher.init();
        } else {
            //preLoadBornArea();
            WorldEntityRefresher.start();
        }
    }

    private void initWorldPointsFromMapData() {
        EData eData = GameEngine.getEData();
        int total = WORLD_W * WORLD_H;
        int count = total / 1000;
        int id = 0;
        logger.info("Initializing world points from config, pls wait...");
        areas.values().forEach(area -> area.initted = true);
        String mapDataFile = "mapdata" + File.separator + "map.data";
        Date now = Calendar.getInstance().getTime();
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(mapDataFile)))) {
            List<IModel> models = new ArrayList<>(count);
            while (id < total) {
                int data = in.readInt();
                WorldPoint point = new WorldPoint(new WorldPointModel());
                point.setId(id);
                int[] xy = WorldMapManager.getPointXY(id);
                point.setWx((short) xy[0]);
                point.setWy((short) xy[1]);
                point.setPass((data & 0x01) == 0);
                point.setObjValid((data & 0x02) != 0);
                point.setObjValid(true); //TODO wangsp 暂时忽略地上物生成点规则
                point.setTileType((byte) ((data >> 2) & 0x1F));
                point.setUpdateTime(now);
                points[id++] = point;
                models.add(point.getModel());
                if (id % count == 0 || id == total) {
                    eData.insertBatchAsync(models);
                    models = new ArrayList<>(count);
                }
            }
        } catch (Exception e) {
            logger.warn("init world map {} from map data error!", id, e);
        }
    }


    public void initWorldEntities() {
        //CfgMgr.ins.getWorldinitcfgs().values().forEach(cfg -> {
        //    try {
        //        int cfgId = getCfgId(cfg.idCfg);
        //        WorldPoint.PointObjType pointObjType = WorldPoint.PointObjType.valueOf(cfgId / WorldEntityRefresher.ID_DIV);
        //        refreshers.get(pointObjType).initStaticEntity(cfg.x, cfg.y, cfgId);
        //    } catch (Exception e) {
        //        logger.error("init world entities error!", e);
        //    }
        //});
    }

    //private static int getCfgId(IdConfig config) {
    //    try {
    //        Field f = config.getClass().getDeclaredField("cfgId");
    //        return f.getInt(config);
    //    } catch (Exception e) {
    //        logger.error("get config {} id error!", config, e);
    //    }
    //    return -1;
    //}

    private boolean hasInit2DB() throws Exception {
        return GameEngine.getEData().select(WorldPointModel.class, DBWhere.EQ("id", 0)) != null;
    }

    private Area getArea(int areaId) {
        Area area = areas.get(areaId);
        if (!area.initted) {
            area.init();
            logger.debug("world map area {} inited!!", areaId);
        }
        return area;
    }

    public void refreshOne(int cfgId) {
        WorldEntityRefresher.refreshOne(cfgId);
    }

    public WorldPoint getFreeWorldPointInRange(MapRectRange range, int grid, int tileTypeBits) {
        List<Integer> ids = new ArrayList<>((range.getEndX() - range.getStartX()) * (range.getEndY() - range.getStartY()));
        for (int y = range.getStartY(); y < range.getEndY() - grid; ++y) {
            for (int x = range.getStartX(); x < range.getEndX() - grid; ++x) {
                int id = WorldMapManager.getPointIdx(x, y);
                WorldPoint point = points[id];
                if (point != null && !point.isFree()) {
                    continue;
                }
                ids.add(id);
            }
        }
        while (!ids.isEmpty()) {
            int pointId = ids.remove(ThreadLocalRandom.current().nextInt(ids.size()));
            if (canOccupyWithTileRequire(pointId, grid, tileTypeBits)) {
                return getPointById(pointId);
            }
        }
        return null;
    }

    public WorldPoint getFreeWorldPointInRange(MapRectRange range, int grid) {
        return getFreeWorldPointInRange(range, grid, -1);
    }

    public boolean canOccupy(int wx, int wy, int grid) {
        for (int x = wx; x < wx + grid; ++x) {
            for (int y = wy; y < wy + grid; ++y) {
                WorldPoint point = getPointByXY(x, y);
                if (!point.isFree()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean canOccupy(int pointId, int grid) {
        int[] xy = getPointXY(pointId);
        return this.canOccupy(xy[0], xy[1], grid);
    }

    public boolean canOccupyWithTileRequire(int wx, int wy, int grid, int tileTypeBits) {
        for (int x = wx; x < wx + grid; ++x) {
            for (int y = wy; y < wy + grid; ++y) {
                WorldPoint point = getPointByXY(x, y);
                if (!point.isFree()) {
                    return false;
                }

                if (point.getTileType() == WorldPoint.TileType.NONE) {
                    continue;
                }

                if (((tileTypeBits >> point.getTileType().getType()) & 0x01) == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean canOccupyWithTileRequire(int pointId, int grid, int tileTypeBits) {
        int[] xy = getPointXY(pointId);
        return this.canOccupyWithTileRequire(xy[0], xy[1], grid, tileTypeBits);
    }

    public WorldPoint transition(WorldPoint point, MapRectRange range, int grid) throws WorldException {
        return searchAndOccupy(range, grid, point.getOwnerId(), point.getObjId(), point.getObjCfgId(), point.getObjType());
    }

    public WorldPoint transition(WorldPoint point, int x, int y, int grid) throws WorldException {
        occupy(x, y, grid, point.getOwnerId(), point.getObjId(), point.getObjType(), point.getObjCfgId());
        return getPointByXY(x, y);
    }

    public WorldPoint searchAndOccupy(MapRectRange range, int grid, long ownerId, long objId, int objCfgId, WorldPoint.PointObjType type) throws WorldException {
        WorldPoint p = getFreeWorldPointInRange(range, grid);
        if (p == null) {
            throw new WorldException(GameExceptionCode.WORLD_POINT_FULL, "can't found world map point");
        }
        occupy(p.getWx(), p.getWy(), grid, ownerId, objId, type, objCfgId);
        return p;
    }

    public WorldPoint occupy(int wx, int wy, int grid, long objId, WorldPoint.PointObjType type, int configId) throws WorldException {
        return occupy(wx, wy, grid, 0, objId, type, configId);
    }

    public WorldPoint occupy(int wx, int wy, int grid, long ownerId, long objId, WorldPoint.PointObjType type, int configId) throws WorldException {
        for (int x = wx; x < wx + grid; ++x) {
            for (int y = wy; y < wy + grid; ++y) {
                WorldPoint p = getPointByXY(x, y);
                if (!p.isObjValid()) {
                    throw new WorldException(GameExceptionCode.WORLD_POINT_OBJ_INVALID);
                } else if (p.getObjType() != WorldPoint.PointObjType.NONE) {
                    throw new WorldException(GameExceptionCode.WORLD_POINT_HAS_OCCUPIED);
                }
            }
        }
        List<IModel> models = new ArrayList<>(grid * grid);
        Date now = Calendar.getInstance().getTime();
        for (int x = wx; x < wx + grid; ++x) {
            for (int y = wy; y < wy + grid; ++y) {
                WorldPoint point = getPointByXY(x, y);
                if (x == wx && y == wy) {
                    point.setObjId(objId);
                    point.setObjType(type);
                    point.setOwnerId(ownerId);
                    addWordPoint2Area(point);
                } else {
                    point.setObjId(getPointIdx(wx, wy));
                    point.setObjType(WorldPoint.PointObjType.ASSISTED);
                }
                point.setObjCfgId(configId);
                point.setUpdateTime(now);
                models.add(point.getModel());
            }
        }
        GameEngine.getEData().updateBatchAsync(models);
        WorldPoint point = getPointByXY(wx, wy);
        pushPointMsg2ViewPlayers(point, IEntity.OP_ADD);
        return point;
    }

    public void clear(int pointId, int grid) {
        int[] xy = getPointXY(pointId);
        clear(xy[0], xy[1], grid);
    }

    public void clear(int wx, int wy, int grid) {
        Date now = Calendar.getInstance().getTime();
        List<IModel> models = new ArrayList<>(grid * grid);
        for (int x = wx; x < wx + grid; ++x) {
            for (int y = wy; y < wy + grid; ++y) {
                WorldPoint point = getPointByXY(x, y);
                point.setObjId(0);
                point.setObjCfgId(0);
                point.setObjType(WorldPoint.PointObjType.NONE);
                point.setOwnerId(0);
                point.setUpdateTime(now);
                removeWordPointFromArea(point);
                models.add(point.getModel());
            }
        }
        GameEngine.getEData().updateBatchAsync(models);
        pushPointMsg2ViewPlayers(getPointByXY(wx, wy), IEntity.OP_DEL);
    }

    public void addWordPoint2Area(WorldPoint point) {
        getAreaByPointId(point.getId()).addWorldPoint(point);
    }

    public void removeWordPointFromArea(WorldPoint point) {
        getAreaByPointId(point.getId()).removeWorldPoint(point);
    }

    public WorldPoint getPointById(int id) {
        if (id < 0 || id >= points.length) {
            throw new IllegalArgumentException("point id not valid " + id);
        }
        if (points[id] == null) {
            getAreaByPointId(id); //init area points
        }
        return points[id];
    }

    public WorldPoint getPointByXY(int x, int y) {
        return getPointById(getPointIdx(x, y));
    }

    public Set<Integer> getPointIds(int x, int y, int width, int heigh) {
        Set<Integer> ids = new HashSet<>();
        for (int i = x; i < x + width; ++i) {
            for (int j = y; j < y + heigh; ++j) {
                ids.add(getPointIdx(i, j));
            }
        }
        return ids;
    }

    public Set<WorldPoint> getPoints(int x, int y, int width, int heigh) {
        Set<WorldPoint> points = new HashSet<>();
        for (int i = x; i < x + width; ++i) {
            for (int j = y; j < y + heigh; ++j) {
                points.add(getPointByXY(i, j));
            }
        }
        return points;
    }

    public boolean isPointLoaded(int id) {
        return points[id] != null;
    }

    public static double distance(WorldPoint from, WorldPoint to) {
        return Math.sqrt(squareDistance(from, to));
    }

    public static long squareDistance(WorldPoint from, WorldPoint to) {
        return squareDistance(from, to.getWx(), to.getWy());
    }

    public static long squareDistance(WorldPoint from, int toWx, int toWy) {
        int deltaX = toWx - from.getWx();
        int deltaY = toWy - from.getWy();
        return deltaX * deltaX + deltaY * deltaY;
    }

    public Area getAreaByPointId(int id) {
        int[] xy = getPointXY(id);
        return getAreaByPointXY(xy[0], xy[1]);
    }

    public Area getAreaByPointXY(int wx, int wy) {
        int areaW = wx / AREA_W;
        int areaH = wy / AREA_H;
        return getArea(areaW + areaH * WORLD_AREA_W);
    }

    public List<WorldPoint> getDynamicPoints(int wx, int wy, int width, int height) {
        List<WorldPoint> list = new ArrayList<>();
        int startAreaX = wx / AREA_W;
        int startAreaY = wy / AREA_H;
        int endAreaX = (wx + width) / AREA_W;
        int endAreaY = (wy + height) / AREA_H;
        for (int i = startAreaY; i <= endAreaY; ++i) {
            for (int j = startAreaX; j <= endAreaX; ++j) {
                Area area = getArea(j + i * WORLD_AREA_W);
                list.addAll(area.getAllPoints());
            }
        }
        return list;
    }

    public void pushPointMsg2ViewPlayers(WorldPoint point, int op) {
        //TODO
    }

    public static WorldPoint[] getPoints() {
        return points;
    }

    public Map<Integer, Area> getAreas() {
        return areas;
    }

    /**
     * 世界地图区域
     */
    public static class Area {
        public static final int AREA_W = 72;
        public static final int AREA_H = 72;
        public static final int WORLD_AREA_W = WORLD_W / AREA_W;

        private volatile boolean initted = false;
        private Map<Integer, WorldPoint> dynamicPoints = new HashMap<>();
        private Map<Long, AbstractWorldMarch> marches = new HashMap<>();

        public final int id;

        Area(int id) {
            this.id = id;
        }

        int getId() {
            return id;
        }

        public void init() {
            if (initted) {
                return;
            }
            try {
                int y = id / WORLD_AREA_W;
                int x = id - y * WORLD_AREA_W;
                int startWx = x * AREA_W;
                int startWy = y * AREA_H;
                EData eData = GameEngine.getEData();
                List<WorldPointModel> models = eData.selectList(WorldPointModel.class, //
                        new DBWhere("x", startWx, DBWhere.WhereCond.GE), //x >= startWx
                        new DBWhere("x", startWx + AREA_W, DBWhere.WhereCond.LT), //x < startWx + AREA_W
                        new DBWhere("y", startWy, DBWhere.WhereCond.GE), //y >= startWy
                        new DBWhere("y", startWy + AREA_H, DBWhere.WhereCond.LT)); //y < startWy + AREA_H
                models.forEach((m) -> {
                    WorldPoint p = (points[(int) m.getId()] = new WorldPoint(m));
                    if (p.getObjType() != WorldPoint.PointObjType.NONE && p.getObjType() != WorldPoint.PointObjType.ASSISTED) {
                        addWorldPoint(p);
                    }
                });
                this.initted = true;
            } catch (Exception e) {
                logger.error("init area {} points error!", id, e);
            }
        }

        void addWorldPoint(WorldPoint point) {
            dynamicPoints.put(point.getId(), point);
        }

        void removeWorldPoint(WorldPoint point) {
            dynamicPoints.remove(point.getId());
        }

        public Collection<WorldPoint> getAllPoints() {
            return Collections.unmodifiableCollection(dynamicPoints.values());
        }
    }

    public List<WorldPoint> getWorldPointsByObjType(WorldPoint.PointObjType type) {
        return refreshers.get(type).getAllEntityPoints();
    }

    /**
     * 世界实例刷新器抽象类
     */
    public static abstract class WorldEntityRefresher {

        public static final int ID_DIV = 1000000;

        protected static Map<Integer, Object> refreshCfgs = new HashMap<>();

        protected ListMultimap<Integer, WorldPoint> entityPoints = ArrayListMultimap.create();

        public void addEntityPoint(WorldPoint point) {
            entityPoints.put(point.getObjCfgId(), point);
        }

        public List<WorldPoint> getAllEntityPoints() {
            List<WorldPoint> list = new ArrayList<>();
            entityPoints.asMap().values().forEach(points -> list.addAll(points));
            return list;
        }

        public static void init() {
            cacheRefreshCfgs();
            //CfgMgr.ins.getWorldrefreshcfgs().values().stream().sorted(Comparator.comparingInt(o -> o.priority)).forEach((cfg) -> {
            //    int cfgId = getCfgId(cfg.idCfg);
            //    WorldPoint.PointObjType pointObjType = WorldPoint.PointObjType.valueOf(cfgId / ID_DIV);
            //    WorldEntityRefresher refresher = refreshers.get(pointObjType);
            //    if (refresher != null) {
            //        refresher.refresh(cfgId);
            //    }
            //});
            refreshers.values().forEach(m -> m.addRefreshJob());
        }

        public static void start() {
            cacheRefreshCfgs();
            refreshers.values().forEach(m -> m.start0());
        }

        public static void reset() {
            cacheRefreshCfgs();
            //CfgMgr.ins.getWorldrefreshcfgs().values().stream().sorted(Comparator.comparingInt(o -> o.priority)).forEach(cfg -> {
            //    int cfgId = getCfgId(cfg.idCfg);
            //    WorldPoint.PointObjType pointObjType = WorldPoint.PointObjType.valueOf(cfgId / ID_DIV);
            //    WorldEntityRefresher refresher = refreshers.get(pointObjType);
            //    refresher.reset(cfg);
            //});
        }

        public static void refreshAll() {
            refreshCfgs.forEach((k, v) -> {
                WorldPoint.PointObjType pointObjType = WorldPoint.PointObjType.valueOf(k / ID_DIV);
                WorldEntityRefresher refresher = refreshers.get(pointObjType);
                ThreadPoolManager.execute(() -> refresher.refresh(k));
            });
        }

        public static void refreshAll(int cfgId) {
            refreshCfgs.forEach((k, v) -> {
                if (k == cfgId) {
                    WorldPoint.PointObjType pointObjType = WorldPoint.PointObjType.valueOf(k / ID_DIV);
                    WorldEntityRefresher refresher = refreshers.get(pointObjType);
                    ThreadPoolManager.execute(() -> refresher.refresh(k));
                }
            });
        }

        public static void refreshOne(int cfgId) {
            WorldPoint.PointObjType pointObjType = WorldPoint.PointObjType.valueOf(cfgId / ID_DIV);
            WorldEntityRefresher refresher = refreshers.get(pointObjType);
            ThreadPoolManager.execute(() -> refresher.refresh(cfgId, 1));
        }

        protected void start0() {
            WorldMapManager manager = WorldManager.getWorldMapManager();
            EData eData = GameEngine.getEData();
            eData.selectListAsync(models -> models.forEach(m -> {
                WorldManager.execute(() -> {
                    WorldPoint point = manager.getPointById((int) m.getId());
                    WorldEntityRefresher.this.addEntityPoint(point);
                });
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    logger.error("refresher sleep error!", e);
                }
            }), WorldPointModel.class, Lists.newArrayList("id"), DBWhere.EQ("obj_type", getType().getType()));
            addRefreshJob();
        }

        protected void addRefreshJob() {
            //CfgMgr.ins.getWorldrefreshcfgs().values().forEach(cfg -> {
            //    int cfgId = getCfgId(cfg.idCfg);
            //    if (cfgId / ID_DIV == getType().getType()) {
            //        Map<String, Object> map = new HashMap<>();
            //        map.put("refresher", this);
            //        map.put("cfgId", cfgId);
            //        ThreadPoolManager.addCronJob(getClass().getSimpleName() + "_" + cfgId, RefreshJob.class, cfg.schedule, map);
            //    }
            //});
        }

        //private void reset(WorldRefreshCfg cfg) {
        //    int cfgId = getCfgId(cfg.idCfg);
        //    String jobName = getClass().getSimpleName() + "_" + cfgId;
        //    boolean exist = ExecutorsManager.removeCronJob(jobName);
        //    if (!exist) {
        //        refresh(cfgId);
        //    }
        //    Map<String, Object> map = new HashMap<>();
        //    map.put("refresher", this);
        //    map.put("cfgId", cfgId);
        //    ExecutorsManager.addCronJob(jobName, RefreshJob.class, cfg.schedule, map);
        //}

        private static void cacheRefreshCfgs() {
            refreshCfgs.clear();
            //CfgMgr.ins.getWorldrefreshcfgs().values().forEach(cfg -> {
            //    int cfgId = getCfgId(cfg.idCfg);
            //    refreshCfgs.put(cfgId, cfg);
            //});
        }

        protected abstract Long createEntity(int cfgId, int wx, int wy, long startTime, long endTime);

        protected abstract void removeEntity(long id);

        protected abstract int getWorldGrid(int cfgId);

        protected abstract WorldPoint.PointObjType getType();

        protected abstract void onStaticEntityCreated(long id, int cfgId);

        private void refresh(int cfgId) {
            List<WorldPoint> deletes = new ArrayList<>();
            WorldMarchManager marchManager = WorldManager.getWorldMarchManager();
            WorldMapManager mapManager = WorldManager.getWorldMapManager();
            List<WorldPoint> points = entityPoints.get(cfgId);
            int worldGrid = getWorldGrid(cfgId);
            //delete old entity
            //for (WorldPoint point : points) {
            //    new Procedure() {
            //        @Override
            //        protected boolean process() {
            //            runSucc(() -> {
            //                mapManager.clear(point.getWx(), point.getWy(), worldGrid);
            //                deletes.add(point);
            //            });
            //            if (point.getOwnerId() != -1 && !marchManager.has2PointMarch(point)) {
            //                removeEntity(point.getObjId());
            //                logger.info("The {} city {} was removed in world refresh!!", getType(), point.getObjId());
            //                return true;
            //            }
            //
            //            return false;
            //        }
            //    }.call();
            //}
            //deletes.forEach(p -> entityPoints.remove(cfgId, p));
            ////refresh new entity
            //WorldRefreshCfg worldrefreshcfg = refreshCfgs.get(cfgId);
            //int count = worldrefreshcfg.amount - entityPoints.get(cfgId).size();
            //ExecutorsManager.execute(() -> this.refresh(cfgId, count));
        }

        private void refresh(int cfgId, int count) {
            long now = System.currentTimeMillis();
            WorldMapManager mapManager = WorldManager.getWorldMapManager();
            int worldGrid = getWorldGrid(cfgId);
            //WorldRefreshCfg worldrefreshcfg = refreshCfgs.get(cfgId);
            //if (worldrefreshcfg == null) {
            //    logger.error("not found refresher for config id {}, refresh error!", cfgId);
            //    return;
            //}
            //RangeData[] range = worldrefreshcfg.range;
            //Set<Integer> idSet = new HashSet<>();
            //for (RangeData rangeData : range) {
            //    for (int y = rangeData.startY; y < rangeData.endY - worldGrid; ++y) {
            //        for (int x = rangeData.startX; x < rangeData.endX - worldGrid; ++x) {
            //            int id = WorldMapManager.getPointIdx(x, y);
            //            WorldPoint p = points[id];
            //            if (p != null && !p.isFree()) {
            //                continue;
            //            }
            //            idSet.add(id);
            //        }
            //    }
            //}
            //
            //List<Integer> ids = Lists.newLinkedList(idSet);
            //int num = 0;
            //int tileTypeRequire = Integer.parseInt(worldrefreshcfg.sterrainReq, 2) << 1;
            //logger.info("==== world entity {}, config id {}, count {} will be refreshed", getType(), cfgId, count);
            //for (int i = 0; i < count; ++i) {
            //    if (ids.isEmpty()) {
            //        break;
            //    }
            //    while (!ids.isEmpty()) {
            //        int pointId = ids.remove(ThreadLocalRandom.current().nextInt(ids.size()));
            //        if (!mapManager.canOccupyWithTileRequire(pointId, worldGrid, tileTypeRequire)) {
            //            continue;
            //        }
            //        int[] xy = getPointXY(pointId);
            //        Long entityId = createEntity(cfgId, xy[0], xy[1], now, -1);
            //        if (entityId != null) {
            //            try {
            //                WorldPoint p = mapManager.occupy(xy[0], xy[1], worldGrid, entityId, getType(), cfgId);
            //                WorldManager.execute(() -> entityPoints.put(cfgId, p));
            //                num++;
            //                logger.debug("======== world entity {}, type {}, config id {} has been refreshed at point {}:{}", entityId, getType(), cfgId, xy[0], xy[1]);
            //            } catch (Exception e) {
            //                new Procedure() {
            //                    @Override
            //                    protected boolean process() {
            //                        removeEntity(entityId);
            //                        return true;
            //                    }
            //                }.call();
            //                logger.warn("======== world entity {}, type {}, config id {} has been removed, error: {}", entityId, getType(), cfgId, e.getMessage());
            //            }
            //            break;
            //        }
            //    }
            //    try {
            //        TimeUnit.MILLISECONDS.sleep(10);
            //    } catch (InterruptedException e) {
            //        logger.error("refresher sleep error!", e);
            //    }
            //}
            //logger.info("==== world entity {}, config id {} refresh finished, total count {} cost {} ms", getType(), cfgId, num, System.currentTimeMillis() - now);
        }

        private void initStaticEntity(int wx, int wy, int cfgId) {
            //long now = System.currentTimeMillis();
            //WorldMapManager mapManager = WorldManager.getWorldMapManager();
            //int worldGrid = getWorldGrid(cfgId);
            //Long entityId = createEntity(cfgId, wx, wy, now, -1);
            //if (entityId != null) {
            //    try {
            //        //Attention:: world static point owner id is -1
            //        WorldPoint p = mapManager.occupy(wx, wy, worldGrid, -1, entityId, getType(), cfgId);
            //        addEntityPoint(p);
            //        onStaticEntityCreated(entityId, cfgId);
            //        logger.info("******* init world entity {}, type {}, config id {} has been inited at point {}:{}", entityId, getType(), cfgId, wx, wy);
            //    } catch (Exception e) {
            //        new Procedure() {
            //            @Override
            //            protected boolean process() {
            //                removeEntity(entityId);
            //                return true;
            //            }
            //        }.call();
            //        logger.error("******* init world entity {}, type {}, config id {} has been removed", entityId, getType(), cfgId, e);
            //    }
            //}
        }

        public static class RefreshJob implements Job {
            @Override
            public void execute(JobExecutionContext context) throws JobExecutionException {
                WorldEntityRefresher refresher = (WorldEntityRefresher) context.getJobDetail().getJobDataMap().get("refresher");
                int cfgId = (int) context.getJobDetail().getJobDataMap().get("cfgId");
                WorldManager.execute(() -> refresher.refresh(cfgId));
            }
        }
    }

    /**
     * 世界资源刷新器
     */
    private static class WorldResourceRefresher extends WorldEntityRefresher {
        @Override
        public WorldPoint.PointObjType getType() {
            return WorldPoint.PointObjType.RESOURCE;
        }

        @Override
        protected Long createEntity(int cfgId, int wx, int wy, long startTime, long endTime) {
            //WorldResourceEntity entity = WorldEntityManager.initWorldResourceEntity(cfgId, wx, wy, startTime, endTime);
            //if (entity != null) {
            //    return entity.getId();
            //}
            return null;
        }

        @Override
        protected void removeEntity(long id) {
        }

        @Override
        protected int getWorldGrid(int cfgId) {
            return 8;//TODO
        }

        @Override
        protected void onStaticEntityCreated(long id, int cfgId) {
        }
    }

    /**
     * 世界英雄城刷新器
     */
    private static class WorldAdventureCityRefresher extends WorldEntityRefresher {
        @Override
        public WorldPoint.PointObjType getType() {
            return WorldPoint.PointObjType.HERO_ADVENTURE_CITY;
        }

        @Override
        protected Long createEntity(int cfgId, int wx, int wy, long startTime, long endTime) {
            //WorldCityEntity entity = WorldEntityManager.initHeroAdventureCity(cfgId, wx, wy, startTime, endTime);
            //if (entity != null) {
            //    return entity.getId();
            //}
            return null;
        }

        @Override
        protected void removeEntity(long id) {
        }

        @Override
        protected int getWorldGrid(int cfgId) {
            return 0;//TODO
        }

        @Override
        protected void onStaticEntityCreated(long id, int cfgId) {
        }
    }

    /**
     * 世界NPC城刷新器
     */
    private static class WorldNpcCityRefresher extends WorldEntityRefresher {

        @Override
        protected WorldPoint.PointObjType getType() {
            return WorldPoint.PointObjType.NPC_CITY;
        }

        @Override
        protected Long createEntity(int cfgId, int wx, int wy, long startTime, long endTime) {
            //WorldCityEntity entity = WorldEntityManager.initPveCity(cfgId, wx, wy, startTime, endTime);
            //if (entity != null) {
            //    return entity.getId();
            //}
            return null;
        }

        @Override
        protected void removeEntity(long id) {
        }

        @Override
        protected int getWorldGrid(int cfgId) {
            return 0;//TODO
        }

        @Override
        protected void onStaticEntityCreated(long id, int cfgId) {
        }
    }

    /**
     * 世界Maze城刷新器
     */
    private static class WorldMazeCityRefresher extends WorldEntityRefresher {

        @Override
        protected WorldPoint.PointObjType getType() {
            return WorldPoint.PointObjType.HERO_MAZE_CITY;
        }

        @Override
        public Long createEntity(int cfgId, int wx, int wy, long startTime, long endTime) {
            //WorldCityEntity entity = WorldEntityManager.initMazeCity(cfgId, wx, wy, startTime, endTime);
            //if (entity != null) {
            //    return entity.getId();
            //}
            return null;
        }

        @Override
        public void removeEntity(long id) {
        }

        @Override
        protected int getWorldGrid(int cfgId) {
            return 0;//TODO
        }

        @Override
        protected void onStaticEntityCreated(long id, int cfgId) {
        }
    }
}
