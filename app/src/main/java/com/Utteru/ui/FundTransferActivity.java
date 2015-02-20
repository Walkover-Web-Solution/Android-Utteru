package com.Utteru.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.CustomKeyboardOther;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FundTransferActivity extends Activity {

    FontTextView username_text, amount_text;
    EditText username_ed, amount_ed;
    Button send_fund_button;
    RelativeLayout error_layout;
    FontTextView error_FontTextView;
    Context ctx;
    Double ratio;
    String receiver_currency;
    FontTextView sender_currency;
    String receiver_name;
    FontTextView tittleback;
    String user_name;
    String amount;
    CustomKeyboardOther keyboard;
    LinearLayout dialpad_layout;
    Button close_button, confirm_transfer;
    FontTextView user_receive_detail_text;
    LinearLayout fund_layout, enter__password_layout;
    RelativeLayout transfer_details_layout;
    EditText user_pin_ed;
    String password;
    Boolean askpassword = false;
    Boolean isuservalid = false;
    FontTextView balance_text;
    ImageView backpress,gototohome;
    RelativeLayout Root_sharett;

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
        setContentView(R.layout.fund_transfer_layout);
        init();
        Mint.initAndStartSession(FundTransferActivity.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));

        resetView();
    }
    @Override
    protected void onPause() {
        super.onPause();
        Prefs.setLastActivity(ctx, getClass().getName());

    }
    @Override
    protected void onResume() {

        send_fund_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {



                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }return true;
            }
        });
        tittleback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });


        username_ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                isuservalid = false;
                transfer_details_layout.setVisibility(View.GONE);

                if (CommonUtility.isNumeric(s.toString())) {
                    if (s.toString().startsWith("+") || s.toString().startsWith("00"))
                        showErrorMessage(false, "");
                    else {
                        showErrorMessage(true, getResources().getString(R.string.number_validation));
                    }

                } else {

                    showErrorMessage(false, "");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        amount_ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (ratio != null && ratio != 0.0 && !(s.toString().equals(""))) {
                    transfer_details_layout.setVisibility(View.VISIBLE);

                    if (receiver_name == null || receiver_name.equals(""))
                        receiver_name = user_name;

//                    receiver_name_text.setText(receiver_name + " will receive :");
                    if (CommonUtility.isNumeric(s.toString())) {

                        if ((Double.parseDouble(s.toString()) < (Double.parseDouble(Prefs.getUserBalanceAmount(ctx))))) {
                            showErrorMessage(false, "");
                            double amount =CommonUtility.round(ratio * Double.parseDouble(s.toString()), 2);
                            String user_receive_text = "<p>"+user_name+" will receive: <font color=#3BB8FF> "+amount+"</font>"+""+receiver_currency+"<p>";
                            user_receive_detail_text.setText(Html.fromHtml(user_receive_text));


                        } else
                            showErrorMessage(true, "You have insufficient balance");
                    } else
                        showErrorMessage(true, getString(R.string.number_not_valid));

//                    receive_detail_currency.setText(receiver_currency);

                } else
                    transfer_details_layout.setVisibility(View.GONE);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        close_button.setOnClickListener(new View.OnClickListener() {
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


        username_ed.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                user_name = username_ed.getText().toString();
                if (!hasFocus) {


                    if (!user_name.equals("")) {
                        if (CommonUtility.isNumeric(user_name)) {
                            if ((user_name.startsWith("00") || user_name.startsWith("+"))) {

                                if (user_name.startsWith("00"))
                                    user_name = user_name.replaceFirst("00", "");
                                user_name = user_name.replaceAll("[+]", "");
                                if (user_name.length() > 8 && user_name.length() < 18) {
                                    if (CommonUtility.isNetworkAvailable(ctx))
                                        new checkUser().execute(null, null, null);
                                } else {
                                    showErrorMessage(true, getString(R.string.number_not_valid));
                                }
                            } else
                                showErrorMessage(true, getString(R.string.number_validation));

                        } else {
                            if (CommonUtility.isNetworkAvailable(ctx))
                                new checkUser().execute(null, null, null);
                        }
                    }
                }


                if (!CommonUtility.isNumeric(user_name) || user_name.equals("")) {
                    username_ed.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    username_ed.setInputType(InputType.TYPE_CLASS_NUMBER);
                }

            }
        });

        send_fund_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
                user_name = username_ed.getText().toString();
                amount = amount_ed.getText().toString();
                if (!user_name.equals("") && !amount.equals("")) {
                    if (isuservalid) {
                        if (CommonUtility.isNumeric(amount)) {

                            if (Double.parseDouble(amount) < Double.parseDouble(Prefs.getUserBalanceAmount(ctx))) {
                                askpassword = true;
                                fund_layout.setVisibility(View.GONE);
                                enter__password_layout.setVisibility(View.VISIBLE);
                                user_pin_ed.setText("");
                                amount_ed.clearFocus();
                                user_pin_ed.requestFocus();
                            } else {
                                showErrorMessage(true, getResources().getString(R.string.balance_validation));
                            }
                        } else
                            showErrorMessage(true, getResources().getString(R.string.balance_validation_decimal));
                    } else
                        showErrorMessage(true, getResources().getString(R.string.validUser_msg));


                } else {
                    showErrorMessage(true, getResources().getString(R.string.fill_all));

                }


            }
        });

        confirm_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
                password = user_pin_ed.getText().toString();
                if (!password.equals("")) {
                    if (password.equals(Prefs.getUserPassword(ctx))) {
                        new TransferFund().execute();
                    } else
                        new Login().execute(null, null, null);
                } else {
                    showErrorMessage(true, getResources().getString(R.string.fill_all));
                }
            }
        });
        Root_sharett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyBoard(false);
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
        user_pin_ed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    user_pin_ed.setInputType(InputType.TYPE_NULL);
                }
                showKeyBoard(true);
                return false;
            }
        });
        amount_ed.setOnTouchListener(new View.OnTouchListener() {
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
        send_fund_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showKeyBoard(false);
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
    void showErrorMessage(Boolean shown, String message) {
        if (shown) {
            error_FontTextView.setText(message);
            if (error_layout.getVisibility() == View.GONE)
                CommonUtility.expand(error_layout);
        } else {
            if (error_layout.getVisibility() == View.VISIBLE)
                CommonUtility.collapse(error_layout);
        }
    }


    void init() {

        ctx = this;
        Root_sharett= (RelativeLayout) findViewById(R.id.root_sharett);
        backpress = (ImageView)findViewById(R.id.auto_detect_country_back);
        tittleback = (FontTextView)findViewById(R.id.auto_detect_coutry_header);
        balance_text = (FontTextView) findViewById(R.id.transfer_fund_balance);
        balance_text.setText(Prefs.getUserBalance(ctx));
        sender_currency = (FontTextView) findViewById(R.id.ft_sender_currency);
        sender_currency.setText(Prefs.getUserCurrency(ctx));
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        error_FontTextView = (FontTextView) findViewById(R.id.error_text);
        close_button = (Button) findViewById(R.id.close_button);
        username_text = (FontTextView) findViewById(R.id.ft_enter_user_txt);
        username_ed = (EditText) findViewById(R.id.ft_enter_user_name);
        amount_text = (FontTextView) findViewById(R.id.ft_enter_fund_text);
        amount_ed = (EditText) findViewById(R.id.ft_enter_fund);
        dialpad_layout = (LinearLayout) findViewById(R.id.dialpad_layout);
        amount_ed.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 2)});
        keyboard = new CustomKeyboardOther(FundTransferActivity.this, R.id.keyboardview, R.xml.numberic_keypad_other, null);
        keyboard.registerEditText(username_ed.getId(), null);
        keyboard.registerEditText(amount_ed.getId(), null);
        ctx = getApplicationContext();
        send_fund_button = (Button) findViewById(R.id.ft_transfer_send_button);
        amount_ed.setInputType(InputType.TYPE_NULL);
        user_pin_ed.setInputType(InputType.TYPE_NULL);
        username_ed.setInputType(InputType.TYPE_NULL);
        user_receive_detail_text = (FontTextView) findViewById(R.id.ft_user_receive_text);

        user_pin_ed = (EditText) findViewById(R.id.ft_enter_user_password);
        confirm_transfer = (Button) findViewById(R.id.ft_transfer_fund_confirmation);

        keyboard.registerEditText(user_pin_ed.getId(), null);
        fund_layout = (LinearLayout) findViewById(R.id.ft_amount_layout);
        enter__password_layout = (LinearLayout) findViewById(R.id.ft_enter_password_layout);
        transfer_details_layout = (RelativeLayout) findViewById(R.id.ft_transfer_details_layout);

    }

    void resetView() {

        username_ed.setText("");
        amount_ed.setText("");

        fund_layout.setVisibility(View.VISIBLE);
        enter__password_layout.setVisibility(View.GONE);
        transfer_details_layout.setVisibility(View.GONE);


    }

    @Override
    public void onBackPressed() {
        if (dialpad_layout.getVisibility() == View.VISIBLE) {
            showKeyBoard(false);
            Log.e("hiding  keyboard", "hiding keyboard");

        }
        else if (askpassword) {
            askpassword = false;

            fund_layout.setVisibility(View.VISIBLE);
            enter__password_layout.setVisibility(View.GONE);


        }

        else {
            Intent menu =new Intent(FundTransferActivity.this,MenuScreen.class);
            menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(menu);
            this.finish();
            overridePendingTransition(R.anim.animation3, R.anim.animation4);
        }
    }

    public void setBalance() {
        balance_text.setText(Prefs.getUserBalance(ctx));
    }

    class TransferFund extends AsyncTask<Void, Void, Void> {

        String response;
        Boolean iserror = false;
        double bal = 0.0;

        @Override
        protected void onPostExecute(Void result) {
            confirm_transfer.setEnabled(true);
            CommonUtility.dialog.dismiss();

            if (iserror) {
                showErrorMessage(true, response);

            } else {

                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );

                CommonUtility.getUserBalanceFund(FundTransferActivity.this);

                CommonUtility.showCustomAlert(FundTransferActivity.this, getString(R.string.transferSuccess));
                resetView();
            }

            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            confirm_transfer.setEnabled(false);
            CommonUtility.show_PDialog(FundTransferActivity.this, getResources().getString(R.string.please_wait));


            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject joparent, jochild;

            response = Apis.getApisInstance(ctx).transferFund(user_name, amount);
            if (!response.equals("")) {
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
                }
            } else {
                iserror = true;
                response = getResources().getString(R.string.server_error);
            }
            return null;
        }


    }

    class checkUser extends AsyncTask<Void, Void, Void> {

        String response;
        Boolean iserror = false;

        @Override
        protected void onPostExecute(Void result) {
            if (iserror) {
                isuservalid = false;
                transfer_details_layout.setVisibility(View.GONE);
                showErrorMessage(true, response);
            } else {
                isuservalid = true;
                showErrorMessage(false, "");


                String s = amount_ed.getText().toString();
                if (ratio != null && ratio != 0.0 && !(s.toString().equals(""))) {
                    transfer_details_layout.setVisibility(View.VISIBLE);

                    if (receiver_name != null && !receiver_name.equals("") && receiver_name.equals("NameRequired"))
                        receiver_name = user_name;


                    if (CommonUtility.isNumeric(s.toString())) {
                        showErrorMessage(false, "");
                        double amount =CommonUtility.round(ratio * Double.parseDouble(s.toString()), 2);
                        Log.e("mount",""+amount);
                        String user_receive_text = "<p>"+user_name+" will receive: <font color=#3BB8FF>"+amount+"</font>"+ " "+receiver_currency+"<p>";
                        user_receive_detail_text.setText(Html.fromHtml(user_receive_text));
                    } else {
                        showErrorMessage(true, getResources().getString(R.string.number_not_valid));
                    }



                } else
                    transfer_details_layout.setVisibility(View.GONE);


            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject joparent, jochild;
            JSONArray jsonArray;

            response = Apis.getApisInstance(ctx).validateUser(user_name);
            if (!response.equals("")) {
                try {
                    Log.e("", "Response User: " + response);
                    joparent = new JSONObject(response);
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserror = true;
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    } else {
                        jsonArray = joparent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                        jochild = jsonArray.getJSONObject(0);

                        ratio = jochild.getDouble(VariableClass.ResponseVariables.RATIOS);
                        receiver_currency = jochild.getString(VariableClass.ResponseVariables.CURRENCY);

                        if (jochild.has(VariableClass.ResponseVariables.USERNAME))
                            receiver_name = jochild.getString(VariableClass.ResponseVariables.USERNAME);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                    iserror = true;
                    response = getResources().getString(R.string.parse_error);
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
            ;
            CommonUtility.show_PDialog(FundTransferActivity.this, getResources().getString(R.string.please_wait));
            confirm_transfer.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(ctx).signInApi(Prefs.getUserActualName(ctx), password, Prefs.getResellerID(ctx), "", "", new String[]{Prefs.getUserCountryCode(ctx)});
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

            CommonUtility.dialog.dismiss();
            if (iserr) {
                showErrorMessage(true, response);
            } else {

                new TransferFund().execute();
            }

            confirm_transfer.setEnabled(true);

            super.onPostExecute(result);
        }
    }

    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }

    }
}
