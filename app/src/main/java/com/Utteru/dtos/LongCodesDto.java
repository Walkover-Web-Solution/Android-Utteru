package com.Utteru.dtos;

import java.io.Serializable;

/**
 * Created by walkover on 27/2/15.
 */
public class LongCodesDto implements Serializable {

    String longCodeType;
    String assignId;
    String expiryDate;
    String longCodeNo;
    String sourceNo;
    String destinationNo;
    String planId;
    String renew;
    String country;
    String state;
    String callerid;

    public String getCallerid() {
        return callerid;
    }

    public void setCallerid(String callerid) {
        this.callerid = callerid;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getBal_deduct() {
        return bal_deduct;
    }

    public void setBal_deduct(String bal_deduct) {
        this.bal_deduct = bal_deduct;
    }

    String duration;
    String date_time;
    String bal_deduct;


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




    public String getLongCodeType() {
        return longCodeType;
    }

    public void setLongCodeType(String longCodeType) {
        this.longCodeType = longCodeType;
    }

    public String getAssignId() {
        return assignId;
    }

    public void setAssignId(String assignId) {
        this.assignId = assignId;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getLongCodeNo() {
        return longCodeNo;
    }

    public void setLongCodeNo(String longCodeNo) {
        this.longCodeNo = longCodeNo;
    }

    public String getSourceNo() {
        return sourceNo;
    }

    public void setSourceNo(String sourceNo) {
        this.sourceNo = sourceNo;
    }

    public String getDestinationNo() {
        return destinationNo;
    }

    public void setDestinationNo(String destinationNo) {
        this.destinationNo = destinationNo;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getRenew() {
        return renew;
    }

    public void setRenew(String renew) {
        this.renew = renew;
    }



/*
    public LongCodesDto(String longCodeNo, String assignId, String expiryDate, String sourceNo,String destinationNo, String planId, String longCodeType) {

        this.longCodeNo = longCodeNo;
        this.expiryDate = expiryDate;
        this.sourceNo = sourceNo;
        this.destinationNo = destinationNo;
        this.planId = planId;
        this.longCodeType = longCodeType;

    }*/

}
