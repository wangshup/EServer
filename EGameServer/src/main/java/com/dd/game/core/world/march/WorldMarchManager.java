package com.dd.game.core.world.march;

import com.dd.edata.EData;
import com.dd.edata.db.DBWhere;
import com.dd.game.core.GameEngine;
import com.dd.game.core.world.WorldPoint;
import com.dd.game.entity.model.WorldMarchModel;
import com.dd.game.entity.player.Player;
import com.dd.game.exceptions.GameExceptionCode;
import com.dd.game.exceptions.MarchException;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: server
 * @description: 世界行军管理类
 * @author: wangshupeng
 * @create: 2018-11-12 16:13
 **/
public class WorldMarchManager {

    private static final Logger logger = LoggerFactory.getLogger(WorldMarchManager.class);

    private Map<Long, AbstractWorldMarch> worldMarches = new HashMap<>();
    private ListMultimap<Long, AbstractWorldMarch> playerMarches = ArrayListMultimap.create();

    public void init() throws Exception {
        EData eData = GameEngine.getEData();
        List<WorldMarchModel> marchModels = eData.selectList(WorldMarchModel.class, new DBWhere("state", AbstractWorldMarch.MarchState.RETURNED.getState(), DBWhere.WhereCond.LT));
        marchModels.forEach((model) -> {
            try {
                AbstractWorldMarch march = createMarch(AbstractWorldMarch.MarchType.valueOf(model.getMarchType()), model);
                march.recover();
                addMarch(march);
            } catch (Exception e) {
                logger.error("load march {} error!", model, e);
            }
        });
    }

    //public AbstractWorldMarch march(Player player, C2SWorldMarch marchRequest) throws Exception {
    //    AbstractWorldMarch march = createMarch(AbstractWorldMarch.MarchType.valueOf(marchRequest.type), new WorldMarchModel());
    //    march.setMarchRequest(marchRequest);
    //    march.setId(IdWorker.nextId());
    //    march.setPlayerId(player.id);
    //    march.setCityId(marchRequest.cityId);
    //    march.setDest(WorldMapManager.getPointIdx(marchRequest.toWx, marchRequest.toWy));
    //    march.setMarchTime(System.currentTimeMillis());
    //    march.setState(AbstractWorldMarch.MarchState.INIT);
    //    march.verify(player);
    //    march.start();
    //    addMarch(march);
    //    return march;
    //}

    private void addMarch(AbstractWorldMarch march) {
        worldMarches.put(march.getId(), march);
        playerMarches.put(march.getPlayerId(), march);
    }

    /**
     * remove from memory and delete from database
     *
     * @param march
     */
    public void deleteMarch(AbstractWorldMarch march) {
        this.removeMarch(march);
        march.delete(true);
    }

    public AbstractWorldMarch deleteMarch(long marchId) {
        AbstractWorldMarch march = this.getMarchById(marchId);
        if (march != null) {
            this.deleteMarch(march);
        }
        return march;
    }

    /**
     * remove from memory
     *
     * @param march
     */
    public void removeMarch(AbstractWorldMarch march) {
        worldMarches.remove(march.getId());
        playerMarches.remove(march.getPlayerId(), march);
    }

    public void removeMarch(long marchId) {
        AbstractWorldMarch march = this.getMarchById(marchId);
        if (march != null) {
            this.removeMarch(march);
        }
    }

    private AbstractWorldMarch createMarch(AbstractWorldMarch.MarchType type, WorldMarchModel model) throws MarchException {
        switch (type) {
            //case FIGHT_PVE:
            //    return new FightNpcMarch(model);
            //case HERO_ADVENTURE:
            //    return new HeroAdventureMarch(model);
            //case NPC_VS_NPC:
            //    return new NpcVsNpcShowMarch(model);
            //case NPC_TRADE_NPC:
            //    return new NpcTradeNpcShowMarch(model);
            default:
                throw new MarchException(GameExceptionCode.MARCH_TYPE_ERROR);
        }
    }

    public AbstractWorldMarch getMarchById(long id) {
        return worldMarches.get(id);
    }

    public List<AbstractWorldMarch> getPlayerMarchs(long playerId) {
        return playerMarches.get(playerId);
    }

    public int getMarchingCountByType(long playerId, AbstractWorldMarch.MarchType type) {
        int count = 0;
        List<AbstractWorldMarch> marches = playerMarches.get(playerId);
        for (AbstractWorldMarch march : marches) {
            if (march.getMarchType() == type) {
                count++;
            }
        }
        return count;
    }

    public int getMarchingCountExceptType(long playerId, AbstractWorldMarch.MarchType type) {
        int count = 0;
        List<AbstractWorldMarch> marches = playerMarches.get(playerId);
        for (AbstractWorldMarch march : marches) {
            if (march.getMarchType() != type) {
                count++;
            }
        }
        return count;
    }

    public AbstractWorldMarch recallMarch(long marchId, long cityId) throws Exception {
        AbstractWorldMarch march = this.worldMarches.get(marchId);
        march.recall(cityId);
        return march;
    }

    public AbstractWorldMarch withdrawMarch(long marchId) throws Exception {
        AbstractWorldMarch march = this.worldMarches.get(marchId);
        march.withdraw();
        return march;
    }

    public boolean has2PointMarch(final WorldPoint point) {
        for (AbstractWorldMarch march : worldMarches.values()) {
            if (march.getDest() == point.getId()) {
                return true;
            }
        }
        return false;
    }

    public void accelerateMarch(Player player, long marchId, long itemId) throws Exception {
        //TODO
    }

    public Map<Long, AbstractWorldMarch> getAllWorldMarches() {
        return Collections.unmodifiableMap(worldMarches);
    }

    public void pushMarches2Player(Player player) {
        //TODO
    }

    public void pushMarch2Players(AbstractWorldMarch march, int op) {
        //TODO
    }
}
