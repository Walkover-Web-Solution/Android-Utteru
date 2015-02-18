package com.Utteru.utteru_sip;

import android.util.Log;

import java.util.Date;

/**
 * Created by vikas on 03/02/15.
 */
public class CallData {



    String callee_name;


    String callee_number;
    String call_price;

    long date;

    public void setTime_elapsed(long init_time) {
        this.time_elapsed = init_time;
    }

    long time_elapsed;
    Boolean callType;

    public CallData(String name,String number,String price,long time_elapsed,Boolean type ,long date)
    {
        Log.e("created ","call data ");

        this.callee_name =name;
        this.callee_number = number;
        this.call_price=price;
        this.time_elapsed = time_elapsed;

        this. callType =type;
        this.date=date;

    }

    public String getCallee_number() {
        return callee_number;
    }

    public String getCallee_name() {
        return callee_name;
    }

    public String getCall_price() {
        return call_price;
    }

    public long getTime_elapsed() {
        return time_elapsed;
    }

    public Boolean getCallType() {
        return callType;
    }

    public void setPrice (String price )
    {
        call_price = price;
    }


    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }



}
