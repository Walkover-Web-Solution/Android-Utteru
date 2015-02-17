package com.Utteru.dtos;

/**
 * Created by root on 12/26/14.
 */
public class TransactionLogsDto {

    int _id, type;
    String transaction_id;
    String date;
    String amount;
    String currency;
    String description;
    String current_balance;
    String payment_mode;
    String admin_name;


    String user_name;


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrentBalance() {
        return current_balance;
    }

    public void setCurrent_balance(String balance_status) {
        this.current_balance = balance_status;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getAdmin_name() {

        if(admin_name==null)
            admin_name="";
        return admin_name;
    }

    public void setAdmin_name(String admin_name) {
        if(admin_name==null)
        {
            admin_name="";
        }
        this.admin_name = admin_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }



}
