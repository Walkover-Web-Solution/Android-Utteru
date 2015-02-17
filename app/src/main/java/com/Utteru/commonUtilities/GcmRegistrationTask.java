package com.Utteru.commonUtilities;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.Utteru.ui.Apis;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by root on 11/25/14.
 */
public class GcmRegistrationTask extends AsyncTask<Void, Void, Void> {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    Activity ctx;
    String SENDER_ID = "1081209428204";
    int APPID = 3;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    String regid;
    int login;
    String user;
    String response = null;

    public GcmRegistrationTask(Activity ctx, int login, String user) {
        this.ctx = ctx;
        this.login = login;
        this.user = user;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        Log.e("play service  available", "play service  available");
        String response = null;
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(ctx);
            }
            regid = gcm.register(SENDER_ID);


            Prefs.setGCMID(ctx, regid);
            response = Apis.getApisInstance(ctx).gcmRegistration(regid, APPID, user);


            try {
                JSONObject joparent = new JSONObject(response);
                if (joparent.getString("status").equalsIgnoreCase("sucess")) {
                    if (login == 1) {
                        Prefs.setGCMIdState(ctx, true);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);
    }


}
