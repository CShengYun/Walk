package com.txzh.walk.ToolClass;

public class NewsCounts {
    private static String SystemPush;  //系统推送通知
    private static String entryGroup;  //申请入群通知
    private static String entryedFroup;//群主邀请入群通知
    private static String entryResult; //入群结果通知

    public static String getSystemPush() {
        return SystemPush;
    }

    public static void setSystemPush(String systemPush) {
        SystemPush = systemPush;
    }

    public static String getEntryGroup() {
        return entryGroup;
    }

    public static void setEntryGroup(String entryGroup) {
        NewsCounts.entryGroup = entryGroup;
    }

    public static String getEntryedFroup() {
        return entryedFroup;
    }

    public static void setEntryedFroup(String entryedFroup) {
        NewsCounts.entryedFroup = entryedFroup;
    }

    public static String getEntryResult() {
        return entryResult;
    }

    public static void setEntryResult(String entryResult) {
        NewsCounts.entryResult = entryResult;
    }
}
