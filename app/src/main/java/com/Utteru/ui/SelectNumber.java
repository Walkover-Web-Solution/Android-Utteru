package com.Utteru.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.MultipleVerifiedNumber;
import com.splunk.mint.Mint;

import org.json.JSONException;
import org.json.JSONObject;

public class SelectNumber extends ActionBarActivity {

    RadioGroup numbers_group;
    Bundle bundle;
    MultipleVerifiedNumber selected_number;
    SparseArray<MultipleVerifiedNumber> radio_id_list;
    Context ctx = this;
    Button send_verification_code_btn, close_em_button;
    String tempId = "";
    FontTextView error_FontTextView;
    RelativeLayout error_layout;

    @Override
    protected void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_number);
        init();
        Mint.initAndStartSession(SelectNumber.this, "395e969a");
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));



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
        numbers_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selected_number = radio_id_list.get(checkedId);

            }
        });


        send_verification_code_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    if (selected_number != null && !selected_number.getNumber().equals("")) {

                        showErrorMessage(false, "");

                        new ValidateForgotPassword().execute(null, null, null);
                    } else {
                        showErrorMessage(true, getResources().getString(R.string.select_verified_number));

                    }
                } else {
                    showErrorMessage(true, getResources().getString(R.string.internet_error));
                }

            }

        });

        super.onResume();
    }

    public void init() {
        numbers_group = (RadioGroup) findViewById(R.id.verified_numbers_group);
        bundle = getIntent().getExtras();
        Log.e("list size", "" + CommonUtility.c_list.size());
        radio_id_list = new SparseArray<MultipleVerifiedNumber>();
        send_verification_code_btn = (Button) findViewById(R.id.select_number_button);
        tempId = bundle.getString(VariableClass.Vari.TEMP_ID);

        error_FontTextView = (FontTextView) findViewById(R.id.error_text);
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        close_em_button = (Button) findViewById(R.id.close_button);
        addRadioButton();

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

    public void addRadioButton() {
        int id;
        for (int i = 0; i < CommonUtility.c_list.size(); i++) {
            final int WC = RadioGroup.LayoutParams.WRAP_CONTENT;
            RadioGroup.LayoutParams rParams;
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(CommonUtility.c_list.get(i).getCountry_code() + CommonUtility.c_list.get(i).getNumber());
            Log.e("getting number", "" + CommonUtility.c_list.get(i).getCountry_code() + CommonUtility.c_list.get(i).getNumber());
            radioButton.setTextSize(this.getResources().getDimension(R.dimen.textsize_bal

            ));
            radioButton.setTextColor(this.getResources().getColor(android.R.color.white));
            StateListDrawable mState = new StateListDrawable();
            mState.addState(new int[]{android.R.attr.state_checked},
                    getResources().getDrawable(android.R.drawable.radiobutton_on_background));
            mState.addState(new int[]{},
                    getResources().getDrawable(android.R.drawable.radiobutton_off_background));
            radioButton.setButtonDrawable(mState);
            id = 1000 + i;
            radioButton.setId(id);
            rParams = new RadioGroup.LayoutParams(WC, WC);
            numbers_group.addView(radioButton, rParams);
            radio_id_list.put(id, CommonUtility.c_list.get(i));
        }
    }

    @Override
    protected void onStop() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {


        send_verification_code_btn.performClick();

    }

    public class ValidateForgotPassword extends AsyncTask<Void, Void, Void> {
        String response = "";
        Boolean iserror = false;

        @Override
        protected void onPostExecute(Void result) {
            if (iserror) {
                showErrorMessage(true, response);
            } else {
                //show verification code screen
                Intent insertverficationCode = new Intent(ctx, VerificationCodeActivity.class);
                insertverficationCode.putExtra(VariableClass.Vari.USERID, selected_number.getNumber());
                insertverficationCode.putExtra(VariableClass.Vari.SHOWOTHERNUMBER, "");
                insertverficationCode.putExtra(VariableClass.Vari.SOURCECLASS, "1");
                insertverficationCode.putExtra(VariableClass.Vari.COUNTRYCODE, selected_number.getCountry_code());
                startActivity(insertverficationCode);
                SelectNumber.this.finish();
                overridePendingTransition(R.anim.animation1, R.anim.animation2);
            }
            send_verification_code_btn.setEnabled(true);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            CommonUtility.show_PDialog(SelectNumber.this, getResources().getString(R.string.please_wait));
            send_verification_code_btn.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(ctx).forgotPassword(selected_number.getCountry_code() + selected_number.getNumber(), new String[]{Prefs.getUserCountryCode(ctx)}, "2", Prefs.getResellerID(ctx));
            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;
                try {
                    joparent = new JSONObject(response);
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserror = true;
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
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
}
