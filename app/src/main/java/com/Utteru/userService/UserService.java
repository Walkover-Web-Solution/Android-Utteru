package com.Utteru.userService;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.Utteru.dbHandler.DBHandler;
import com.Utteru.dtos.AccessContactDto;
import com.Utteru.dtos.AccessDataDto;
import com.Utteru.dtos.ContactsDto;
import com.Utteru.dtos.RecentCallsDto;
import com.Utteru.dtos.TransactionLogsDto;

import java.util.ArrayList;

public class UserService {

    public static String Lock = "dblock";
    static Context context;
    static DBHandler dbH;
    static UserService userservice;
    SQLiteDatabase sdb;

    private UserService(Context context) {
        UserService.context = context;
    }

    public static synchronized void extraFunction() {
        PackageInfo packinfo;
        int version = 0;
        try {
            packinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = packinfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if (dbH == null) {
            dbH = new DBHandler(context, null, null, version);
        }
    }

    public static UserService getUserServiceInstance(Context c) {
        if (userservice == null)
            userservice = new UserService(c);
        return userservice;
    }

    public long addAccessData(AccessDataDto dto) {
        Log.d("Insertion in database", "Insertion database ");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.AND_ACCESSNUMBER, dto.getAccessNumber());
        values.put(DBHandler.AND_COUNTRY, dto.getCountry());
        values.put(DBHandler.AND_STATE, dto.getState());
        values.put(DBHandler.AND_COUNTRYCODE, dto.getCountryCode());
        long i = sdb.insert(DBHandler.ACCESSNUMBERDATA, null, values);
        Log.d("Insertion in database", "Insertion database " + i + "" + dto.getState());
        return i;
    }

    public ArrayList<AccessDataDto> getAllCountries() {
        System.out.println("starting fetch");
        ArrayList<AccessDataDto> recentDtoList = new ArrayList<AccessDataDto>();

        extraFunction();
        sdb = dbH.getReadableDatabase();

        String s = "SELECT *  FROM " + DBHandler.ACCESSNUMBERDATA + " GROUP BY " + DBHandler.AND_COUNTRY;

        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                AccessDataDto dto;
                do {
                    dto = new AccessDataDto();

                    dto.set_id(c.getInt(c.getColumnIndex(DBHandler.AND_ID)));

                    dto.setAccessNumber(c.getString(c.getColumnIndex(DBHandler.AND_ACCESSNUMBER)));

                    dto.setCountry(c.getString(c
                            .getColumnIndex(DBHandler.AND_COUNTRY)));
                    dto.setState(c.getString(c.getColumnIndex(DBHandler.AND_STATE)));
                    dto.setCountryCode(c.getString(c.getColumnIndex(DBHandler.AND_COUNTRYCODE)));


                    recentDtoList.add(dto);
                } while (c.moveToNext());
            }
            c.close();
        }

        System.out.println("stoping fetch");
        return recentDtoList;
    }

    public long addTransaction(TransactionLogsDto dto) {
        Log.d("Insertion in database", "Insertion database ");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.T_AMOUNT, dto.getAmount());
        values.put(DBHandler.T_CURRECNY, dto.getCurrency());
        values.put(DBHandler.T_CURRENT_BALANCE, dto.getCurrentBalance());
        values.put(DBHandler.T_DATE, dto.getDate());
        values.put(DBHandler.T_DESCRIPTION, dto.getDescription());
        values.put(DBHandler.T_MODE, dto.getPayment_mode());
        values.put(DBHandler.T_TID, dto.getTransaction_id());
        values.put(DBHandler.T_TYPE, dto.getType());
        values.put(DBHandler.T_ADMIN_NAME, dto.getAdmin_name());
        values.put(DBHandler.T_USER_NAME, dto.getUser_name());
        long i = sdb.insert(DBHandler.TRANSACTIONLOGS, null, values);
        return i;
    }

    public ArrayList<TransactionLogsDto> getAllTransaction() {
        System.out.println("starting fetch");
        ArrayList<TransactionLogsDto> transactionList = new ArrayList<>();

        extraFunction();
        sdb = dbH.getReadableDatabase();

        String s = "SELECT *  FROM " + DBHandler.TRANSACTIONLOGS;

        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                TransactionLogsDto dto;
                do {
                    dto = new TransactionLogsDto();

                    dto.set_id(c.getInt(c.getColumnIndex(DBHandler.T_ID)));

                    dto.setAmount(c.getString(c.getColumnIndex(DBHandler.T_AMOUNT)));

                    dto.setCurrent_balance(c.getString(c
                            .getColumnIndex(DBHandler.T_CURRENT_BALANCE)));
                    dto.setCurrency(c.getString(c.getColumnIndex(DBHandler.T_CURRECNY)));
                    dto.setDate(c.getString(c.getColumnIndex(DBHandler.T_DATE)));
                    dto.setDescription(c.getString(c
                            .getColumnIndex(DBHandler.T_DESCRIPTION)));
                    dto.setPayment_mode(c.getString(c.getColumnIndex(DBHandler.T_MODE)));

                    dto.setTransaction_id(c.getString(c.getColumnIndex(DBHandler.T_TID)));
                    dto.setType(c.getInt(c.getColumnIndex(DBHandler.T_TYPE)));
                    dto.setAdmin_name(c.getString(c.getColumnIndex(DBHandler.T_ADMIN_NAME)));
                    dto.setUser_name(c.getString(c.getColumnIndex(DBHandler.T_USER_NAME)));
                    transactionList.add(dto);
                } while (c.moveToNext());
            }
            c.close();
        }

        System.out.println("stoping fetch");
        return transactionList;
    }

    public ArrayList<AccessDataDto> getAllStates(String value) {
        System.out.println("starting fetch");
        ArrayList<AccessDataDto> recentDtoList = new ArrayList<AccessDataDto>();

        extraFunction();
        sdb = dbH.getReadableDatabase();


        String s = "SELECT * FROM " + DBHandler.ACCESSNUMBERDATA + " WHERE " + DBHandler.AND_COUNTRY + " = '" + value + "' GROUP BY " + DBHandler.AND_STATE;

        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                AccessDataDto dto;
                do {
                    dto = new AccessDataDto();

                    dto.set_id(c.getInt(c.getColumnIndex(DBHandler.AND_ID)));

                    dto.setAccessNumber(c.getString(c.getColumnIndex(DBHandler.AND_ACCESSNUMBER)));

                    dto.setCountry(c.getString(c
                            .getColumnIndex(DBHandler.AND_COUNTRY)));
                    dto.setState(c.getString(c.getColumnIndex(DBHandler.AND_STATE)));
                    Log.e("getting state", "" + dto.getState());
                    dto.setCountryCode(c.getString(c.getColumnIndex(DBHandler.AND_COUNTRYCODE)));


                    recentDtoList.add(dto);
                } while (c.moveToNext());
            }
            c.close();
        }

        System.out.println("stoping fetch");
        return recentDtoList;
    }

    public int deleteTransaction() {
        extraFunction();
        String countQuery = "DELETE FROM " + DBHandler.TRANSACTIONLOGS + ";";
        sdb = dbH.getReadableDatabase();
        Cursor cursor = sdb.rawQuery(countQuery, null);

        int cnt = cursor.getCount();
        Log.e("delete contacts", "" + cnt);
        cursor.close();
        return cnt;
    }

    public ArrayList<AccessDataDto> getAllAccess(String value) {
        System.out.println("starting fetch");
        ArrayList<AccessDataDto> recentDtoList = new ArrayList<AccessDataDto>();

        extraFunction();
        sdb = dbH.getReadableDatabase();


        String s = "SELECT * FROM " + DBHandler.ACCESSNUMBERDATA + " WHERE " + DBHandler.AND_STATE + " = '" + value + "' GROUP BY " + DBHandler.AND_ACCESSNUMBER;

        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                AccessDataDto dto;
                do {
                    dto = new AccessDataDto();

                    dto.set_id(c.getInt(c.getColumnIndex(DBHandler.AND_ID)));

                    dto.setAccessNumber(c.getString(c.getColumnIndex(DBHandler.AND_ACCESSNUMBER)));

                    dto.setCountry(c.getString(c
                            .getColumnIndex(DBHandler.AND_COUNTRY)));
                    dto.setState(c.getString(c.getColumnIndex(DBHandler.AND_STATE)));
                    dto.setCountryCode(c.getString(c.getColumnIndex(DBHandler.AND_COUNTRYCODE)));


                    recentDtoList.add(dto);
                } while (c.moveToNext());
            }
            c.close();
        }

        System.out.println("stoping fetch");
        return recentDtoList;
    }

    public Boolean isAccessNumberDedicated(String accessNumber) {
        Boolean state=false;
        extraFunction();
        sdb = dbH.getReadableDatabase();


        String s  = "SELECT * FROM " + DBHandler.ACCESSCONTACTSTABLE + " WHERE " + DBHandler.AN_ACCESS_NUMBER + " = '" + accessNumber + "' AND "+DBHandler.AN_EXTENSION_NUMBER+"='100'";

        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
            state= true;
            }
            c.close();
        }

        System.out.println("stoping fetch");
        return state;
    }




    public int deleteAllAccessData() {
        extraFunction();
        String countQuery = "DELETE FROM " + DBHandler.ACCESSNUMBERDATA + ";";
        sdb = dbH.getReadableDatabase();
        Cursor cursor = sdb.rawQuery(countQuery, null);

        int cnt = cursor.getCount();
        Log.e("delete contacts", "" + cnt);
        cursor.close();
        return cnt;
    }

    public long addAccessContacts(AccessContactDto dto) {
        Log.d("Insertion in database", "Insertion database ");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.AN_ACCESS_NUMBER, dto.getAccess_number());
        values.put(DBHandler.AN_CONTACT_ID, dto.getContact_id());
        values.put(DBHandler.AN_DISPLAY_NAME, dto.getDisplay_name());
        values.put(DBHandler.AN_EXTENSION_NUMBER, dto.getExtension_number());
        values.put(DBHandler.AN_CONTACT_ID, dto.getContact_id());
        values.put(DBHandler.AN_MOBILE_NUMBER, dto.getMobile_number());
        values.put(DBHandler.AN_COUNTRY, dto.getCountry());
        values.put(DBHandler.AN_STATE, dto.getState());

        long i = sdb.insert(DBHandler.ACCESSCONTACTSTABLE, null, values);
        Log.d("Insertion in database", "Insertion database " + i);
        return i;
    }

    public ArrayList<AccessContactDto> getAllAccessContacts() {
        System.out.println("starting fetch");
        ArrayList<AccessContactDto> recentDtoList = new ArrayList<AccessContactDto>();

        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s = "SELECT * " + " FROM " + DBHandler.ACCESSCONTACTSTABLE;
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                AccessContactDto dto;
                do {
                    dto = new AccessContactDto();

                    dto.set_id(c.getInt(c.getColumnIndex(DBHandler.AN_ID)));

                    dto.setContact_id(c.getString(c.getColumnIndex(DBHandler.AN_CONTACT_ID)));

                    dto.setDisplay_name(c.getString(c
                            .getColumnIndex(DBHandler.AN_DISPLAY_NAME)));
                    dto.setAccess_number(c.getString(c.getColumnIndex(DBHandler.AN_ACCESS_NUMBER)));

                    dto.setExtension_number(c.getString(c
                            .getColumnIndex(DBHandler.AN_EXTENSION_NUMBER)));

                    dto.setMobile_number(c.getString(c
                            .getColumnIndex(DBHandler.AN_MOBILE_NUMBER)));

                    dto.setThumbUri(null);
                    dto.setContactUri(null);
                    dto.setIsAccess(true);
                    dto.setCountry(c.getString(c
                            .getColumnIndex(DBHandler.AN_COUNTRY)));
                    dto.setState(c.getString(c
                            .getColumnIndex(DBHandler.AN_STATE)));


                    recentDtoList.add(dto);
                } while (c.moveToNext());
            }
            c.close();
        }

        System.out.println("stopingfetch");
        return recentDtoList;
    }

    public ArrayList<String> getAllExtensionByAccessNumber(String value) {
        System.out.println("starting fetch");
        ArrayList<String> recentDtoList = new ArrayList<String>();

        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s = "SELECT  " + DBHandler.AN_EXTENSION_NUMBER + " FROM " + DBHandler.ACCESSCONTACTSTABLE + " WHERE " + DBHandler.AN_ACCESS_NUMBER + " LIKE '%" + value + "'";
        Log.e("query ", "" + s);
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                String dto;
                do {
                    dto = (c.getString(c
                            .getColumnIndex(DBHandler.AN_EXTENSION_NUMBER)));
                    Log.e("extension", "" + dto);
                    recentDtoList.add(dto);
                } while (c.moveToNext());
            }
            c.close();
        }

        System.out.println("stopingfetch");
        return recentDtoList;
    }


    public AccessContactDto getAccessConDataByNumber(String number) {
        System.out.println("starting fetch");

        AccessContactDto dto = null;
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s = "SELECT * " + " FROM " + DBHandler.ACCESSCONTACTSTABLE + " WHERE " + DBHandler.AN_MOBILE_NUMBER + " LIKE  '%" + number + "'";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {


                dto = new AccessContactDto();

                dto.set_id(c.getInt(c.getColumnIndex(DBHandler.AN_ID)));

                dto.setContact_id(c.getString(c.getColumnIndex(DBHandler.AN_CONTACT_ID)));

                dto.setDisplay_name(c.getString(c
                        .getColumnIndex(DBHandler.AN_DISPLAY_NAME)));
                dto.setAccess_number(c.getString(c.getColumnIndex(DBHandler.AN_ACCESS_NUMBER)));

                dto.setExtension_number(c.getString(c
                        .getColumnIndex(DBHandler.AN_EXTENSION_NUMBER)));

                dto.setMobile_number(c.getString(c
                        .getColumnIndex(DBHandler.AN_MOBILE_NUMBER)));

                dto.setThumbUri(null);
                dto.setContactUri(null);
                dto.setIsAccess(true);
                dto.setCountry(c.getString(c
                        .getColumnIndex(DBHandler.AN_COUNTRY)));
                dto.setState(c.getString(c
                        .getColumnIndex(DBHandler.AN_STATE)));


            }
            c.close();
        }

        System.out.println("stopingfetch");
        return dto;
    }



    public ArrayList<AccessContactDto> getAllAccessContactsByAccessNumber(String accessNumber) {
        System.out.println("starting fetch");
        ArrayList<AccessContactDto> recentDtoList = new ArrayList<AccessContactDto>();

        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s = "SELECT * " + " FROM " + DBHandler.ACCESSCONTACTSTABLE + " WHERE " + DBHandler.AN_ACCESS_NUMBER + " LIKE '%" + accessNumber + "'";
        Log.e("get all contact", "" + s);
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                AccessContactDto dto;
                do {
                    dto = new AccessContactDto();

                    dto.set_id(c.getInt(c.getColumnIndex(DBHandler.AN_ID)));

                    dto.setContact_id(c.getString(c.getColumnIndex(DBHandler.AN_CONTACT_ID)));

                    dto.setDisplay_name(c.getString(c
                            .getColumnIndex(DBHandler.AN_DISPLAY_NAME)));
                    dto.setAccess_number(c.getString(c.getColumnIndex(DBHandler.AN_ACCESS_NUMBER)));

                    dto.setExtension_number(c.getString(c
                            .getColumnIndex(DBHandler.AN_EXTENSION_NUMBER)));

                    dto.setMobile_number(c.getString(c
                            .getColumnIndex(DBHandler.AN_MOBILE_NUMBER)));

                    dto.setThumbUri(null);
                    dto.setContactUri(null);
                    dto.setIsAccess(true);
                    dto.setCountry(c.getString(c
                            .getColumnIndex(DBHandler.AN_COUNTRY)));
                    dto.setState(c.getString(c
                            .getColumnIndex(DBHandler.AN_STATE)));


                    recentDtoList.add(dto);
                } while (c.moveToNext());
            }
            c.close();
        }

        System.out.println("stopingfetch");
        return recentDtoList;
    }


    public int deleteAllAccessContacts() {
        extraFunction();
        String countQuery = "DELETE FROM " + DBHandler.ACCESSCONTACTSTABLE + ";";
        sdb = dbH.getReadableDatabase();
//        Cursor cursor = sdb.rawQuery(countQuery, null);
        int cou = sdb.delete(DBHandler.ACCESSCONTACTSTABLE, null, null);

//        int cnt = cursor.getCount();
        Log.e("delete contacts", "" + cou);
//        cursor.close();
        return cou;
    }

    public ArrayList<RecentCallsDto> getAllRecentCallByGroup() {
        System.out.println("starting fetch");
        ArrayList<RecentCallsDto> recentDtoList = new ArrayList<RecentCallsDto>();

        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s = "SELECT * " + " FROM " + DBHandler.RECENTCALLS + " GROUP BY " + DBHandler.C_DESTNUMBER + " ORDER BY " + DBHandler.C_ID + " DESC";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                RecentCallsDto dto;
                do {
                    dto = new RecentCallsDto();


                    dto.setName(c.getString(c
                            .getColumnIndex(DBHandler.C_DESTNAME)));
                    dto.setNumber(c.getString(c
                            .getColumnIndex(DBHandler.C_DESTNUMBER)));
                    dto.setDuration(c.getString(c
                            .getColumnIndex(DBHandler.C_DURATION)));
                    dto.setTime(c.getString(c
                            .getColumnIndex(DBHandler.C_TIME)));
                    dto.setSource_number(c.getString(c
                            .getColumnIndex(DBHandler.C_SOURCE_NUM)));


                    recentDtoList.add(dto);
                } while (c.moveToNext());
            }
            c.close();
        }

        System.out.println("stopingfetch");
        return recentDtoList;
    }

    public ArrayList<RecentCallsDto> getAllRecentCallByNumber(String number) {
        System.out.println("starting fetch");
        ArrayList<RecentCallsDto> recentDtoList = new ArrayList<RecentCallsDto>();

        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s = "SELECT * " + " FROM " + DBHandler.RECENTCALLS + " where " + DBHandler.C_DESTNUMBER + "='" + number + "'  ORDER BY " + DBHandler.C_ID + " DESC ";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                RecentCallsDto dto;
                do {
                    dto = new RecentCallsDto();


                    dto.setName(c.getString(c
                            .getColumnIndex(DBHandler.C_DESTNAME)));
                    dto.setNumber(c.getString(c
                            .getColumnIndex(DBHandler.C_DESTNUMBER)));

                    dto.setDuration(c.getString(c
                            .getColumnIndex(DBHandler.C_DURATION)));
                    dto.setTime(c.getString(c
                            .getColumnIndex(DBHandler.C_TIME)));

                    dto.setSource_number(c.getString(c
                            .getColumnIndex(DBHandler.C_SOURCE_NUM)));

                    recentDtoList.add(dto);
                } while (c.moveToNext());
            }
            c.close();
        }

        System.out.println("stopingfetch");
        return recentDtoList;
    }


    public long addRecentCall(RecentCallsDto dto) {
        Log.d("Insertion in database", "Insertion database ");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.C_DESTNAME, dto.getName());
        values.put(DBHandler.C_SOURCE_NUM, dto.getSource_number());
        values.put(DBHandler.C_DESTNUMBER, dto.getNumber());
        values.put(DBHandler.C_DURATION, dto.getDuration());
        values.put(DBHandler.C_TIME, dto.getTime());


        long i = sdb.insertWithOnConflict(DBHandler.RECENTCALLS,null,values,SQLiteDatabase.CONFLICT_REPLACE);
        Log.d("Insertion in database", "Insertion database " + i);
        return i;
    }

    public long addTwoWayCall(ContactsDto dto) {
        Log.d("Insertion in database", "Insertion database ");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.TWC_DEST_NAME, dto.getDestination_name());
        values.put(DBHandler.TWC_DESTNUMBER, dto.getDestination_number());
        values.put(DBHandler.TWC_DURATION, dto.getDuration());
        values.put(DBHandler.TWC_PRICE, dto.getPrice());
        values.put(DBHandler.TWC_SOURCE_NAME, dto.getSource_name());
        values.put(DBHandler.TWC_SOURCENUMBER, dto.getSource_number());
        values.put(DBHandler.TWC_DATE, dto.getDate());
        values.put(DBHandler.TWC_UNIQUEID, dto.getUniqueId());
        long i = sdb.insert(DBHandler.TWRECENTCALLSTABLE, null, values);
        Log.d("Insertion in database", "Insertion database " + i);
        return i;
    }




    public ArrayList<ContactsDto> getAllTwoRecentCallByGroup() {
        Log.e("start fetching ", "start fetching ");
        ArrayList<ContactsDto> twrecentDtoList = new ArrayList<ContactsDto>();

        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s = "SELECT * " + " FROM " + DBHandler.TWRECENTCALLSTABLE + " GROUP BY " + DBHandler.TWC_DESTNUMBER + " ORDER BY " + DBHandler.TWC_ID + " DESC";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                ContactsDto dto;
                do {
                    dto = new ContactsDto();
                    dto.set_id(c.getInt(c.getColumnIndex(DBHandler.TWC_ID)));
                    dto.setDate(c.getString(c
                            .getColumnIndex(DBHandler.TWC_DATE)));
                    dto.setDestination_name(c.getString(c
                            .getColumnIndex(DBHandler.TWC_DEST_NAME)));
                    dto.setDestination_number(c.getString(c
                            .getColumnIndex(DBHandler.TWC_DESTNUMBER)));
                    dto.setDuration(c.getString(c
                            .getColumnIndex(DBHandler.TWC_DURATION)));

                    dto.setPrice(c.getString(c.getColumnIndex(DBHandler.TWC_PRICE)));
                    dto.setSource_name(c.getString(c
                            .getColumnIndex(DBHandler.TWC_SOURCE_NAME)));
                    dto.setSource_number(c.getString(c
                            .getColumnIndex(DBHandler.TWC_SOURCENUMBER)));
                    dto.setDuration(c.getString(c
                            .getColumnIndex(DBHandler.TWC_DURATION)));
                    dto.setUniqueId(c.getString(c
                            .getColumnIndex(DBHandler.TWC_UNIQUEID)));

                    twrecentDtoList.add(dto);
                } while (c.moveToNext());
            }
            c.close();

        }

        Log.e("stop fetching ", "stop fetching");
        return twrecentDtoList;
    }

    public long addCpLog(String time) {
        Log.d("Insertion in database", "Insertion database ");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.CP_DATE, time);
        long i = sdb.insert(DBHandler.CHANGEPASSWORDLOG, null, values);
        Log.d("Insertion in database", "Insertion database " + i);

        int count = getCpLogCount();
        if (count > 10)
            deleteLastIndexFromCplog();

        return i;
    }


    public ArrayList<String> getCpLogs() {
        Log.e("start fetching ", "start fetching ");
        ArrayList<String> cplogs = new ArrayList<String>();
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s = "SELECT * " + " FROM " + DBHandler.CHANGEPASSWORDLOG + " ORDER BY " + DBHandler.CP_ID + " DESC";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            Log.e("cursor not null", "cursor not null");
            if (c.moveToFirst()) {
                do {
                    Log.e("cursor moved to first", "cursor moved to first");
                    cplogs.add(c.getString(c.getColumnIndex(DBHandler.CP_DATE)));
                } while (c.moveToNext());
            }
            c.close();
        }
        Log.e("stop fetching ", "stop fetching");
        return cplogs;
    }

    public String getLastupdateCp() {
        Log.e("start fetching ", "start fetching ");
        extraFunction();
        String lastupdate = null;

        sdb = dbH.getReadableDatabase();
        String s = "SELECT * " + " FROM " + DBHandler.CHANGEPASSWORDLOG + " WHERE " + DBHandler.CP_ID + "= (SELECT MAX(" + DBHandler.CP_ID + ") FROM " + DBHandler.CHANGEPASSWORDLOG + ")";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                lastupdate = c.getString(c.getColumnIndex(DBHandler.CP_DATE));
            }
            c.close();
        }

        Log.e("stop fetching ", "stop fetching");
        return lastupdate;
    }

    public int getCpLogCount() {
        extraFunction();
        String countQuery = "SELECT  * FROM " + DBHandler.CHANGEPASSWORDLOG;
        sdb = dbH.getReadableDatabase();
        Cursor cursor = sdb.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public int deleteLastIndexFromCplog() {
        extraFunction();
        String countQuery = "DELETE  FROM " + DBHandler.CHANGEPASSWORDLOG + " WHERE " + DBHandler.CP_ID + "=(SELECT MIN (" + DBHandler.CP_ID + ") FROM " + DBHandler.CHANGEPASSWORDLOG + ")";
        sdb = dbH.getReadableDatabase();
        Cursor cursor = sdb.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public int deleteAllCpLogs() {
        extraFunction();
        String countQuery = "DELETE   FROM " + DBHandler.CHANGEPASSWORDLOG;
        sdb = dbH.getReadableDatabase();
        Cursor cursor = sdb.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public int deleteAllTwowayLogs() {
        extraFunction();
        String countQuery = "DELETE   FROM " + DBHandler.TWRECENTCALLSTABLE;
        sdb = dbH.getReadableDatabase();
        Cursor cursor = sdb.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }
    public int deleteRecentCalls() {
        extraFunction();
        String countQuery = "DELETE   FROM " + DBHandler.RECENTCALLS;
        sdb = dbH.getReadableDatabase();
        Cursor cursor = sdb.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public ArrayList<ContactsDto> getAllTwoRecentCallByName(String bynumber) {
        System.out.println("starting fetch");
        ArrayList<ContactsDto> twrecentDtoList = new ArrayList<ContactsDto>();

        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s = "SELECT * " + " FROM " + DBHandler.TWRECENTCALLSTABLE + " where " + DBHandler.TWC_DESTNUMBER + "='" + bynumber + "'  ORDER BY " + DBHandler.TWC_ID + " DESC ";
        Log.e("queery", "" + s);
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            Log.e("cursor is not null", "cursor is not null");
            if (c.moveToFirst()) {
                Log.e("cursor moved to first", "cursor moved to first");
                ContactsDto dto;
                do {
                    dto = new ContactsDto();
                    dto.set_id(c.getInt(c.getColumnIndex(DBHandler.TWC_ID)));
                    dto.setDate(c.getString(c
                            .getColumnIndex(DBHandler.TWC_DATE)));
                    dto.setDestination_name(c.getString(c
                            .getColumnIndex(DBHandler.TWC_DEST_NAME)));
                    dto.setDestination_number(c.getString(c
                            .getColumnIndex(DBHandler.TWC_DESTNUMBER)));
                    dto.setDuration(c.getString(c
                            .getColumnIndex(DBHandler.TWC_DURATION)));

                    dto.setPrice(c.getString(c.getColumnIndex(DBHandler.TWC_PRICE)));
                    dto.setSource_name(c.getString(c
                            .getColumnIndex(DBHandler.TWC_SOURCE_NAME)));
                    dto.setSource_number(c.getString(c
                            .getColumnIndex(DBHandler.TWC_SOURCENUMBER)));
                    dto.setDuration(c.getString(c
                            .getColumnIndex(DBHandler.TWC_DURATION)));
                    dto.setUniqueId(c.getString(c
                            .getColumnIndex(DBHandler.TWC_UNIQUEID)));

                    twrecentDtoList.add(dto);
                } while (c.moveToNext());
            } else
                Log.e("cursor is null", "cursor is null");
            c.close();
        }

        System.out.println("stopingfetch");
        return twrecentDtoList;
    }

}
