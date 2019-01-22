package com.dd.game.core.world.march;


import com.dd.game.core.ThreadPoolManager;
import com.dd.game.core.world.WorldManager;
import com.dd.game.core.world.WorldMapManager;
import com.dd.game.core.world.WorldPoint;
import com.dd.game.core.world.map.Point;
import com.dd.game.entity.BaseEntity;
import com.dd.game.entity.IEntity;
import com.dd.game.entity.model.WorldMarchModel;
import com.dd.game.entity.player.Player;
import com.dd.game.exceptions.GameExceptionCode;
import com.dd.game.exceptions.MarchException;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @program: server
 * @description: 世界行军信息
 * @author: wangshupeng
 * @create: 2018-11-09 11:46
 **/
public abstract class AbstractWorldMarch extends BaseEntity {

    protected Future<?> future;

    public AbstractWorldMarch(MarchType type, WorldMarchModel model) {
        super(model);
        setMarchType(type);
    }

    protected abstract long calcMarchTime(WorldPoint from, WorldPoint to);

    /**
     * 开始行军
     */
    protected abstract void start();

    /**
     * 行军达到处理
     */
    protected abstract void onMarchingEnd();

    /**
     * 达到完成后处理
     */
    protected abstract void onArrivingEnd();

    /**
     * 行军返回后处理
     */
    protected abstract void onReturningEnd();

    /**
     * 行军验证
     *
     * @return
     */
    protected abstract void verify(Player player) throws Exception;

    /**
     * 召回
     */
    protected abstract void recall(long cityId) throws Exception;

    /**
     * 撤回
     */
    protected abstract void withdraw() throws Exception;

    protected abstract void push2Players(int op);

    /**
     * 加速
     *
     * @param ratio 速度加倍
     * @throws Exception
     */
    protected void accelerateBySpeed(double ratio) throws Exception {
        long now = System.currentTimeMillis();
        long marchTime = (long) ((getEndTime() - now) / ratio);
        accelerateByFixed(marchTime);
    }

    /**
     * 加速
     *
     * @param ratio 时间减倍
     * @throws Exception
     */
    protected void accelerateByTime(double ratio) throws Exception {
        long now = System.currentTimeMillis();
        long marchTime = (long) ((getEndTime() - now) * ratio);
        accelerateByFixed(marchTime);
    }

    /**
     * 加速
     *
     * @param fixedTime 固定时间
     * @throws Exception
     */
    protected void accelerateByFixed(long fixedTime) throws Exception {
        long now = System.currentTimeMillis();
        long marchTime = Math.max(0, fixedTime);
        switch (getState()) {
            case MARCHING:
                if (!scheduleTask(marchTime, this::onMarchingEnd)) {
                    throw new MarchException(GameExceptionCode.MARCH_STATE_ERROR, "march accelerate error, current state: " + getState());
                }
                break;
            case RETURNING:
                if (!scheduleTask(marchTime, this::onReturningEnd)) {
                    throw new MarchException(GameExceptionCode.MARCH_STATE_ERROR, "march accelerate error, current state: " + getState());
                }
                break;
            default:
                throw new MarchException(GameExceptionCode.MARCH_STATE_ERROR, "march accelerate error, current state: " + getState());

        }
        Point dest = WorldMapManager.getPoint(getDest());
        Point start = WorldMapManager.getPoint(getAcceleratePoint());
        double ratio = (now - getAccelerateTime()) * 1.D / (getEndTime() - getAccelerateTime());
        start.setX(start.getX() + (int) ((dest.getX() - start.getX()) * ratio));
        start.setY(start.getY() + (int) ((dest.getY() - start.getY()) * ratio));
        setAcceleratePoint(WorldMapManager.getPointIdx(start));
        setAccelerateTime(now);
        setEndTime(now + marchTime);
        push2Players(IEntity.OP_UPDATE);
    }


    /**
     * 服务重启，从数据库从新加载march的时候调用
     */
    protected void recover() {
        long delay = this.getEndTime() - System.currentTimeMillis();
        switch (getState()) {
            case MARCHING:
                this.scheduleTask(delay, this::onMarchingEnd);
                break;
            case ARRIVING:
                this.scheduleTask(delay, this::onArrivingEnd);
                break;
            case RETURNING:
                this.scheduleTask(delay, this::onReturningEnd);
                break;
            case RETURNED:
                break;
            default:
                break;
        }
    }

    protected boolean scheduleTask(long delay, Runnable task) {
        if (cancelFuture()) {
            future = ThreadPoolManager.schedule(() -> {
                WorldManager.execute(task);
                future = null;
            }, delay < 0 ? 1 : delay, TimeUnit.MILLISECONDS);
            return true;
        }
        return false;
    }

    protected boolean cancelFuture() {
        boolean cancelled = true;
        if (future != null) {
            cancelled = future.cancel(false);
            future = null;
        }
        return cancelled;
    }

    public long getId() {
        return ((WorldMarchModel) model).getId();
    }

    public void setId(long id) {
        ((WorldMarchModel) model).setId(id);
    }

    public long getPlayerId() {
        return ((WorldMarchModel) model).getPlayerId();
    }

    public void setPlayerId(long playerId) {
        ((WorldMarchModel) model).setPlayerId(playerId);
    }

    public long getCityId() {
        return ((WorldMarchModel) model).getCityId();
    }

    public void setCityId(long cityId) {
        ((WorldMarchModel) model).setCityId(cityId);
    }

    public long getTeamId() {
        return ((WorldMarchModel) model).getTeamId();
    }

    public void setTeamId(long teamId) {
        ((WorldMarchModel) model).setTeamId(teamId);
    }

    public MarchType getMarchType() {
        return MarchType.valueOf(((WorldMarchModel) model).getMarchType());
    }

    public void setMarchType(MarchType marchType) {
        ((WorldMarchModel) model).setMarchType(marchType.getType());
    }

    public MarchState getState() {
        return MarchState.valueOf(((WorldMarchModel) model).getState());
    }

    public void setState(MarchState state) {
        ((WorldMarchModel) model).setState(state.getState());
    }

    public int getFrom() {
        return ((WorldMarchModel) model).getFrom();
    }

    public void setFrom(int from) {
        ((WorldMarchModel) model).setFrom(from);
        //设置加速的起点，默认为行军的起点
        this.setAcceleratePoint(from);
    }

    public int getDest() {
        return ((WorldMarchModel) model).getDest();
    }

    public void setDest(int dest) {
        ((WorldMarchModel) model).setDest(dest);
    }

    public long getTargetUid() {
        return ((WorldMarchModel) model).getTargetUid();
    }

    public void setTargetUid(long targetUid) {
        ((WorldMarchModel) model).setTargetUid(targetUid);
    }

    public long getTargetAid() {
        return ((WorldMarchModel) model).getTargetAid();
    }

    public void setTargetAid(long targetAid) {
        ((WorldMarchModel) model).setTargetAid(targetAid);
    }

    public long getMarchTime() {
        return ((WorldMarchModel) model).getMarchTime();
    }

    public void setMarchTime(long marchTime) {
        ((WorldMarchModel) model).setMarchTime(marchTime);
    }

    public long getStartTime() {
        return ((WorldMarchModel) model).getStartTime();
    }

    public void setStartTime(long startTime) {
        ((WorldMarchModel) model).setStartTime(startTime);
        //设置加速的起点时间，默认为行军的起点时间
        this.setAccelerateTime(startTime);
    }

    public long getEndTime() {
        return ((WorldMarchModel) model).getEndTime();
    }

    public void setEndTime(long arrivalTime) {
        ((WorldMarchModel) model).setEndTime(arrivalTime);
    }

    public int getAcceleratePoint() {
        return ((WorldMarchModel) model).getAcceleratePoint();
    }

    public void setAcceleratePoint(int acceleratePoint) {
        ((WorldMarchModel) model).setAcceleratePoint(acceleratePoint);
    }

    public long getAccelerateTime() {
        return ((WorldMarchModel) model).getAccelerateTime();
    }

    public void setAccelerateTime(long accelerateTime) {
        ((WorldMarchModel) model).setAccelerateTime(accelerateTime);
    }

    public byte[] getArmyInfo() {
        return ((WorldMarchModel) model).getArmyInfo();
    }

    public void setArmyInfo(byte[] armyInfo) {
        ((WorldMarchModel) model).setArmyInfo(armyInfo);
    }

    public enum MarchType {
        NULL(-1), RECALL(0), WITH_DRAW(1), FIGHT_PVE(2), FIGHT_PVP(3), HERO_ADVENTURE(4), NPC_VS_NPC(100), NPC_TRADE_NPC(101);

        private int type;

        MarchType(int type) {
            this.type = type;
        }

        public byte getType() {
            return (byte) this.type;
        }

        public static MarchType valueOf(int type) {
            for (MarchType mt : MarchType.values()) {
                if (mt.getType() == type) {
                    return mt;
                }
            }
            return null;
        }
    }

    public enum MarchState {
        INIT(0), MARCHING(1), ARRIVING(2), RETURNING(3), RETURNED(100),
        ;

        private byte state;

        MarchState(int state) {
            this.state = (byte) state;
        }

        public byte getState() {
            return state;
        }

        public static MarchState valueOf(int state) {
            for (MarchState ms : MarchState.values()) {
                if (ms.getState() == state) {
                    return ms;
                }
            }
            return null;
        }
    }
}