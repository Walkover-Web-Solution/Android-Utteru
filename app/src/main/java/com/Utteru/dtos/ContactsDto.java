package com.Utteru.dtos;

import java.io.Serializable;

public class ContactsDto implements Serializable {


    int _id;
    String source_name, source_number, destination_number, destination_name;
    String duration, date, uniqueId, price;


    public ContactsDto() {

    }

    public ContactsDto(String source_name, String source_number, String destination_name, String destination_number, String date) {
        this.source_name = source_name;
        this.source_number = source_number;
        this.destination_name = destination_name;
        this.destination_number = destination_number;
        this.date = date;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getSource_name() {
        return source_name;
    }

    public void setSource_name(String source_name) {
        this.source_name = source_name;
    }

    public String getSource_number() {
        return source_number;
    }

    public void setSource_number(String source_number) {
        this.source_number = source_number;
    }

    public String getDestination_number() {
        return destination_number;
    }

    public void setDestination_number(String destination_number) {
        this.destination_number = destination_number;
    }

    public String getDestination_name() {
        return destination_name;
    }

    public void setDestination_name(String destination_name) {
        this.destination_name = destination_name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


}
