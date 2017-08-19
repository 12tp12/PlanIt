package com.planit.planit.utils;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by HP on 26-Jun-17.
 */

public class Item {
    private String title;
    private String unit;
    private int neededAmount;
    private String requestedByPhone;
    private String requestedByName;
    private HashMap<String, AmountUnitPhone> quantities;

    public Item () {
        // for firebase usage
    }

    public Item(String title, int neededAmount, String unit, String requestedByPhone, String requestedByName){
        this.title = title;
        this.unit = unit;
        this.neededAmount = neededAmount;
        this.requestedByPhone = requestedByPhone;
        this.requestedByName = requestedByName;
        this.quantities = new HashMap<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HashMap<String, AmountUnitPhone> getQuantities() {
        return quantities;
    }

    public void setQuantities(HashMap<String, AmountUnitPhone> quantities) {
        this.quantities = quantities;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public String getRequestedByPhone() {
        return this.requestedByPhone;
    }

    public void setRequestedByPhone(String requestedByPhone) {
        this.requestedByPhone = requestedByPhone;
    }

    public String getRequestedByName() {
        return this.requestedByName;
    }

    public void setRequestedByName(String requestedByName) {
        this.requestedByName = requestedByName;
    }

    public void addQuantity(String phone, int amount, String fullName)
    {
        if (this.quantities == null)
        {
            this.quantities = new HashMap<>();
        }
        this.quantities.put(phone, new AmountUnitPhone(amount, fullName));
    }

    public int getNeededAmount() { return this.neededAmount; }

    public void setNeededAmount(int neededAmount) { this.neededAmount = neededAmount; }

    public int getLeftNeeded()
    {
        if (this.quantities == null || this.quantities.isEmpty())
        {
            return this.neededAmount;
        }
        int sum = 0;
        for (Map.Entry<String, AmountUnitPhone> unit : this.quantities.entrySet())
        {
            sum += unit.getValue().getAmount();
        }
        return this.neededAmount - sum;
    }

    public boolean isUserInQuantites(String phoneToCheck)
    {
        return (this.quantities != null) && this.quantities.keySet().contains(phoneToCheck);
    }

    public HashMap<String, Object> toFirebaseMap()
    {
        HashMap<String, Object> result = new HashMap<>();

        result.put("neededAmount", this.neededAmount);
        result.put("unit", this.unit);
        result.put("requestedByPhone", this.requestedByPhone);
        result.put("requestedByName", this.requestedByName);
        result.put("quantities", this.quantities);

        return result;
    }
}
