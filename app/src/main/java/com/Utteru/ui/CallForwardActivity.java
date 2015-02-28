package com.Utteru.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.Utteru.R;

public class CallForwardActivity extends FragmentActivity {


    static ViewPager mPager;
    public static String[] CONTENT = new String[]{"Rent a Number", "Free"};


    Context ctx;


    @Override
    public void onBackPressed() {
        Intent menu = new Intent(this, MenuScreen.class);
        menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(menu);
        this.finish();

        overridePendingTransition(R.anim.animation3, R.anim.animation4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_forward_baseactivity);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new CallForwardPagerAdapter(getSupportFragmentManager()));

        //Mint.initAndStartSession(CallForwardActivity.this, CommonUtility.BUGSENSEID);

        //  Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));

    }

    @Override
    protected void onResume() {


        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //  Prefs.setLastActivity(ctx, getClass().getName());

    }

    private class CallForwardPagerAdapter extends FragmentPagerAdapter {


        public CallForwardPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {

                case 0:
                    return CallForwardFrag.newInstance(0, "Dedicated");
                case 1:
                    return CallForwardFrag.newInstance(1, "free");
                default:
                    return null;
            }

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length];
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
    // Returns the page title for the top indicator


}







