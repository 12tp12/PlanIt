package com.planit.planit.utils;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by HP on 26-Jun-17.
 */

public class Item {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<AmountUnitPhone> getQuantities() {
        return quantities;
    }

    public void setQuantities(ArrayList<AmountUnitPhone> quantities) {
        this.quantities = quantities;
    }

    private String title;
    private ArrayList<AmountUnitPhone> quantities;

    public Item(String title, ArrayList<AmountUnitPhone> p){
        this.title = title;
        this.quantities = p;
    }

    public Item(String title, String phoneNumber, String amount, String units){
        this.title = title;
        AmountUnit p = new AmountUnit(amount, units);
        this.quantities = new ArrayList<AmountUnitPhone>();
        this.quantities.add(new AmountUnitPhone(amount, units, phoneNumber));
    }



}
