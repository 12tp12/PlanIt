package com.planit.planit.utils;

/**
 * Created by HP on 29-Jun-17.
 */

public class Utilities {

    public static String encodeKey(String email)
    {
        return email.replace('.', ',');
    }

    public static String decodeKey(String email)
    {
        return email.replace(',', '.');
    }

}
