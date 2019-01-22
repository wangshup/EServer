package com.dd.game.core.world.map;

/**
 * @program: server
 * @description: 地图矩形范围
 * @author: wangshupeng
 * @create: 2018-12-27 10:31
 **/
public class MapRectRange extends MapRange {
    private int startX;
    private int startY;
    private int endX;
    private int endY;

    public MapRectRange(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }
}