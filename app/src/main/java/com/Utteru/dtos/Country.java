package com.Utteru.dtos;

import android.util.Log;


public class Country implements Comparable<Country> {

    String countryName;
    String countryCode;
    String countryIso;
    String price;


    Boolean isSection;
    String currency;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryIso() {
        return countryIso;
    }

    public void setCountryIso(String countryIso) {
        this.countryIso = countryIso;
    }

    public Boolean getIsSection() {
        return isSection;
    }

    public void setIsSection(Boolean isSection) {
        this.isSection = isSection;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public int compareTo(Country another) {

        Log.e("country name", "" + countryName);
        Log.e("country name", "" + another.countryName);


        int i = this.countryName.compareTo(another.countryName);
        return i;
    }


}
