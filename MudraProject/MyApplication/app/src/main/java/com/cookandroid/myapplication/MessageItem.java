package com.cookandroid.myapplication;

public class MessageItem {
    String name;
    String message;
    String time;
    String profileUrl;

    public MessageItem(String name, String message, String time, String profileUrl){
        this.name = name;
        this.message = message;
        this.time = time;
        this.profileUrl = profileUrl;
    }

    // firebase DB에 객체로 값을 읽어올 때
    // 파라미터가 비어있는 생성자 필요
    public MessageItem() {

    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getTime(){
        return time;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}

