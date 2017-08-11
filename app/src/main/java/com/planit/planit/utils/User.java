package com.planit.planit.utils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HP on 15-Jun-17.
 */

public class User {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private HashMap<String, Boolean> invited;
    private HashMap<String, Boolean> hosted;
    private String token;

    public User()
    {
        // for firebase use
    }

    public User(String firstName, String lastName, String email, String phoneNumber, String token)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.invited = new HashMap<String, Boolean>();
        this.hosted = new HashMap<String, Boolean>();
        this.token = token;
    }

    public void setUser(User other)
    {
        this.firstName = other.getFirstName();
        this.lastName = other.getLastName();
        this.email = other.getEmail();
        this.phoneNumber = other.getPhoneNumber();
        this.invited = other.getInvited();
        this.hosted = other.getHosted();
        this.token = other.getToken();
    }

    public void addHostedEvent(String eventKey)
    {
        if (this.hosted == null)
        {
            this.hosted = new HashMap<String, Boolean>();
        }
        this.hosted.put(eventKey, true);
    }

    public String getToken() {
        return token;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Exclude
    public String getFullName()
    {
        return this.firstName + " " + this.lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public HashMap<String, Boolean> getInvited() {
        return invited;
    }

    public HashMap<String, Boolean> getHosted() {
        return hosted;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setInvited(HashMap<String, Boolean> invited) {
        this.invited = invited;
    }

    public void setHosted(HashMap<String, Boolean> hosted) {
        this.hosted = hosted;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, Object> toMapUser() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("firstName", this.firstName);
        result.put("lastName", this.lastName);
        result.put("email", this.email);
        result.put("phoneNumber", this.phoneNumber);
        result.put("invited", this.invited);
        result.put("hosted", this.hosted);
        result.put("token", this.token);

        return result;
    }
}
