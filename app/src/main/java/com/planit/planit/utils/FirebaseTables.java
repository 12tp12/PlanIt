package com.planit.planit.utils;

/**
 * Created by HP on 10-Aug-17.
 */

public interface FirebaseTables {

    /* Main to fetch data fast for home page */
    final static String eventsInfoTable = "events"; // TODO change name to "events-info"

    /* Main table to fetch data on users fast */
    final static String users = "users";

    /* Main table to fetch data of users and their invited/hosted events */
    final static String usersToEvents = "usersToEvents";

    /* Main table to fetch data of events and their invited/hosting users */
    final static String eventsToUsers = "eventsToUsers";
}
