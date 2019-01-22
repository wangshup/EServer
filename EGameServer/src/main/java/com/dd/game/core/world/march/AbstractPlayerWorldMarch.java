package com.dd.game.core.world.march;


import com.dd.game.core.world.WorldManager;
import com.dd.game.core.world.WorldMapManager;
import com.dd.game.core.world.WorldPoint;
import com.dd.game.entity.IEntity;
import com.dd.game.entity.model.WorldMarchModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: server
 * @description: Player 世界行军
 * @author: wangshupeng
 * @create: 2019-01-03 16:46
 **/
public abstract class AbstractPlayerWorldMarch extends AbstractWorldMarch {

    private static final Logger logger = LoggerFactory.getLogger(AbstractPlayerWorldMarch.class);

    public AbstractPlayerWorldMarch(MarchType type, WorldMarchModel model) {
        super(type, model);
    }

    @Override
    protected long calcMarchTime(WorldPoint from, WorldPoint to) {
        return (long) (WorldMapManager.distance(from, to));
    }

    @Override
    protected void start() {
        long now = System.currentTimeMillis();
        WorldMapManager pointManager = WorldManager.getWorldMapManager();
        WorldPoint from = pointManager.getPointById(getFrom());
        WorldPoint to = pointManager.getPointById(getDest());
        long marchTime = calcMarchTime(from, to);
        this.setStartTime(now);
        this.setEndTime(now + marchTime);
        this.setState(MarchState.MARCHING);
        this.scheduleTask(marchTime, this::onMarchingEnd);
        insert(true);
        push2Players(IEntity.OP_ADD);
        logger.info("player march started [{}] ", this);
    }

    @Override
    protected void recall(long cityId) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void withdraw() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void push2Players(int op) {
        WorldManager.getWorldMarchManager().pushMarch2Players(this, op);
    }

    @Override
    protected void accelerateByFixed(long fixedTime) throws Exception {
        super.accelerateByFixed(fixedTime);
        update(true);
    }
}