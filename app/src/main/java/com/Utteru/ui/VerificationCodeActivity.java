package com.Utteru.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.CustomKeyboardOther;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.notifier.IncomingSms;
import com.splunk.mint.Mint;

import org.json.JSONException;
import org.json.JSONObject;

public class VerificationCodeActivity extends BaseActivity {

    static EditText vc_code;
    static Button verify_code;
    static CountDownTimer counter;
    FontTextView resend_code_sms, resend_code_call, other_numbers;
    int carrieType = 0;
    RelativeLayout error_layout;
    FontTextView error_FontTextView;
    Button close_error_button;
    Context ctx = this;
    String confirmCode;
    Bundle bundle;
    String usernumber, countrycode, tempid = "";
    Boolean isforgotpass = false;
    FontTextView title;
    RelativeLayout root_layout;
    int SMS = 0;
    int CALL = 1;
    ProgressDialog pdialog;
    CustomKeyboardOther keyboard;
    LinearLayout dialpad_layout;

    public static void setCode(String code) {


        counter.onFinish();
        vc_code.setText(code);

        verify_code.performClick();


    }

    @Override
    protected void onDestroy() {
        if (pdialog != null) {
            pdialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(R.anim.animation3, R.anim.animation4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verification_code_layout);
        init();
        Mint.initAndStartSession(VerificationCodeActivity.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));

        // Calling Auto SMS Code Verification
        new IncomingSms();
        pdialog.show();
        counter = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                pdialog.dismiss();
            }
        }.start();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {

        verify_code.setEnabled(true);
        resend_code_call.setEnabled(true);
        resend_code_sms.setEnabled(true);
        if (pdialog != null)
            pdialog.dismiss();
        super.onStop();
    }

    @Override
    protected void onResume() {

        resend_code_call.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
                carrieType = 1;

                if (isforgotpass) {

                    new ResendCodeForgot().execute(null, null, null);
                } else
                    new ResendCodeSignup().execute(null, null, null);
            }
        });

        resend_code_sms.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                carrieType = 0;
                showErrorMessage(false, "");
                if (isforgotpass) {
                    new ResendCodeForgot().execute(null, null, null);
                } else
                    new ResendCodeSignup().execute(null, null, null);
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
                pdialog.dismiss();
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    if (!vc_code.getText().equals("") && vc_code.length() == 4) {
                        confirmCode = vc_code.getText().toString();

                        if (tempid != null && !tempid.equals(""))
                            new VerifyCodeWithSignup().execute(null, null, null);
                        else
                            new VerifyCodeForgot().execute(null, null, null);

                    } else
                        showErrorMessage(true, getResources().getString(R.string.fill_all));
                } else
                    showErrorMessage(true, getResources().getString(R.string.internet_error));
            }
        });

        other_numbers.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent show_other_numbs = new Intent(ctx, SelectNumber.class);
                show_other_numbs.putExtra(VariableClass.Vari.TEMP_ID, tempid);
                startActivity(show_other_numbs);
                VerificationCodeActivity.this.finish();
            }
        });

        /*root_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
                return false;
            }
        });*/
        root_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyBoard(false);
            }
        });
        vc_code.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                showKeyBoard(true);
                return false;
            }
        });


        super.onResume();
    }
    public void showKeyBoard(Boolean showKeyBoard) {
        //show keyboard
        if (showKeyBoard) {

            Animation bottomUp = AnimationUtils.loadAnimation(ctx,
                    R.anim.bottom_up);

            if (dialpad_layout.getVisibility() == View.GONE) {
                dialpad_layout.setAnimation(bottomUp);
                dialpad_layout.setVisibility(View.VISIBLE);

            }
        }
        //hide keyboard
        else {
            Animation bottpmdown = AnimationUtils.loadAnimation(ctx,
                    R.anim.bottom_down);
            if (dialpad_layout.getVisibility() == View.VISIBLE) {
                dialpad_layout.setAnimation(bottpmdown);
                dialpad_layout.setVisibility(View.GONE);



            }
        }
    }
    void init() {

        root_layout = (RelativeLayout) findViewById(R.id.verification_root);
        pdialog = new ProgressDialog(this, R.style.MyTheme);
        pdialog.setMessage(getString(R.string.please_wait));
        pdialog.setCancelable(false);
        pdialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);

        title = (FontTextView) findViewById(R.id.vc_text);
        resend_code_call = (FontTextView) findViewById(R.id.vc_resend_call_button);
        resend_code_sms = (FontTextView) findViewById(R.id.vc_resend_button_sms);
        resend_code_call.setPaintFlags(resend_code_call.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        resend_code_sms.setPaintFlags(resend_code_sms.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        vc_code = (EditText) findViewById(R.id.vc_code);

        verify_code = (Button) findViewById(R.id.vc_verifycode_button);
        other_numbers = (FontTextView) findViewById(R.id.vc_othernumber_option);
        error_FontTextView = (FontTextView) findViewById(R.id.error_text);
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        if (error_layout == null) {
            Log.e("verification", "true");
        }
        close_error_button = (Button) findViewById(R.id.close_button);
        resend_code_call = (FontTextView) findViewById(R.id.vc_resend_call_button);
        resend_code_call = (FontTextView) findViewById(R.id.vc_resend_call_button);
        bundle = getIntent().getExtras();
        usernumber = bundle.getString(VariableClass.Vari.USERID);

        countrycode = bundle.getString(VariableClass.Vari.COUNTRYCODE);
        title.setText(title.getText() + " :+" + countrycode + usernumber);
        dialpad_layout = (LinearLayout) findViewById(R.id.dialpad_layout);
        keyboard = new CustomKeyboardOther(VerificationCodeActivity.this, R.id.keyboardview, R.xml.numberic_key_only, null);
        keyboard.registerEditText(vc_code.getId(), null);
        Log.e("country code in verification", "" + countrycode);
        tempid = bundle.getString(VariableClass.Vari.TEMP_ID);
        if (bundle.containsKey(VariableClass.Vari.SHOWOTHERNUMBER)) {
            other_numbers.setVisibility(View.VISIBLE);
        } else
            other_numbers.setVisibility(View.GONE);
        if (bundle.containsKey(VariableClass.Vari.SOURCECLASS)) {
            isforgotpass = true;
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

    class VerifyCodeWithSignup extends AsyncTask<Void, Void, Void> {

        String response;
        Boolean askpin = false;
        Boolean iserror = false;
        String verifiedBy = "2";

        @Override
        protected void onPostExecute(Void result) {
            resend_code_call.setEnabled(true);
            resend_code_sms.setEnabled(true);
            verify_code.setEnabled(true);
            Log.e("ask pin value", "" + askpin);
            if (iserror) {

                showErrorMessage(true, response);
                pdialog.dismiss();

            } else if (askpin) {

                Intent startcreatpin = new Intent(ctx, CreatePasswordActivity.class);
                startcreatpin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startcreatpin.putExtra(VariableClass.Vari.VERIFI_CODE, confirmCode);
                startcreatpin.putExtra(VariableClass.Vari.COUNTRYCODE, countrycode);

                startcreatpin.putExtra(VariableClass.Vari.USERID, usernumber);
                ctx.startActivity(startcreatpin);
                overridePendingTransition(R.anim.animation1, R.anim.animation2);
                finish();

            }


            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            Log.e("Verify Code with Sign up", "Verify Code with Sign up");
            pdialog.show();
            resend_code_call.setEnabled(false);
            resend_code_sms.setEnabled(false);
            verify_code.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject parent, child;
            response = Apis.getApisInstance(ctx).verifyCodeSignup(usernumber, countrycode, Prefs.getResellerID(ctx), confirmCode, "" + carrieType, tempid, Prefs.getUserTariff(ctx));
            if (!response.equalsIgnoreCase("")) {
                try {
                    parent = new JSONObject(response);
                    //if response of failed show message
                    if (parent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserror = true;
                        child = parent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = child.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }
                    //if response of success send to create pin
                    if (parent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {

                        askpin = true;
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


    class VerifyCodeForgot extends AsyncTask<Void, Void, Void> {

        String response;
        Boolean askpin = false;
        Boolean iserror = false;
        String verifiedBy = "2";

        @Override
        protected void onPostExecute(Void result) {

            verify_code.setEnabled(true);
            resend_code_call.setEnabled(true);
            resend_code_sms.setEnabled(true);

            Log.e("ask pin value", "" + askpin);
            if (iserror) {
                showErrorMessage(true, response);
                pdialog.dismiss();
            } else if (askpin) {

                Intent startcreatpin = new Intent(ctx, CreatePasswordActivity.class);
                startcreatpin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startcreatpin.putExtra(VariableClass.Vari.VERIFI_CODE, confirmCode);
                startcreatpin.putExtra(VariableClass.Vari.COUNTRYCODE, countrycode);

                startcreatpin.putExtra(VariableClass.Vari.USERID, usernumber);
                ctx.startActivity(startcreatpin);

                overridePendingTransition(R.anim.animation1, R.anim.animation2);
                finish();
            }


            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            Log.e("Verify Code forgot", "Verify Code forgot");
            pdialog.show();
            verify_code.setEnabled(false);
            resend_code_call.setEnabled(false);
            resend_code_sms.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject parent, child;
            response = Apis.getApisInstance(ctx).verifyCode(usernumber, countrycode, Prefs.getResellerID(ctx), confirmCode, "" + carrieType);
            if (!response.equalsIgnoreCase("")) {
                try {
                    parent = new JSONObject(response);
                    //if response of failed show message
                    if (parent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserror = true;
                        child = parent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = child.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }
                    //if response of success send to create pin
                    if (parent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {

                        askpin = true;
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

    public class ResendCodeSignup extends AsyncTask<Void, Void, Void> {
        String response = "";
        Boolean iserror = false;

        @Override
        protected void onPostExecute(Void result) {
            verify_code.setEnabled(true);
            resend_code_call.setEnabled(true);
            resend_code_sms.setEnabled(true);

            if (iserror) {
                pdialog.dismiss();
                showErrorMessage(true, response);
            } else {
                CommonUtility.showCustomAlert(VerificationCodeActivity.this, getString(R.string.resend_string));
            }
            verify_code.setEnabled(true);
            resend_code_call.setEnabled(true);
            resend_code_sms.setEnabled(true);
            pdialog.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            pdialog.show();
            verify_code.setEnabled(false);
            resend_code_call.setEnabled(false);
            resend_code_sms.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (tempid != null && !tempid.equals(""))
                response = Apis.getApisInstance(ctx).signupWithNumber(usernumber, countrycode, Prefs.getResellerID(ctx), tempid, "" + carrieType);
            else
                response = Apis.getApisInstance(ctx).signupWithNumber(usernumber, countrycode, Prefs.getResellerID(ctx), "", "" + carrieType);

            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;
                try {
                    joparent = new JSONObject(response);
                    //failed response
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
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


    public class ResendCodeForgot extends AsyncTask<Void, Void, Void> {
        String response = "";
        Boolean iserror = false;

        @Override
        protected void onPostExecute(Void result) {
            verify_code.setEnabled(true);
            resend_code_call.setEnabled(true);
            resend_code_sms.setEnabled(true);
            pdialog.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            verify_code.setEnabled(false);
            resend_code_call.setEnabled(false);
            resend_code_sms.setEnabled(false);
            pdialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(ctx).forgotPassword(countrycode + usernumber, new String[]{Prefs.getUserCountryCode(ctx)}, "" + carrieType, Prefs.getResellerID(ctx));
            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;
                try {
                    joparent = new JSONObject(response);

                    //failed response
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserror = true;
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }
                    //success response i.e. user is valid
                    else if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORCODE);
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
