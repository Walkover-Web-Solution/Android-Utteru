package com.Utteru.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.Utteru.R;
import com.Utteru.ui.TwoWayCallFrag;
import com.Utteru.ui.TwoWayCallLogFrag;
import com.viewpagerindicator.IconPagerAdapter;

public class ViewPagerAdapterTwoWay extends FragmentPagerAdapter implements IconPagerAdapter {
    protected static final String[] CONTENT = new String[]{"One", "Two"};
    protected static final int[] ICONS = new int[]{
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,

    };
    private int type;

    private int mCount = CONTENT.length;

    public ViewPagerAdapterTwoWay(FragmentManager fm) {

        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            default:
                return null;
            case 0:
                return new TwoWayCallFrag();
            case 1:
                return new TwoWayCallLogFrag();


        }
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

    @Override
    public CharSequence getPageTitle(int position) {
        return ViewPagerAdapterTwoWay.CONTENT[position % CONTENT.length];
    }

    @Override
    public int getIconResId(int index) {
        return ICONS[index % ICONS.length];
    }
}