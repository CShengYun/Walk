package com.txzh.walk.ToolClass;

public class Tools {
    public static String accounts;               //帐号
    public static String phongNumber;            //电话号码
    public static String nickName;               //昵称
    public static String sex;                    //性别

    public static String getNickName() {
        return nickName;
    }

    public static void setNickName(String nickName) {
        Tools.nickName = nickName;
    }

    public static String getSex() {
        return sex;
    }

    public static void setSex(String sex) {
        Tools.sex = sex;
    }

    public static String getAccounts() {
        return accounts;
    }

    public static void setAccounts(String accounts) {
        Tools.accounts = accounts;
    }

    public static String getPhongNumber() {
        return phongNumber;
    }

    public static void setPhongNumber(String phongNumber) {
        Tools.phongNumber = phongNumber;
    }

}
