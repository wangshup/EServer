package com.dd.server.utils;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author wangshupeng
 */
public final class IdWorker {
    private static final long twepoch = 1500825600000L; //max 17 years
    private static final long workerIdBits = 16L; //max 65535
    private static final long workerIdMask = (1L << workerIdBits) - 1;
    private static final long sequenceBits = 8L; //max 256*1000/ms
    private static final long workerIdShift = sequenceBits;
    private static final long timestampLeftShift = sequenceBits + workerIdBits;
    private static final long sequenceMask = (1L << sequenceBits) - 1;
    private static long sequence = 0L;
    private static long lastTimestamp = -1L;

    public static synchronized long nextId(int workerId) {
        long timestamp = timeGen();
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = ThreadLocalRandom.current().nextInt(10);
        }
        if (timestamp < lastTimestamp) {
            try {
                throw new Exception(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        lastTimestamp = timestamp;
        long nextId = ((timestamp - twepoch << timestampLeftShift)) | ((workerId & workerIdMask) << workerIdShift) | (sequence);
        return nextId;
    }

    private static long tilNextMillis(final long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private static long timeGen() {
        return System.currentTimeMillis();
    }

    public static String nextUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    public static int getServerId(long id) {
        return (int) ((id >> workerIdShift) & workerIdMask);
    }

    public static long getTimeMills(long id) {
        return ((id >> timestampLeftShift) + twepoch);
    }

    public static long timeShift(long time) {
        return ((time - twepoch << timestampLeftShift));
    }
}
