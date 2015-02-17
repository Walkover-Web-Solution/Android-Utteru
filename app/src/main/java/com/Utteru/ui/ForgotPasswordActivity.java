package com.Utteru.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.CustomKeyboardOther;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.MultipleVerifiedNumber;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ForgotPasswordActivity extends BaseActivity {
    public static final String NOTVALID = "6017";
    public static final String NOVERIFICATIONNUM = "6013";
    public static final String MESSAGENOTSENT = "6005";
    public static final String MESSAGESENT = "6004";
    static Boolean is_forgot_pass = false;
    Button verify_button;
    String userId, countryCode, tempId;
    Context ctx = this;
    TextView error_textview;
    Button error_close;
    RelativeLayout error_layout, Root_forgotpw;
    EditText user_detail_ed;
    CustomKeyboardOther keyboard;
    LinearLayout dialpad_layout;

    @Override
    protected void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle arg0) {

        setContentView(R.layout.forgot_password_layout);

        init();
        super.onCreate(arg0);
    }

    @Override
    protected void onResume() {

        is_forgot_pass = true;

        error_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showErrorMessage(false, "");
            }
        });
        error_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showErrorMessage(false, "");
            }
        });
        user_detail_ed.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                    verify_button.performClick();
                }
                return false;
            }
        });
        verify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showErrorMessage(false, "");
                if (CommonUtility.isNetworkAvailable(ctx)) {

                    userId = user_detail_ed.getText().toString();

                    if (!userId.equals("")) {
//                        if (!CommonUtility.isNumeric(userId)) {
                        new ValidateForgotPassword().execute(null, null, null);
//                        }
//                        else{
//                            if(userId.length()>8&&userId.length()<18) {
//                                if (userId.startsWith("+") || userId.startsWith("00")) {
//                                    if(userId.startsWith("00"))
//                                        userId=userId.replace("00","");
//                                    userId=userId.replace("[+]","");
//                                    new ValidateForgotPassword().execute(null, null, null);
//
//                                } else
//                                    showErrorMessage(true, getString(R.string.number_validation));
//                            }
//                            else
//                                showErrorMessage(true, getString(R.string.number_not_valid));
//                        }
                    } else
                        showErrorMessage(true, getResources().getString(R.string.fill_all));
                } else
                    showErrorMessage(true, getResources().getString(R.string.internet_error));
            }
        });
        user_detail_ed.setOnTouchListener(new View.OnTouchListener() {
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
        Root_forgotpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyBoard(false);
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
        Mint.initAndStartSession(ForgotPasswordActivity.this, "395e969a");
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));
        verify_button = (Button) findViewById(R.id.forgotpass_button);
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        error_close = (Button) findViewById(R.id.close_button);
        Root_forgotpw = (RelativeLayout) findViewById(R.id.root_forgotpw);
        error_textview = (TextView) findViewById(R.id.error_text);
        dialpad_layout = (LinearLayout) findViewById(R.id.dialpad_layout);
        user_detail_ed = (EditText) findViewById(R.id.forgotpass_details);
        keyboard = new CustomKeyboardOther(ForgotPasswordActivity.this, R.id.keyboardview, R.xml.numberic_keypad_other, null);
        keyboard.registerEditText(user_detail_ed.getId(), null);
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

    @Override
    public void onBackPressed() {
        if (dialpad_layout.getVisibility() == View.VISIBLE) {
            showKeyBoard(false);
            Log.e("hiding  keyboard", "hiding keyboard");

        }
        else{
        super.onBackPressed();
        overridePendingTransition(R.anim.animation3, R.anim.animation4);}
    }

    public class ValidateForgotPassword extends AsyncTask<Void, Void, Void> {
        String response = "";
        String tempId;
        Boolean isValid = false;
        Boolean hasNumber = false;
        Boolean hasmsgsent = false;
        Boolean iserror = false;

        @Override
        protected void onPostExecute(Void result) {
            CommonUtility.dialog.dismiss();
            Log.e("is error in forgot password", "" + iserror);
            if (iserror) {
                showErrorMessage(true, response);
            } else if (isValid) {
                if (hasNumber) {
                    hasNumber = true;
                    if (hasmsgsent) {
                        //show verification code screen
                        Intent insertverficationCode = new Intent(ctx, VerificationCodeActivity.class);
                        insertverficationCode.putExtra(VariableClass.Vari.USERID, userId);
                        insertverficationCode.putExtra(VariableClass.Vari.COUNTRYCODE, countryCode);
                        Log.e("country code in forgot password", "" + countryCode);
                        insertverficationCode.putExtra(VariableClass.Vari.SOURCECLASS, "1");
                        if (CommonUtility.c_list.size() > 1)
                            insertverficationCode.putExtra(VariableClass.Vari.SHOWOTHERNUMBER, "");
                        startActivity(insertverficationCode);
                        overridePendingTransition(R.anim.animation1, R.anim.animation2);
                    } else if (response.equals(MESSAGENOTSENT)) {
                        //ask numbers

                        Intent select_number = new Intent(ctx, SelectNumber.class);
                        select_number.putExtra(VariableClass.Vari.SHOWOTHERNUMBER, "");
                        select_number.putExtra(VariableClass.Vari.USERID, userId);
                        select_number.putExtra(VariableClass.Vari.COUNTRYCODE, countryCode);
                        startActivity(select_number);
                        overridePendingTransition(R.anim.animation1, R.anim.animation2);
                    }
                } else
                    showErrorMessage(true, "Has no verified number");
            } else {
                //forgot password screen
                showErrorMessage(true, "user not valid");
            }

            verify_button.setEnabled(true);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            CommonUtility.show_PDialog(ForgotPasswordActivity.this, getResources().getString(R.string.please_wait));
            verify_button.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            response = Apis.getApisInstance(ctx).forgotPassword(CommonUtility.validateNumberForApi(userId), new String[]{Prefs.getUserCountryCode(ctx)}, "2", "2");
            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;

                JSONArray jarray;
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
                        Log.e("forgot password", "" + response);
                        if (!response.equals(NOTVALID)) {
                            isValid = true;
                            if (!response.equals(NOVERIFICATIONNUM)) {
                                hasNumber = true;
                                if (response.equals(MESSAGESENT)) {
                                    hasmsgsent = true;

                                }
                                jarray = joparent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                                MultipleVerifiedNumber mdtos;
                                CommonUtility.c_list = new ArrayList<MultipleVerifiedNumber>();

                                String temp_num = CommonUtility.validateNumberForApi(userId);
                                for (int i = 0; i < jarray.length(); i++) {

                                    mdtos = new MultipleVerifiedNumber();
                                    jochild = jarray.getJSONObject(i);

                                    mdtos.setCountry_code(jochild.getString(VariableClass.ResponseVariables.COUNTRY_CODE));
                                    mdtos.setNumber(jochild.getString(VariableClass.ResponseVariables.VERIFIED_NUMBER));

                                    if (mdtos.getNumber().equals(temp_num))
                                        countryCode = mdtos.getCountry_code();

                                    CommonUtility.c_list.add(mdtos);

                                }
                            }
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
}
