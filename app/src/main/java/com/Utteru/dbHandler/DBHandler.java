package com.Utteru.dbHandler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Phone91DBv2";

    //table names
    public static final String RECENTCALLS = "recentcalls";
    public static final String TWRECENTCALLSTABLE = "twowayrecentcalls";
    public static final String CHANGEPASSWORDLOG = "change_password_log";
    public static final String ACCESSCONTACTSTABLE = "access_contacts_table";
    public static final String ACCESSNUMBERDATA = "accessnumberdata";
    public static final String TRANSACTIONLOGS = "transaction_logs";

    //recent calls  table coloumn
    public static final String C_ID = "_id";
    public static final String C_DESTNAME = "name";
    public static final String C_DESTNUMBER = "number";
    public static final String C_DURATION = "duration";
    public static final String C_SOURCE_NUM = "source_number";

    public static final String C_TIME = "time";
    String CREATE_RECENT = " CREATE TABLE" + " " + RECENTCALLS + "("
            + C_DESTNAME + " TEXT," + C_DURATION + " TEXT," + C_TIME + " TEXT," + C_DESTNUMBER + " TEXT,"+ C_SOURCE_NUM+ " TEXT," + C_ID+ " INTEGER  PRIMARY KEY AUTOINCREMENT " + ", UNIQUE ( "+C_DESTNUMBER+" ));";

    //two way call log
    public static final String TWC_ID = "_id";
    public static final String TWC_SOURCE_NAME = "source_name";
    public static final String TWC_SOURCENUMBER = "source_number";
    public static final String TWC_DATE = "date";
    public static final String TWC_DURATION = "duration";
    public static final String TWC_UNIQUEID = "unique_id";
    public static final String TWC_PRICE = "price";
    public static final String TWC_DESTNUMBER = "dest_number";
    public static final String TWC_DEST_NAME = "dest_name";
    String CREATE_TW_RECENT_CALL = " CREATE TABLE" + " " + TWRECENTCALLSTABLE + "("
            + TWC_SOURCE_NAME + " TEXT," + TWC_DEST_NAME + " TEXT," + TWC_DESTNUMBER + " TEXT," + TWC_PRICE + " TEXT," + TWC_UNIQUEID + " TEXT," + TWC_DURATION + " TEXT," + TWC_DATE + " TEXT," + TWC_SOURCENUMBER + " TEXT," + TWC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ");";

    //change password log
    public static final String CP_ID = "_id";
    public static final String CP_DATE = "date";
    String CREATE_CP_LOG = " CREATE TABLE" + " " + CHANGEPASSWORDLOG + "("
            + CP_DATE + " TEXT," + CP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ");";

    //Access Contacts
    public static final String AN_ID = "_id";
    public static final String AN_CONTACT_ID = "contact_id";
    public static final String AN_DISPLAY_NAME = "display_name";
    public static final String AN_MOBILE_NUMBER = "mobile_number";
    public static final String AN_ACCESS_NUMBER = "access_number";
    public static final String AN_EXTENSION_NUMBER = "extension_number";
    public static final String AN_COUNTRY = "country";
    public static final String AN_STATE = "state";

    String CREATE_ACCESS_TABLE = " CREATE TABLE" + " " + ACCESSCONTACTSTABLE + "("
            + AN_DISPLAY_NAME + " TEXT," + AN_MOBILE_NUMBER + " TEXT," + AN_COUNTRY + " TEXT," + AN_STATE + " TEXT," + AN_ACCESS_NUMBER + " TEXT," + AN_EXTENSION_NUMBER + " TEXT," + AN_CONTACT_ID + " TEXT," + AN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ");";


    //Access Number Data

    public static final String AND_ID = "_id";
    public static final String AND_ACCESSNUMBER = "access_number";
    public static final String AND_COUNTRY = "country_name";
    public static final String AND_STATE = "country_state";
    public static final String AND_COUNTRYCODE = "country_code";
    public static final String AND_EXTSTATUS = "ext_status";

    String CREATE_ACCESS_NUMBER_DATA = " CREATE TABLE" + " " + ACCESSNUMBERDATA + "("
            + AND_STATE + " TEXT," + AND_COUNTRY + " TEXT," + AND_EXTSTATUS + " TEXT," + AND_ACCESSNUMBER + " TEXT," + AND_COUNTRYCODE + " TEXT," + AND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ");";


    //transactionlogs

    public static final String T_ID = "_id";
    public static final String T_TID = "transaction_id";
    public static final String T_TYPE = "tran_type";
    public static final String T_DATE = "tran_date";
    public static final String T_AMOUNT = "tran_amount";
    public static final String T_CURRECNY = "tran_currency";
    public static final String T_MODE = "tran_mode";
    public static final String T_DESCRIPTION = "tran_descript";
    public static final String T_CURRENT_BALANCE = "tran_current_bal";
    public static final String T_ADMIN_NAME = "tran_admin_name";
    public static final String T_USER_NAME = "tran_user_name";



    String CREATE_TRANSACTION_TABLE = " CREATE TABLE" + " " + TRANSACTIONLOGS + "("
            + T_TID + " TEXT," + T_TYPE + " INTEGER," + T_DATE + " TEXT,"+ T_ADMIN_NAME + " TEXT,"+ T_USER_NAME + " TEXT," + T_AMOUNT + " TEXT," + T_MODE + " TEXT," + T_DESCRIPTION + " TEXT," + T_CURRENT_BALANCE + " TEXT," + T_CURRECNY + " TEXT," + T_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ");";


    public DBHandler(Context context, String name, CursorFactory factory,
                     int version) {
        super(context, DATABASE_NAME, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_RECENT);
        db.execSQL(CREATE_TW_RECENT_CALL);
        db.execSQL(CREATE_CP_LOG);
        db.execSQL(CREATE_ACCESS_TABLE);
        db.execSQL(CREATE_ACCESS_NUMBER_DATA);
        db.execSQL(CREATE_TRANSACTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//updation one
        try {
            db.execSQL(CREATE_ACCESS_TABLE);
            db.execSQL(CREATE_ACCESS_NUMBER_DATA);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        //updation two

        try {


            if (newVersion > oldVersion) {
                db.execSQL("ALTER TABLE " + ACCESSCONTACTSTABLE + " ADD COLUMN " + AN_COUNTRY + " TEXT ");
                db.execSQL("ALTER TABLE " + ACCESSCONTACTSTABLE + " ADD COLUMN " + AN_STATE + " TEXT ");


            }
        } catch (Exception e) {
//            e.printStackTrace();
        }

        try {

            if (newVersion > oldVersion) {
                db.execSQL("ALTER TABLE " + ACCESSNUMBERDATA + " ADD COLUMN " + AND_EXTSTATUS + " TEXT ");

            }
        } catch (Exception e) {
//            e.printStackTrace();
        }

        try {

            if (newVersion > oldVersion) {
                db.execSQL(CREATE_TRANSACTION_TABLE);

            }
        } catch (Exception e) {
//            e.printStackTrace();
        }

        try {

            if (newVersion > oldVersion) {
                db.execSQL("ALTER TABLE " + TRANSACTIONLOGS + " ADD COLUMN " + T_ADMIN_NAME + " TEXT ");
                db.execSQL("ALTER TABLE " + TRANSACTIONLOGS + " ADD COLUMN " + T_USER_NAME + " TEXT ");

            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
        try {

            if (newVersion > oldVersion) {
                db.execSQL("ALTER TABLE " + RECENTCALLS + " ADD COLUMN " + C_SOURCE_NUM + " TEXT ");

            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
        try {

            if (newVersion > oldVersion) {
                db.execSQL("DROP TABLE IF EXISTS "+RECENTCALLS);
                db.execSQL(CREATE_RECENT);

            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }
}

