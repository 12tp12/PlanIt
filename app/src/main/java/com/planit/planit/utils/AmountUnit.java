package com.planit.planit.utils;



public class AmountUnit {
    private String Amount;
    private String Unit;

    public AmountUnit(){

    }

    public AmountUnit(String Amount, String Unit){
        this.Amount = Amount;
        this.Unit = Unit;
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
}
