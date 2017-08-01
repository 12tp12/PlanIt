package com.planit.planit.utils;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.author;
import static android.R.attr.required;
import static com.planit.planit.R.string.location;

/**
 * Created by HP on 26-Jun-17.
 */

public class Event {

    private String name;
    private String date;
    private String time;
    private String location;
    private String about;
    private Map<String, Message> chat;
    private Map<String, Item> foodAndDrinks;
    private Map<String, Item> equipment;
    private Map<String,Item> playlist;
    private ArrayList<String> invited;
    private ArrayList<String> hosted;

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
        this.invited = new ArrayList<>();
        this.hosted = new ArrayList<>();
        this.hosted.add(userCreator);
    }

    public Map<String, Object> toMapBaseEvent() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("EventName", this.name);
        result.put("Date", this.date);
        result.put("Time", this.time);
        result.put("Location", this.location);
        result.put("About", this.about);

        result.put("Chat", this.chat);
        result.put("FoodAndDrinks", this.foodAndDrinks);
        result.put("Equipment", this.equipment);
        result.put("Playlist", this.playlist);
        result.put("Invited", this.invited);
        result.put("Hosted", this.hosted);

        return result;
    }

}
