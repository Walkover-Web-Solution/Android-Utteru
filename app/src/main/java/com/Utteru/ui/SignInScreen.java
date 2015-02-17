package com.Utteru.ui;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Base64;
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
import android.widget.Toast;

import com.Utteru.R;
import com.Utteru.com.facebook.android.DialogError;
import com.Utteru.com.facebook.android.Facebook;
import com.Utteru.com.facebook.android.Facebook.DialogListener;
import com.Utteru.com.facebook.android.FacebookError;
import com.Utteru.com.facebook.android.SessionStore;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.Constants;
import com.Utteru.commonUtilities.CustomKeyboardOther;
import com.Utteru.commonUtilities.GcmRegistrationTask;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.Country;
import com.Utteru.dtos.MultipleVerifiedNumber;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class SignInScreen extends AccountAuthenticatorActivity {

    public static final int REQUEST_CODE = 01;
    public static final int RESULT_OK = 01;
    public static final int ACCESSTYPEFB = 1;
    public static final int ACCESSTYPEGOOGLE = 2;
    public static final String NEWSIGNUP = "2001";
    public static final String SIGNIN = "2002";
    public static final String NOTVALID = "6017";
    public static final String NOVERIFICATIONNUM = "6013";
    public static final String MESSAGENOTSENT = "6005";
    public static final String MESSAGESENT = "6004";
    public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";
    private static final String[] PERMISSIONS = new String[]{"email", "public_profile"};

    private static final String APP_ID = "764962106885610";
    private static final String TAG = "SignInScreen";
    static int accessType;
    static String accessToken;
    public boolean checkAccount = false;
    protected boolean mRequestNewAccount = false;
    RelativeLayout root_layout;
    TextView forgot_password;
    Button fb_button, google_button;
    Context ctx;
    TextView error_textview;
    Button error_close, login_button;
    RelativeLayout error_layout;
    String username, password = "";
    String countryCode = "";
    Bundle bundle;
    EditText username_ed, password_ed;
    CustomKeyboardOther keyboard;
    LinearLayout dialpad_layout;
    Tracker tracker;
    private AccountManager mAccountManager;
    private Boolean mConfirmCredentials = false;
    private Facebook mFacebook;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {


            if (msg.what == 1) {
                CommonUtility.showCustomAlertError(SignInScreen.this, "Facebook logout failed");

            } else {
                CommonUtility.showCustomAlertError(SignInScreen.this, "Disconnected from facebook");

            }
        }
    };

    public void setPassword() {

        mAccountManager.setPassword(new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE), Prefs.getUserPassword(ctx));
    }

    @Override
    public void onBackPressed() {

        if (dialpad_layout.getVisibility() == View.VISIBLE) {
            showKeyBoard(false);
            Log.e("hiding  keyboard", "hiding keyboard");

        } else {
            super.onBackPressed();

            Intent startsign_in = new Intent(this, SignUpHome.class);
            startActivity(startsign_in);
            overridePendingTransition(R.anim.animation3, R.anim.animation4);
        }
    }

    @Override
    protected void onStart() {
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        // Set screen name.
        tracker.setScreenName("SignIn Screen Android");
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
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        tracker = MyAnalyticalTracker.getTrackerInstance().getTracker(MyAnalyticalTracker.TrackerName.APP_TRACKER, this);
        setContentView(R.layout.sign_in_layout);

        init();
        Mint.initAndStartSession(SignInScreen.this, "395e969a");
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));

        mAccountManager = AccountManager.get(this);
        Account[] accArray = mAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        checkAccount = accArray.length > 0;
        if (!Prefs.getUserID(this).equals("") && !Prefs.getUserPassword(this).equals("") && checkAccount) {
            CommonUtility.showCustomAlertError(this, "Can not create more than one account");

            this.finish();
        }
        final Intent intent = getIntent();
        username = intent.getStringExtra(PARAM_USERNAME);
        mRequestNewAccount = username == null;
        mConfirmCredentials = intent.getBooleanExtra(PARAM_CONFIRM_CREDENTIALS, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("activity result", "activity result ");
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data.hasExtra(HelloActivity.TOKEN)) {
                accessToken = data.getExtras().getString(HelloActivity.TOKEN);

                new LoginGoogleFb().execute(null, null, null);

            } else if (data.hasExtra(HelloActivity.ERROR_MESSAGE)) {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {

        username_ed.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                    password_ed.requestFocus();
                }
                return false;
            }
        });


        password_ed.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                    login_button.performClick();
                }
                return false;
            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    username = username_ed.getText().toString();
                    if (username != null && !username.equals(""))
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

        fb_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    accessType = ACCESSTYPEFB;
                    onFacebookClick();
                } else
                    showErrorMessage(true, getResources().getString(R.string.internet_error));
            }
        });

        google_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    accessType = ACCESSTYPEGOOGLE;
                    Intent i = new Intent(ctx, HelloActivity.class);
                    startActivityForResult(i, REQUEST_CODE);
                    overridePendingTransition(R.anim.animation1, R.anim.animation2);
                } else
                    showErrorMessage(true, getResources().getString(R.string.internet_error));
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    username = username_ed.getText().toString();
                    password = password_ed.getText().toString();

                 /*   if (!username.equals("") && username.startsWith("call_")) {
                        Prefs.setUserSipName(ctx, username);
                        Prefs.setUserSipPassword(ctx, password);
                        Intent call = new Intent(ctx, DialerActivity.class);
                        call.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        ctx.startActivity(call);

                        SignInScreen.this.finish();
                        overridePendingTransition(R.anim.animation1, R.anim.animation2);


                    } else */
                    if (username.equals("")) {
                        showErrorMessage(true, getResources().getString(R.string.login_text));
                    } else if (password.equals("")) {
                        showErrorMessage(true, getResources().getString(R.string.login_text));
                    } else {
                        new Login().execute(null, null, null);
                    }
                } else {
                    showErrorMessage(true, getResources().getString(R.string.internet_error));
                }
            }


        });
        error_close.setOnClickListener(new View.OnClickListener() {

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
        root_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
                return false;
            }
        });
        username_ed.setOnTouchListener(new View.OnTouchListener() {
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
        password_ed.setOnTouchListener(new View.OnTouchListener() {
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
        root_layout.setOnClickListener(new View.OnClickListener() {
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
        ctx = this;
        root_layout = (RelativeLayout) findViewById(R.id.sing_in_root);
        SignUpFragement.isnewSignup = false;
        AskNumber.isnewSignup = false;
        forgot_password = (TextView) findViewById(R.id.sign_in_forgot_password);
        forgot_password.setPaintFlags(forgot_password.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mFacebook = new Facebook(APP_ID);
        fb_button = (Button) findViewById(R.id.facebook_button);
        google_button = (Button) findViewById(R.id.google_button);
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        error_close = (Button) findViewById(R.id.close_button);
        error_textview = (TextView) findViewById(R.id.error_text);
        dialpad_layout = (LinearLayout) findViewById(R.id.dialpad_layout);
        bundle = getIntent().getExtras();
        username_ed = (EditText) findViewById(R.id.sign_in_username);
        password_ed = (EditText) findViewById(R.id.sign_in_password);
        keyboard = new CustomKeyboardOther(SignInScreen.this, R.id.keyboardview, R.xml.numberic_keypad_other, null);
        keyboard.registerEditText(username_ed.getId(), null);
        keyboard.registerEditText(password_ed.getId(), null);
        login_button = (Button) findViewById(R.id.sign_in_button);
        if (bundle != null && bundle.containsKey(VariableClass.Vari.USERID)) {
            username = bundle.getString(VariableClass.Vari.USERID);
            countryCode = bundle.getString(VariableClass.Vari.COUNTRYCODE);
            username_ed.setText(countryCode + username);
            username_ed.setEnabled(false);
            CommonUtility.showCustomAlertError(this, getString(R.string.enter_password));
        }
        generateFBKeyHash();
        SearchListActivity.country_list = new ArrayList<Country>();
        SearchListActivity.country_list = new CsvReader().readCsv(ctx, new CsvReader().getUserCountryIso(ctx), false);
    }

    public void onNumbers(View v) {
        password_ed.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    public void onLetters(View v) {
        password_ed.setInputType(InputType.TYPE_CLASS_TEXT);
    }

    private void onFacebookClick() {
        if (mFacebook.isSessionValid()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

            builder.setMessage("Delete current Facebook connection?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            fbLogout();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                        }
                    });

            final AlertDialog alert = builder.create();

            alert.show();
        } else {

            mFacebook.authorize(SignInScreen.this, PERMISSIONS, -1, new FbLoginDialogListener());
        }
    }

    private void fbLogout() {


        new Thread() {
            @Override
            public void run() {
                SessionStore.clear(ctx);
                int what = 1;
                try {
                    mFacebook.logout(ctx);
                    what = 0;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                mHandler.sendMessage(mHandler.obtainMessage(what));
            }
        }.start();
    }

    public void generateFBKeyHash() {
        try {
            PackageInfo info = ctx.getPackageManager().getPackageInfo("com.mobulous.desicomicsreader", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
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
        if (!mConfirmCredentials) {
            finishLogin(success);
        } else {
            finishConfirmCredentials(success);
        }
    }

    private void finishConfirmCredentials(boolean result) {
        Log.i(TAG, "finishConfirmCredentials()");
        final Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        mAccountManager.setPassword(account, password);
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, result);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onDestroy();
    }

    private void finishLogin(Boolean success) {

        Log.i(TAG, "finishLogin()");
        final Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        if (mRequestNewAccount) {
            mAccountManager.addAccountExplicitly(account, password, null);
            // Set contacts sync for this account.
        }
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, Constants.ACCOUNT_NAME);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
    }

    private final class FbLoginDialogListener implements DialogListener {
        public void onComplete(Bundle values) {
            SessionStore.save(mFacebook, ctx);
            accessToken = mFacebook.getAccessToken();
            Log.e("facebook token", "" + accessToken);
            //check validation
            new LoginGoogleFb().execute(null, null, null);
        }

        public void onFacebookError(FacebookError error) {
            Toast.makeText(ctx, "Facebook connection failed", Toast.LENGTH_SHORT).show();
            Log.e("token inside listener", "facebook error");
        }

        public void onError(DialogError error) {
            Toast.makeText(getApplicationContext(), "Facebook connection failed", Toast.LENGTH_LONG).show();

        }

        public void onCancel() {
        }

    }

    class Login extends AsyncTask<Void, Void, Void> {

        String response = "";
        Boolean iserr = false;
        JSONObject parent, child;
        JSONArray jarry;

        @Override
        protected void onPreExecute() {


            CommonUtility.show_PDialog(SignInScreen.this, getResources().getString(R.string.please_wait));
            login_button.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(ctx).signInApi(CommonUtility.validateNumberForApi(username), password, Prefs.getResellerID(ctx), "", "", new String[]{Prefs.getUserCountryCode(ctx)});
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
                        int listen_voice = child.getInt(VariableClass.ResponseVariables.LISTENVOICE);
                        String sipPassword = child.getString(VariableClass.ResponseVariables.USER_PASSWORD);
//                        if (reseller_id.equals("2")) {
                        Prefs.setResellerID(ctx, reseller_id);
                        Prefs.setUserDisplay(ctx, display_name);
                        Prefs.setUserName(ctx, userName);
                        Prefs.setUserSipName(ctx, userName);
                        Prefs.setUserSipPassword(ctx, sipPassword);
                        Prefs.setUserPassword(ctx, password);
                        Prefs.setUserTarrif(ctx, tariff_id);
                        Prefs.setUserId(ctx, user_id);
                        Prefs.setListenVoice(ctx, listen_voice);
                        Prefs.setUserType(ctx, userType);
                        onAuthenticationResult(true);
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
            } else {
                iserr = true;
                response = getString(R.string.server_error);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (iserr) {
                showErrorMessage(true, response);
            } else {
                if (CommonUtility.isNetworkAvailable(SignInScreen.this)) {


                    new IntialiseData(ctx).initVerifiedData();
                    new IntialiseData(ctx).initAccessData();


                    if (Prefs.getGCMID(ctx).equals("") || !Prefs.getGCMIdState(ctx)) {
                        Log.e("registering at gcm", "registering at gcm");
                        if (CommonUtility.checkPlayServices(SignInScreen.this))
                            new GcmRegistrationTask(SignInScreen.this, 1, Prefs.getUserActualName(ctx)).execute();

                    }
                }


                Intent startmenu = new Intent(ctx, MenuScreen.class);
                startmenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(startmenu);
                overridePendingTransition(R.anim.animation1, R.anim.animation2);

            }

            login_button.setEnabled(true);
            CommonUtility.dialog.dismiss();

            super.onPostExecute(result);
        }
    }

    class LoginGoogleFb extends AsyncTask<Void, Void, Void> {

        String response = "";
        Boolean iserr = false;
        JSONObject parent, child;
        JSONArray jarray;
        String tempId;
        Boolean isSignup = false;
        Boolean isSignIn = false;


        @Override
        protected void onPreExecute() {
            CommonUtility.show_PDialog(SignInScreen.this, getResources().getString(R.string.please_wait));

            google_button.setEnabled(true);
            fb_button.setEnabled(true);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(ctx).signupWithGoogleFacebook(accessToken, "" + accessType, Prefs.getResellerID(ctx));
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
                    else if (parent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                        Log.e("success response", "success respo");
                        child = parent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        //new signup
                        jarray = parent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                        if (child.getString(VariableClass.ResponseVariables.ERRORCODE).equals(NEWSIGNUP)) {
                            isSignup = true;
                            child = jarray.getJSONObject(0);
                            tempId = child.getString(VariableClass.ResponseVariables.TEMPID);
                        }
                        //signin
                        else if (child.getString(VariableClass.ResponseVariables.ERRORCODE).equals(SIGNIN)) {
                            isSignIn = true;

                            child = jarray.getJSONObject(0);
                            username = child.getString(VariableClass.ResponseVariables.USERNAME);
                            String id = child.getString(VariableClass.ResponseVariables.USERID);
                            String password = child.getString(VariableClass.ResponseVariables.PASSWORD);
                            String resellerId = child.getString(VariableClass.ResponseVariables.RESELLER_ID);
                            String userName = child.getString(VariableClass.ResponseVariables.USERNAME);
                            String userType = child.getString(VariableClass.ResponseVariables.USER_TYPE);
                            int listen_voice = child.getInt(VariableClass.ResponseVariables.LISTENVOICE);
                            String sipPassword = child.getString(VariableClass.ResponseVariables.USER_PASSWORD);
//                            if (resellerId.equals("2")) {
                            Prefs.setUserName(ctx, userName);
                            Prefs.setUserPassword(ctx, password);
                            Prefs.setResellerID(ctx, resellerId);
                            Prefs.setUserId(ctx, id);
                            Prefs.setListenVoice(ctx, listen_voice);
                            Prefs.setUserType(ctx, userType);
                            Prefs.setUserSipName(ctx, userName);
                            Prefs.setUserSipPassword(ctx, sipPassword);
                            onAuthenticationResult(true);
//                            } else {
//                                iserr = true;
//                                response = getResources().getString(R.string.reseller_msg);
//                            }

                        }
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
                if (isSignup) {
                    Log.e("new signup", "new signup");
                    //ask number with tempId
                    Intent getNumber = new Intent(ctx, AskNumber.class);
                    getNumber.putExtra(VariableClass.Vari.TEMP_ID, tempId);
                    getNumber.putExtra(VariableClass.Vari.TOKEN, accessToken);
                    getNumber.putExtra(VariableClass.Vari.ACCESS_TYPE, "" + accessType);

                    startActivity(getNumber);
                } else if (isSignIn) {
                    if (CommonUtility.isNetworkAvailable(SignInScreen.this)) {

                        new IntialiseData(ctx).initVerifiedData();

                        if (Prefs.getGCMID(ctx).equals("") || !Prefs.getGCMIdState(ctx)) {
                            Log.e("registering at gcm", "registering at gcm");
                            if (CommonUtility.checkPlayServices(SignInScreen.this))
                                new GcmRegistrationTask(SignInScreen.this, 1, Prefs.getUserActualName(ctx)).execute();

                        }
                    }

                    Intent startmenu = new Intent(ctx, MenuScreen.class);
                    startmenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(startmenu);
                    overridePendingTransition(R.anim.animation1, R.anim.animation2);


                }

            }
            CommonUtility.dialog.dismiss();


            google_button.setEnabled(true);
            fb_button.setEnabled(true);
            super.onPostExecute(result);
        }
    }

    public class ValidateForgotPassword extends AsyncTask<Void, Void, Void> {
        String response = "";
        Boolean isValid = false;
        Boolean hasNumber = false;
        Boolean hasmsgsent = false;
        Boolean iserror = false;
        String tempNumber;

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

                        insertverficationCode.putExtra(VariableClass.Vari.USERID, username);
                        insertverficationCode.putExtra(VariableClass.Vari.COUNTRYCODE, countryCode);
                        insertverficationCode.putExtra(VariableClass.Vari.SOURCECLASS, "1");
                        if (CommonUtility.c_list.size() > 1)
                            insertverficationCode.putExtra(VariableClass.Vari.SHOWOTHERNUMBER, "");

                        startActivity(insertverficationCode);
                    } else if (response.equals(MESSAGENOTSENT)) {
                        //ask numbers
                        Intent select_number = new Intent(ctx, SelectNumber.class);
                        select_number.putExtra(VariableClass.Vari.USERID, username);
                        select_number.putExtra(VariableClass.Vari.COUNTRYCODE, countryCode);
                        select_number.putExtra(VariableClass.Vari.SHOWOTHERNUMBER, "");
                        startActivity(select_number);
                    }
                } else
                    showErrorMessage(true, "Has no verified number");
            } else {
                //forgot password screen
                Intent startforgotpass = new Intent(ctx, ForgotPasswordActivity.class);
                startforgotpass.putExtra(VariableClass.Vari.USERID, username);
                startforgotpass.putExtra(VariableClass.Vari.COUNTRYCODE, countryCode);
                ctx.startActivity(startforgotpass);
            }

            forgot_password.setEnabled(true);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            CommonUtility.show_PDialog(SignInScreen.this, getResources().getString(R.string.please_wait));
            forgot_password.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(ctx).forgotPassword(CommonUtility.validateNumberForApi(username), new String[]{Prefs.getUserCountryCode(ctx)}, "2", "2");

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
                                String tempnum = CommonUtility.validateNumberForApi(username);
                                for (int i = 0; i < jarray.length(); i++) {
                                    mdtos = new MultipleVerifiedNumber();
                                    jochild = jarray.getJSONObject(i);
                                    mdtos.setCountry_code(jochild.getString(VariableClass.ResponseVariables.COUNTRY_CODE));
                                    mdtos.setNumber(jochild.getString(VariableClass.ResponseVariables.VERIFIED_NUMBER));


                                    if (mdtos.getNumber().equals(tempnum))
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
