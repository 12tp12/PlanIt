package com.planit.planit.utils;

/**
 * Created by תומר on 8/5/2017.
 */

public class AmountUnitPhone {
    private int amount;
    private String fullName;

    public AmountUnitPhone() {

    }

    public AmountUnitPhone(int amount, String fullName) {
        this.amount = amount;
        this.fullName = fullName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getFullname() {
        return fullName;
    }

    public void setFullname(String fullName) {
        this.fullName = fullName;
    }
}