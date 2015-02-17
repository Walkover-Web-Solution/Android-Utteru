package com.Utteru.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Patterns;

import com.Utteru.R;
import com.Utteru.adapters.ViewPagerAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.Constants;
import com.Utteru.commonUtilities.GcmRegistrationTask;
import com.Utteru.commonUtilities.Prefs;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.splunk.mint.Mint;
import com.viewpagerindicator.PageIndicator;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import intercom.intercomsdk.Intercom;

public class SignUpHome extends BaseActivity {
    public static Timer timer;
    static ViewPager mPager;
    static PageIndicator mIndicator;
    public boolean checkAccount = false;
    public AccountManager myAccountManager;
    Handler handler;
    boolean wasAccountDeleted;
    Activity ctx = this;
    Calendar calender;
    Account[] accArray;
    Tracker tracker;
    ViewPagerAdapter mAdapter;

    @Override
    protected void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        tracker = MyAnalyticalTracker.getTrackerInstance().getTracker(MyAnalyticalTracker.TrackerName.APP_TRACKER, this);
        overridePendingTransition(R.anim.animation1, R.anim.animation2);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_home);



        Mint.initAndStartSession(SignUpHome.this, "395e969a");
    
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(SignUpHome.this));
        overridePendingTransition(R.anim.animation1, R.anim.animation2);


        myAccountManager = AccountManager.get(this);
        accArray = myAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        checkAccount = accArray.length > 0;


        if (!Prefs.getUserID(this).equals("") && !Prefs.getUserPassword(this).equals("") && checkAccount) {
            Class<?> activityClass;
            try {

                Log.e("got uid&pw","got uid&pw");
                activityClass = Class.forName(
                        Prefs.getLastActivity(ctx));

                Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
                Account[] accounts = AccountManager.get(ctx).getAccounts();
                Log.e("got uid&pw","got uid&pw");

                for (Account account : accounts) {
                    Log.e("got uid&pw","got uid&pw");
                    if (emailPattern.matcher(account.name).matches()) {
                        String possibleEmail = account.name;
                        Intercom.setApiKey("android_sdk-d602fce9df901dbe4e9ddb066b70166020b18203", "d602fce9df901dbe4e9ddb066b70166020b18203");
                       Intercom.beginSessionWithEmail(null, new Intercom.IntercomEventListener() {
                            @Override
                            public void onComplete(String s) {
                                Log.e("complete","complete");
                                Log.e("Intercom","error code"+s);
                            }
                        });

                    }
                    break;
                }


            } catch (ClassNotFoundException ex) {
                activityClass = MenuScreen.class;
            }


            Intent startmenu = new Intent(ctx, activityClass);
            startmenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(startmenu);
            this.finish();
            overridePendingTransition(R.anim.animation1, R.anim.animation2);

        }
//        else
//        if (!Prefs.getUserSipName(this).equals("") && !Prefs.getUserSipPassword(this).equals("")) {
//            VariableClass.Vari.CALL_SHOP_USER=true;
//
//
//            Intent startdialer = new Intent(ctx, DialerActivity.class);
//            startdialer.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(startdialer);
//            this.finish();
//            overridePendingTransition(R.anim.animation1, R.anim.animation2);
//
//        }
        if (!checkAccount) {

            //done by sneha :update
            CommonUtility.clearData(ctx);


        }
        if (Prefs.getUserID(this).equals("") && Prefs.getUserPassword(this).equals("") && checkAccount) {
            Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
            myAccountManager.removeAccount(account, new AccountManagerCallback<Boolean>() {
                @Override
                public void run(AccountManagerFuture<Boolean> future) {
                    // This is the line that actually starts the call to remove the account.
                    //done by sneha:add
                    CommonUtility.clearData(ctx);
                    try {
                        wasAccountDeleted = future.getResult();
                    } catch (OperationCanceledException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (AuthenticatorException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
        }

        calender = Calendar.getInstance();
        calender.add(Calendar.SECOND, 3);


        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);


        handler = new Handler();
        timer = new Timer(false);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {

                    @Override
                    public void run() {

                        ctx.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                int id = mPager.getCurrentItem();
                                switch (id) {
                                    case 0:
                                        mPager.setCurrentItem(1, true);

                                        break;
                                    case 1:

                                        mPager.setCurrentItem(2, true);
                                        break;
                                    case 2:
                                        timer.cancel();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });

                    }
                });
            }
        };
        timer.schedule(timerTask, calender.getTime(), 3000); // 1000 = 1 second.


    }

    @Override
    public void onBackPressed() {
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + mPager.getCurrentItem());
        if (mPager.getCurrentItem() == 2 && page != null) {
            ((SignUpFragement) page).onBackPress();
            Log.e("backfrag", "backfrag");
        } else {
            this.finish();
            this.overridePendingTransition(R.anim.animation3, R.anim.animation4);
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        // Set screen name.
        tracker.setScreenName("SignupHome Android");
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
    protected void onResume() {


        if (CommonUtility.isNetworkAvailable(this))
            if (Prefs.getGCMID(this).equals("") || !Prefs.getGCMIdState(this)) {
                if (CommonUtility.checkPlayServices(ctx))
                    new GcmRegistrationTask(this, 1, Prefs.getUserActualName(this)).execute();

            }


        super.onResume();
    }


}