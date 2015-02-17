package com.Utteru.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.splunk.mint.Mint;
import com.stripe.model.Charge;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;


/**
 * Created by root on 11/26/14.
 */
public class BuyNowActivity extends Activity {


    Button buy_now_stripe,buy_now_paypal;
    private static final String TAG = "paymentExample";

    Charge charge;
    SeekBar seekBar;
    FontTextView tittleback;
    FontTextView balance_text,balance_currecncy;
    int min_value=0;
    int max_value=0;
    int increase_value=0;
    int seek_bar_max=0;
    Boolean hastariff=false;
    String orderId;
    Context ctx=this;
    String amount,paypal_amount;
    String payId,trackId;
    Tracker tracker;
    RelativeLayout error_layout;
    FontTextView error_FontTextView;
    Button close_em_button;
    ImageView backpress,gototohome;

    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
    private static final String CONFIG_CLIENT_ID = "ARTjPxAjSiwxQitY7GrR07Iyx5SvvqgBAI1fcFQ16Bk-zHyiQglLueNOQeYD";
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .acceptCreditCards(false)
                    // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("Utteru")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_now_layout);
        init();

        Mint.initAndStartSession(BuyNowActivity.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));
        tracker = MyAnalyticalTracker.getTrackerInstance().getTracker(MyAnalyticalTracker.TrackerName.APP_TRACKER,this);


    }

    @Override
    protected void onStart() {
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        // Set screen name.
        tracker.setScreenName("Buy Now Screen Android");
        // Send a screen view.
        tracker.send(new HitBuilders.AppViewBuilder().build());
        super.onStart();
    }
    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);

        super.onStop();
    }




    @Override
    protected void onResume() {
        error_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
            }
        });
        close_em_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showErrorMessage(false, "");
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = min_value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.e("progess value",""+i);
                progress=min_value+(increase_value*i);
                Log.e("calculated",""+progress);
                balance_text.setText("" + progress);

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                balance_text.setText("" + progress);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                balance_text.setText(""+progress);
                amount=""+progress;

            }
        });
        buy_now_stripe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showErrorMessage(false,"");
                Intent sendviaStripe=new Intent(BuyNowActivity.this,BuyByStripe.class);
                sendviaStripe.putExtra(VariableClass.ResponseVariables.AMOUNT,amount);
                startActivity(sendviaStripe);
                overridePendingTransition(R.anim.animation1, R.anim.animation2);

            }
        });

        buy_now_paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(CommonUtility.isNetworkAvailable(ctx)) {
                    showErrorMessage(false, "");
                    new PaymentPreviousData().execute();
                }
                else{
                    showErrorMessage(true,getString(R.string.internet_error));
                }


            }
        });
        tittleback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        buy_now_paypal.setEnabled(true);
        buy_now_stripe.setEnabled(true);
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.e(TAG,""+confirm.toString());
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));

                        payId=confirm.toJSONObject().getJSONObject(VariableClass.ResponseVariables.RESPONSE).getString(VariableClass.ResponseVariables.PAYID);

                        new ConfirmPaymentTask().execute();


                    } catch (JSONException e) {

                        showErrorMessage(true,getString(R.string.payment_error1));

                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {

                showErrorMessage(true,getString(R.string.payment_cancle));
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {

                showErrorMessage(true,getString(R.string.invalid_recharge));
            }
        }
    }
    private PayPalPayment getThingToBuy(String paymentIntent) {

try {
    return new PayPalPayment(new BigDecimal(paypal_amount), "USD", "Utteru Recharge ", paymentIntent);
}
catch (Exception e)
{
    return  null;
}
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    boolean setScrollBarDetails()
    {

        int tarrif= Integer.parseInt(Prefs.getUserTariff(this));
        Boolean gottarrif=false;
        if(tarrif!=0) {
            switch (tarrif) {
                case 7:
                    gottarrif = true;
                    min_value = 500;
                    max_value = 3000;
                    increase_value = 50;

                    seek_bar_max=((max_value-min_value)/increase_value);

                    break;
                case 9:
                    gottarrif = true;
                    min_value = 20;
                    max_value = 110;
                    increase_value = 10;
                    seek_bar_max=((max_value-min_value)/increase_value);

                    break;
                case 84:
                    gottarrif = true;
                    min_value = 5;
                    max_value = 30;
                    increase_value = 5;
                    seek_bar_max=((max_value-min_value)/increase_value);
                    break;
            }
            seekBar.setMax(seek_bar_max);
            balance_text.setText(""+min_value);
            balance_currecncy.setText(Prefs.getUserCurrency(BuyNowActivity.this));
            amount=""+min_value;
        }
        return  gottarrif;

    }
    void init(){

        backpress = (ImageView)findViewById(R.id.auto_detect_country_back);
        gototohome = (ImageView)findViewById(R.id.auto_detect_country_home);
        tittleback = (FontTextView)findViewById(R.id.auto_detect_coutry_header);
        error_FontTextView = (FontTextView)findViewById(R.id.error_text);
        error_layout = (RelativeLayout)findViewById(R.id.error_layout);
        close_em_button = (Button)findViewById(R.id.close_button);

        buy_now_stripe = (Button) findViewById(R.id.buy_now_button_stripe);
        buy_now_paypal = (Button) findViewById(R.id.buy_now_button_paypal);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        balance_currecncy=(FontTextView)findViewById(R.id.bal_currecny);
        balance_text = (FontTextView)findViewById(R.id.bal_amount);

        if(getIntent().getExtras()!=null)
        {
            buy_now_paypal.setVisibility(View.GONE);
            buy_now_stripe.setVisibility(View.VISIBLE);
        }
        else{
            buy_now_stripe.setVisibility(View.GONE);
            buy_now_paypal.setVisibility(View.VISIBLE);
        }
        hastariff=setScrollBarDetails();

        if(hastariff) {

            Intent intent = new Intent(this, PayPalService.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            startService(intent);

        }
        else{
            //show alert
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {


                        case DialogInterface.BUTTON_NEUTRAL:
                            //No button clicked
                            dialog.dismiss();
                            BuyNowActivity.this.finish();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(BuyNowActivity.this);
            builder.setMessage("Sorry not able initialize set up .Please contact to support")
                    .setNeutralButton("OK",dialogClickListener).show();

        }
    }

    public class ConfirmPaymentTask extends AsyncTask<Void, Void, Void> {
        String response = "";
        Boolean iserror = false;

        @Override
        protected void onPostExecute(Void result) {

            if (iserror) {
                showErrorMessage(true, response);
            } else {

                CommonUtility.showCustomAlert(BuyNowActivity.this,getString(R.string.success_message)).show();

                overridePendingTransition(R.anim.animation3, R.anim.animation4);
            }
            CommonUtility.dialog.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            CommonUtility.show_PDialog(BuyNowActivity.this, getResources().getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            response = Apis.getApisInstance(BuyNowActivity.this).paypalInfo(orderId,payId ,trackId);
            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;
                try {
                    joparent = new JSONObject(response);
                    //failed response
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                        iserror=true;
                    }
                    //success response
                    else if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                    }
                } catch (JSONException e) {
                    iserror = true;
                    response = getResources().getString(R.string.parse_error);
                    e.printStackTrace();
                }
            } else {
                iserror = true;
                response = getResources().getString(R.string.server_error);
            }
            return null;
        }

    }
    public class PaymentPreviousData extends AsyncTask<Void, Void, Void> {
        String response = "";
        Boolean iserror = false;

        @Override
        protected void onPostExecute(Void result) {


            if (iserror) {
                buy_now_paypal.setEnabled(true);
                buy_now_stripe.setEnabled(true);
                showErrorMessage(true, response);
            } else {
                PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

                if(thingToBuy!=null)
                {
                    Intent intent = new Intent(BuyNowActivity.this, PaymentActivity.class);

                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

                    startActivityForResult(intent, REQUEST_CODE_PAYMENT);
                    overridePendingTransition(R.anim.animation1,R.anim.animation2);
                }
                else{
                    CommonUtility.showCustomAlertError(BuyNowActivity.this,getString(R.string.parse_error));
                }

            }
            CommonUtility.dialog.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            buy_now_paypal.setEnabled(false);
            buy_now_stripe.setEnabled(false);
            CommonUtility.show_PDialog(BuyNowActivity.this, getResources().getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            response = Apis.getApisInstance(BuyNowActivity.this).paypalpreviousInfo(amount);
            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;
                JSONArray japarent;

                try {
                    joparent = new JSONObject(response);
                    //failed response
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                        iserror=true;
                    }
                    //success response
                    else if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {

                        japarent=joparent.getJSONArray(VariableClass.ResponseVariables.CONTENT);

                        jochild=japarent.getJSONObject(0);
                        Log.e("child json ",""+jochild);

                        orderId=jochild.getString(VariableClass.ResponseVariables.ORDERID);

                        paypal_amount=jochild.getString(VariableClass.ResponseVariables.AMOUNT);
                        trackId=jochild.getString(VariableClass.ResponseVariables.TRACKID);
                        if(amount==null||orderId==null||trackId==null)
                        {
                            iserror=true;
                            response=getString(R.string.payment_error);
                        }


                    }
                } catch (JSONException e) {
                    iserror = true;
                    response = getResources().getString(R.string.parse_error);
                    e.printStackTrace();
                }
            } else {
                iserror = true;
                response = getResources().getString(R.string.server_error);
            }
            return null;
        }

    }
    void showErrorMessage(Boolean showm, String message) {
        if (showm) {
            error_FontTextView.setText(message);
            if(error_layout.getVisibility()==View.GONE)
                CommonUtility.expand(error_layout);
        } else {
            if(error_layout.getVisibility()==View.VISIBLE)
                CommonUtility.collapse(error_layout);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.animation3,R.anim.animation4);

    }
}
