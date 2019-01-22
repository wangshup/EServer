package com.dd.game.utils;

import com.google.api.client.util.Lists;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomUtil {
    private RandomUtil() {
    }

    public static int random(int max) {
        ThreadLocalRandom R = ThreadLocalRandom.current();
        return R.nextInt(max);
    }

    /**
     * 计算 min <= value < max 之间的均匀分布随机值<br>
     *
     * @param min
     * @param max
     * @return value
     * @author wangsp 2016.06.24
     */
    public static int random(int min, int max) {
        if (min == max) return min;
        return (min + random(max - min));
    }

    public static boolean isProbHit(double value) {
        return ((int) (value * 100) > random(0, 100));
    }

    public static double random() {
        ThreadLocalRandom R = ThreadLocalRandom.current();
        return R.nextDouble();
    }

    /**
     * 根据权重随机，返回对应的索引值
     *
     * @param rates 存放的权重值
     * @return 随机对应的索引值
     */
    public static int randomIndex(List<Integer> rates) {
        int totalRates = 0;
        for (int rate : rates) {
            totalRates += rate;
        }
        if (totalRates == 0) {
            return -1;
        }
        int randomRate = random(totalRates);

        for (int m = 0; m < rates.size(); m++) {
            if (randomRate < rates.get(m)) {
                return m;
            } else {
                randomRate -= rates.get(m);
            }
        }
        return -1;
    }

    /**
     * 根据权重随机，返回对应的索引值
     *
     * @param rates 存放的权重值
     * @return 随机对应的索引值
     */
    public static int randomIndex(int[] rates) {
        int totalRates = 0;
        for (int rate : rates) {
            totalRates += rate;
        }
        if (totalRates == 0) {
            return -1;
        }
        int randomRate = random(totalRates);
        for (int m = 0; m < rates.length; m++) {
            if (randomRate < rates[m]) {
                return m;
            } else {
                randomRate -= rates[m];
            }
        }
        return -1;
    }

    /**
     * 随机选择一个值
     * <p>
     * str 格式如下: 值;权重|值;权重
     *
     * @param str
     * @return
     */
    public static int randomValue(String str) {
        Map<Integer, Integer> map = CommonUtils.string2map(str, Integer.class, Integer.class);
        List<Integer> ids = Lists.newArrayList();
        List<Integer> weights = Lists.newArrayList();
        for (Entry<Integer, Integer> entry : map.entrySet()) {
            ids.add(entry.getKey());
            weights.add(entry.getValue());
        }
        int chooseIndex = randomIndex(weights);
        return ids.get(chooseIndex);
    }

    /**
     * 随机选择一个 value 值
     *
     * @param value 格式 value1;value2|value3;value4
     * @param rate  格式 rate1;rate2|rate3;rate4
     * @param index 默认0，取第几组
     * @return
     */
    public static int randomValue(String value, String rate, int index) {
        String[] valueStrs = value.split("\\|");
        String[] rateStrs = rate.split("\\|");

        List<Integer> values = CommonUtils.string2list(valueStrs[index], Integer.class, ";");
        List<Integer> rates = CommonUtils.string2list(rateStrs[index], Integer.class, ";");

        return values.get(randomIndex(rates));
    }
}
