package com.dd.game.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class SpamFilter {

    public static boolean isSimilarMsg(String msg1, String msg2) {
        if (StringUtils.isBlank(msg1) || StringUtils.isBlank(msg2)) {
            return false;
        }
        // 根据长度先排除一遍
        if (!similarMsgByLength(msg1, msg2)) {
            return false;
        }

        // 把中文单独提出来计算
        StringBuilder chinese1 = new StringBuilder();
        StringBuilder chinese2 = new StringBuilder();

        for (char c : msg1.toCharArray()) {
            if (isChineseOrDigit(c)) {
                chinese1.append(c);
            }
        }
        for (char c : msg2.toCharArray()) {
            if (isChineseOrDigit(c)) {
                chinese2.append(c);
            }
        }
        float chinesePercent1 = (float) chinese1.length() / msg1.length();
        float chinesePercent2 = (float) chinese2.length() / msg2.length();
        // 中文占比大于30%的话，那就以中文为主进行判断
        if (chinesePercent1 > 0.3 && chinesePercent2 > 0.3) {
            return similarMsg(chinese1.toString(), chinese2.toString());
        } else if (chinese1.length() > 8 && chinese2.length() > 8) {
            return similarMsg(chinese1.toString(), chinese2.toString());
        } else {
            return similarMsg(msg1, msg2);
        }
    }

    /*
     * 两个字符串判断相似性，如果长度大于subLength，则先用余弦定理来算一下。否则直接计算编辑距离。
     * 余弦定理判定相似后，有可能误判，将原文分成长度为subLength的子串，计算每一字串相应的编辑距离。
     * 如果最终发现70%的字串都相似，那认为两段话相似。
     */
    private static boolean similarMsg(String msg1, String msg2) {
        int subLength = 20;
        if (msg1.length() > subLength && msg2.length() > subLength) {
            if (similarMsgByCosine(msg1, msg2)) {
                String[] msgArr1 = splitByLength(msg1, subLength);
                String[] msgArr2 = splitByLength(msg2, subLength);
                double similarValue = 0;
                for (int i = 0; i < Math.min(msgArr1.length, msgArr2.length); i++) {
                    similarValue += getSimilarValueByLevenshtein(msgArr1[i], msgArr2[i])
                            * Math.max(msgArr1[i].length(), msgArr2[i].length());
                }
                double similar = (similarValue / Math.max(msg1.length(), msg1.length()));
                if (similar >= 0.7) {
                    return true;
                } else {
                    // 余弦定理是相似的，但是子串编辑距离不相似
                    return false;
                }
            } else {
                return false;
            }
        } else {
            double similar = getSimilarValueByLevenshtein(msg1, msg2);
            if (similar >= 0.9) {
                return true;
            } else {
                return false;
            }
        }
    }

    /*
     * 利用余弦定理来判断字符串相似性
     *
     * x1*y1+x2*y2+...+xn*yn/(sqrt(x1*x1+x2*x2+...+xn*xn)*(y1*y1+y2*y2+...+yn*yn
     * )))
     */
    private static boolean similarMsgByCosine(String msg1, String msg2) {
        Map<Character, int[]> charMap = new HashMap<Character, int[]>();
        for (char c : msg1.toCharArray()) {
            int[] nums = null;
            if (charMap.containsKey(c)) {
                nums = charMap.get(c);
            }
            if (nums == null) {
                nums = new int[2];
                nums[0] = 0;
                nums[1] = 0;
            }
            nums[0]++;
            charMap.put(c, nums);
        }
        for (char c : msg2.toCharArray()) {
            int[] nums = null;
            if (charMap.containsKey(c)) {
                nums = charMap.get(c);
            }
            if (nums == null) {
                nums = new int[2];
                nums[0] = 0;
                nums[1] = 0;
            }
            nums[1]++;
            charMap.put(c, nums);
        }
        float molecular = 0;
        float denominator1 = 0;
        float denominator2 = 0;
        for (char c : charMap.keySet()) {
            int[] nums = charMap.get(c);
            if (nums.length != 2) {
                continue;
            }
            molecular += nums[0] * nums[1];
            denominator1 += nums[0] * nums[0];
            denominator2 += nums[1] * nums[1];
        }
        double similar = molecular / (Math.sqrt(denominator1 * denominator2));
        return similar >= 0.9;
    }

    /*
     * 利用Levenshtein距离判断字符串相似性
     */
    @SuppressWarnings("unused")
    private static boolean similarMsgByLevenshtein(String msg1, String msg2) {
        return getSimilarValueByLevenshtein(msg1, msg2) >= 0.9;
    }

    private static double getSimilarValueByLevenshtein(String msg1, String msg2) {
        int distance = getLevenshteinDistance(msg1, msg2);
        double similar = 1.0 - (double) distance / Math.max(msg1.length(), msg2.length());
        return similar;
    }

    /*
     * 字符串相似的长度规则 如果长度相差很多，则直接判定为不相似
     */
    private static boolean similarMsgByLength(String msg1, String msg2) {
        int length1 = msg1.length();
        int length2 = msg2.length();
        float min = Math.min(length1, length2);
        float diff = Math.abs(length1 - length2) / min;
        return diff <= 0.1;
    }

    /**
     * 判断某一个字符是否是汉字
     * 
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    public static boolean isChineseOrDigit(char c) {
        return isChinese(c) || Character.isDigit(c);
    }

    private static int min(int first, int... others) {
        for (int i : others) {
            first = Math.min(first, i);
        }
        return first;
    }

    /*
     * 计算Levenshtein距离
     */
    private static int getLevenshteinDistance(String str1, String str2) {
        int n = str1.length();
        int m = str2.length();
        if (n == 0)
            return m;
        if (m == 0)
            return n;
        int[][] d = new int[n + 1][m + 1]; // 记录矩阵
        for (int i = 0; i <= n; i++)
            d[i][0] = i; // 初始化行
        for (int i = 0; i <= m; i++)
            d[0][i] = i; // 初始化列
        for (int i = 1; i <= n; i++) {
            char ch1 = str1.charAt(i - 1);
            for (int j = 1; j <= m; j++) {
                char ch2 = str2.charAt(j - 1);
                int temp = ch1 == ch2 ? 0 : 1; // 若两字符相等这斜上方的加权值为0,否则为1
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
            }
        }
        return d[n][m];
    }

    private static String[] splitByLength(String msg, int length) {
        if (msg.length() <= length) {
            return new String[] { msg };
        } else {
            int len = msg.length() / length;
            if ((msg.length() % length) != 0) {
                len += 1;
            }
            String[] res = new String[len];
            for (int i = 0; i < res.length; i++) {
                int begin = i * length;
                int end = begin + length;
                if (end > msg.length()) {
                    end = msg.length();
                }
                res[i] = msg.substring(begin, end);
            }
            return res;
        }
    }

    static final char DBC_CHAR_START = 33; // 半角!
    static final char DBC_CHAR_END = 126; // 半角~
    static final char SBC_CHAR_START = 65281; // 全角！
    static final char SBC_CHAR_END = 65374; // 全角～
    static final int CONVERT_STEP = 65248; // 全角半角转换间隔
    static final char SBC_SPACE = 12288; // 全角空格 12288
    static final char DBC_SPACE = ' '; // 半角空格

    /**
     * 全角转半角
     * 
     * @param src
     * @return
     */
    public static String qj2bj(String src) {
        if (src == null) {
            return src;
        }
        StringBuilder buf = new StringBuilder(src.length());
        char[] ca = src.toCharArray();
        for (int i = 0; i < src.length(); i++) {
            if (ca[i] >= SBC_CHAR_START && ca[i] <= SBC_CHAR_END) {
                buf.append((char) (ca[i] - CONVERT_STEP));
            } else if (ca[i] == SBC_SPACE) {
                buf.append(DBC_SPACE);
            } else {
                buf.append(ca[i]);
            }
        }
        return buf.toString();
    }

    /**
     * 判断消息中是否包含unicode控制字符
     * 
     * @param msg
     * @return
     */
    public static boolean hasISOcontrol(String msg) {
        for (char c : msg.toCharArray()) {
            if (Character.isISOControl(c)) {
                if (c == '\n') {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    public static String changeISOcontrol(String msg) {
        StringBuffer sb = new StringBuffer();
        for (char c : msg.toCharArray()) {
            if (Character.isISOControl(c)) {
                if (c == '\n') {
                    sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static boolean containISOcontrol(String msg) {
        for (char c : msg.toCharArray()) {
            if (Character.isISOControl(c)) {
                return true;
            }
        }
        return false;
    }

}
