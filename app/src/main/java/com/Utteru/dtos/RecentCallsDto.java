package com.Utteru.dtos;


import java.io.Serializable;

public class RecentCallsDto implements Serializable {

    int _id;
    String name;
    String number;
    String duration;
    String time;
    String price;
    String source_number;



    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getSource_number() {
        return source_number;
    }

    public void setSource_number(String source_number) {
        this.source_number = source_number;
    }


}
