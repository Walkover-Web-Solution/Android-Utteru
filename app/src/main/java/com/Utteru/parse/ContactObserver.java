package com.Utteru.parse;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.Prefs;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ContactObserver extends ContentObserver {

Context mContext;

    public  ContactObserver(Handler handler,Context context)
    {
        super(handler);
        mContext=context;
    }
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        syncContactsAll(CommonUtility.readContactsNew(mContext));
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        Log.e("change in contacts","change in contacts");
        onChange(selfChange);

   }

    public void syncContactsAll(final ArrayList<ContactsDto> contactlist)
    {

        try {
            final List<ContactsDto> local_con_list = contactlist;
            final List<ContactsDto> server_list = new ArrayList<>();
            final ContactsOperation con_operation = new ContactsOperation();

            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseDb.CB_CLASS_NAME);
            query.whereEqualTo(ParseDb.CB_USERNUMBER, Prefs.getUserDefaultNumber(mContext));
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    ContactsDto contactsDto;
                    for (ParseObject obj : parseObjects) {
                        contactsDto = new ContactsDto();
                        contactsDto.setStatus(obj.getBoolean(ParseDb.CB_STATUS));
                        contactsDto.setNumber(obj.getString(ParseDb.CB_CONTACTNUMBER));
                        contactsDto.setState(obj.getBoolean(ParseDb.CB_STATE));
                        contactsDto.setUserNumber(obj.getString(ParseDb.CB_USERNUMBER));
                        contactsDto.setObjectId(obj.getObjectId());
                        Log.e("get object Id",""+contactsDto.getObjectId());
                        server_list.add(contactsDto);
                    }

                    //delete extra numbers after removing commom numbers
                    List<ContactsDto> differList = con_operation.intersection(local_con_list, server_list);
                    local_con_list.removeAll(differList);
                    server_list.removeAll(differList);
                    Log.e("removed common ","removed common ");
                    if (server_list.size() > 0) {
                        Log.e("deleting  numbers ","deleting numbers ");
                        List<ParseObject> objectList = new ArrayList<ParseObject>();
                        ParseObject obj;
                        for (ContactsDto cdto : server_list) {
                            obj =  ParseObject.createWithoutData("HangOut", cdto.getObjectId());;
                            obj.put(ParseDb.CB_STATUS, false);
                            obj.put(ParseDb.CB_CONTACTNUMBER, cdto.getNumber());
                            obj.put(ParseDb.CB_USERNUMBER, Prefs.getUserDefaultNumber(mContext));
                            obj.put(ParseDb.CB_STATE, false);
                            objectList.add(obj);
                        }

                        ParseObject.deleteAllInBackground(objectList, new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                } else
                                    e.printStackTrace();

                                //add number to server after deleting
                                Log.e("adding number ", "adding  numbers ");
                                if (local_con_list.size() > 0) {
                                    ParseObject obj;
                                    List<ParseObject> objectList = new ArrayList<ParseObject>();

                                    for (ContactsDto cdto : local_con_list) {
                                        obj = new ParseObject(ParseDb.CB_CLASS_NAME);
                                        obj.put(ParseDb.CB_STATUS, false);
                                        obj.put(ParseDb.CB_CONTACTNUMBER, cdto.getNumber());
                                        obj.put(ParseDb.CB_USERNUMBER, Prefs.getUserDefaultNumber(mContext));
                                        obj.put(ParseDb.CB_STATE, false);
                                        objectList.add(obj);
                                    }
                                    ParseObject.saveAllInBackground(objectList, new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {

                                            } else
                                                e.printStackTrace();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
    }

    public static void  registerObserver (Context context,Uri uri ,ContactObserver observer)
    {
        context.getContentResolver().
                registerContentObserver(
                        uri,
                        true,
                        observer);
    }
    public static  void unregister (Context context,ContactObserver observer)
    {
        context.getContentResolver().unregisterContentObserver(observer);


    }
}
