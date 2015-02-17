package com.Utteru.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.Utteru.R;
import com.Utteru.adapters.ViewPagerAdapter;
import com.viewpagerindicator.CirclePageIndicator;

public class Tutorial_fragement_two extends Fragment {

    View tutorial_view2;
    ViewPager pager;
    CirclePageIndicator indicator;
    ViewPagerAdapter mAdapter;
    Button skip;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        tutorial_view2 = inflater.inflate(R.layout.tutorial_two, container, false);

        pager = (ViewPager) tutorial_view2.findViewById(R.id.pager);
        indicator = (CirclePageIndicator) tutorial_view2.findViewById(R.id.indicator);
        skip = (Button) tutorial_view2.findViewById(R.id.skip_2);
        mAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        pager.setAdapter(mAdapter);
        indicator.setViewPager(pager);
        indicator.setCurrentItem(1);
        pager.setEnabled(false);

        return tutorial_view2;
    }

    @Override
    public void onResume() {

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SignUpHome.timer.cancel();
                    SignUpHome.mPager.setCurrentItem(2);
                    SignUpHome.mIndicator.setCurrentItem(2);
                } catch (Exception e) {

                }


            }
        });
        super.onResume();
    }
}
