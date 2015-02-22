package com.Utteru.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.Utteru.R;
import com.Utteru.adapters.CustomGridAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.Constants;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.userService.UserService;
import com.Utteru.utteru_sip.DialerActivity;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.helpshift.Helpshift;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


public class MenuScreen extends BaseActivity {
    public static final long MILLISECONDS_PER_SECOND = 1000L;
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 60L;
    public static final long SYNC_INTERVAL_IN_HOURS = 24L;


    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static long back_pressed = 0;
    final String title1 = "NameRequired";
    final String title2 = "WeMissYourName";
    public AccountManager myAccountManager;
    GridView grid;
    FontTextView userName;
    Account[] accArray;
    boolean checkAccount;
    Button userBalance;
    String token;
    Tracker tracker;
    String browserapi;
    CustomGridAdapter adapter;
    public static boolean showbal =false;



    Context ctx = this;
    String[] web = {"Call",
            "Access Number",
            "Two Way Call",
            "Contacts",
            "My Account", "Reset Pin",
            "Call rates",
            "Share Talktime",
            "Refer and Earn",
            "Go to web",
            "Help"


    };
    String[] web1 = {"Call",
            "Two Way Call",
            "Call rates",
            "My Account",
            "Go to web",
            "Chat with us"


    };

    int[] imageId = {
            R.drawable.phone,
            R.drawable.access_number,
            R.drawable.twc,
            R.drawable.contact,
            R.drawable.my_account,
            R.drawable.reset_pin,
            R.drawable.call_rate,
            R.drawable.share_talk,
            R.drawable.earn,
            R.drawable.go_to_web,
            R.drawable.help

    };

    int[] imageId1 = {
            R.drawable.phone,
            R.drawable.twc,
            R.drawable.call_rate,
            R.drawable.reset_pin,
            R.drawable.go_to_web,
            R.drawable.help


    };
    String Utteru_GROUP_NAME = "Utteru Contacts";

    @Override
    protected void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("initialised port sip ", "initilized portsip");
        tracker = MyAnalyticalTracker.getTrackerInstance().getTracker(MyAnalyticalTracker.TrackerName.APP_TRACKER, this);
        setContentView(R.layout.menu_screen);
        init();
        Mint.initAndStartSession(MenuScreen.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));



        Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        ContentResolver resolver = ctx.getContentResolver();
        long groupId = 0;
        Cursor cursor = resolver.query(ContactsContract.Groups.CONTENT_URI, new String[]{ContactsContract.Groups._ID},
                ContactsContract.Groups.ACCOUNT_NAME + "=? AND " + ContactsContract.Groups.ACCOUNT_TYPE + "=? AND " +
                        ContactsContract.Groups.TITLE + "=?",
                new String[]{account.name, account.type, Utteru_GROUP_NAME}, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    groupId = cursor.getLong(0);
                }
            } finally {
                cursor.close();
            }
        }

        if (groupId == 0) {
            ContentResolver.setMasterSyncAutomatically(true);
            ContentResolver.setSyncAutomatically(account, ContactsContract.AUTHORITY, true);
        } else {
            ContentResolver.setMasterSyncAutomatically(false);
            ContentResolver.setSyncAutomatically(account, ContactsContract.AUTHORITY, false);
        }




    }

    @Override
    protected void onStart() {


        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        // Set screen name.
        tracker.setScreenName("Menu Screen Android");
        // Send a screen view.
        tracker.send(new HitBuilders.AppViewBuilder().build());

        if (SignUpFragement.isnewSignup || AskNumber.isnewSignup) {
            CustomDialogue d = new CustomDialogue(this);
            d.show();
            Window window = d.getWindow();
            window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            SignUpFragement.isnewSignup = false;
            AskNumber.isnewSignup = false;
        }
        CommonUtility.getUserBalance(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);

        super.onStop();
    }

    @Override
    public void onBackPressed() {

        if (back_pressed + 2000 > System.currentTimeMillis())
            this.finish();
        else
            CommonUtility.showCustomAlert(this, "Press once again to exit!");

        back_pressed = System.currentTimeMillis();
        overridePendingTransition(R.anim.animation3, R.anim.animation4);
    }

    public void setBalance() {

        if (showbal) {


            userBalance.setText("Buy More");
            showbal=false;
        } else {
            userBalance.setText(Prefs.getUserBalance(ctx));
            showbal =true;
        }

//        long temp_time = System.currentTimeMillis();
//        if(difference==20)
//        {
//         if(lastupdated-temp_time==20) {
//
//             userBalance.setText("Buy More");
//             lastupdated=temp_time;
//             difference =5;
//         }
//
//        }
//        else if(difference==5)
//        {
//            if(lastupdated-temp_time==5)
//            {
//                userBalance.setText(Prefs.getUserBalance(ctx));
//                lastupdated=temp_time;
//                difference=20;
//            }
//
//        }
//        else if(difference==0) {
//            userBalance.setText(Prefs.getUserBalance(ctx));
//            lastupdated=temp_time;
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Prefs.setLastActivity(ctx, getClass().getName());

    }


    @Override
    protected void onResume() {


        if (UserService.getUserServiceInstance(ctx).getAllCountries().size() == 0) {
            new IntialiseData(ctx).initAccessData();
        }


        if (!Prefs.getUserDisplay(ctx).equalsIgnoreCase(title1) && !Prefs.getUserDisplay(ctx).equalsIgnoreCase(title2)) {
            userName.setText(Prefs.getUserDisplay(ctx));
        }

        myAccountManager = AccountManager.get(this);
        accArray = myAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        checkAccount = accArray.length > 0;


        if (!checkAccount) {
            Prefs.setUserId(this, "");
            Prefs.setUserPassword(this, "");
            Prefs.setUserName(this, "");
            Prefs.setUserDisplay(this, "");
            Intent startmenu = new Intent(ctx, SignUpHome.class);
            startmenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(startmenu);
            this.finish();
            overridePendingTransition(R.anim.animation1, R.anim.animation2);

        }
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                grid.setEnabled(false);
                Intent start_screen;
                if (!Prefs.getUserType(ctx).equals("4") && Prefs.getResellerID(ctx).equals("2")) {
                    switch (position) {
                        case 1:
                            //access number
                            ctx.startActivity(new Intent(MenuScreen.this, AllCountryActivity.class));
                            overridePendingTransition(R.anim.animation1, R.anim.animation2);
                            break;
                        case 2:
                            //two way
                            start_screen = new Intent(ctx, TwoWayHome.class);
                            ctx.startActivity(start_screen);
                            overridePendingTransition(R.anim.animation1, R.anim.animation2);

                            break;
                        case 3:
                            //contacts
                            final String Utteru_GROUP_NAME = "Utteru Contacts";
                            final ContentResolver resolver = getApplicationContext().getContentResolver();
                            long groupId = 0;
                            final Cursor cursor = resolver.query(ContactsContract.Groups.CONTENT_URI, new String[]{ContactsContract.Groups._ID},
                                    ContactsContract.Groups.ACCOUNT_NAME + "=? AND " + ContactsContract.Groups.ACCOUNT_TYPE + "=? AND " +
                                            ContactsContract.Groups.TITLE + "=?",
                                    new String[]{Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE, Utteru_GROUP_NAME}, null);
                            if (cursor != null) {
                                try {
                                    if (cursor.moveToFirst()) {
                                        groupId = cursor.getLong(0);
                                    }
                                } finally {
                                    cursor.close();
                                }
                            }
                            if (groupId == 0) {
                                final Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
                                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                                ContentResolver.requestSync(account, ContactsContract.AUTHORITY, bundle);
                            }
                            ctx.startActivity(new Intent(MenuScreen.this, ContactsListActivity.class));

                            overridePendingTransition(R.anim.animation1, R.anim.animation2);
                            break;


                        case 6:
                            //Pricing
                            ctx.startActivity(new Intent(MenuScreen.this, SearchRateActivity.class));
                            overridePendingTransition(R.anim.animation1, R.anim.animation2);
                            break;
                        case 7:
                            //Transfer Fund
                            ctx.startActivity(new Intent(MenuScreen.this, FundTransferActivity.class));
                            overridePendingTransition(R.anim.animation1, R.anim.animation2);
                            break;
                        case 5:
                            //resetpin
                            start_screen = new Intent(ctx, ChangePasswordHome.class);
                            ctx.startActivity(start_screen);
                            overridePendingTransition(R.anim.animation1, R.anim.animation2);
                            break;
                        case 8:
//                        earn credits
                            start_screen = new Intent(ctx, EarnCreditsActivity.class);
                            ctx.startActivity(start_screen);
                            overridePendingTransition(R.anim.animation1, R.anim.animation2);
                            break;
                        case 0:
//                       call now
                            start_screen = new Intent(ctx, DialerActivity.class);
                            ctx.startActivity(start_screen);
                            overridePendingTransition(R.anim.animation1, R.anim.animation2);
                            break;
                        case 4:
                            //settings
                            start_screen = new Intent(ctx, SettingsActivity.class);
                            ctx.startActivity(start_screen);
                            overridePendingTransition(R.anim.animation1, R.anim.animation2);
                            break;
                        case 9:
                            //gotoweb

                            if (CommonUtility.isNetworkAvailable(ctx)) {
                                new GotoWeb().execute();
                            } else
                                CommonUtility.showCustomAlertError(MenuScreen.this, getString(R.string.internet_error));
                            break;
                        case 10:
                            //contact us

                            Helpshift.install(getApplication(), "f791ca5c1cf8602084c8f5efcd4f7a39", "walkover.helpshift.com", "walkover_platform_20150113085919138-fc8f1be901e44d4");

                            Log.d("Helpshift", Helpshift.libraryVersion + " - is the version for gradle");

                            Helpshift.showFAQs(MenuScreen.this);
                            overridePendingTransition(R.anim.animation1, R.anim.animation2);
                            break;

                    }
                } else

                {
                    switch (position) {
                        case 1:
                            //two way
                            start_screen = new Intent(ctx, TwoWayHome.class);
                            ctx.startActivity(start_screen);
                            overridePendingTransition(R.anim.animation1, R.anim.animation2);
                            break;
                        case 2:
                            //Pricing
                            ctx.startActivity(new Intent(MenuScreen.this, SearchRateActivity.class));
                            overridePendingTransition(R.anim.animation1, R.anim.animation2);
                            break;

                        case 0:
//                       buy now
                            //  start_screen = new Intent(ctx, BuyNowOptionsActivity.class);
                            //  ctx.startActivity(start_screen);
                            start_screen = new Intent(ctx, DialerActivity.class);
                            ctx.startActivity(start_screen);
                            overridePendingTransition(R.anim.animation1, R.anim.animation2);
                            break;
                        case 3:
                            //settings
                            start_screen = new Intent(ctx, SettingsActivity.class);
                            ctx.startActivity(start_screen);
                            overridePendingTransition(R.anim.animation1, R.anim.animation2);
                            break;
                        case 4:
                            //gotoweb
                            if (CommonUtility.isNetworkAvailable(ctx)) {
                                new GotoWeb().execute();
                            } else
                                CommonUtility.showCustomAlertError(MenuScreen.this, getString(R.string.internet_error));
                            break;
                        case 5:
                            //contact us
                            Helpshift.install(getApplication(), "f791ca5c1cf8602084c8f5efcd4f7a39", "walkover.helpshift.com", "walkover_platform_20150113085919138-fc8f1be901e44d4");

                            Log.d("Helpshift", Helpshift.libraryVersion + " - is the version for gradle");

                            Helpshift.showFAQs(MenuScreen.this);
                            overridePendingTransition(R.anim.animation1, R.anim.animation2);
                            break;


                    }
                }

                grid.setEnabled(true);


            }
        });

        userBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent open_dialer = new Intent(ctx, BuyNowOptionsActivity.class);
                ctx.startActivity(open_dialer);

                overridePendingTransition(R.anim.animation1, R.anim.animation2);

            }
        });



        super.onResume();
    }

    void init() {

        grid = (GridView) findViewById(R.id.grid);

        userName = (FontTextView) findViewById(R.id.user_display_name);
        userBalance = (Button) findViewById(R.id.user_balance);
        myAccountManager = AccountManager.get(this);

        adapter = new CustomGridAdapter(MenuScreen.this, web, imageId);

        if (Prefs.getUserType(ctx).equals("4") || ! Prefs.getResellerID(ctx).equals("2")) {
            myAccountManager = AccountManager.get(this);
            adapter = new CustomGridAdapter(MenuScreen.this, web1, imageId1);
        }
        grid.setAdapter(adapter);
        grid.setVerticalSpacing(1);
        grid.setHorizontalSpacing(2);

    }


    private boolean checkPlayServices(Activity c) {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(c);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, c,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("Utteru", "This device is not supported.");
            }
            return false;
        }
        return true;
    }


    class GotoWeb extends AsyncTask<Void, Void, Void> {

        String response;
        Boolean iserror = false;

        @Override
        protected void onPreExecute() {
            CommonUtility.show_PDialog(ctx, getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            CommonUtility.dialog.dismiss();
            if (iserror) {
                CommonUtility.showCustomAlertError(MenuScreen.this, response);
            } else {
                //hit browser api
                browserapi = "https://voice.utteru.com/loginAs.php?token=";
                browserapi = browserapi + token;
                Log.e("url ", "url" + browserapi);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(browserapi));
                startActivity(browserIntent);
            }
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = Apis.getApisInstance(ctx).getTokenApi();
            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;
                JSONArray jarray;
                try {
                    joparent = new JSONObject(response);

                    //failed response
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }
                    //success response
                    else if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                        jarray = joparent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                        jochild = jarray.getJSONObject(0);
                        token = jochild.getString(VariableClass.ResponseVariables.TOKEN);


                    }
                } catch (JSONException e) {
                    iserror = true;
                    response = getResources().getString(R.string.parse_error);
                    e.printStackTrace();

                }
            } else {
                iserror = true;
                response = getResources().getString(R.string.server_error);

            }


            return null;
        }
    }


}
