package com.planit.planit.utils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.GetTokenResult;

import java.util.ArrayList;

/**
 * Created by HP on 15-Jun-17.
 */

public class User {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private ArrayList<Event> invited;
    private ArrayList<Event> hosted;
    private String token;

    public User(String firstName, String lastName, String email, String phoneNumber, String token)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.invited = new ArrayList<Event>();
        this.hosted = new ArrayList<Event>();
        this.token = token;
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

    public ArrayList<Event> getInvited() {
        return invited;
    }

    public ArrayList<Event> getHosted() {
        return hosted;
    }
}
