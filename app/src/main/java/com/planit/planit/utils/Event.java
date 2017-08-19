package com.planit.planit.utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.author;
import static android.R.attr.required;
import static com.planit.planit.R.string.location;

/**
 * Created by HP on 26-Jun-17.
 */

public class Event {

    @Exclude
    private String key;
    private String name;
    private String date;
    private String time;
    private String location;
    private String about;
    private Map<String, Message> chat;
    private Map<String, Item> foodAndDrinks;
    private Map<String, Item> equipment;
    private Map<String, Item> playlist;
    private HashMap<String, Boolean> invited;
    private HashMap<String, Boolean> hosted;

    public Event(){
    // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Event(String name, String date, String time, String location, String about, String userCreator){
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.about = about;

        this.chat = new HashMap<>();;
        this.foodAndDrinks = new HashMap<>();
        this.equipment = new HashMap<>();
        this.playlist = new HashMap<>();
        this.invited = new HashMap<>();
        this.hosted = new HashMap<>();
        this.hosted.put(userCreator, true);
    }

    public void setEvent(Event other)
    {
        this.name = other.name;
        this.location = other.location;
        this.date = other.date;
        this.time = other.time;
        this.about = other.about;

        this.chat = other.chat;;
        this.foodAndDrinks = other.foodAndDrinks;
        this.equipment = other.equipment;
        this.playlist = other.playlist;
        this.invited = other.invited;
        this.hosted = other.hosted;
    }

    public void addHost(User user)
    {
        if (hosted == null)
        {
            this.hosted = new HashMap<>();
        }
        this.hosted.put(user.getPhoneNumber(), true);
    }

    public void addInvited(User user)
    {
        if (invited == null)
        {
            this.invited = new HashMap<>();
        }
        this.invited.put(user.getPhoneNumber(), true);
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String newKey) { this.key = newKey; }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public String getAbout() {
        return about;
    }

    public Map<String, Message> getChat() {
        return chat;
    }

    public Map<String, Item> getFoodAndDrinks() {
        return foodAndDrinks;
    }

    public Map<String, Item> getEquipment() {
        return equipment;
    }

    public Map<String, Item> getPlaylist() {
        return playlist;
    }

    public HashMap<String, Boolean> getInvited() {
        return invited;
    }

    public void setInvited(HashMap<String, Boolean> newInvited)
    {
        this.invited = newInvited;
    }

    public HashMap<String, Boolean> getHosted() {
        return hosted;
    }

    public void setHosted(HashMap<String, Boolean> newHosted)
    {
        this.hosted = newHosted;
    }

    public Map<String, Object> toMapBaseEventInfoTable() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", this.name);
        result.put("date", this.date);
        result.put("time", this.time);
        result.put("location", this.location);
        result.put("about", this.about);

        result.put("chat", this.chat);
        result.put("foodAndDrinks", this.foodAndDrinks);
        result.put("equipment", this.equipment);
        result.put("playlist", this.playlist);

        return result;
    }

    public Map<String, Object> toMapBaseEventUsers() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("invited", this.invited);
        result.put("hosted", this.hosted);

        return result;
    }

}
