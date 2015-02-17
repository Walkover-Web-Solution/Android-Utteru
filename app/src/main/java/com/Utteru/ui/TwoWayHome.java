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
import com.Utteru.adapters.ViewPagerAdapterTwoWay;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.splunk.mint.Mint;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class TwoWayHome extends FragmentActivity {

    FontTextView title;
    Tracker tracker;
    FontTextView tittleback;

    ImageView backpress, gototohome;
    ViewPagerAdapterTwoWay mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;


    @Override
    protected void onPause() {
        super.onPause();
        Prefs.setLastActivity(this, getClass().getName());

    }

    @Override
    public void onBackPressed() {

        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.tw_pager + ":" + mPager.getCurrentItem());
        if (mPager.getCurrentItem() == 0 && page != null) {
            ((TwoWayCallFrag)page).onBackPress();
            Log.e("backfrag", "backfrag");
        }
          else {
            Intent menu = new Intent(TwoWayHome.this, MenuScreen.class);
            menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(menu);
            overridePendingTransition(R.anim.animation3, R.anim.animation4);
            Log.e("backhome", "backhome");
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_way_call_home);
        Mint.initAndStartSession(TwoWayHome.this, "395e969a");
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(TwoWayHome.this));
        backpress = (ImageView) findViewById(R.id.auto_detect_country_back);
        gototohome = (ImageView) findViewById(R.id.auto_detect_country_home);
        tittleback = (FontTextView) findViewById(R.id.auto_detect_coutry_header);
        tracker = MyAnalyticalTracker.getTrackerInstance().getTracker(MyAnalyticalTracker.TrackerName.APP_TRACKER, this);
        mAdapter = new ViewPagerAdapterTwoWay(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.tw_pager);
        mPager.setAdapter(mAdapter);
        mIndicator = (CirclePageIndicator) findViewById(R.id.tw_indicator);
        mIndicator.setViewPager(mPager);
        title = (FontTextView) findViewById(R.id.auto_detect_coutry_header);
    }

    @Override
    protected void onResume() {
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {

                mIndicator.setCurrentItem(arg0);
                switch (arg0) {
                    case 0:
                        title.setText(getResources().getString(R.string.two_way_call));
                        break;
                    case 1:
                        title.setText(getResources().getString(R.string.recent_calls));
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

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
                startActivity(new Intent(TwoWayHome.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        super.onResume();
    }

    @Override
    protected void onStart() {
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        // Set screen name.
        tracker.setScreenName("Two way  Android");
        // Send a screen view.
        tracker.send(new HitBuilders.AppViewBuilder().build());
        super.onStart();
    }

    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);

        super.onStop();
    }

}
