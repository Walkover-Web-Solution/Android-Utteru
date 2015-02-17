package com.Utteru.dtos;

import java.io.Serializable;

public class VerifiedData implements Serializable {

    int id;
    int type, state;
    String particualr, countryCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getParticualr() {
        return particualr;
    }

    public void setParticualr(String particualr) {
        this.particualr = particualr;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }


}
