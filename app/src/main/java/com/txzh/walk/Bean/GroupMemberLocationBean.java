package com.txzh.walk.Bean;

import java.io.Serializable;

public class GroupMemberLocationBean implements Serializable {


    public GroupMemberLocationBean(){

    }

    public String userID;
    public String phone;
    public String nickName;
    public String headPath;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadPath() {
        return headPath;
    }

    public void setHeadPath(String headPath) {
        this.headPath = headPath;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String direction;
    public String latitude;
    public String longitude;



    public GroupMemberLocationBean(String latitude, String longitude, String name, String phone,String headPath) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.headPath=headPath;
        this.nickName=name;

    }
}
