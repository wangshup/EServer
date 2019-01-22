package com.dd.game.utils;

import com.dd.game.entity.player.Player;
import org.apache.commons.lang3.StringUtils;

public final class FunctionSwitch {


    private FunctionSwitch() {
    }

    public static boolean isFunctionOn(int functionId, String appVer, int playerLevel) {
        //if (!ConfigTable.containsKey("switch", functionId)) {
        //    return false;
        //}
        //
        //int switchOpen = ConfigTable.getInt("switch", functionId, "switch");
        //if (switchOpen == 0)
        //    return false;
        //String minVersion = ConfigTable.getString("switch", functionId, "minversion");
        //String maxVersion = ConfigTable.getString("switch", functionId, "maxversion");
        //String servers = ConfigTable.getString("switch", functionId, "servers");
        //int leaderLv = Integer.valueOf(ConfigTable.getString("switch", functionId, "leaderlv"));
        //if (leaderLv != 0 && leaderLv > playerLevel) {
        //    return false;
        //}
        //if (checkVersion(appVer, minVersion, maxVersion) && checkInServer(servers)) {
        //    return true;
        //}
        return false;
    }


    public static String buildFunctionStr(Player player, int playerLevel) {
        StringBuilder sb = new StringBuilder();
        //Map<String, Map<String, Object>> rows = ConfigTable.getTableRows("switch");
        //String separator = "";
        //for (Entry<String, Map<String, Object>> entry : rows.entrySet()) {
        //    String id = entry.getKey();
        //    sb.append(separator);
        //    sb.append(id);
        //    sb.append(";");
        //    sb.append(isABTestFunctionOn(Integer.valueOf(id), player, playerLevel) ? 1 : 0);
        //    separator = "|";
        //}
        return sb.toString();
    }

    private static boolean checkInServer(String servers) {
        int serverId = Constants.SERVER_ID;
        String[] serverArray = servers.split("\\|");
        for (String serverStr : serverArray) {
            String[] strs = serverStr.split(";");
            if (strs.length == 1) {
                if (serverId == Integer.parseInt(strs[0])) {
                    return true;
                }
            } else if (strs.length == 2) {
                if (serverId >= Integer.parseInt(strs[0]) && serverId <= Integer.parseInt(strs[1])) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean checkVersion(String version, String minVersion, String maxVersion) {
        if (!"-1".equals(minVersion) && !StringUtils.isEmpty(minVersion)) {
            if (compareVersion(version, minVersion) == -1) {
                return false;
            }
        }
        if (!"-1".equals(maxVersion) && !StringUtils.isEmpty(maxVersion)) {
            if (compareVersion(version, maxVersion) == 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * <pre>
     * 0 : version1==version2
     * 1 : version1>version2
     * -1 : version1<version2
     * </pre>
     *
     * @return
     */
    public static int compareVersion(String version1, String version2) {
        int versionNum1[] = getIntArray(StringUtils.split(version1, '.'));
        int versionNum2[] = getIntArray(StringUtils.split(version2, '.'));
        int comPare;
        if (versionNum1[0] > versionNum2[0]) {
            comPare = 1;
        } else if (versionNum1[0] < versionNum2[0]) {
            comPare = -1;
        } else if (versionNum1[1] > versionNum2[1]) {
            comPare = 1;
        } else if (versionNum1[1] < versionNum2[1]) {
            comPare = -1;
        } else if (versionNum1[2] > versionNum2[2]) {
            comPare = 1;
        } else if (versionNum1[2] < versionNum2[2]) {
            comPare = -1;
        } else {
            comPare = 0;
        }
        return comPare;
    }

    private static int[] getIntArray(String[] strs) {
        int[] array = new int[strs.length];
        for (int m = 0; m < array.length; m++) {
            array[m] = Integer.parseInt(strs[m]);
        }
        return array;
    }
}
