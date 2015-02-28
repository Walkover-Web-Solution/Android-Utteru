package com.Utteru.dtos;

import java.io.Serializable;

/**
 * Created by walkover on 25/2/15.
 */
public class AccessForwardDto implements Serializable {
    String accessNumber;
    String country;
    String countryCode;

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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
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

    String state;
    int _id;

    public AccessForwardDto() {

    }

    public AccessForwardDto(String country, String code, String state, String accessNumber) {

        this.country = country;
        this.countryCode = code;
        this.state = state;
        this.accessNumber = accessNumber;


    }


}
