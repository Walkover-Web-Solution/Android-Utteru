package com.Utteru.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.splunk.mint.Mint;

/**
 * Created by root on 12/29/14.
 */
public class BuyNowOptionsActivity extends Activity {

    Button recharge_via_paypal, recharge_by_credit_card, recharge_by_pin, transactions;
    Context ctx;
    ImageView backpress,gototohome;
    FontTextView tittleback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_now_options_layout);
        init();
        Mint.initAndStartSession(BuyNowOptionsActivity.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));


    }

    @Override
    protected void onResume() {

        recharge_by_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ctx, RechargeViaPin.class));
                overridePendingTransition(R.anim.animation1, R.anim.animation2);
            }
        });
        recharge_by_credit_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ctx, BuyNowActivity.class);
                intent.putExtra(VariableClass.Vari.SELECTEDDATA, "credit card");
                startActivity(intent);
                overridePendingTransition(R.anim.animation1, R.anim.animation2);
            }
        });
        recharge_via_paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ctx, BuyNowActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.animation1, R.anim.animation2);
            }
        });
        transactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, TransactionLogActivity.class));
                overridePendingTransition(R.anim.animation1, R.anim.animation2);

            }
        });
        tittleback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menu =new Intent(BuyNowOptionsActivity.this,MenuScreen.class);
                menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(menu);
             //   onBackPressed();
            }
        });
        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menu =new Intent(BuyNowOptionsActivity.this,MenuScreen.class);
                menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(menu);

            }
        });
        gototohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        super.onResume();
    }

    void init() {

        ctx = this;
        backpress = (ImageView)findViewById(R.id.auto_detect_country_back);
        gototohome = (ImageView)findViewById(R.id.auto_detect_country_home);
        tittleback = (FontTextView)findViewById(R.id.auto_detect_coutry_header);
        recharge_by_pin = (Button) findViewById(R.id.buy_now_rechargeviapin_option);
        recharge_by_credit_card = (Button) findViewById(R.id.buy_now_stripe_option);
        recharge_via_paypal = (Button) findViewById(R.id.buy_now_paypal_option);
        transactions = (Button) findViewById(R.id.buy_now_transaction_option);
    }
}

