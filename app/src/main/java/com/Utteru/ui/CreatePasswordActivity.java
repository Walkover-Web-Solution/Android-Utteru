package com.Utteru.ui;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.Utteru.commonUtilities.Constants;
import com.Utteru.commonUtilities.CustomKeyboardOther;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.GcmRegistrationTask;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class CreatePasswordActivity extends AccountAuthenticatorActivity {
    public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";
    public static final String PARAM_USERNAME = "username";
    protected boolean mRequestNewAccount = false;
    String password;
    RelativeLayout error_layout;
    RelativeLayout root_layout;
    FontTextView error_FontTextView, label_FontTextView;
    Button close_button, create_pin;
    Context ctx = this;
    Bundle bundle1;
    String userId;
    String countryCode;
    String verifyCode;
    EditText create_pin_new_pin_code;
    EditText create_pin_confirm_pin_code;
    private AccountManager mAccountManager;
    private Boolean mConfirmCredentials = false;
    private String mUsername;
    CustomKeyboardOther keyboard;
    LinearLayout dialpad_layout;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animation3, R.anim.animation4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("oncreate pin", "on create pin");
        setContentView(R.layout.create_password_screen);
        init();
        Mint.initAndStartSession(CreatePasswordActivity.this, "395e969a");
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));


        mAccountManager = AccountManager.get(this);
        final Intent intent = getIntent();
        mUsername = intent.getStringExtra(PARAM_USERNAME);
        mRequestNewAccount = mUsername == null;
        mConfirmCredentials = intent.getBooleanExtra(PARAM_CONFIRM_CREDENTIALS, false);

    }


    public void onAuthenticationResult(Boolean success) {

        if (!mConfirmCredentials) {
            finishLogin(success);
        } else {
            finishConfirmCredentials(success);
        }
    }

    private void finishConfirmCredentials(boolean result) {

        final Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        mAccountManager.setPassword(account, password);
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, result);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void finishLogin(Boolean success) {


        final Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        if (mRequestNewAccount) {
            mAccountManager.addAccountExplicitly(account, password, null);
            // Set contacts sync for this account.
            ContentResolver.requestSync(account, ContactsContract.AUTHORITY, new Bundle());
        }
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUsername);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onResume() {
        close_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
            }
        });


        create_pin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    password = create_pin_new_pin_code.getText().toString();

                    String confirm_password = create_pin_confirm_pin_code.getText().toString();

                    if (!(password.equals("")) && password.length() == 4) {
                        if (!(confirm_password.equals("")) && password.length() == 4) {
                            if ((confirm_password.equals(password))) {
                                new CreatePin().execute(null, null, null);
                            } else {
                                showErrorMessage(true, getResources().getString(R.string.confirm_error));
                            }
                        } else {
                            showErrorMessage(true, getResources().getString(R.string.fill_all));
                        }
                    } else {
                        showErrorMessage(true, getResources().getString(R.string.fill_all));
                    }


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

      /*  root_layout.setOnTouchListener(new View.OnTouchListener() {
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
        create_pin_new_pin_code.setOnTouchListener(new View.OnTouchListener() {
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
        create_pin_confirm_pin_code.setOnTouchListener(new View.OnTouchListener() {
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

    @Override
    protected void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onDestroy();
    }

    void init() {
        root_layout = (RelativeLayout) findViewById(R.id.create_pin_root);

        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        error_FontTextView = (FontTextView) findViewById(R.id.error_text);
        close_button = (Button) findViewById(R.id.close_button);
        label_FontTextView = (FontTextView) findViewById(R.id.creat_pin_text);
        create_pin = (Button) findViewById(R.id.creat_pin_done);
        bundle1 = getIntent().getExtras();
        userId = bundle1.getString(VariableClass.Vari.USERID);
        countryCode = bundle1.getString(VariableClass.Vari.COUNTRYCODE);
        verifyCode = bundle1.getString(VariableClass.Vari.VERIFI_CODE);
        label_FontTextView.setText(label_FontTextView.getText().toString() + " +" + countryCode + userId);
        create_pin_new_pin_code = (EditText) findViewById(R.id.creat_pin_new_pin_code);


        create_pin_confirm_pin_code = (EditText) findViewById(R.id.creat_pin_confirm_pin_code);
        dialpad_layout = (LinearLayout) findViewById(R.id.dialpad_layout);
        keyboard = new CustomKeyboardOther(CreatePasswordActivity.this, R.id.keyboardview, R.xml.numberic_key_only, null);
        keyboard.registerEditText(create_pin_new_pin_code.getId(), null);
        keyboard.registerEditText(create_pin_confirm_pin_code.getId(), null);
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

    class CreatePin extends AsyncTask<Void, Void, Void> {
        String response;
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {

            CommonUtility.dialog.dismiss();
            if (iserr) {
                showErrorMessage(iserr, response);
            } else //Login
            {
                new Login().execute(null, null, null);
            }
            create_pin.setEnabled(true);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            CommonUtility.show_PDialog(CreatePasswordActivity.this, getResources().getString(R.string.please_wait));
            create_pin.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject joparent;
            response = Apis.getApisInstance(ctx).createPin(countryCode + userId, verifyCode, Prefs.getResellerID(ctx), password);
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

    class Login extends AsyncTask<Void, Void, Void> {
        String response = "";
        Boolean iserr = false;
        JSONObject parent, child;
        JSONArray jarry;

        @Override
        protected void onPreExecute() {
            create_pin.setEnabled(false);

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (ForgotPasswordActivity.is_forgot_pass)
                response = Apis.getApisInstance(ctx).signInApi(countryCode + userId, password, Prefs.getResellerID(ctx), SignInScreen.accessToken, "" + SignInScreen.accessType, new String[]{Prefs.getUserCountryCode(ctx)});
            else
                response = Apis.getApisInstance(ctx).signInApi(countryCode + userId, password, Prefs.getResellerID(ctx), "", "", new String[]{Prefs.getUserCountryCode(ctx)});


            if (!response.equalsIgnoreCase("")) {
                try {
                    parent = new JSONObject(response);
                    //if response of failed
                    if (parent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserr = true;
                        child = parent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = child.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }
                    //if response of success
                    if (parent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                        jarry = parent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                        child = jarry.getJSONObject(0);

                        String display_name = child.getString(VariableClass.ResponseVariables.USER_DISPLAY_NAME);
                        String reseller_id = child.getString(VariableClass.ResponseVariables.RESELLER_ID);
                        String user_id = child.getString(VariableClass.ResponseVariables.USERID);
                        String tariff_id = child.getString(VariableClass.ResponseVariables.TARRIFFID);
                        String userName = child.getString(VariableClass.ResponseVariables.USERNAME);
                        String userType = child.getString(VariableClass.ResponseVariables.USER_TYPE);
                        String sipPassword=child.getString(VariableClass.ResponseVariables.USER_PASSWORD);

                        int listen_voice = child.getInt(VariableClass.ResponseVariables.LISTENVOICE);
//                        if (reseller_id.equals("2")) {
                            Prefs.setResellerID(ctx, reseller_id);
                            Prefs.setUserDisplay(ctx, display_name);
                            Prefs.setUserTarrif(ctx, tariff_id);
                            Prefs.setUserName(ctx, userName);
                            Prefs.setUserSipName(ctx,userName);
                            Prefs.setUserSipPassword(ctx,sipPassword);
                            Prefs.setUserPassword(ctx, password);
                            Prefs.setUserId(ctx, user_id);
                            Prefs.setListenVoice(ctx, listen_voice);
                            Prefs.setUserType(ctx, userType);
//                        } else {
//                            iserr = true;
//                            response = getResources().getString(R.string.reseller_msg);
//                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    iserr = true;
                    response = getResources().getString(R.string.parse_error);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (iserr) {
                showErrorMessage(true, response);
            } else {
                //Login
                if (CommonUtility.isNetworkAvailable(CreatePasswordActivity.this)) {

                    new IntialiseData(ctx).initVerifiedData();
                    new IntialiseData(ctx).initAccessData();

                    if (Prefs.getGCMID(ctx).equals("") || !Prefs.getGCMIdState(ctx)) {
                        Log.e("registering at gcm", "registering at gcm");
                        if (CommonUtility.checkPlayServices(CreatePasswordActivity.this))
                            new GcmRegistrationTask(CreatePasswordActivity.this, 1, Prefs.getUserActualName(ctx)).execute();

                    }
                }
                onAuthenticationResult(true);
                Intent startmenu = new Intent(ctx, MenuScreen.class);
                startmenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(startmenu);
                CommonUtility.showCustomAlert(CreatePasswordActivity.this, getString(R.string.success_message)).show();
                overridePendingTransition(R.anim.animation1, R.anim.animation2);
                CommonUtility.dialog.dismiss();


            }

            create_pin.setEnabled(true);

            super.onPostExecute(result);
        }
    }


}
