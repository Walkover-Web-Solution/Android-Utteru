package com.Utteru.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.Utteru.R;
import com.Utteru.adapters.ViewPagerAdapterCP;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.splunk.mint.Mint;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class ChangePasswordHome extends FragmentActivity {

    FontTextView title;
    Tracker tracker;
    ImageView backpress, gototohome;
    FontTextView tittleback;
    ViewPagerAdapterCP mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.cp_pager + ":" + mPager.getCurrentItem());
        if (mPager.getCurrentItem() == 0 && page != null) {
            ((ChangePinFrag) page).onBackPress();
            Log.e("backfrag", "backfrag");
        } else {
            Intent menu = new Intent(this, MenuScreen.class);
            menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(menu);
            overridePendingTransition(R.anim.animation3, R.anim.animation4);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Prefs.setLastActivity(this, getClass().getName());
    }

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
        tracker = MyAnalyticalTracker.getTrackerInstance().getTracker(MyAnalyticalTracker.TrackerName.APP_TRACKER, this);
        setContentView(R.layout.change_password_home);
        Mint.initAndStartSession(ChangePasswordHome.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ChangePasswordHome.this));
        backpress = (ImageView) findViewById(R.id.auto_detect_country_back);
        tittleback = (FontTextView) findViewById(R.id.auto_detect_coutry_header);
        gototohome = (ImageView) findViewById(R.id.auto_detect_country_home);

        mAdapter = new ViewPagerAdapterCP(getSupportFragmentManager());

        mPager = (ViewPager) findViewById(R.id.cp_pager);
        title = (FontTextView) findViewById(R.id.change_password_title);
        mPager.setAdapter(mAdapter);


        mIndicator = (CirclePageIndicator) findViewById(R.id.cp_indicator);
        mIndicator.setViewPager(mPager);


    }

    @Override
    protected void onStart() {
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        // Set screen name.
        tracker.setScreenName("ChangePassword Screen Android");
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
        tittleback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menu = new Intent(ChangePasswordHome.this, MenuScreen.class);
                menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(menu);
            }
        });
        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menu = new Intent(ChangePasswordHome.this, MenuScreen.class);
                menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(menu);

            }
        });
        gototohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangePasswordHome.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        super.onResume();
    }
}
