package com.Utteru.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.Utteru.adapters.ViewPagerAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.CustomKeyboardOther;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.Country;
import com.Utteru.dtos.MultipleVerifiedNumber;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class  SignUpFragement extends Fragment {
    public static final int REQUEST_CODE = 1;
    public static final int RESULT_OK = 1;
    public static final String NOTVALID = "6017";
    public static final String NOVERIFICATIONNUM = "6013";
    public static final String MESSAGENOTSENT = "6005";
    public static final String MESSAGESENT = "6004";
    public static final String NEWSIGNUPCODE = "8007";
    static boolean isnewSignup = false;
    LinearLayout root_layout;
    View sign_up_view;
    TextView alredy_have_acc_text;
    RelativeLayout error_layout;
    TextView error_textview;
    Button sign_up, close_em_button, country_code;
    EditText user_number_ed;
    String user_number_string;
    String country_code_string;
    String carrierType = "2";
    ViewPager pager;
    Context ctx;
    CirclePageIndicator indicator;
    ViewPagerAdapter mAdapter;
    CustomKeyboardOther keyboard;
    LinearLayout dialpad_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sign_up_view = inflater.inflate(R.layout.sign_up_layout, container, false);
        init();

        return sign_up_view;
    }

    @Override
    public void onResume() {
        //getting currency list
        CommonUtility.setCurrency(getActivity().getApplicationContext());
        alredy_have_acc_text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent startsign_in = new Intent(getActivity(), SignInScreen.class);
                getActivity().startActivity(startsign_in);
                getActivity().overridePendingTransition(R.anim.animation1, R.anim.animation2);
            }
        });

        country_code.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SearchListActivity.class);
                startActivityForResult(i, REQUEST_CODE);
                getActivity().overridePendingTransition(R.anim.animation1, R.anim.animation2);
            }
        });


        user_number_ed.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                    sign_up.performClick();
                }
                return false;
            }
        });
        sign_up.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
                if (CommonUtility.isNetworkAvailable(getActivity().getBaseContext())) {
                    user_number_string = user_number_ed.getText().toString().trim().replace("+", "");
                    if (!user_number_string.equals("")) {
                        new SignUpClass().execute(null, null, null);

                    } else
                        showErrorMessage(true, getResources().getString(R.string.fill_all));

                } else
                    showErrorMessage(true, getResources().getString(R.string.internet_error));
            }
        });

        close_em_button.setOnClickListener(new View.OnClickListener() {

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
     /*   root_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getActivity().getWindow().setSoftInputMode(
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

        super.onResume();
    }

    public void showKeyBoard(Boolean showKeyBoard) {
        //show keyboard
        if (showKeyBoard) {

            Animation bottomUp = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.bottom_up);

            if (dialpad_layout.getVisibility() == View.GONE) {
                dialpad_layout.setAnimation(bottomUp);
                dialpad_layout.setVisibility(View.VISIBLE);
            }
        }
        //hide keyboard
        else {
            Animation bottpmdown = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.bottom_down);
            if (dialpad_layout.getVisibility() == View.VISIBLE) {
                dialpad_layout.setAnimation(bottpmdown);
                dialpad_layout.setVisibility(View.GONE);


            }
        }
    }

    void init() {
        Log.e("signup init", "signup init");
        root_layout = (LinearLayout) sign_up_view.findViewById(R.id.signup_root);
        //getting currency from api
        pager = (ViewPager) sign_up_view.findViewById(R.id.pager);
        indicator = (CirclePageIndicator) sign_up_view.findViewById(R.id.indicator);
        mAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        pager.setAdapter(mAdapter);
        indicator.setViewPager(pager);
        indicator.setCurrentItem(2);
        pager.setEnabled(false);
        alredy_have_acc_text = (TextView) sign_up_view.findViewById(R.id.alredy_have_acc_txt);
        alredy_have_acc_text.setPaintFlags(alredy_have_acc_text.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        country_code = (Button) sign_up_view.findViewById(R.id.sign_up_country_code);
        error_textview = (TextView) sign_up_view.findViewById(R.id.error_text);
        sign_up = (Button) sign_up_view.findViewById(R.id.sign_up_button);
        error_layout = (RelativeLayout) sign_up_view.findViewById(R.id.error_layout);
        close_em_button = (Button) sign_up_view.findViewById(R.id.close_button);
        user_number_ed = (EditText) sign_up_view.findViewById(R.id.signup_user_number);
        SearchListActivity.country_list = new ArrayList<Country>();
        SearchListActivity.country_list = new CsvReader().readCsv(getActivity(), new CsvReader().getUserCountryIso(getActivity()), true);
        country_code_string = Prefs.getUserCountryCode(getActivity());
        country_code.setText("+" + country_code_string);
        dialpad_layout = (LinearLayout) sign_up_view.findViewById(R.id.dialpad_layout);
        keyboard = new CustomKeyboardOther(getActivity(), R.id.keyboardview, R.xml.numberic_key_only, sign_up_view);
        keyboard.registerEditText(user_number_ed.getId(), sign_up_view);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("activity result", "activity result ");
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            String countryCode = data.getExtras().getString(VariableClass.Vari.COUNTRYCODE);
            if (countryCode != null && !countryCode.equals("")) {
                Prefs.setUserCountryCode(getActivity(), countryCode);
                country_code.setText(countryCode);
                country_code_string = countryCode;
            }

            String countryIso = data.getExtras().getString(VariableClass.Vari.COUNTRYISO);
            if (countryIso != null && !countryIso.equals("")) {

                if (CommonUtility.currency_list != null && CommonUtility.currency_list.size() != 0) {
                    String tarrifId = CommonUtility.currency_list.get(new CsvReader().getUserCurrecncy(countryIso.toUpperCase()));
                    if (tarrifId != null && !tarrifId.equals("")) {
                        Prefs.setUserTarrif(getActivity().getBaseContext(), tarrifId);
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void showErrorMessage(Boolean showm, String message) {
        if (showm) {
            error_textview.setText(message);
            CommonUtility.expand(error_layout);
        } else {
            CommonUtility.collapse(error_layout);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class SignUpClass extends AsyncTask<Void, Void, Void> {
        String response = "";
        String tempId;
        Boolean isSignUp = false;
        Boolean iserror = false;

        @Override
        protected void onPostExecute(Void result) {
            CommonUtility.dialog.dismiss();
            sign_up.setEnabled(true);
            if (iserror) {
                showErrorMessage(true, response);
            } else if (isSignUp) {
                Intent startverificationcode = new Intent(getActivity(), VerificationCodeActivity.class);
                startverificationcode.putExtra(VariableClass.Vari.TEMP_ID, tempId);
                startverificationcode.putExtra(VariableClass.Vari.USERID, user_number_string);
                startverificationcode.putExtra(VariableClass.Vari.COUNTRYCODE, country_code_string);
                getActivity().startActivity(startverificationcode);
                getActivity().overridePendingTransition(R.anim.animation1, R.anim.animation2);
            } else {
                new ValidateForgotPassword().execute(null, null, null);
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            CommonUtility.show_PDialog(getActivity(), getResources().getString(R.string.please_wait));
            sign_up.setEnabled(true);
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(getActivity()).signupWithNumber(CommonUtility.validateNumberForApi(user_number_string), country_code_string, Prefs.getResellerID(getActivity()), "", carrierType);
            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;

                JSONArray jarray;
                try {
                    joparent = new JSONObject(response);
                    //failed response
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                        iserror = true;
                    }
                    //success response
                    else if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                        //if new signup
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        if (jochild.getString(VariableClass.ResponseVariables.ERRORCODE).equals(NEWSIGNUPCODE)) {

                            isSignUp = true;
                            isnewSignup = true;
                            jarray = joparent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                            jochild = jarray.getJSONObject(0);
                            tempId = jochild.getString(VariableClass.ResponseVariables.TEMPID);
                        } else {
                            isnewSignup = false;
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

    public class ValidateForgotPassword extends AsyncTask<Void, Void, Void> {
        String response = "";
        Boolean isValid = false;
        Boolean hasNumber = false;
        Boolean hasmsgsent = false;
        Boolean iserror = false;

        String tempNumber = null;

        @Override
        protected void onPostExecute(Void result) {
            Log.e("is error in forgot password", "" + iserror);
            if (iserror) {
                showErrorMessage(true, response);
            } else if (isValid) {
                if (hasNumber) {
                    hasNumber = true;
                    if (hasmsgsent) {
                        Intent insertverficationCode = new Intent(getActivity(), VerificationCodeActivity.class);
                        insertverficationCode.putExtra(VariableClass.Vari.USERID, user_number_string);
                        insertverficationCode.putExtra(VariableClass.Vari.COUNTRYCODE, country_code_string);
                        insertverficationCode.putExtra(VariableClass.Vari.SOURCECLASS, "1");
                        if (CommonUtility.c_list.size() > 1)
                            insertverficationCode.putExtra(VariableClass.Vari.SHOWOTHERNUMBER, "");
                        startActivity(insertverficationCode);
                        getActivity().overridePendingTransition(R.anim.animation1, R.anim.animation2);
                    } else if (response.equals(MESSAGENOTSENT)) {
                        Intent select_number = new Intent(getActivity(), SelectNumber.class);
                        select_number.putExtra(VariableClass.Vari.USERID, user_number_string);
                        select_number.putExtra(VariableClass.Vari.COUNTRYCODE, country_code_string);
                        select_number.putExtra(VariableClass.Vari.SHOWOTHERNUMBER, "");
                        startActivity(select_number);
                        getActivity().overridePendingTransition(R.anim.animation1, R.anim.animation2);
                    }
                } else
                    showErrorMessage(true, "Has no verified number");
            } else {
                Intent startforgotpass = new Intent(getActivity(), ForgotPasswordActivity.class);
                startforgotpass.putExtra(VariableClass.Vari.USERID, user_number_string);
                startforgotpass.putExtra(VariableClass.Vari.COUNTRYCODE, country_code_string);
                getActivity().startActivity(startforgotpass);
                getActivity().overridePendingTransition(R.anim.animation3, R.anim.animation4);
            }
            CommonUtility.dialog.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            CommonUtility.show_PDialog(getActivity(), getResources().getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(getActivity()).forgotPassword(CommonUtility.validateNumberForApi(user_number_string), new String[]{Prefs.getUserCountryCode(getActivity())}, "2", "2");
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
                                String tempnum=CommonUtility.validateNumberForApi(user_number_string);
                                for (int i = 0; i < jarray.length(); i++) {
                                    mdtos = new MultipleVerifiedNumber();
                                    jochild = jarray.getJSONObject(i);
                                    mdtos.setCountry_code(jochild.getString(VariableClass.ResponseVariables.COUNTRY_CODE));
                                    mdtos.setNumber(jochild.getString(VariableClass.ResponseVariables.VERIFIED_NUMBER));
                                    if (mdtos.getNumber().equals(tempnum))
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

    public void onBackPress() {
        if (dialpad_layout.getVisibility() == View.VISIBLE) {
            showKeyBoard(false);
            Log.e("hiding  keyboard", "hiding keyboard");

        } else {
            getActivity().finish();

            super.getActivity().onBackPressed();
        }
    }

}

