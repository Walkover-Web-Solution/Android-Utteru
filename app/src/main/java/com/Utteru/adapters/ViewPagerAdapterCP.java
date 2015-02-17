package com.Utteru.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.Utteru.R;
import com.Utteru.ui.ChangePasswordFrag;
import com.Utteru.ui.ChangePinFrag;
import com.viewpagerindicator.IconPagerAdapter;

public class ViewPagerAdapterCP extends FragmentPagerAdapter implements IconPagerAdapter {
    protected static final String[] CONTENT = new String[]{"One", "Two"};
    protected static final int[] ICONS = new int[]{
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
    };

    private int mCount = CONTENT.length;

    public ViewPagerAdapterCP(FragmentManager fm) {

        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            default:
                return null;
            case 0:
                return new ChangePinFrag();
            case 1:
                return new ChangePasswordFrag();


        }
    }

    @Override
    public int getCount() {
        return 1;
    }

    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ViewPagerAdapterCP.CONTENT[position % CONTENT.length];
    }

    @Override
    public int getIconResId(int index) {
        return ICONS[index % ICONS.length];
    }
}