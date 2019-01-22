package com.dd.game.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateTimeUtil {
    public static final Date MIN_DATE = new Date(Long.MIN_VALUE);
    public static final Date MAX_DATE = new Date(Long.MAX_VALUE);
    private static final Logger logger = LoggerFactory.getLogger(DateTimeUtil.class);
    private static final ThreadLocal<SimpleDateFormat> threadLocalSimpleDateFormat = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat();
        }

        ;
    };

    private DateTimeUtil() {
    }

    /**
     * 判断两个时间戳(Timestamp)是否在同一小时
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isTheSameHour(long time1, long time2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        int y1 = c1.get(Calendar.YEAR);
        int m1 = c1.get(Calendar.MONTH);
        int d1 = c1.get(Calendar.DATE);
        int h1 = c1.get(Calendar.HOUR);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(time2);
        int y2 = c2.get(Calendar.YEAR);
        int m2 = c2.get(Calendar.MONTH);
        int d2 = c2.get(Calendar.DATE);
        int h2 = c2.get(Calendar.HOUR);
        if (y1 == y2 && m1 == m2 && d1 == d2 && h1 == h2) {
            return true;
        }
        return false;
    }

    /**
     * 判断两个时间戳(Timestamp)是否在同一天
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isTheSameDay(long time1, long time2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        int y1 = c1.get(Calendar.YEAR);
        int m1 = c1.get(Calendar.MONTH);
        int d1 = c1.get(Calendar.DATE);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(time2);
        int y2 = c2.get(Calendar.YEAR);
        int m2 = c2.get(Calendar.MONTH);
        int d2 = c2.get(Calendar.DATE);
        if (y1 == y2 && m1 == m2 && d1 == d2) {
            return true;
        }
        return false;
    }

    /**
     * 判断两个时间戳(Timestamp)是否在同一周
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isTheSameWeek(long time1, long time2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        c1.setFirstDayOfWeek(Calendar.MONDAY);
        int y1 = c1.get(Calendar.YEAR);
        int w1 = c1.get(Calendar.WEEK_OF_YEAR);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(time2);
        c2.setFirstDayOfWeek(Calendar.MONDAY);
        int y2 = c2.get(Calendar.YEAR);
        int w2 = c2.get(Calendar.WEEK_OF_YEAR);
        if (y1 == y2 && w1 == w2) {
            return true;
        }
        return false;
    }

    public static Date parseDate(String date, String pattern) {
        try {
            SimpleDateFormat dateFormat = threadLocalSimpleDateFormat.get();
            dateFormat.applyPattern(pattern);
            return dateFormat.parse(date);
        } catch (Exception e) {
            logger.error("parse date {} ,pattern {} error", date, pattern);
        }
        return null;
    }

    /**
     * 得到日期中的小时
     *
     * @param date
     * @return
     */
    public static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 得到是每周的星期几,注意星期日是1，星期一是2
     *
     * @param date
     * @return
     */
    public static int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 得到是每周的星期几,自动转为星期一是1，星期二是2，...,星期日是7
     *
     * @param date
     * @return
     */
    public static int getDayOfWeek2(Date date) {
        int dayOfWeek = getDayOfWeek(date) - 1;
        return dayOfWeek == 0 ? 7 : dayOfWeek;
    }

    /**
     * 获取下一个最近的hour点时间
     *
     * @param hour
     * @return
     */
    public static long getNextHourTime(int hour, int minute, int second) {
        Calendar cal = Calendar.getInstance();
        int calHour = cal.get(Calendar.HOUR_OF_DAY);
        int calMinute = cal.get(Calendar.MINUTE);
        int calSecond = cal.get(Calendar.SECOND);

        if (calHour > hour || (calHour == hour && calMinute > minute) || (calHour == hour && calMinute == minute && calSecond > second)) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 获取上一个最近的hour点时间
     *
     * @param hour
     * @return
     */
    public static long getLastHourTime(int hour, int minute, int second) {
        Calendar cal = Calendar.getInstance();
        int calHour = cal.get(Calendar.HOUR_OF_DAY);

        if (calHour < hour) {
            cal.add(Calendar.DAY_OF_YEAR, -1);
        }
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * @param dayOfWeek 周一为1，周二为2 ...
     * @param hour
     * @return
     */
    public static long getWeekTime(int dayOfWeek, int hour) {
        Calendar cal = Calendar.getInstance();
        int calDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int calHour = cal.get(Calendar.HOUR_OF_DAY);
        if (calDayOfWeek == 0) {
            calDayOfWeek = 7;
        }
        if (calDayOfWeek < dayOfWeek || (calDayOfWeek == dayOfWeek && calHour < hour)) {
            cal.add(Calendar.DAY_OF_YEAR, -7);
        }

        cal.set(Calendar.DAY_OF_WEEK, dayOfWeek % 7 + 1);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}
