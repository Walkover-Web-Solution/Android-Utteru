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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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
import com.Utteru.commonUtilities.Constants;
import com.Utteru.commonUtilities.CustomKeyboardOther;
import com.Utteru.commonUtilities.GcmRegistrationTask;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.Country;
import com.Utteru.dtos.MultipleVerifiedNumber;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AskNumber extends AccountAuthenticatorActivity {

    public static final int REQUEST_CODE = 1;
    public static final int RESULT_OK = 1;
    public static final String NOTVALID = "6017";
    public static final String NOVERIFICATIONNUM = "6013";
    public static final String MESSAGENOTSENT = "6005";
    public static final String MESSAGESENT = "6004";
    public static final String NEWSIGNUPCODE = "8007";


    /**
     * The Intent flag to confirm credentials.
     */
    public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";
    /**
     * The Intent extra to store password.
     */
    public static final String PARAM_PASSWORD = "password";
    /**
     * The Intent extra to store username.
     */
    public static final String PARAM_USERNAME = "username";
    /**
     * The Intent extra to store username.
     */
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";
    protected static final String TOKEN = null;
    private static final String[] PERMISSIONS = new String[]{"email", "public_profile"};
    private static final String APP_ID = "764962106885610";
    public static boolean isnewSignup = false;
    static Boolean attach_email_case = false;
    protected boolean mRequestNewAccount = false;
    RelativeLayout error_layout;
    TextView error_textview;
    TextView forgot_password;
    LinearLayout root_layout;
    Button verify_button, close_em_button, country_code, login_btn;
    EditText user_number_ed, user_password_ed;
    String user_number_string;
    String country_code_string;
    Context ctx = this;
    String tempId;
    Bundle bundle;
    String userPassword;
    String token, accessType;
    LinearLayout verify_password_layout;
    Boolean isaskpassword = false;
    private AccountManager mAccountManager;
    CustomKeyboardOther keyboard;
    LinearLayout dialpad_layout;
    private Boolean mConfirmCredentials = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_number);
        init();
        Mint.initAndStartSession(AskNumber.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));

        mAccountManager = AccountManager.get(this);

        final Intent intent = getIntent();
        user_number_string = intent.getStringExtra(PARAM_USERNAME);
        mRequestNewAccount = user_number_string == null;
        mConfirmCredentials = intent.getBooleanExtra(PARAM_CONFIRM_CREDENTIALS, false);
    }

    @Override
    protected void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {


        forgot_password.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showErrorMessage(false, "");
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    user_number_string = user_number_ed.getText().toString();
                    if (user_number_string != null && !user_number_string.equals(""))
                        new ValidateForgotPassword().execute(null, null, null);
                    else {
                        //go to forgot password
                        Intent startforgotpass = new Intent(ctx, ForgotPasswordActivity.class);
                        ctx.startActivity(startforgotpass);
                        overridePendingTransition(R.anim.animation1, R.anim.animation2);
                    }
                } else
                    showErrorMessage(true, getResources().getString(R.string.internet_error));

            }
        });

        country_code.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(ctx, SearchListActivity.class);
                startActivityForResult(i, REQUEST_CODE);
                overridePendingTransition(R.anim.animation1, R.anim.animation2);
            }
        });

        user_number_ed.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                    verify_button.performClick();
                }
                return false;
            }
        });
        user_number_ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                verify_password_layout.setVisibility(View.GONE);
                user_password_ed.setText("");
                login_btn.setVisibility(View.GONE);
                verify_button.setVisibility(View.VISIBLE);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        user_password_ed.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                    login_btn.performClick();
                }
                return false;
            }
        });
        verify_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    user_number_string = user_number_ed.getText().toString().trim().replace("+", "");
                    if (!user_number_string.equals("")) {

                        new SignUpClass().execute(null, null, null);

                    }

                } else
                    showErrorMessage(true, getResources().getString(R.string.internet_error));

            }
        });

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
        login_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
                userPassword = user_password_ed.getText().toString().replace("+", "");

                if (!userPassword.equals("")) {
                    new Login().execute(null, null, null);
                } else
                    showErrorMessage(true, getResources().getString(R.string.invalid_userpassword));

            }
        });
        root_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
                return false;
            }
        });
        root_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyBoard(false);
            }
        });

        user_number_ed.setOnTouchListener(new View.OnTouchListener() {
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
        user_password_ed.setOnTouchListener(new View.OnTouchListener() {
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
        //getting currency from apis


        country_code = (Button) findViewById(R.id.verify_country_code);
        root_layout = (LinearLayout) findViewById(R.id.askpassword_root);

        verify_button = (Button) findViewById(R.id.verify_number_button);
        error_textview = (TextView) findViewById(R.id.error_text);
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        close_em_button = (Button) findViewById(R.id.close_button);
        user_number_ed = (EditText) findViewById(R.id.verify_number_text);
        user_password_ed = (EditText) findViewById(R.id.verify_password_text);
        forgot_password = (TextView) findViewById(R.id.verify_forgot_password);

        dialpad_layout = (LinearLayout) findViewById(R.id.dialpad_layout);
        keyboard = new CustomKeyboardOther(AskNumber.this, R.id.keyboardview, R.xml.numberic_keypad_other, null);
        keyboard.registerEditText(user_number_ed.getId(), null);
        keyboard.registerEditText(user_password_ed.getId(), null);
        //searchlist variable initailise  done to set country code in preference
        SearchListActivity.country_list = new ArrayList<Country>();
        SearchListActivity.country_list = new CsvReader().readCsv(ctx, new CsvReader().getUserCountryIso(ctx), true);


        country_code_string = Prefs.getUserCountryCode(ctx);
        country_code.setText("+" + country_code_string);

        bundle = getIntent().getExtras();
        tempId = bundle.getString(VariableClass.Vari.TEMP_ID);
        Log.e("tempId in askNumber", "" + tempId);

        token = bundle.getString(VariableClass.Vari.TOKEN);
        accessType = bundle.getString(VariableClass.Vari.ACCESS_TYPE);

        login_btn = (Button) findViewById(R.id.verify_number_login);
        login_btn.setVisibility(View.GONE);

        user_password_ed = (EditText) findViewById(R.id.verify_password_text);
        verify_password_layout = (LinearLayout) findViewById(R.id.verify_password_layout);
        verify_password_layout.setVisibility(View.GONE);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("activity result", "activity result ");
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            String countryCode = data.getExtras().getString(VariableClass.Vari.COUNTRYCODE);
            if (countryCode != null && !countryCode.equals("")) {
                Prefs.setUserCountryCode(ctx, countryCode);
                country_code.setText("+" + Prefs.getUserCountryCode(ctx));
                country_code_string = Prefs.getUserCountryCode(ctx);


            }


            String countryIso = data.getExtras().getString(VariableClass.Vari.COUNTRYISO);
            if (countryIso != null && !countryIso.equals("")) {

                if (CommonUtility.currency_list != null && CommonUtility.currency_list.size() != 0) {
                    String tarrifId = CommonUtility.currency_list.get(new CsvReader().getUserCurrecncy(countryIso.toUpperCase()));
                    if (tarrifId != null && !tarrifId.equals("")) {
                        Prefs.setUserTarrif(ctx, tarrifId);
                    }
                }
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
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

    public void onAuthenticationResult(Boolean success) {

        if (success) {
            if (!mConfirmCredentials) {
                finishLogin(success);
            } else {
                finishConfirmCredentials(success);
            }
        } else {

            if (mRequestNewAccount) {


                CommonUtility.showCustomAlertError(AskNumber.this,"Invalid Usernmae/Password");
            } else {
                CommonUtility.showCustomAlertError(AskNumber.this,"Invalid Password");
            }
        }
    }

    private void finishConfirmCredentials(boolean result) {
        final Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        mAccountManager.setPassword(account, userPassword);
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, result);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void finishLogin(Boolean success) {

        final Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        if (mRequestNewAccount) {
            mAccountManager.addAccountExplicitly(account, userPassword, null);
            // Set contacts sync for this account.
            ContentResolver.requestSync(account, ContactsContract.AUTHORITY, new Bundle());
        } else {
            mAccountManager.setPassword(account, userPassword);
        }
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, user_number_string);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        if (CommonUtility.isNetworkAvailable(ctx)) {

            Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            ContentResolver.requestSync(account, ContactsContract.AUTHORITY, bundle);

        } else {

            CommonUtility.showCustomAlertError(AskNumber.this,getString(R.string.internet_error));
        }
        finish();
    }

    @Override
    public void onBackPressed() {


        if (isaskpassword) {
            login_btn.setVisibility(View.GONE);
            verify_password_layout.setVisibility(View.GONE);
            verify_button.setVisibility(View.VISIBLE);
            verify_button.setEnabled(true);
            showErrorMessage(false, getResources().getString(R.string.already_verified));
            isaskpassword = false;
        } else {
            super.onBackPressed();
        }

    }

    public class SignUpClass extends AsyncTask<Void, Void, Void> {
        String response = "";
        String userId;
        Boolean isSignUp = false;
        Boolean iserror = false;
        String token;

        @Override
        protected void onPostExecute(Void result) {
            CommonUtility.dialog.dismiss();

            if (iserror) {
                showErrorMessage(true, response);
            } else if (isSignUp) {
                //ask code
                Intent startverificationcode = new Intent(ctx, VerificationCodeActivity.class);
                startverificationcode.putExtra(VariableClass.Vari.TEMP_ID, tempId);
                startverificationcode.putExtra(VariableClass.Vari.USERID, user_number_string);
                startverificationcode.putExtra(VariableClass.Vari.COUNTRYCODE, country_code_string);
                startActivity(startverificationcode);
                overridePendingTransition(R.anim.animation1, R.anim.animation2);
            } else {
                //ask password
                isaskpassword = true;
                attach_email_case = true;
                login_btn.setVisibility(View.VISIBLE);
                verify_password_layout.setVisibility(View.VISIBLE);
                verify_button.setVisibility(View.GONE);
                showErrorMessage(true, getResources().getString(R.string.already_verified));
            }


            verify_button.setEnabled(true);
            login_btn.setEnabled(true);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            CommonUtility.show_PDialog(AskNumber.this, getResources().getString(R.string.please_wait));
            verify_button.setEnabled(false);
            login_btn.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {


            response = Apis.getApisInstance(ctx).signupWithNumber(CommonUtility.validateNumberForApi(user_number_string), country_code_string, Prefs.getResellerID(ctx), tempId, "0");

            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;

                JSONArray jarray;
                try {
                    joparent = new JSONObject(response);

                    //failed response
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }
                    //success response
                    else if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                        //if new signup
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        if (jochild.getString(VariableClass.ResponseVariables.ERRORCODE).equals(NEWSIGNUPCODE)) {

                            isSignUp = true;
                            isnewSignup = true;
                            //fetch tempId
                            jarray = joparent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                            jochild = jarray.getJSONObject(0);
                            tempId = jochild.getString(VariableClass.ResponseVariables.TEMPID);
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

    class Login extends AsyncTask<Void, Void, Void> {

        String response = "";
        Boolean iserr = false;
        JSONObject parent, child;

        @Override
        protected void onPreExecute() {

            verify_button.setEnabled(false);
            login_btn.setEnabled(false);
            CommonUtility.show_PDialog(AskNumber.this, getResources().getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            response = Apis.getApisInstance(ctx).signInApi(country_code_string + CommonUtility.validateNumberForApi(user_number_string), userPassword, Prefs.getResellerID(ctx), token, accessType, new String[]{Prefs.getUserCountryCode(ctx)});

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
                        JSONArray jarray = parent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                        child = jarray.getJSONObject(0);
                        String display_name = child.getString(VariableClass.ResponseVariables.USER_DISPLAY_NAME);
                        String reseller_id = child.getString(VariableClass.ResponseVariables.RESELLER_ID);
                        String tariff_id = child.getString(VariableClass.ResponseVariables.TARRIFFID);
                        String userName = child.getString(VariableClass.ResponseVariables.USERNAME);
                        String userId = child.getString(VariableClass.ResponseVariables.USERID);
                        String userType = child.getString(VariableClass.ResponseVariables.USER_TYPE);
                        String sipPassword=child.getString(VariableClass.ResponseVariables.USER_PASSWORD);


                        int listen_voice = child.getInt(VariableClass.ResponseVariables.LISTENVOICE);
//                        if (reseller_id.equals("2")) {
                            Prefs.setResellerID(ctx, reseller_id);
                            Prefs.setUserDisplay(ctx, display_name);
                            Prefs.setUserTarrif(ctx, tariff_id);
                            Prefs.setListenVoice(ctx, listen_voice);
                            Prefs.setUserName(ctx, userName);
                            Prefs.setUserSipName(ctx,userName);
                            Prefs.setUserSipPassword(ctx,sipPassword);
                            Prefs.setUserPassword(ctx, userPassword);
                            Prefs.setUserId(ctx, userId);
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

                onAuthenticationResult(true);


                onAuthenticationResult(true);
                if (CommonUtility.isNetworkAvailable(AskNumber.this)) {

                    new IntialiseData(ctx).initVerifiedData();
                    new IntialiseData(ctx).initAccessData();

                    if (Prefs.getGCMID(ctx).equals("") || !Prefs.getGCMIdState(ctx)) {
                        Log.e("registering at gcm", "registering at gcm");
                        if (CommonUtility.checkPlayServices(AskNumber.this))
                            new GcmRegistrationTask(AskNumber.this, 1, Prefs.getUserActualName(ctx)).execute();

                    }
                }

                Intent startmenu = new Intent(ctx, MenuScreen.class);

                startmenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(startmenu);
                overridePendingTransition(R.anim.animation1, R.anim.animation2);

            }
            verify_button.setEnabled(true);
            login_btn.setEnabled(true);
            CommonUtility.dialog.dismiss();

            super.onPostExecute(result);
        }
    }

    public class ValidateForgotPassword extends AsyncTask<Void, Void, Void> {
        String response = "";
        String tempNUmber = null;
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
                        insertverficationCode.putExtra(VariableClass.Vari.USERID, user_number_string);
                        insertverficationCode.putExtra(VariableClass.Vari.COUNTRYCODE, country_code_string);
                        if (CommonUtility.c_list.size() > 1)
                            insertverficationCode.putExtra(VariableClass.Vari.SHOWOTHERNUMBER, "");
                        startActivity(insertverficationCode);
                        overridePendingTransition(R.anim.animation1, R.anim.animation2);
                    } else if (response.equals(MESSAGENOTSENT)) {
                        //ask numbers
                        Intent select_number = new Intent(ctx, SelectNumber.class);
                        select_number.putExtra(VariableClass.Vari.USERID, user_number_string);
                        select_number.putExtra(VariableClass.Vari.COUNTRYCODE, country_code_string);
                        select_number.putExtra(VariableClass.Vari.SHOWOTHERNUMBER, "");
                        startActivity(select_number);
                        overridePendingTransition(R.anim.animation1, R.anim.animation2);
                    }
                } else
                    showErrorMessage(true, "Has no verified number");
            } else {
                //forgot password screen
                Intent startforgotpass = new Intent(ctx, ForgotPasswordActivity.class);
                startforgotpass.putExtra(VariableClass.Vari.USERID, user_number_string);
                startforgotpass.putExtra(VariableClass.Vari.COUNTRYCODE, country_code_string);
                ctx.startActivity(startforgotpass);
                overridePendingTransition(R.anim.animation1, R.anim.animation2);
            }
            forgot_password.setEnabled(true);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            CommonUtility.show_PDialog(AskNumber.this, getResources().getString(R.string.please_wait));
            forgot_password.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            response = Apis.getApisInstance(ctx).forgotPassword(CommonUtility.validateNumberForApi(user_number_string), new String[]{Prefs.getUserCountryCode(ctx)}, "2", "2");

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

                                String temp_num = CommonUtility.validateNumberForApi(user_number_string);
                                for (int i = 0; i < jarray.length(); i++) {
                                    jochild = jarray.getJSONObject(i);
                                    mdtos = new MultipleVerifiedNumber();
                                    mdtos.setCountry_code(jochild.getString(VariableClass.ResponseVariables.COUNTRY_CODE));
                                    mdtos.setNumber(jochild.getString(VariableClass.ResponseVariables.VERIFIED_NUMBER));
                                    if (mdtos.getNumber().equals(temp_num))
                                        country_code_string = mdtos.getCountry_code();

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
