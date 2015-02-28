package com.Utteru.commonUtilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.Utteru.dtos.VerifiedData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Prefs {
    private static final String USER_NAME = "username";
    private static final String USER_PASSWORD = "userpassword";
    private static final String USER_DISPLAY = "user_display";
    private static final String USER_ID = "user_id";
    private static final String DIALER_COUNTRY_CODE = "dialer_country_code";
    private static final String DIALER_COUNTRY_NAME = "dialer_country_name";
    private static final String SERVER_URL = "server_url";
    private static final String LAST_CALL_NO = "last_call_no";
    private static final String USER_BALANCE = "user_balance";
    private static final String USER_TARRIFF = "user_tariff";
    private static final String LAST_ACTIVITY = "last_activity";
    private static final String USER_TYPE = "user_type";
    private static final String COUNTRY_CODE = "country_code";
    private static final String COUNTRY_NAME = "country_name";
    private static final String RESELLERID = "resllerId";
    private static final String POSITION_ARRAY = "position_array";
    private static final String LISTENVOICE = "listen_voice";
    private static final String GENDER = "user_gender";
    private static final String USERCURRENCY = "user_currency";
    private static final String GROUP_ID = "group_id";
    private static final String USER_DEFAULT_NUMBER = "user_default_number";
    private static final String GCMID = "gcmid";
    private static final String GCMIDSTATE = "gcmidstate";
    private static final String VERIFIEDNUMLIST = "verified_number_list";
    private static final String VERIFIEDEMAILLIST = "verified_email_list";
    private static final String PROMOCODE = "promoCode";
    private static final String USERSIPNAME = "user_sip_name";
    private static final String USERSIPPASSWORD = "user_sip_password";
    private static final String TOTALEARN = "total_earn";
    private static final String PLAN_RATE = "plan_rate";
    private static final String ASSIGN_ID = "assignId";
    private static final String PLAN_ID = "planid";
    public static String SIP_SERVER = "sip.phone91.com";
    public static String PERSE_SIP = "45.56.69.233";
    public static String SIP_PORT = "27376";
    public static String PERSE_PORT = "27376";


    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("mypref", Context.MODE_MULTI_PROCESS | Context.MODE_WORLD_READABLE);
    }

    public static String getUserName(Context context) {
        String value = getPrefs(context).getString(USER_ID, "");
        value = value.replace("+", "");
        return value;
    }
    public static String getAssignId(Context context) {
        String value = getPrefs(context).getString(ASSIGN_ID, "");
        value = value.replace("+", "");
        return value;
    }
    public static void setAssignId(Context context, String value) {

        getPrefs(context).edit().putString(ASSIGN_ID, value).commit();
    }
    public static String getPlanRate(Context context) {
        String value = getPrefs(context).getString(PLAN_RATE, "");
        value = value.replace("+", "");
        return value;
    }

    public static void setGroupId(Context context, String value) {

        getPrefs(context).edit().putString(GROUP_ID, value).commit();
    }

    public static String getGroupId(Context context) {
        return getPrefs(context).getString(GROUP_ID, "");
    }

    public static void setLastCallNo(Context context, String value) {

        getPrefs(context).edit().putString(LAST_CALL_NO, value).commit();
    }

    public static String getLastCallNo(Context context) {
        String value = getPrefs(context).getString(LAST_CALL_NO, "");

        return value;
    }

    public static void setUserName(Context context, String value) {
        value = value.replace("+", "");
        getPrefs(context).edit().putString(USER_NAME, value).commit();
    }

    public static void setUserPassword(Context context, String value) {
        // perform validation etc..
        getPrefs(context).edit().putString(USER_PASSWORD, value).commit();
    }

    public static String getUserPassword(Context context) {
        return getPrefs(context).getString(USER_PASSWORD, "");
    }

    public static void setUserSipName(Context context, String value) {
        // perform validation etc..
        getPrefs(context).edit().putString(USERSIPNAME, value).commit();
    }

    public static String getUserSipName(Context context) {
        return getPrefs(context).getString(USERSIPNAME, "");
    }

    public static void setUserSipPassword(Context context, String value) {
        // perform validation etc..
        getPrefs(context).edit().putString(USERSIPPASSWORD, value).commit();
    }

    public static String getUserSipPassword(Context context) {
        return getPrefs(context).getString(USERSIPPASSWORD, "");
    }


    public static String getUserActualName(Context context) {
        return getPrefs(context).getString(USER_NAME, "");
    }


    public static String getServerUrl(Context context) {
        return getPrefs(context).getString(SERVER_URL, "https://voice.utteru.com/api/");
    }


    public static String getUserBalance(Context context) {
        return getPrefs(context).getString(USER_BALANCE, "00.00");
    }

    public static void setUserBalance(Context context, String value) {
        getPrefs(context).edit().putString(USER_BALANCE, value).commit();
    }

    public static String getLastActivity(Context context) {
        return getPrefs(context).getString(LAST_ACTIVITY, "");
    }

    public static void setLastActivity(Context context, String activityName) {
        getPrefs(context).edit().putString(LAST_ACTIVITY, activityName).commit();
    }

    public static String getUserBalanceAmount(Context context) {
        String balance = getPrefs(context).getString(USER_BALANCE, "00.00");
        String[] bal_array = balance.split("\\s+");
        return bal_array[0];

    }

    public static String getPlanId(Context context) {
        return getPrefs(context).getString(PLAN_ID, "");
    }

    public static String getUserTariff(Context context) {
        return getPrefs(context).getString(USER_TARRIFF, "84");
    }

    public static void setUserTarrif(Context context, String value) {
        getPrefs(context).edit().putString(USER_TARRIFF, value).commit();
    }

    public static void setUserDialerCountryCode(Context context, String value) {
        getPrefs(context).edit().putString(DIALER_COUNTRY_CODE, value).commit();
    }

    public static String getUserDialerCountryCode(Context context) {
        return getPrefs(context).getString(DIALER_COUNTRY_CODE, "").replace("+", "");
    }

    public static void setUserDialerCountryName(Context context, String value) {
        getPrefs(context).edit().putString(DIALER_COUNTRY_NAME, value).commit();
    }

    public static String getUserDialerCountryName(Context context) {
        return getPrefs(context).getString(DIALER_COUNTRY_NAME, "");
    }

    public static void setUserCountryCode(Context context, String value) {
        getPrefs(context).edit().putString(COUNTRY_CODE, value).commit();
    }

    public static String getUserCountryCode(Context context) {
        return getPrefs(context).getString(COUNTRY_CODE, "91").replace("+", "");
    }

    public static void setUserCountryName(Context context, String value) {
        getPrefs(context).edit().putString(COUNTRY_NAME, value).commit();
    }

    public static String getUserCountryName(Context context) {
        return getPrefs(context).getString(COUNTRY_NAME, "India");
    }

    public static void setResellerID(Context context, String value) {
        getPrefs(context).edit().putString(RESELLERID, value).commit();
    }

    public static String getResellerID(Context context) {

        return getPrefs(context).getString(RESELLERID, "2");
    }

    public static void setUserDisplay(Context context, String value) {
        getPrefs(context).edit().putString(USER_DISPLAY, value).commit();
    }

    public static String getUserDisplay(Context context) {

        return getPrefs(context).getString(USER_DISPLAY, "");
    }

    public static String getPositionArray(Context context) {
        return getPrefs(context).getString(POSITION_ARRAY, "");
    }

    public static void setPositionArray(Context context, String value) {
        getPrefs(context).edit().putString(POSITION_ARRAY, value).commit();
    }

    public static String getUserType(Context context) {
        return getPrefs(context).getString(USER_TYPE, "");
    }

    public static void setUserType(Context context, String value) {
        getPrefs(context).edit().putString(USER_TYPE, value).commit();
    }

    public static String getUserID(Context context) {
        return getPrefs(context).getString(USER_ID, "");
    }

    public static void setUserId(Context context, String value) {
        getPrefs(context).edit().putString(USER_ID, value).commit();
    }

    public static void setPlanRate(Context context, String value) {
        getPrefs(context).edit().putString(PLAN_RATE, value).commit();
    }

    public static void setPlanId(Context context, String value) {
        getPrefs(context).edit().putString(PLAN_ID, value).commit();
    }

    public static int getListenVoice(Context context) {
        return getPrefs(context).getInt(LISTENVOICE, 0);
    }

    public static void setListenVoice(Context context, int value) {
        getPrefs(context).edit().putInt(LISTENVOICE, value).commit();
    }

    public static int getGender(Context context) {
        return getPrefs(context).getInt(GENDER, 0);
    }

    public static void setGender(Context context, int value) {
        getPrefs(context).edit().putInt(GENDER, value).commit();
    }

    public static String getUserCurrency(Context context) {
        return getPrefs(context).getString(USERCURRENCY, "USD");
    }

    public static void setUserCurrency(Context context, String value) {
        getPrefs(context).edit().putString(USERCURRENCY, value).commit();
    }

    public static String getUserDefaultNumber(Context context) {
        return getPrefs(context).getString(USER_DEFAULT_NUMBER, "");
    }

    public static void setUserDefalutNumber(Context context, String value) {
        getPrefs(context).edit().putString(USER_DEFAULT_NUMBER, value).commit();
    }

    public static String getGCMID(Context context) {
        return getPrefs(context).getString(GCMID, "");
    }

    public static void setGCMID(Context context, String value) {
        getPrefs(context).edit().putString(GCMID, value).commit();
    }

    public static Boolean getGCMIdState(Context context) {
        return getPrefs(context).getBoolean(GCMIDSTATE, false);
    }

    public static void setGCMIdState(Context context, Boolean value) {
        getPrefs(context).edit().putBoolean(GCMIDSTATE, value).commit();
    }

    public static ArrayList<VerifiedData> getVerifiedNumberList(Context context) {

        String json = getPrefs(context).getString(VERIFIEDNUMLIST, null);
        Type type = new TypeToken<ArrayList<VerifiedData>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public static void setVerifiedNumberlist(Context context, ArrayList<VerifiedData> list) {
        getPrefs(context).edit().putString(VERIFIEDNUMLIST, new Gson().toJson(list)).commit();
    }

    public static ArrayList<VerifiedData> getVerifiedEmailList(Context context) {

        String json = getPrefs(context).getString(VERIFIEDEMAILLIST, null);
        Type type = new TypeToken<ArrayList<VerifiedData>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public static void setVerifiedEmaillist(Context context, ArrayList<VerifiedData> list) {
        getPrefs(context).edit().putString(VERIFIEDEMAILLIST, new Gson().toJson(list)).commit();
    }

    public static String getPromocode(Context context) {
        return getPrefs(context).getString(PROMOCODE, "");
    }

    public static void setPromocode(Context context, String value) {
        getPrefs(context).edit().putString(PROMOCODE, value).commit();
    }

    public static String getTotalearn(Context context) {
        return getPrefs(context).getString(TOTALEARN, "0");
    }

    public static void setTotalearn(Context context, String value) {
        getPrefs(context).edit().putString(TOTALEARN, value).commit();
    }

    public static void deletePrefs(Context context) {
        getPrefs(context).edit().clear().commit();
    }


}