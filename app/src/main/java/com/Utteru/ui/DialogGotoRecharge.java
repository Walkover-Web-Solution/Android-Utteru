package com.Utteru.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;

/**
 * Created by walkover on 26/2/15.
 */


public class DialogGotoRecharge extends Dialog implements View.OnClickListener {
    public Activity c;
    Button cancel, proceed;
    Activity mActivity;
    RelativeLayout error_layout;
    FontTextView error_FontTextView;
    String tittletext;
    FontTextView tvdeductmsg;
    Button close_em;

    public DialogGotoRecharge(Activity a) {
        super(a);
        this.c = a;
        mActivity = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_box_call_forward);

        cancel = (Button) findViewById(R.id.done_button);
        proceed = (Button) findViewById(R.id.skip_button);
        tvdeductmsg = (FontTextView) findViewById(R.id.tittle);
        tittletext = tvdeductmsg.getText().toString();


        error_FontTextView = (FontTextView) findViewById(R.id.error_text);
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        close_em = (Button) findViewById(R.id.close_button);
        proceed.setOnClickListener(this);
        cancel.setOnClickListener(this);
        error_layout.setOnClickListener(this);
        close_em.setOnClickListener(this);
        tvdeductmsg.setText("You do not have sufficient amount of balance do you want to recharge your account?");

    }


    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.skip_button:
                Intent rechrge = new Intent(mActivity, BuyNowOptionsActivity.class);

                mActivity.startActivity(rechrge);
                dismiss();

                break;
            case R.id.done_button:
                dismiss();


        }
    }


}
