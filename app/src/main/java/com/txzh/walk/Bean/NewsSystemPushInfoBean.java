package com.txzh.walk.Bean;

public class NewsSystemPushInfoBean {
    private String content;       //主要内容
    private String date;          //时间
    private String theme;         //主题
    private String status;        //状态

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
