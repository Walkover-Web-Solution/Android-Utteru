package com.Utteru.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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


public class DialogConfirm extends Dialog implements View.OnClickListener {
    public Activity c;
    Button cancel, proceed;
    Activity mActivity;
    RelativeLayout error_layout;
    FontTextView error_FontTextView;
    String tittletext;
    FontTextView tvdeductmsg;
    Button close_em;

    public DialogConfirm(Activity a) {
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
        tvdeductmsg.setText(tittletext + " " + Prefs.getPlanRate(c));

    }


    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.skip_button:

                new AddDedicatedNumber().execute();

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

    class AddDedicatedNumber extends AsyncTask<Void, Void, Void> {

        String response;
        Boolean iserr = false;


        @Override
        protected void onPostExecute(Void result) {

            CommonUtility.dialog.dismiss();
            if (iserr) {

                showErrorMessage(true, response);
            } else {

                dismiss();
                CommonUtility.showCustomAlert(c, c.getString(R.string.NumberAdded));
                Intent success = new Intent(mActivity, CallForwardtoNumber.class);
                mActivity.startActivity(success);
                mActivity.finish();


            }

            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            CommonUtility.show_PDialog(c, c.getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            response = Apis.getApisInstance(c).addDedicatedNumber();

            if (!response.equals("")) {
                JSONObject joparent = null;
                JSONObject jochild = null;
                JSONArray japarent = null;
                try {
                    joparent = new JSONObject(response);
                    //success response
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {


                    } else if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {

                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                        iserr = true;


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

    }
}
