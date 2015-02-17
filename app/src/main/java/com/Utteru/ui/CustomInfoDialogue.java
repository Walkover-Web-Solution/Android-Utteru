package com.Utteru.ui;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.Utteru.R;
import com.Utteru.commonUtilities.FontTextView;

/**
 * Created by root on 11/24/14.
 */
public class CustomInfoDialogue extends Dialog implements View.OnClickListener {
    public Activity c;
    Button done;
    String title, subtitle;
    FontTextView title_view, subtitle_view;


    public CustomInfoDialogue(Activity a, String title, String subtitle) {
        super(a);
        this.c = a;
        this.title = title;
        this.subtitle = subtitle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.info_alert_box_layout);

        done = (Button) findViewById(R.id.info_done_button);
        title_view = (FontTextView) findViewById(R.id.title_info);
        subtitle_view = (FontTextView) findViewById(R.id.info_subtitle);
        if (!title.equals(""))
            title_view.setText(title);
        else
            title_view.setVisibility(View.GONE);

        subtitle_view.setText(subtitle);

        done.setOnClickListener(this);

    }


    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.info_done_button:
                this.dismiss();
                break;


        }
    }

}