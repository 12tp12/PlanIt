package com.planit.planit.utils;

/**
 * Created by HP on 26-Jun-17.
 */

public class Message {


    private String phone;
    private String data;
    private String timestamp;

    public Message(){

    }
    public Message(String phone, String data, String timestamp){
        this.phone = phone;
        this.data = data;
        this.timestamp = timestamp;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
