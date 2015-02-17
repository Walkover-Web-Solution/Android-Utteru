package com.Utteru.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.Utteru.R;
import com.Utteru.ui.ManageEmailsFrag;
import com.Utteru.ui.ManageNumbersFrag;
import com.Utteru.ui.MyProfileFrag;
import com.viewpagerindicator.IconPagerAdapter;

public class ViewPagerManageVerifiedDataAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
    protected static final String[] CONTENT = new String[]{"One", "Two", "Three"};
    protected static final int[] ICONS = new int[]{
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
    };

    private int mCount = CONTENT.length;

    public ViewPagerManageVerifiedDataAdapter(FragmentManager fm) {

        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            default:
                return null;
            case 0:
                return new ManageNumbersFrag();
            case 1:
                return new ManageEmailsFrag();
            case 2:
                return new MyProfileFrag();


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
        return ViewPagerManageVerifiedDataAdapter.CONTENT[position % CONTENT.length];
    }

    @Override
    public int getIconResId(int index) {
        return ICONS[index % ICONS.length];
    }
}