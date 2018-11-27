package com.txzh.walk.Bean;

public class GroupAddInfoBean {
    private String userHead;           //用户头像
    private String userNickName;       //用昵称
    private String userAccounts;       //用户帐号
    private String userID;            //用户Id

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }


    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserAccounts() {
        return userAccounts;
    }

    public void setUserAccounts(String userAccounts) {
        this.userAccounts = userAccounts;
    }

}
