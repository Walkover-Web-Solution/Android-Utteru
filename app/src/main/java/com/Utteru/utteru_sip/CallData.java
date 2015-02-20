package com.Utteru.utteru_sip;

import android.util.Log;

/**
 * Created by vikas on 03/02/15.
 */
public class CallData {


    String callee_name;


    String callee_number;
    String call_price;
    long date;
    long time_elapsed;
    Boolean callType;


    public static CallData data;


    private CallData() {

    }



    public static CallData getCallDateInstance() {
        if (data == null) {
            data = new CallData();
            Log.e("creating new instance ","creating new instance ");
        }

        return data;
    }


    public long getTime_elapsed() {
        return time_elapsed;
    }

    public void setTime_elapsed(long time_elapsed) {
        this.time_elapsed = time_elapsed;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getCall_price() {
        return call_price;
    }

    public void setCall_price(String call_price) {
        this.call_price = call_price;
    }

    public String getCallee_number() {
        return callee_number;
    }

    public void setCallee_number(String callee_number) {
        this.callee_number = callee_number;
    }

    public String getCallee_name() {
        return callee_name;
    }

    public void setCallee_name(String callee_name) {
        this.callee_name = callee_name;
    }

    public Boolean getCallType() {
        return callType;
    }

    public void setCallType(Boolean callType) {
        this.callType = callType;
    }



}
