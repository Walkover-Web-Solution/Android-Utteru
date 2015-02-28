package com.Utteru.ui;

import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.syncadapter.SyncAdapter;
import com.splunk.mint.Mint;
import com.viewpagerindicator.IconPagerAdapter;


public class ContactsListActivity extends ActionBarActivity {

    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;
    AccountManager mAccountManager;
    Boolean checkAccount;
    private ContactDetailFragment mContactDetailFragment;
    private boolean isTwoPaneLayout;
    IntentFilter contacts_updated_filter;
    BroadcastReceiver contacts_updated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //show unregister layout
            if (intent.getAction().equals(SyncAdapter.CONTACTS_UPDATED)) {
             //list to be update in access fragments

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Mint.initAndStartSession(ContactsListActivity.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ContactsListActivity.this));
        mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Contacts");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setIcon(android.R.color.transparent);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_contacts_screen)));

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
        isTwoPaneLayout = getResources().getBoolean(R.bool.has_two_panes);
        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_title_strip);
        pagerTabStrip.setTextSpacing(0);
        //  pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setPadding(0, 0, 0, 10);
        pagerTabStrip.setTextSize(1, 20);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.blue_contacts_screen));

        contacts_updated_filter = new IntentFilter();
        contacts_updated_filter.addAction(SyncAdapter.CONTACTS_UPDATED);
        registerReceiver(contacts_updated, contacts_updated_filter);
        if (getIntent().getExtras() != null) {

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.remove("android:support:fragments");
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(ContactsListActivity.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        this.finish();
        overridePendingTransition(R.anim.animation3, R.anim.animation4);
    }

    @Override
    protected void onResume() {

        super.onResume();
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
        try {
            if (contacts_updated != null)
                unregisterReceiver(contacts_updated);
        } catch (Exception e) {

            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public boolean onSearchRequested() {
        boolean isSearchResultView = false;

        return !isSearchResultView && super.onSearchRequested();
    }

    public static class DemoCollectionPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {

        protected static final String[] CONTENT = new String[]{"All Contacts", "Access Contacts","Utteru Users"};
        protected final int[] ICONS = new int[]{
                R.drawable.all_contact,
                R.drawable.access_contacts,
                R.drawable.ic_launcher
        };
        private int mCount = CONTENT.length;

        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {

            switch (index) {
                case 0:
                    // All Contacts Fragment
                    return new ContactsListFragment();
                case 1:
                    // Access Contacts Fragment
                    return new ContactsAccessFragment();
                case 2:
                    // Access Contacts Fragment
                    return new AvailableUsersFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return DemoCollectionPagerAdapter.CONTENT[position % CONTENT.length];
        }

        @Override
        public int getIconResId(int index) {
            return ICONS[index % ICONS.length];
        }

        @Override
        public int getCount() {
            return mCount;
        }

        public void setCount(int count) {
            if (count > 0 && count <= 10) {
                mCount = count;
                notifyDataSetChanged();
            }
        }
    }
    @Override
    public void onStop() {
        try {
            if (contacts_updated != null)
                unregisterReceiver(contacts_updated);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }


}