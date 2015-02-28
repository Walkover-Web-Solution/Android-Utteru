package com.Utteru.parse;

import java.io.Serializable;

/**
 * Created by vikas on 26/02/15.
 */
public class ContactsDto implements Serializable{

    String number;
    String userNumber;
    Boolean status,state;
    String name;



    String objectId;


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    @Override
    public boolean equals(Object o) {

        ContactsDto dto  = (ContactsDto)o;
        return  this.number.equals(dto.getNumber());
    }
}
