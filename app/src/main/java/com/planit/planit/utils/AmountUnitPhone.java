package com.planit.planit.utils;

/**
 * Created by תומר on 8/5/2017.
 */

public class AmountUnitPhone {
    private String Amount;
    private String Unit;
    private String Phone;
    private String fullname;

    public AmountUnitPhone() {

    }

    public AmountUnitPhone(String Amount, String Unit, String Phone, String fullname) {
        this.Amount = Amount;
        this.Unit = Unit;
        this.Phone = Phone;
        this.fullname = fullname;
    }

    public AmountUnitPhone(String Amount, String Unit, String Phone) {
        this.Amount = Amount;
        this.Unit = Unit;
        this.Phone = Phone;
    }
    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}