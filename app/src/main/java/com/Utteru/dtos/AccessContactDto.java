package com.Utteru.dtos;

import java.io.Serializable;

public class AccessContactDto implements Serializable {

    int _id;
    String contact_id, display_name, mobile_number, access_number, extension_number;
    String contactUri;
    String country, state;
    String code;
    String thumbUri;
    Boolean isAccess = false;

    public AccessContactDto() {

    }

    public AccessContactDto(String contact_id, String display_name, String access_number, String mobile_number, String extension_number, String thumbUri, String contactUri, String country, String state, String code) {
        this.contact_id = contact_id;
        this.display_name = display_name;
        this.access_number = access_number;
        this.mobile_number = mobile_number;
        this.extension_number = extension_number;
        this.state = state;
        this.code = code;
        this.country = country;
        this.thumbUri = thumbUri;
        this.contactUri = contactUri;

        if (thumbUri == null || thumbUri.equals(""))
            setIsAccess(true);
        else
            setIsAccess(false);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getThumbUri() {
        return thumbUri;
    }

    public void setThumbUri(String thumbUri) {
        this.thumbUri = thumbUri;
    }

    public String getContactUri() {
        return contactUri;
    }

    public void setContactUri(String conUri) {
        this.contactUri = conUri;
    }

    public Boolean getIsAccess() {
        return isAccess;
    }

    public void setIsAccess(Boolean isAccess) {
        this.isAccess = isAccess;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getAccess_number() {
        return access_number;
    }

    public void setAccess_number(String access_number) {
        this.access_number = access_number;
    }


    public String getExtension_number() {
        return extension_number;
    }

    public void setExtension_number(String extension_number) {
        this.extension_number = extension_number;
    }

    @Override
    public boolean equals(Object o) {
        AccessContactDto dto = (AccessContactDto) o;
        return this.mobile_number.equals(dto.getMobile_number());
    }
}