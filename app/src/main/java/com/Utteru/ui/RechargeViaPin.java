package com.Utteru.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.splunk.mint.Mint;

import org.json.JSONException;
import org.json.JSONObject;

public class RechargeViaPin extends BaseActivity {

    TextView pint_text;
    EditText pin_ed;
    FontTextView  tittleback;
    Button recharge_pin_button;
    String pin;
    RelativeLayout error_layout;
    TextView error_textview;
    Button close_button;
    Context ctx = this;
    Tracker tracker;
    LinearLayout root;
    ImageView backpress,gototohome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recharge_via_pin);


        tracker = MyAnalyticalTracker.getTrackerInstance().getTracker(MyAnalyticalTracker.TrackerName.APP_TRACKER,this);

        init();
        Mint.initAndStartSession(RechargeViaPin.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));
    }

    @Override
    protected void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        // Set screen name.
        tracker.setScreenName("RBP Screen Android");
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
    public void onBackPressed() {
        Intent menu =new Intent(RechargeViaPin.this,MenuScreen.class);
        menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(menu);
        overridePendingTransition(R.anim.animation3, R.anim.animation4);
    }

    @Override
    protected void onResume() {

        error_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
            }
        });

        pin_ed.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                    recharge_pin_button.performClick();
                }
                return false;
            }
        });
        recharge_pin_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    pin = pin_ed.getText().toString();
                    if (pin.equals(""))
                        showErrorMessage(true, getResources().getString(R.string.entert_pin));
                    else if (!(pin.length() < 14))
                        new RechargeViaPinTask().execute(null, null, null);
                    else
                        showErrorMessage(true, getResources().getString(R.string.pin_length_valid));
                } else
                    showErrorMessage(true, getResources().getString(R.string.internet_error));
            }
        });
        close_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
            }
        });

        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
                return false;
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
                startActivity(new Intent(RechargeViaPin.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });


        super.onResume();
    }


    public void init() {
        backpress = (ImageView)findViewById(R.id.auto_detect_country_back);
        gototohome = (ImageView)findViewById(R.id.auto_detect_country_home);
        tittleback = (FontTextView)findViewById(R.id.auto_detect_coutry_header);
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        root = (LinearLayout) findViewById(R.id.recharge_by_pin_root);
        error_textview = (TextView) findViewById(R.id.error_text);
        close_button = (Button) findViewById(R.id.close_button);
        pint_text = (TextView) findViewById(R.id.recharge_pin_text);
        pin_ed = (EditText) findViewById(R.id.recharge_pin_pin);
        recharge_pin_button = (Button) findViewById(R.id.recharge_pin_button);
    }

    void showErrorMessage(Boolean showm, String message) {
        if (showm) {
            error_textview.setText(message);
            if (error_layout.getVisibility() == View.GONE)
                CommonUtility.expand(error_layout);
        } else {
            if (error_layout.getVisibility() == View.VISIBLE)
                CommonUtility.collapse(error_layout);

        }


    }

    class RechargeViaPinTask extends AsyncTask<Void, Void, Void> {
        String response;
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {
            recharge_pin_button.setEnabled(true);
            CommonUtility.dialog.dismiss();
            if (iserr) {
                showErrorMessage(iserr, response);
            } else {
                pin_ed.setText("");

                CommonUtility.showCustomAlert(RechargeViaPin.this, getString(R.string.success_message)).show();
            }

            recharge_pin_button.setEnabled(true);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            CommonUtility.show_PDialog(RechargeViaPin.this, getResources().getString(R.string.please_wait));
            recharge_pin_button.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject joparent;

            response = Apis.getApisInstance(ctx).RechargeByPin(pin);

            if (!(response.equals(""))) {


                try {
                    joparent = new JSONObject(response);
                    //if response of failed
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserr = true;
                        joparent = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = joparent.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    iserr = true;
                    response = getResources().getString(R.string.parse_error);
                }

            } else {
                iserr = true;
                response = getResources().getString(R.string.server_error);
            }
            return null;
        }
    }

}
