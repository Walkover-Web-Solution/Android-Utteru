package com.Utteru.ui;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.Utteru.adapters.ViewPagerAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.viewpagerindicator.PageIndicator;

public abstract class BaseActivity extends FragmentActivity {


    ViewPagerAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;


    @Override
    protected void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

}
