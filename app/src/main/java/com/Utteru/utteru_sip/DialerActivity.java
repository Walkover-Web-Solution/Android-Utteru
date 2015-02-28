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
    RecentDetailFragment recentDetailFragment;
    FragmentTransaction ft;
    IntentFilter mIntentFilter_register;


    public static final String DIALER_FRAGMENT_TAG = "dialer_tag";
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialer_main_layout);
        Mint.initAndStartSession(DialerActivity.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(DialerActivity.this));
        Log.e("Dialer Layout", " on activity create");
        dialerFragment = new DialerFragment();
        recentDetailFragment = new RecentDetailFragment();
        mIntentFilter_register = new IntentFilter();
        mIntentFilter_register.addAction(UtteruSipCore.REGISTRATION_STATE_CHANGE);

        Mint.initAndStartSession(DialerActivity.this, CommonUtility.BUGSENSEID);


        launchDialerFrag();


    }

    @Override
    protected void onResume() {
        context.registerReceiver(registrationReceiver, mIntentFilter_register);

        super.onResume();
    }

    @Override
    public void onCall(UtteruSipCore app, PortSipSdk sdk, String number, int action, RecentCallsDto dto) {

        if (action == 0) {
            launchCallingActivity(number, null, SystemClock.elapsedRealtime(), false, null, System.currentTimeMillis());
        } else {
            launchDetailsFrag(dto);
        }
    }

    @Override
    public void CallFromDetails(UtteruSipCore myapp, PortSipSdk sdk, RecentCallsDto dto, int action) {


        if (action == 0) {
            launchCallingActivity(dto.getNumber(), null, SystemClock.elapsedRealtime(), false, null, System.currentTimeMillis());
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
            recentDetailFragment = (RecentDetailFragment) getSupportFragmentManager().findFragmentByTag(LOG_DETAILS_FRAGMENT_TAG);
            if (recentDetailFragment != null && recentDetailFragment.isVisible()) {
                Log.e("recent   fragment", "recent fragment ");
                recentDetailFragment.onBackPress();
            } else {
                super.onBackPressed();


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

    void launchCallingActivity(String number, String name, long time, boolean isongoing, String price, long date) {

        String calleename = name;
        if (calleename == null)
            calleename = CommonUtility.getContactDisplayNameByNumber(number, context);


        CallData calldata = CallData.getCallDateInstance();
        Log.e("setting variable ", "setting variable " + name);
        calldata.setCallee_number(number);
        calldata.setCallee_name(calleename);
        calldata.setTime_elapsed(time);
        calldata.setCallType(isongoing);
        calldata.setCall_price(price);
        calldata.setDate(date);

        startActivity(new Intent(this, CallingScreenActivity.class));
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

        super.onStop();
    }


}