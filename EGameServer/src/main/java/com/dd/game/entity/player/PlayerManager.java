package com.dd.game.entity.player;

import com.dd.game.core.GameEngine;
import com.dd.game.core.ThreadPoolManager;
import com.dd.game.module.event.EventDispatcher;
import com.dd.game.module.event.EventType;
import com.dd.game.module.redis.RedisKey;
import com.dd.game.utils.Constants;
import com.dd.game.utils.ConstantsPush;
import com.dd.game.utils.FunctionSwitch;
import com.dd.protobuf.PushProtocol.PushFunctionOpen;
import com.dd.server.Server;
import com.dd.server.entities.IUser;
import com.dd.server.entities.IZone;
import com.dd.server.session.ISession;
import com.dd.server.utils.ECacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public final class PlayerManager {
    private static final Logger logger = LoggerFactory.getLogger(PlayerManager.class);
    private static final PlayerManager INSTANCE = new PlayerManager();

    private LoadingCache<Long, Player> playerCache;

    private PlayerManager() {
        initPlayerCache();
        ThreadPoolManager.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                IZone zone = Server.getInstance().getExtensionService().getZone("RPG" + Constants.SERVER_ID);
                playerCache.cleanUp();
                logger.info("---------------------------------------------------------------");
                logger.info("[{}] player count {}, online user count {}", Constants.SERVER_ID, playerCache.size(), zone != null ? zone.getUserCount() : 0);
                logger.info("---------------------------------------------------------------");
            }
        }, 60, 60, TimeUnit.MINUTES);

        EventDispatcher.addListener(EventType.PLAYER_LOGIN, (params) -> onLogin((Player) params[0]));
        EventDispatcher.addListener(EventType.PLAYER_REGISTER, (params) -> onRegister((Player) params[0]));
        EventDispatcher.addListener(EventType.PLAYER_ZONE_JOIN, (params) -> onPlayerZoneJoin((Player) params[0]));
        EventDispatcher.addListener(EventType.PLAYER_LEVEL_UP, (params) -> onLevelUp((Player) params[0]));
        EventDispatcher.addListener(EventType.HOUR_TRIGGER, (params) -> onHourTrigger());
        EventDispatcher.addListener(EventType.CONFIG_RELOAD, (params) -> onConfigReload((String) params[0]));
        EventDispatcher.addListener(EventType.PLAYER_ALTER_MOD, (params) -> onPlayerInfoChanged((Player) params[0]));
        EventDispatcher.addListener(EventType.PLAYER_CHANGE_NAME, (params) -> onPlayerInfoChanged((Player) params[0]));
    }

    public static PlayerManager getInstance() {
        return INSTANCE;
    }

    public static PlayerRedisCache getRedisCache(long id) {
        Map<String, String> map = GameEngine.getEData().hgetAll(RedisKey.playerHashCacheKey(id));
        if (map == null || map.size() == 0) {
            Player player = PlayerManager.getInstance().getPlayer(id);
            map = player.updateRedisCache();
        }
        return new PlayerRedisCache(id, map);
    }

    public static List<PlayerRedisCache> getRedisCache(List<Long> ids) {
        return getRedisCache(ids, true);
    }

    public static List<PlayerRedisCache> getRedisCache(List<Long> ids, boolean autoLoad) {
        List<String> idList = Lists.newArrayList();
        for (Long id : ids) {
            idList.add(RedisKey.playerHashCacheKey(id));
        }
        List<Map<String, String>> list = GameEngine.getEData().hgetAllPipeline(idList);

        List<PlayerRedisCache> resultList = Lists.newArrayList();
        for (int m = 0; m < ids.size(); m++) {
            if (list.get(m).size() == 0) {
                if (autoLoad) {
                    resultList.add(getRedisCache(ids.get(m)));
                }
            } else {
                resultList.add(new PlayerRedisCache(ids.get(m), list.get(m)));
            }
        }
        return resultList;
    }

    public static boolean isOnline(long playerId) {
        Player player = PlayerManager.getInstance().getPlayerIfPresent(playerId);
        if (player == null) return false;
        return (player.getUser() != null);
    }

    private void initPlayerCache() {
        Properties properties = GameEngine.getInstance().getConfigProperties();
        int maxSize = Integer.parseInt(properties.getProperty("cache_max_size", "25000"));
        int expire = Integer.parseInt(properties.getProperty("cache_expire_after_access", "24"));
        playerCache = ECacheBuilder.newBuilder().maximumSize(maxSize).expireAfterAccess(expire, TimeUnit.HOURS).removalListener((RemovalListener<Long, Player>) notification -> {
            Player player = notification.getValue();
            if (player != null) {
                logger.info("player has removed. id {}, name {}", notification.getKey(), player.getName());
            }
        }).recordStats().build(new CacheLoader<Long, Player>() {
            @Override
            public Player load(Long id) {
                try {
                    Player player = Player.load(id);
                    logger.info("load player {}", id);
                    return player;
                } catch (Exception e) {
                    logger.error("load player error", e);
                    return null;
                }
            }
        });

    }

    public void cachePlayer(Player player) {
        playerCache.put(player.getId(), player);
    }

    public void removePlayer(Player player) {
        playerCache.invalidate(player.getId());
    }

    public Map<Long, Player> getAllPlayers() {
        return playerCache.asMap();
    }

    public Player getPlayerIfPresent(long id) {
        return playerCache.getIfPresent(id);
    }

    public Player getPlayer(long id) {
        if (id == 0) {
            return null;
        }
        try {
            return playerCache.get(id);
        } catch (Exception e) {
            logger.error("load player {} failed!", id, e);
        }
        return null;
    }

    public Player getPlayer(IUser user) {
        if (user == null || user.getProperty("uid") == null) {
            return null;
        }
        Long uid = (Long) user.getProperty("uid");
        ISession userSession = user.getSession();
        Channel channel = userSession.getChannel();
        if (channel == null || !channel.isActive()) {
            logger.info("player {} channel not active zone {} user {}", uid, Constants.SERVER_ID, user);
            return null;
        }
        return getPlayer(uid);
    }

    private void onRegister(Player player) {
        player.updateRedisCache();
    }

    private void onLogin(Player player) {
        player.updateRedisCache();
        player.updateRedisLevelSet();

        // 跨天时处理的逻辑
        player.freshHour0();
    }

    private void onPlayerZoneJoin(Player player) {
    }

    private void onHourTrigger() {
    }


    private void onLevelUp(Player player) {
        String functionOpen = FunctionSwitch.buildFunctionStr(player, player.getLevel() - 1);
        String functionOpenNew = FunctionSwitch.buildFunctionStr(player, player.getLevel());
        if (!functionOpenNew.equals(functionOpen)) {
            player.send(ConstantsPush.FUNCTION_SWITCH, PushFunctionOpen.newBuilder().setFunctions(functionOpenNew).build());
        }
        player.updateRedisLevelSet();
    }


    private void onPlayerInfoChanged(Player player) {
        player.updateRedisCache();
    }

    private void onConfigReload(String configName) {
        if ("switch".equalsIgnoreCase(configName)) {
            for (Player player : playerCache.asMap().values()) {
                player.send(ConstantsPush.FUNCTION_SWITCH, PushFunctionOpen.newBuilder().setFunctions(FunctionSwitch.buildFunctionStr(player, player.getLevel())).build());
            }
        }
    }
}
