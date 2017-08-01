package com.planit.planit.utils;

/**
 * Created by HP on 26-Jun-17.
 */

public class Message {


    private User user;
    private String data;
    private String timestamp;

    public Message(){

    }
    public Message(User user, String data, String timestamp){
        this.user = user;
        this.data = data;
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
