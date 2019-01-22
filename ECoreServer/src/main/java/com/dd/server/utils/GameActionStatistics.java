package com.dd.server.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class GameActionStatistics {

    private static Map<String, GameActionStatistic> actionIdStatistics = new ConcurrentHashMap<>();

    public static GameActionStatistic getActionStatistic(String actionId) {
        GameActionStatistic statistic = actionIdStatistics.get(actionId);
        if (statistic == null) {
            synchronized (actionId) {
                if (!actionIdStatistics.containsKey(actionId)) {
                    statistic = new GameActionStatistic();
                    statistic.setActionId(actionId);
                    actionIdStatistics.put(actionId, statistic);
                }
            }
        }
        return statistic;
    }

    public static Map<String, GameActionStatistic> getAllActionIdStatistics() {
        return actionIdStatistics;
    }

    public static void reqStatistic(String actionId, int reqLength) {
        GameActionStatistic statistic = GameActionStatistics.getActionStatistic(actionId);
        statisticMax(statistic.getReqTotalBytes(), statistic.getReqMaxByte(), statistic.getReqMaxByte70Count(), reqLength);
        statistic.getWaitingInQueueCount().incrementAndGet();
    }

    public static void respStatistic(String actionId, int respLength) {
        GameActionStatistic statistic = GameActionStatistics.getActionStatistic(actionId);
        statisticMax(statistic.getRespTotalBytes(), statistic.getRespMaxByte(), statistic.getRespMaxByte70Count(), respLength);
    }

    public static void handlerStatisticStart(String actionId) {
        GameActionStatistic statistic = GameActionStatistics.getActionStatistic(actionId);
        statistic.getWaitingInQueueCount().decrementAndGet();
        statistic.getRunCount().incrementAndGet();
    }

    public static void handlerStatisticEnd(String actionId, long totalTime, long queueTime) {
        GameActionStatistic statistic = GameActionStatistics.getActionStatistic(actionId);
        statistic.getRunCount().decrementAndGet();
        statistic.getCount().incrementAndGet();
        statisticMax(statistic.getTotalTime(), statistic.getMaxTime(), statistic.getMaxTime70Count(), totalTime);
        statisticMax(statistic.getQueueTotalTime(), statistic.getQueueMaxTime(), statistic.getQueryMaxTime70Count(), queueTime);
    }

    private static void statisticMax(AtomicLong totalValue, AtomicLong maxValue, AtomicLong max70Count, long value) {
        totalValue.addAndGet(value);

        long max = maxValue.get();
        if (value > max) {
            maxValue.compareAndSet(max, value);
            max70Count.set(0);
        } else {
            if (value > max * 7 / 10) {
                max70Count.incrementAndGet();
            }
        }
    }

    public static class GameActionStatistic {
        /**
         * 指令
         */
        private String actionId;
        /**
         * 总次数
         */
        private AtomicLong count = new AtomicLong();
        /**
         * 正在执行中的数量
         */
        private AtomicLong runCount = new AtomicLong();
        /**
         * 执行总时间
         */
        private AtomicLong totalTime = new AtomicLong();
        /**
         * 最长耗时
         */
        private AtomicLong maxTime = new AtomicLong();
        /**
         * 时长在 70% maxTime的请求次数
         */
        private AtomicLong maxTime70Count = new AtomicLong();
        /**
         * 在队列中等待的总时长
         */
        private AtomicLong queueTotalTime = new AtomicLong();
        /**
         * 在队列中等待的最长时长
         */
        private AtomicLong queueMaxTime = new AtomicLong();
        /**
         * 时长在70% queueMaxTime的次数
         */
        private AtomicLong queryMaxTime70Count = new AtomicLong();
        /**
         * 当前在队列中的数量
         */
        private AtomicLong waitingInQueueCount = new AtomicLong();
        /**
         * 有异常的次数
         */
        private AtomicLong errorCount = new AtomicLong();
        /**
         * 请求总字节数
         */
        private AtomicLong reqTotalBytes = new AtomicLong();
        /**
         * 单次请求最长的包
         */
        private AtomicLong reqMaxByte = new AtomicLong();
        /**
         * 包长超过 70% reqMaxByte 的次数
         */
        private AtomicLong reqMaxByte70Count = new AtomicLong();

        /**
         * 发送总字节数
         */
        private AtomicLong respTotalBytes = new AtomicLong();
        /**
         * 单次发送最长的包
         */
        private AtomicLong respMaxByte = new AtomicLong();
        /**
         * 包长超过 70% respMaxByte 的次数
         */
        private AtomicLong respMaxByte70Count = new AtomicLong();

        public void reset() {
            count.set(0);
            runCount.set(0);
            totalTime.set(0);
            maxTime.set(0);
            maxTime70Count.set(0);
            queueTotalTime.set(0);
            queueMaxTime.set(0);
            queryMaxTime70Count.set(0);
            waitingInQueueCount.set(0);
            errorCount.set(0);
            reqTotalBytes.set(0);
            reqMaxByte.set(0);
            reqMaxByte70Count.set(0);
            respTotalBytes.set(0);
            respMaxByte.set(0);
            respMaxByte70Count.set(0);
        }

        public String getActionId() {
            return actionId;
        }

        public void setActionId(String actionId) {
            this.actionId = actionId;
        }

        public AtomicLong getCount() {
            return count;
        }

        public void setCount(AtomicLong count) {
            this.count = count;
        }

        public AtomicLong getRunCount() {
            return runCount;
        }

        public AtomicLong getTotalTime() {
            return totalTime;
        }

        public AtomicLong getMaxTime() {
            return maxTime;
        }

        public AtomicLong getMaxTime70Count() {
            return maxTime70Count;
        }

        public AtomicLong getQueueTotalTime() {
            return queueTotalTime;
        }

        public AtomicLong getQueueMaxTime() {
            return queueMaxTime;
        }

        public AtomicLong getQueryMaxTime70Count() {
            return queryMaxTime70Count;
        }

        public AtomicLong getWaitingInQueueCount() {
            return waitingInQueueCount;
        }

        public AtomicLong getErrorCount() {
            return errorCount;
        }

        public AtomicLong getReqTotalBytes() {
            return reqTotalBytes;
        }

        public AtomicLong getReqMaxByte() {
            return reqMaxByte;
        }

        public AtomicLong getReqMaxByte70Count() {
            return reqMaxByte70Count;
        }

        public AtomicLong getRespTotalBytes() {
            return respTotalBytes;
        }

        public AtomicLong getRespMaxByte() {
            return respMaxByte;
        }

        public AtomicLong getRespMaxByte70Count() {
            return respMaxByte70Count;
        }
    }
}
