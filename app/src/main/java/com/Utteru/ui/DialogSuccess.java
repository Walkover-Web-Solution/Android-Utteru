package com.Utteru.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by walkover on 26/2/15.
 */


public class DialogSuccess extends Dialog implements View.OnClickListener {
    public Activity c;
    Button cancel, proceed;
    Activity mActivity;
    RelativeLayout error_layout;
    FontTextView error_FontTextView;
    String tittletext;
    FontTextView tvdeductmsg;
    Button close_em;
    String mNumber;

    public DialogSuccess(Activity a, String number) {
        super(a);
        this.c = a;
        this.mNumber = number;
        mActivity = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_sucess);

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
       // tvdeductmsg.setText(tittletext + " " + Prefs.getPlanRate(c));
        new GetPricing().execute();

    }


    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.skip_button:

                Intent success = new Intent(mActivity, CallForwardActivity.class);
                mActivity.startActivity(success);
                mActivity.finish();

                break;
            case R.id.done_button:
                dismiss();
            case R.id.error_layout:
                showErrorMessage(false, "");
                break;
            case R.id.close_button:
                showErrorMessage(false, "");
                break;


        }
    }

    void showErrorMessage(Boolean showm, String message) {
        if (showm) {
            error_FontTextView.setText(message);
            if (error_layout.getVisibility() == View.GONE)
                CommonUtility.expand(error_layout);
        } else {
            if (error_layout.getVisibility() == View.VISIBLE)
                CommonUtility.collapse(error_layout);

        }
    }


    public class GetPricing extends AsyncTask<Void, Void, Void> {
        String response = "";
        Boolean iserror = false;
        String rate, currency;

        @Override
        protected void onPostExecute(Void result) {

            if (iserror) {

                showErrorMessage(true, response);

            }
            tvdeductmsg.setText(tvdeductmsg.getText().toString() + rate + " " + currency);
            Log.e("Value_rate ",rate+currency+mNumber);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String selected_number = null;


            response = Apis.getApisInstance(mActivity).getTwoWayPricing(mNumber);
            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;
                JSONArray jarray;
                try {
                    joparent = new JSONObject(response);
                    //failed response
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                        iserror = true;
                    }
                    //success response
                    else if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                        jarray = joparent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                        jochild = jarray.getJSONObject(0);
                        rate = jochild.getString(VariableClass.ResponseVariables.RATE);
                        currency = jochild.getString(VariableClass.ResponseVariables.CURRENCY_NAME);
                    }
                } catch (JSONException e) {
                    iserror = true;

                    e.printStackTrace();
                }
            } else {
                iserror = true;

            }
            return null;
        }

    }
}
