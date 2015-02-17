package com.Utteru.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.VerifiedData;
import com.splunk.mint.Mint;

import org.json.JSONException;
import org.json.JSONObject;

public class EditNumberEmails extends Activity {
    Context ctx = this;
    VerifiedData selecteddata;
    EditText vc_code1, vc_code2, vc_code3, vc_code4;
    Button close_error_button;
    Button verify_code;
    RelativeLayout error_layout;
    FontTextView error_FontTextView;
    String confirmCode;
    Button delete_number, make_number_defalut;
    LinearLayout verification_layout;
    FontTextView data_to_be_edit;
    int carrierType = 2;
    FontTextView resend_sms, resend_call, resend_email, text_or;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_verified_data_layout);
        init();
        Mint.initAndStartSession(EditNumberEmails.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));



    }

    @Override
    protected void onResume() {
        resend_call.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
                carrierType = 1;
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    new ResendCode().execute(null, null, null);
                } else
                    showErrorMessage(true, getResources().getString(R.string.internet_error));


            }
        });
        resend_sms.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
                carrierType = 0;
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    new ResendCode().execute(null, null, null);
                } else
                    showErrorMessage(true, getResources().getString(R.string.internet_error));


            }
        });
        resend_email.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    new ResendCode().execute(null, null, null);
                } else
                    showErrorMessage(true, getResources().getString(R.string.internet_error));


            }
        });


        vc_code1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (vc_code1.length() > 0) {
                    vc_code2.requestFocus();
                }
            }
        });
        vc_code2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (vc_code2.length() > 0) {
                    vc_code3.requestFocus();
                } else {
                    vc_code1.requestFocus();
                }
            }
        });

        vc_code3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (vc_code3.length() > 0) {
                    vc_code4.requestFocus();
                } else {
                    vc_code2.requestFocus();
                }
            }
        });

        vc_code4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (vc_code4.length() == 0) {
                    vc_code3.requestFocus();
                }
            }
        });


        close_error_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
            }
        });

        error_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
            }
        });

        verify_code.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    if (!vc_code1.getText().toString().equals("") && !vc_code2.getText().toString().equals("") && !vc_code3.getText().toString().equals("") && !vc_code4.getText().toString().equals("")) {
                        confirmCode = vc_code1.getText().toString() + vc_code2.getText().toString() + vc_code3.getText().toString() + vc_code4.getText().toString();
                        new VerifyData().execute(null, null, null);
                    } else
                        showErrorMessage(true, getResources().getString(R.string.fill_all));
                } else
                    showErrorMessage(true, getResources().getString(R.string.internet_error));
            }
        });
        make_number_defalut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    new MakeDataDefault().execute(null, null, null);
                } else
                    showErrorMessage(true, getResources().getString(R.string.internet_error));

            }
        });
        delete_number.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    if (selecteddata.getState() == ManageNumbersHome.ISVERIFIED) {

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        new DeleteData().execute(null, null, null);
                                        dialog.dismiss();
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        builder.setMessage("Are you sure to delete " + selecteddata.getParticualr() + "?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    } else
                        new DeleteData().execute(null, null, null);

                } else
                    showErrorMessage(true, getResources().getString(R.string.internet_error));


            }
        });

        super.onResume();
    }

    void init() {

//        resend_sms,resend_call,resend_email,text_or
        resend_sms = (FontTextView) findViewById(R.id.mn_resend_button_sms);
        resend_call = (FontTextView) findViewById(R.id.mn_resend_call_button);
        resend_email = (FontTextView) findViewById(R.id.mn_resend_email_button);

        verification_layout = (LinearLayout) findViewById(R.id.mn_verify_number_layout);
        selecteddata = (VerifiedData) getIntent().getSerializableExtra(VariableClass.Vari.SELECTEDDATA);
        vc_code1 = (EditText) findViewById(R.id.mn_code1);
        vc_code2 = (EditText) findViewById(R.id.mn_code2);
        vc_code3 = (EditText) findViewById(R.id.mn_code3);
        vc_code4 = (EditText) findViewById(R.id.mn_code4);

        resend_sms.setPaintFlags(resend_sms.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        resend_call.setPaintFlags(resend_call.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        resend_email.setPaintFlags(resend_email.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        verify_code = (Button) findViewById(R.id.mn_verifycode_button);

        error_FontTextView = (FontTextView) findViewById(R.id.error_text);
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        close_error_button = (Button) findViewById(R.id.close_button);
        delete_number = (Button) findViewById(R.id.delete_number);
        make_number_defalut = (Button) findViewById(R.id.make_number_default);
        text_or = (FontTextView) findViewById(R.id.text_or);


        if (selecteddata.getType() == ManageNumbersHome.ISNUMBER) {
            resend_email.setVisibility(View.GONE);
            resend_call.setVisibility(View.VISIBLE);
            resend_sms.setVisibility(View.VISIBLE);
            text_or.setVisibility(View.VISIBLE);

        } else if (selecteddata.getType() == ManageNumbersHome.ISEMAIL) {
            resend_email.setVisibility(View.VISIBLE);
            resend_call.setVisibility(View.GONE);
            resend_sms.setVisibility(View.GONE);
            text_or.setVisibility(View.GONE);
        }

        if (selecteddata.getState() == ManageNumbersHome.ISUNVERIFIED) {
            verification_layout.setVisibility(View.VISIBLE);
            make_number_defalut.setVisibility(View.GONE);
        } else if (selecteddata.getState() == ManageNumbersHome.ISVERIFIED) {
            verification_layout.setVisibility(View.GONE);
            make_number_defalut.setVisibility(View.VISIBLE);
        }
        data_to_be_edit = (FontTextView) findViewById(R.id.mn_data_to_be_edit);
        Log.e("getting data", "" + selecteddata.getParticualr());
        if (selecteddata.getType() == ManageNumbersHome.ISNUMBER)
            data_to_be_edit.setText("+" + selecteddata.getCountryCode() + selecteddata.getParticualr());
        if (selecteddata.getType() == ManageNumbersHome.ISEMAIL)
            data_to_be_edit.setText(selecteddata.getParticualr());
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

    @Override
    public void onBackPressed() {

        this.finish();
        overridePendingTransition(R.anim.animation3, R.anim.animation4);
    }

    @Override
    protected void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onDestroy();
    }

    class MakeDataDefault extends AsyncTask<Void, Void, Void> {

        String response = "";
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {
            CommonUtility.dialog.cancel();
            if (iserr)
                showErrorMessage(true, response);
            else {
                showErrorMessage(false, "");
                CommonUtility.showCustomAlert(EditNumberEmails.this, getString(R.string.success_message)).show();
                startActivity(new Intent(EditNumberEmails.this, ManageNumbersHome.class));
                overridePendingTransition(R.anim.animation3, R.anim.animation4);
                finish();
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            CommonUtility.show_PDialog(EditNumberEmails.this, getResources().getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(ctx).makeDefaultNumberEmail(selecteddata.getType(), selecteddata.getParticualr(), selecteddata.getCountryCode());
            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;
                try {
                    joparent = new JSONObject(response);
                    //failed response
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserr = true;
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }

                } catch (JSONException e) {
                    iserr = true;
                    response = getResources().getString(R.string.parse_error);
                    e.printStackTrace();
                }
            } else {
                iserr = true;
                response = getResources().getString(R.string.server_error);
            }


            return null;
        }

    }

    class DeleteData extends AsyncTask<Void, Void, Void> {

        String response;
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {
            CommonUtility.dialog.cancel();
            if (iserr)
                showErrorMessage(true, response);
            else {
                showErrorMessage(false, "");
                CommonUtility.showCustomAlert(EditNumberEmails.this, getString(R.string.success_message)).show();
                startActivity(new Intent(EditNumberEmails.this, ManageNumbersHome.class));
                overridePendingTransition(R.anim.animation3, R.anim.animation4);
                finish();
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            CommonUtility.show_PDialog(EditNumberEmails.this, getResources().getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(ctx).deleteNumberEmail(selecteddata.getType(), selecteddata.getParticualr(), selecteddata.getCountryCode());
            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;
                try {
                    joparent = new JSONObject(response);
                    //failed response
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                        iserr = true;
                    }

                } catch (JSONException e) {
                    iserr = true;
                    response = getResources().getString(R.string.parse_error);
                    e.printStackTrace();
                }
            } else {
                iserr = true;
                response = getResources().getString(R.string.server_error);
            }


            return null;
        }

    }

    class VerifyData extends AsyncTask<Void, Void, Void> {

        String response;
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {
            CommonUtility.dialog.dismiss();
            if (iserr)
                showErrorMessage(true, response);
            else {
                showErrorMessage(false, "");
                CommonUtility.showCustomAlert(EditNumberEmails.this, getString(R.string.success_message)).show();
                startActivity(new Intent(EditNumberEmails.this, ManageNumbersHome.class));
                overridePendingTransition(R.anim.animation3, R.anim.animation4);
                finish();
            }

            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            CommonUtility.show_PDialog(EditNumberEmails.this, getResources().getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(ctx).verifyEmailNumber(selecteddata.getType(), selecteddata.getParticualr(), selecteddata.getCountryCode(), confirmCode);
            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;
                try {
                    joparent = new JSONObject(response);
                    //failed response
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                        iserr = true;
                    }

                } catch (JSONException e) {
                    iserr = true;
                    response = getResources().getString(R.string.parse_error);
                    e.printStackTrace();
                }
            } else {
                iserr = true;
                response = getResources().getString(R.string.server_error);
            }


            return null;
        }

    }

    class ResendCode extends AsyncTask<Void, Void, Void> {

        String response = "";
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {
            CommonUtility.dialog.dismiss();
            if (iserr)
                showErrorMessage(true, response);
            else {
                CommonUtility.showCustomAlert(EditNumberEmails.this, getString(R.string.success_message)).show();

            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            CommonUtility.show_PDialog(EditNumberEmails.this, getResources().getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(ctx).resendCodeEmailNumber(selecteddata.getType(), selecteddata.getParticualr(), selecteddata.getCountryCode(), "" + carrierType);
            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;
                try {
                    joparent = new JSONObject(response);
                    //failed response
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                        iserr = true;
                    }

                } catch (JSONException e) {
                    iserr = true;
                    response = getResources().getString(R.string.parse_error);
                    e.printStackTrace();
                }
            } else {
                iserr = true;
                response = getResources().getString(R.string.server_error);
            }


            return null;
        }

    }
}
