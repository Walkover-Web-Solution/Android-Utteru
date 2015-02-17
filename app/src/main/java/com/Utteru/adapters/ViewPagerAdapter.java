package com.Utteru.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.Utteru.R;
import com.Utteru.ui.SignUpFragement;
import com.Utteru.ui.Tutorial_fragement_one;
import com.Utteru.ui.Tutorial_fragement_two;
import com.viewpagerindicator.IconPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
    protected static final String[] CONTENT = new String[]{"One", "Two", "Three"};
    protected static final int[] ICONS = new int[]{
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,

    };
    private int type;

    private int mCount = CONTENT.length;

    public ViewPagerAdapter(FragmentManager fm) {

        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            default:
                return null;
            case 0:
                return new Tutorial_fragement_one();
            case 1:
                return new Tutorial_fragement_two();
            case 2:
                return new SignUpFragement();

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
        return ViewPagerAdapter.CONTENT[position % CONTENT.length];
    }

    @Override
    public int getIconResId(int index) {
        return ICONS[index % ICONS.length];
    }
}