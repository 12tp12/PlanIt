package com.planit.planit.utils;
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

    public User(String firstName, String lastName, String email, String phoneNumber)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.invited = new ArrayList<Event>();
        this.hosted = new ArrayList<Event>();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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
