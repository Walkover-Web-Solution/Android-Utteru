package com.Utteru.dtos;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by root on 12/10/14.
 */
public class AccessDataDto implements Serializable {
    String accessNumber;
    String country;
    ArrayList<AccessDataDto> statelist;
    String countryCode;
    String state;

    String accessid;

    int _id;


    public ArrayList<AccessDataDto> getStatelist() {
        return statelist;
    }

    public void setStatelist(ArrayList<AccessDataDto> statelist) {
        this.statelist = statelist;
    }


    public String getAccessid() {
        return accessid;
    }

    public void setAccessid(String accessid) {
        this.accessid = accessid;
    }


    public AccessDataDto() {

    }

    public AccessDataDto(String country, String code, String state, String accessNumber) {

        this.country = country;
        this.countryCode = code;
        this.state = state;
        this.accessNumber = accessNumber;

    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAccessNumber() {
        return accessNumber;
    }

    public void setAccessNumber(String accessNumber) {
        this.accessNumber = accessNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    @Override
    public boolean equals(Object o) {

        AccessDataDto dto = (AccessDataDto) o;

        Log.e("" + this.country, "" + dto.getCountry());
        return this.country.toLowerCase().equals((dto.getCountry().toLowerCase()));


    }
}
