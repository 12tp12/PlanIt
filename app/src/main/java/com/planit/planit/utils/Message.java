package com.planit.planit.utils;


import android.util.Log;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Created by HP on 26-Jun-17.
 */

public class Message{

    private String phone;
    private String name;
    private String content;
    private String currentTime;

    public Message(){

    }

    public Message(String phone, String name, String content){
        this.phone = phone;
        this.name = name;
        this.content = content;
        //this.currentTime = genCurrentTime();
        this.currentTime = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(new Date());
        Log.d("check", "current time is " + this.currentTime);
    }

    private String genCurrentTime()
    {
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        Log.d("message time", "hour is " + hour);
        String hourStr = String.valueOf(hour);
        if (hour < 10)
        {
            hourStr = "0" + hourStr;
        }
        int minute = calendar.get(Calendar.MINUTE);
        Log.d("message time", "minute is " + minute);
        String minuteStr = String.valueOf(minute);
        if (minute < 10)
        {
            minuteStr = "0" + minuteStr;
        }
        return hourStr + ":" + minuteStr;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) { this.phone = phone; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    @Exclude
    public String getDate() { return this.currentTime.split(" ")[0]; }

    @Exclude
    public String getHour() { return this.currentTime.split(" ")[1]; }

}
