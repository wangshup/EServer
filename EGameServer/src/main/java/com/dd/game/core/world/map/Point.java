package com.dd.game.core.world.map;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * @program: server
 * @description: point
 * @author: wangshupeng
 * @create: 2019-01-04 17:07
 **/
public class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public static int dot(Point a, Point b) {
        return a.x * b.x + a.y * b.y;
    }

    public static int cross(Point a, Point b) {
        return a.x * b.y - a.y * b.x;
    }

    public static boolean inSegment(Point p, Point a, Point b) {
        Point pa = new Point(a.x - p.x, a.y - p.y);
        Point pb = new Point(b.x - p.x, b.y - p.y);
        return cross(pa, pb) == 0 && dot(pa, pb) <= 0;
    }

    public static boolean segmentIntersection(Point a1, Point a2, Point b1, Point b2) {
        Point a2a1 = new Point(a2.x - a1.x, a2.y - a1.y);
        Point b1a1 = new Point(b1.x - a1.x, b1.y - a1.y);
        Point b2a1 = new Point(b2.x - a1.x, b2.y - a1.y);
        Point b2b1 = new Point(b2.x - b1.x, b2.y - b1.y);
        Point a1b1 = new Point(a1.x - b1.x, a1.y - b1.y);
        Point a2b1 = new Point(a2.x - b1.x, a2.y - b1.y);
        long c1 = cross(a2a1, b1a1), c2 = cross(a2a1, b2a1);
        long c3 = cross(b2b1, a1b1), c4 = cross(b2b1, a2b1);
        if (c1 * c2 < 0 && c3 * c4 < 0) {
            return true;
        }
        if (c1 == 0 && inSegment(b1, a1, a2)) {
            return true;
        }
        if (c2 == 0 && inSegment(b2, a1, a2)) {
            return true;
        }
        if (c3 == 0 && inSegment(a1, b1, b2)) {
            return true;
        }
        if (c4 == 0 && inSegment(a2, b1, b2)) {
            return true;
        }
        return false;
    }

    /**
     * 判断线段ab是否和矩形相交
     *
     * @param rectLD 矩形的左下点
     * @param rectRU 矩形的右上点
     * @param a      线段点a
     * @param b      线段点b
     * @return
     */
    public static boolean rectIntersection(Point rectLD, Point rectRU, Point a, Point b) {
        int rect_minX = min(rectLD.x, rectRU.x), rect_maxX = max(rectLD.x, rectRU.x);
        int rect_minY = min(rectLD.y, rectRU.y), rect_maxY = max(rectLD.y, rectRU.y);
        int line_minX = min(a.x, b.x), line_maxX = max(a.x, b.x);
        int line_minY = min(a.y, b.y), line_maxY = max(a.y, b.y);
        if (rect_minX <= line_minX && line_maxX <= rect_maxX && rect_minY <= line_minY && line_maxY <= rect_maxY) {
            return true;//线段在矩形内
        } else {
            Point[] p = {new Point(rect_minX, rect_minY), new Point(rect_maxX, rect_minY), new Point(rect_maxX, rect_maxY), new Point(rect_minX, rect_maxY)};
            for (int i = 0; i < 4; ++i)
                if (segmentIntersection(a, b, p[i], p[(i + 1) % 4])) {
                    return true;
                }
        }
        return false;
    }

    public static void main(String[] args) {
        Point rLD = new Point(80, 80);
        Point rRU = new Point(100, 100);
        Point a = new Point(0, 0);
        Point b = new Point(195, 244);
        boolean bool = rectIntersection(rLD, rRU, a, b);
        System.out.println(bool);
    }
}