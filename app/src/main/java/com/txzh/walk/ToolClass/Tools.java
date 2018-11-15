package com.txzh.walk.ToolClass;

public class Tools {
    public static String accounts;               //帐号
    public static String phongNumber;            //电话号码
    public static String nickName;               //昵称
    public static String sex;                    //性别
    public static String headPhoto;              //头像
    public static int userID;                      //用户id
    public static boolean headPicIfChange;          //判断头像是否改变
    public static boolean isHeadPicIfChange() {
        return headPicIfChange;
    }

    public static void setHeadPicIfChange(boolean headPicIfChange) {
        Tools.headPicIfChange = headPicIfChange;
    }


    public static int getUserID() {
        return userID;
    }

    public static void setUserID(int userID) {
        Tools.userID = userID;
    }


    public static String getHeadPhoto() {
        return headPhoto;
    }

    public static void setHeadPhoto(String headPhoto) {
        Tools.headPhoto = headPhoto;
    }

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
