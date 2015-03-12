package com.Utteru.parse;

import android.content.Context;
import android.util.Log;

import com.Utteru.commonUtilities.Prefs;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

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


public void updateOnlineStatus(final Boolean state)
{

    ParseQuery<ParseObject> query = ParseQuery.getQuery(US_CLASS_NAME);
    query.whereEqualTo(US_CONTACTNUMBER, Prefs.getUserDefaultNumber(ctx));
    query.findInBackground(new FindCallback<ParseObject>() {
        @Override
        public void done(List<ParseObject> nameList, ParseException e)
        {
            if (e == null)
            {
                for (ParseObject users : nameList)
                {
                    users.put(US_STATE, state);
                    users.saveInBackground();
                }
            }
            else
            {
                Log.d("Post retrieval", "Error: " + e.getMessage());
            }
        }
    });
}






}
