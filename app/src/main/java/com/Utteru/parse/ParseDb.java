package com.Utteru.parse;

import android.content.Context;

/**
 * Created by vikas on 26/02/15.
 */
public class ParseDb {


    public static final String CB_CLASS_NAME="ContactBook";
    public static final String CB_CONTACTNUMBER="contactNumber";
    public static final String CB_STATUS="status";
    public static final String CB_STATE="state";
    public static final String CB_USERNUMBER="userNumber";
    public static final String CB_PREFIX="prefix";
    public static final String CB_OBJECTID="objectId";



       public static final String US_CLASS_NAME="UserState";
       public static final String US_CONTACTNUMBER="contactNumber";
       public static final String US_STATE="state";
       public static final String US_USERNUMBER="userNumber";
       public static final String US_OBJECTID="objectId";

       public static ParseDb parseDb;
       Context ctx;


        public static ParseDb getParseInstance(Context ctx)
        {
          if(parseDb==null)
              parseDb = new ParseDb(ctx);
          return parseDb;
        }

        private ParseDb (Context ctx)
        {
            this.ctx = ctx;
        }









}
