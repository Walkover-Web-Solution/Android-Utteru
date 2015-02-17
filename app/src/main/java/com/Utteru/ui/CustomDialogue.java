package com.Utteru.ui;


import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.VariableClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 11/24/14.
 */
public class CustomDialogue extends Dialog implements View.OnClickListener {
    public Activity c;
    Button done, skip;
    EditText promo_ed;
    RelativeLayout error_layout;
    FontTextView error_FontTextView;
    String promoCode;
    Button close_em;

    public CustomDialogue(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_box_layout);

        done = (Button) findViewById(R.id.done_button);
        skip = (Button) findViewById(R.id.skip_button);
        promo_ed = (EditText) findViewById(R.id.promocode);


        error_FontTextView = (FontTextView) findViewById(R.id.error_text);
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        close_em = (Button) findViewById(R.id.close_button);
        skip.setOnClickListener(this);
        done.setOnClickListener(this);
        error_layout.setOnClickListener(this);
        close_em.setOnClickListener(this);

    }


    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.done_button:
                promoCode = promo_ed.getText().toString();

                if (!promoCode.equals("")) {
                    showErrorMessage(false, "");

                    new updatePromoCode().execute(null, null, null);
                } else {

                    showErrorMessage(true, c.getResources().getString(R.string.fill_all));
                }
                break;
            case R.id.skip_button:
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

    class updatePromoCode extends AsyncTask<Void, Void, Void> {

        String response;
        Boolean iserr = false;


        @Override
        protected void onPostExecute(Void result) {

            CommonUtility.dialog.dismiss();
            if (iserr) {

                showErrorMessage(true, response);
            } else {

                dismiss();
                CommonUtility.showCustomAlert(c,c.getString(R.string.promo_code_updated));


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

            response = Apis.getApisInstance(c).signupPromo(promoCode);

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