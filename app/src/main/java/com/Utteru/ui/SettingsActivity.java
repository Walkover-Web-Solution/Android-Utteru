package com.Utteru.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SettingsActivity extends BaseActivity {

    Context context = this;
    FontTextView my_numbers, my_emails, my_profile;
    Tracker tracker;
    FontTextView   tittleback;
    ImageView backpress,gototohome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        Mint.initAndStartSession(SettingsActivity.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(SettingsActivity.this));
        tracker = MyAnalyticalTracker.getTrackerInstance().getTracker(MyAnalyticalTracker.TrackerName.APP_TRACKER,this);
        init();
        if (CommonUtility.isNetworkAvailable(context)) {
            new ListenPin().execute();

        } else {
            CommonUtility.showCustomAlertError(this,getString(R.string.internet_error));
        }
    }

    @Override
    protected void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        // Set screen name.
        tracker.setScreenName("Settings Screen Android");
        // Send a screen view.
        tracker.send(new HitBuilders.AppViewBuilder().build());
        super.onStart();
    }

    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);

        super.onStop();
    }
    @Override
    protected void onPause() {
        super.onPause();
        Prefs.setLastActivity(this, getClass().getName());

    }
    @Override
    protected void onResume() {

        my_numbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, ManageNumbersHome.class);
                i.putExtra(VariableClass.Vari.ACCESS_TYPE, 0);
                startActivity(i);
                overridePendingTransition(R.anim.animation1, R.anim.animation2);
            }
        });
        my_emails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, ManageNumbersHome.class);
                i.putExtra(VariableClass.Vari.ACCESS_TYPE, 1);
                startActivity(i);
                overridePendingTransition(R.anim.animation1, R.anim.animation2);
            }
        });
        my_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, ManageNumbersHome.class);
                i.putExtra(VariableClass.Vari.ACCESS_TYPE, 2);
                startActivity(i);
                overridePendingTransition(R.anim.animation1, R.anim.animation2);
            }
        });
        tittleback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        gototohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        super.onResume();
    }

    void init() {
        context = this;
        my_numbers = (FontTextView) findViewById(R.id.my_numbers);
        my_emails = (FontTextView) findViewById(R.id.my_emails);
        my_profile = (FontTextView) findViewById(R.id.my_profile);
        backpress = (ImageView)findViewById(R.id.auto_detect_country_back);
        gototohome = (ImageView)findViewById(R.id.auto_detect_country_home);
        tittleback = (FontTextView)findViewById(R.id.auto_detect_coutry_header);


    }

    @Override
    public void onBackPressed() {
        Intent menu =new Intent(SettingsActivity.this,MenuScreen.class);
        menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(menu);
        overridePendingTransition(R.anim.animation3, R.anim.animation4);
    }

    class ListenPin extends AsyncTask<Void, Void, Void> {

        String response;
        Boolean iserror = false;

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject joparent, jochild;
            JSONArray jarray;
            response = Apis.getApisInstance(context).listenVoice(0, 0, null, false);
            if (!response.equals("")) {
                try {
                    joparent = new JSONObject(response);
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserror = true;
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    } else {
                        jarray = joparent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                        jochild = jarray.getJSONObject(0);
                        String name = jochild.getString(VariableClass.ResponseVariables.USER_DISPLAY_NAME);
                        int listen_voice_state = jochild.getInt(VariableClass.ResponseVariables.LISTENVOICE);
                        int gender = jochild.getInt(VariableClass.ResponseVariables.GENDER);
                        Prefs.setListenVoice(context, listen_voice_state);
                        Prefs.setGender(context, gender);
                        Prefs.setUserDisplay(context, name);
                    }
                } catch (JSONException e) {
                    iserror = true;
                    response = getResources().getString(R.string.parse_error);
                }
            } else {
                iserror = true;
                response = getResources().getString(R.string.server_error);
            }
            return null;
        }


    }

}
