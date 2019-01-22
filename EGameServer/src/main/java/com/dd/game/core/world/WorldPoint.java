package com.dd.game.core.world;

import com.dd.game.entity.BaseEntity;
import com.dd.game.entity.model.WorldPointModel;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @program: server
 * @description: 世界点信息
 * @author: wangshupeng
 * @create: 2018-11-09 11:46
 **/
public class WorldPoint extends BaseEntity {
    private static final Logger logger = LoggerFactory.getLogger(WorldPoint.class);

    public WorldPoint(WorldPointModel model) {
        super(model);
    }

    public int getId() {
        return (int) ((WorldPointModel) model).getId();
    }

    public void setId(int id) {
        ((WorldPointModel) model).setId(id);
    }

    public short getWx() {
        return ((WorldPointModel) model).getX();
    }

    public void setWx(short wx) {
        ((WorldPointModel) model).setX(wx);
    }

    public short getWy() {
        return ((WorldPointModel) model).getY();
    }

    public void setWy(short wy) {
        ((WorldPointModel) model).setY(wy);
    }

    public TileType getTileType() {
        return TileType.valueOf(((WorldPointModel) model).getTileType());
    }

    public void setTileType(byte type) {
        ((WorldPointModel) model).setTileType(type);
    }

    public boolean isObjValid() {
        return ((WorldPointModel) model).getObjValid() == 1;
    }

    public void setObjValid(boolean valid) {
        ((WorldPointModel) model).setObjValid((byte) (valid ? 1 : 0));
    }

    public boolean isPass() {
        return ((WorldPointModel) model).getPass() == 0;
    }

    public void setPass(boolean pass) {
        ((WorldPointModel) model).setPass((byte) (pass ? 0 : 1));
    }

    public PointObjType getObjType() {
        return PointObjType.valueOf(((WorldPointModel) model).getObjType());
    }

    public void setObjType(PointObjType pt) {
        ((WorldPointModel) model).setObjType(pt.getType());
    }

    public long getObjId() {
        return ((WorldPointModel) model).getObjId();
    }

    public void setObjId(long objId) {
        ((WorldPointModel) model).setObjId(objId);
    }

    public long getOwnerId() {
        return ((WorldPointModel) model).getOwnerId();
    }

    public void setOwnerId(long ownerId) {
        ((WorldPointModel) model).setOwnerId(ownerId);
    }

    public int getObjCfgId() {
        return ((WorldPointModel) model).getObjCfgId();
    }

    public void setObjCfgId(int objCfgId) {
        ((WorldPointModel) model).setObjCfgId(objCfgId);
    }

    public Date getUpdateTime() {
        return ((WorldPointModel) model).getUpdateTime();
    }

    public void setUpdateTime(Date updateTime) {
        ((WorldPointModel) model).setUpdateTime(updateTime);
    }

    public long getExtData() {
        return ((WorldPointModel) model).getExtData();
    }

    public void setExtData(long extData) {
        ((WorldPointModel) model).setExtData(extData);
    }

    public boolean isFree() {
        return isObjValid() && getObjType() == WorldPoint.PointObjType.NONE;
    }

    public enum TileType {
        NONE(0), ICE(1), DESERT(2), GRASS(3), LAKE_SIDE(4), LAKE(5), SEA_SIDE(6), SEA(7), FOREST(8), HILL(9);

        private byte type;

        TileType(int type) {
            this.type = (byte) type;
        }

        public byte getType() {
            return this.type;
        }

        public static TileType valueOf(int type) {
            for (TileType tt : TileType.values()) {
                if (tt.type == type) {
                    return tt;
                }
            }
            return NONE;
        }
    }

    public enum PointObjType {
        //根据策划表中的约定
        NONE(0), ASSISTED(1), PLAY_CITY(2), HERO_MAZE_CITY(96), RESOURCE(97), HERO_ADVENTURE_CITY(98), NPC_CITY(99);

        private byte type;

        PointObjType(int type) {
            this.type = (byte) type;
        }

        public byte getType() {
            return this.type;
        }

        public static PointObjType valueOf(int type) {
            for (PointObjType pt : PointObjType.values()) {
                if (pt.type == type) {
                    return pt;
                }
            }
            return NONE;
        }
    }

    @Override
    public Message toProtoBuf(int op) {
        return null;//TODO
    }

    @Override
    public String toString() {
        return "WorldPoint{" + "model=" + model + '}';
    }
}