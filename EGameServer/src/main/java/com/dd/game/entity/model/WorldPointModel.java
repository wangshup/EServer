package com.dd.game.entity.model;

import com.dd.edata.db.annotation.*;

import java.util.Date;

/**
 * @program: server
 * @description: 世界点信息模型
 * @author: wangshupeng
 * @create: 2018-11-12 17:15
 **/
@Table(name = "world_point")
@TablePrimaryKey(members = {"id"})
@TableIndices({@TableIndex(name = "idx_x_y", members = {"x", "y"}),//idx_x_y
        @TableIndex(name = "idx_owner_id", members = {"owner_id"}), //idx_owner_id
        @TableIndex(name = "idx_obj_type", members = {"obj_type"})})
public class WorldPointModel implements IModel {
    @Column(name = "id", isNull = false)
    private int id;

    @Column
    private short x;

    @Column
    private short y;

    @Column(name = "tile_type")
    private byte tileType;

    @Column(name = "march_pass")
    private byte pass;

    @Column(name = "obj_valid")
    private byte objValid;

    @Column(name = "obj_type")
    private byte objType;

    @Column(name = "obj_id")
    private long objId;

    @Column(name = "obj_cfg_id")
    private int objCfgId;

    @Column(name = "owner_id")
    private long ownerId;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "ext_data")
    private long extData;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = (int) id;
    }

    public short getX() {
        return x;
    }

    public void setX(short x) {
        this.x = x;
    }

    public short getY() {
        return y;
    }

    public void setY(short y) {
        this.y = y;
    }

    public byte getTileType() {
        return tileType;
    }

    public void setTileType(byte tileType) {
        this.tileType = tileType;
    }

    public byte getPass() {
        return pass;
    }

    public void setPass(byte pass) {
        this.pass = pass;
    }

    public byte getObjValid() {
        return objValid;
    }

    public void setObjValid(byte objValid) {
        this.objValid = objValid;
    }

    public byte getObjType() {
        return objType;
    }

    public void setObjType(byte objType) {
        this.objType = objType;
    }

    public long getObjId() {
        return objId;
    }

    public void setObjId(long objId) {
        this.objId = objId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public int getObjCfgId() {
        return objCfgId;
    }

    public void setObjCfgId(int objCfgId) {
        this.objCfgId = objCfgId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public long getExtData() {
        return extData;
    }

    public void setExtData(long extData) {
        this.extData = extData;
    }

    @Override
    public String toString() {
        return "WorldPointModel{" + "id=" + id + ", x=" + x + ", y=" + y + ", tileType=" + tileType + ", pass=" + pass + ", objValid=" + objValid + ", objType=" + objType + ", objId=" + objId + ", objCfgId=" + objCfgId + ", ownerId=" + ownerId + ", updateTime=" + updateTime + ", extData=" + extData + '}';
    }
}