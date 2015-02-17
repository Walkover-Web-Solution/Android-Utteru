package com.Utteru.utteru_sip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessContactDto;
import com.Utteru.dtos.RecentCallsDto;
import com.Utteru.ui.ContactDetailActivity;
import com.Utteru.userService.UserService;
import com.portsip.PortSipSdk;
import com.splunk.mint.Mint;

public class DialerActivity extends ActionBarActivity
        implements DialerFragment.OnCallListener, RecentDetailFragment.CallDetails {

    Context context = this;
    DialerFragment dialerFragment;
    CallingScreenFragment callingScreenFragment;
    RecentDetailFragment recentDetailFragment;
    Boolean fromNotification = false;
    FragmentTransaction ft;
    IntentFilter mIntentFilter_register;
    IntentFilter mIntentFilter_call;

    public static final String DIALER_FRAGMENT_TAG = "dialer_tag";
    public static final String CALLING_FRAGMENT_TAG = "calling_tag";
    public static final String LOG_DETAILS_FRAGMENT_TAG = "log_details_tag";

    BroadcastReceiver registrationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //show unregister layout
            if (intent.getAction().equals(UtteruSipCore.REGISTRATION_STATE_CHANGE)) {


                Bundle bundle = intent.getExtras();

                dialerFragment = (DialerFragment) getSupportFragmentManager().findFragmentByTag(DIALER_FRAGMENT_TAG);
                if (dialerFragment != null)
                    dialerFragment.onRegisterStatusReceive(bundle.getString("message"), bundle.getInt("code"));

            }

        }
    };

    BroadcastReceiver callReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {


            if (intent.getAction().equals(UtteruSipCore.CALL_STATE_CHANGE)) {
                Log.e("call status in receiver", "" + intent.getExtras().getString("message") + "" + intent.getExtras().getInt("code"));
                int code = intent.getExtras().getInt("code");
                String message = intent.getExtras().getString("message");


                callingScreenFragment = (CallingScreenFragment) getSupportFragmentManager().findFragmentByTag(CALLING_FRAGMENT_TAG);
                if (callingScreenFragment != null)
                    callingScreenFragment.onCallStateChange(message, code);

            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialer_main_layout);
        Mint.initAndStartSession(DialerActivity.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(DialerActivity.this));
        Log.e("Dialer Layout", " on activity create");
        dialerFragment = new DialerFragment();
        callingScreenFragment = new CallingScreenFragment();
        recentDetailFragment = new RecentDetailFragment();
        mIntentFilter_register = new IntentFilter();
        mIntentFilter_register.addAction(UtteruSipCore.REGISTRATION_STATE_CHANGE);
        mIntentFilter_call = new IntentFilter();
        mIntentFilter_call.addAction(UtteruSipCore.CALL_STATE_CHANGE);
        Mint.initAndStartSession(DialerActivity.this, CommonUtility.BUGSENSEID);

        //not from notification
        if (getIntent().getExtras() == null) {

            launchDialerFrag();
        } else {

            fromNotification = true;

            String number = getIntent().getExtras().getString(VariableClass.Vari.SELECTEDNUMBER);
            String name = getIntent().getExtras().getString(VariableClass.Vari.SELECTEDNAME);
            long elapsed_time = getIntent().getExtras().getLong(VariableClass.Vari.CALL_ELAPSED_TIME);
            String price = getIntent().getExtras().getString(VariableClass.Vari.CALLPRICE);
            long date = getIntent().getExtras().getLong(VariableClass.Vari.CALLDATE);

            launchCallingFrag(number, name, elapsed_time, true, price,date);

        }


    }

    @Override
    protected void onResume() {
        context.registerReceiver(registrationReceiver, mIntentFilter_register);
        context.registerReceiver(callReceiver, mIntentFilter_call);
        super.onResume();
    }

    @Override
    public void onCall(UtteruSipCore app, PortSipSdk sdk, String number, int action, RecentCallsDto dto) {

        if (action == 0) {
            launchCallingFrag(number, null, SystemClock.elapsedRealtime(), false, null,System.currentTimeMillis());
        } else {
            launchDetailsFrag(dto);
        }
    }

    @Override
    public void CallFromDetails(UtteruSipCore myapp, PortSipSdk sdk, RecentCallsDto dto, int action) {
        if (action == 0) {
            launchCallingFrag(dto.getNumber(), null, SystemClock.elapsedRealtime(), false, null,System.currentTimeMillis());
        } else {
            launchProfile(dto);
        }
    }

    @Override
    protected void onPause() {

        Prefs.setLastActivity(this, getClass().getName());
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        Log.e("back press called ", "" + getSupportFragmentManager().getBackStackEntryCount());


        dialerFragment = (DialerFragment) getSupportFragmentManager().findFragmentByTag(DIALER_FRAGMENT_TAG);
        if (dialerFragment != null && dialerFragment.isVisible()) {
            // add your code here
            Log.e("dialer fragment", "dialer fragment ");

            dialerFragment.onBackPress();
        } else {

            callingScreenFragment = (CallingScreenFragment) getSupportFragmentManager().findFragmentByTag(CALLING_FRAGMENT_TAG);
            if (callingScreenFragment != null && callingScreenFragment.isVisible()) {

                Log.e("calling  fragment", "calling fragment ");

                callingScreenFragment.onBackPress(fromNotification);

            } else {
                recentDetailFragment = (RecentDetailFragment) getSupportFragmentManager().findFragmentByTag(LOG_DETAILS_FRAGMENT_TAG);
                if (recentDetailFragment != null && recentDetailFragment.isVisible()) {
                    Log.e("recent   fragment", "recent fragment ");
                    recentDetailFragment.onBackPress();
                } else {
                    super.onBackPressed();


                }
            }

        }


    }

    public void launchDialerFrag() {
        ft = getSupportFragmentManager().beginTransaction();

        ft.add(R.id.dial_fragment, dialerFragment, DIALER_FRAGMENT_TAG);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();

    }

    void launchCallingFrag(String number, String name, long time, boolean isongoing, String price,long date) {

        String calleename = name;
        if (calleename == null)
            calleename = CommonUtility.getContactDisplayNameByNumber(number, context);
        callingScreenFragment.setNumber(number, calleename, time, isongoing, price,this,date);
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.dial_fragment, callingScreenFragment, CALLING_FRAGMENT_TAG);
        dialerFragment = (DialerFragment) getSupportFragmentManager().findFragmentByTag(DIALER_FRAGMENT_TAG);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(DIALER_FRAGMENT_TAG);
        ft.commit();
        overridePendingTransition(R.anim.animation1, R.anim.animation2);
    }

    void launchDetailsFrag(RecentCallsDto dto) {

        recentDetailFragment.setData(dto);

        ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.dial_fragment, recentDetailFragment, LOG_DETAILS_FRAGMENT_TAG);

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.addToBackStack(DIALER_FRAGMENT_TAG);

        ft.commit();

        overridePendingTransition(R.anim.animation1, R.anim.animation2);

    }

    void launchProfile(RecentCallsDto dto) {

        AccessContactDto cdto = new AccessContactDto();
        cdto.setMobile_number(dto.getNumber());
        cdto.setDisplay_name(dto.getName());
        Log.e("number on click", "" + cdto.getMobile_number());
        AccessContactDto cdto_from_db = UserService.getUserServiceInstance(context).getAccessConDataByNumber(cdto.getMobile_number());
        if (cdto_from_db != null)
            cdto = cdto_from_db;
        Intent detailsActivity = new Intent(context, ContactDetailActivity.class);
        detailsActivity.putExtra(VariableClass.Vari.SELECTEDDATA, cdto);
        detailsActivity.putExtra(VariableClass.Vari.SOURCECLASS, "");
        startActivity(detailsActivity);
        overridePendingTransition(R.anim.animation1, R.anim.animation2);

    }

    @Override
    public void onStop() {

        if (registrationReceiver != null)
            context.unregisterReceiver(registrationReceiver);
        if (callReceiver != null)
            context.unregisterReceiver(callReceiver);
        super.onStop();
    }



    @Override
    protected void onDestroy() {
        callingScreenFragment = (CallingScreenFragment) getSupportFragmentManager().findFragmentByTag(CALLING_FRAGMENT_TAG);
        if (callingScreenFragment != null && callingScreenFragment.isVisible()) {

            Log.e("calling  fragment", "calling fragment ");

            callingScreenFragment.onDestroyFrag();



        }

        super.onDestroy();
    }
}