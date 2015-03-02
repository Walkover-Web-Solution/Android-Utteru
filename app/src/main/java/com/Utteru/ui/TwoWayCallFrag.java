package com.Utteru.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.CustomKeyboardOther;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessContactDto;
import com.Utteru.dtos.ContactsDto;
import com.Utteru.userService.UserService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TwoWayCallFrag extends Fragment {


    final int REQUEST_SELECT_CONTACT_SOURCE = 1;
    final int REQUEST_SELECT_CONTACT_DEST = 2;
    public Timer timer_source, timer_destination;
    RelativeLayout call_price_view;
    TimerTask timerTask_source, timerTask_dest;
    View two_way_call;
    RelativeLayout error_layout;
    TextView error_textview;
    Button close_em_button;
    CustomKeyboardOther keyboard;
    EditText source_ed, dest_ed;
    Button source_con, dest_con, source_status_btn, dest_status_btn;
    RelativeLayout source_state_layout, destination_state_layout;
    TextView source_text_status, destination_text_status, call_price_rate, noteTextView, call_price_currecny;
    ImageButton call_btn, endcall_btn;
    TextView destination_time_elapsed, source_time_elapsed;
    String source_string, destination_string = "";
    String uniqueId1, uniqueId2;
    String uniqueId_selected;
    Activity ctx;
    SimpleDateFormat ft;
    Handler handler;
    String plus;
    int number;
    String source_rate;
    String destination_rate;
    RelativeLayout root;
    CountDownTimer source_timer, destination_timer;
    LinearLayout dialpad_layout;


    String TWO_WAY_CALL_STATE_ANSWER = "ANSWER";
    String TWO_WAY_CALL_STATE_ANSWERED = "ANSWERED";

    String TWO_WAY_CALL_STATE_DIALING = "DIALING";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        two_way_call = inflater.inflate(R.layout.two_way_calling, container, false);
        init();
        String next = "<font color='#EE0000'>Note: </font>";
        noteTextView.setText(Html.fromHtml(next + getString(R.string.two_way_call_msg2)));

        if (ctx.getIntent().getExtras() != null && ctx.getIntent().getExtras().containsKey(VariableClass.Vari.SELECTEDDATA)) {
            ContactsDto cdto = (ContactsDto) ctx.getIntent().getExtras().get(VariableClass.Vari.SELECTEDDATA);
            source_ed.setText(cdto.getSource_number());
            dest_ed.setText(cdto.getDestination_number());
        }

        return two_way_call;
    }

    @Override
    public void onResume() {


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


        source_state_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                source_ed.requestFocus();

            }
        });
        destination_state_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dest_ed.requestFocus();
            }
        });

        source_ed.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    dest_ed.requestFocus();
                }
                return false;
            }
        });
        source_ed.setOnTouchListener(new View.OnTouchListener() {
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
        dest_ed.setOnTouchListener(new View.OnTouchListener() {
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

        dest_ed.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    call_btn.performClick();
                }
                return false;
            }
        });
        call_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
                //make two way call
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    source_string = source_ed.getText().toString();
                    destination_string = dest_ed.getText().toString();
                    if (!(source_string.equals("")) && !(destination_string.equals(""))) {


                        if (source_string.startsWith("+") || source_string.startsWith("00")) {
                            if (destination_string.startsWith("+") || destination_string.startsWith("00")) {
                                if (source_string.length() > 8 && source_string.length() < 18) {
                                    if (destination_string.length() > 8 && destination_string.length() < 18) {

                                        source_string = CommonUtility.validateNumberForApi(source_string);
                                        destination_string = CommonUtility.validateNumberForApi(destination_string);

                                        new TwoWayCall().execute(null, null, null);
                                    } else
                                        showErrorMessage(true, getResources().getString(R.string.number_not_valid));
                                } else
                                    showErrorMessage(true, getResources().getString(R.string.number_not_valid));
                            } else
                                showErrorMessage(true, getResources().getString(R.string.number_validation));
                        } else
                            showErrorMessage(true, getResources().getString(R.string.number_validation));
                    } else {
                        showErrorMessage(true, getResources().getString(R.string.empty_number));
                    }
                } else
                    showErrorMessage(true, getResources().getString(R.string.internet_error));

            }
        });
        source_ed.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                try {
                    source_string = s.toString();
                    if (!source_string.equals("")) {
                        if (source_string.startsWith("+") || source_string.startsWith("00")) {
                            showsourceprice();


                        } else {
                            showErrorMessage(true, getResources().getString(R.string.number_validation));

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        dest_ed.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                try {
                    destination_string = s.toString();
                    if (!destination_string.equals("")) {
                        if (destination_string.startsWith("+") || destination_string.startsWith("00")) {


                            showDestinationprice();


                        } else {

                            showErrorMessage(true, getResources().getString(R.string.number_validation));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyBoard(false);
            }
        });

        source_con.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectContact(REQUEST_SELECT_CONTACT_SOURCE);
            }
        });
        dest_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectContact(REQUEST_SELECT_CONTACT_DEST);
            }
        });

        endcall_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new TwoWayCallEnd().execute(null, null, null);
            }
        });

        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
                return false;
            }
        });
        super.onResume();
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent
            data) {
        super.onActivityResult(reqCode, resultCode, data);

        Log.e("inisie activity result", "inside activity result " + reqCode + " " + resultCode);

        if (resultCode == Activity.RESULT_OK) {

            AccessContactDto selected_con = (AccessContactDto) data.getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);


            if (selected_con != null) {

                Log.e("number from phone", "" + selected_con.getMobile_number());
                String con_number = selected_con.getMobile_number();


                Log.e("number after replace", "" + con_number);
                if (con_number != null && !con_number.equals("")) {

                    switch (reqCode) {
                        case (REQUEST_SELECT_CONTACT_SOURCE):
                            //source_ed.setText(CommonUtility.validateNumberForUI(con_number,ctx));
                            source_ed.setText("+" + con_number);
                            break;
                        case (REQUEST_SELECT_CONTACT_DEST):
                            // dest_ed.setText(CommonUtility.validateNumberForUI(con_number,ctx));
                            if (con_number.startsWith("+"))
                                dest_ed.setText(con_number);
                            else
                                dest_ed.setText("+" + con_number);
                            break;
                    }

                } else {

                    // CommonUtility.showCustomAlertError(getActivity(), getString(R.string.no_contact_found));
                }
            }
//            else {
//                CommonUtility.showCustomAlertError(getActivity(), getString(R.string.no_contact_found));
//
//
//            }

        }


    }

    void init() {
        Log.e("init", "init");
        ctx = getActivity();
        plus = new String("+");
        root = (RelativeLayout) two_way_call.findViewById(R.id.tw_root);
        noteTextView = (TextView) two_way_call.findViewById(R.id.noteTextView);
        error_textview = (TextView) two_way_call.findViewById(R.id.error_text);
        error_layout = (RelativeLayout) two_way_call.findViewById(R.id.error_layout);
        close_em_button = (Button) two_way_call.findViewById(R.id.close_button);
        source_ed = (EditText) two_way_call.findViewById(R.id.tw_source_num);
        String default_num = Prefs.getUserDefaultNumber(ctx);
        destination_rate = "0.0";
        source_rate = "0.0";

        dest_ed = (EditText) two_way_call.findViewById(R.id.tw_destination_num);
        source_con = (Button) two_way_call.findViewById(R.id.tw_source_con_button);
        dest_con = (Button) two_way_call.findViewById(R.id.tw_destination_con_button);
        call_btn = (ImageButton) two_way_call.findViewById(R.id.two_way_call_btn);
        endcall_btn = (ImageButton) two_way_call.findViewById(R.id.two_way_endcall_btn);
        source_status_btn = (Button) two_way_call.findViewById(R.id.tw_source_status_image);
        dest_status_btn = (Button) two_way_call.findViewById(R.id.tw_destination_status_image);
        source_state_layout = (RelativeLayout) two_way_call.findViewById(R.id.tw_source_status_layout);
        destination_state_layout = (RelativeLayout) two_way_call.findViewById(R.id.tw_destination_status_layout);
        source_text_status = (TextView) two_way_call.findViewById(R.id.tw_source_num_status);
        destination_text_status = (TextView) two_way_call.findViewById(R.id.tw_destination_num_status);
        call_price_rate = (TextView) two_way_call.findViewById(R.id.tw_pricing_rate);
        call_price_currecny = (TextView) two_way_call.findViewById(R.id.tw_price_currecny);
        dialpad_layout = (LinearLayout) two_way_call.findViewById(R.id.dialpad_layout);
        ft = new SimpleDateFormat("d MMM,hh:mm aaa");
        handler = new Handler();
        destination_time_elapsed = (TextView) two_way_call.findViewById(R.id.tw_destination_time_elapsed);
        source_time_elapsed = (TextView) two_way_call.findViewById(R.id.tw_source_time_elapsed);
        endcall_btn.setEnabled(false);
        call_price_view = (RelativeLayout) two_way_call.findViewById(R.id.two_way_price_view);
        keyboard = new CustomKeyboardOther(getActivity(), R.id.keyboardview, R.xml.numberic_key_only, two_way_call);
        keyboard.registerEditText(dest_ed.getId(), two_way_call);
        keyboard.registerEditText(source_ed.getId(), two_way_call);

        if (!default_num.equals("")) {
            source_ed.setText("+" + default_num);
            number = REQUEST_SELECT_CONTACT_SOURCE;
            source_string = source_ed.getText().toString();

            new GetPricing().execute();
        }


    }

    public void showKeyBoard(Boolean showKeyBoard) {
        //show keyboard
        if (showKeyBoard) {

            Animation bottomUp = AnimationUtils.loadAnimation(ctx,
                    R.anim.bottom_up);

            if (dialpad_layout.getVisibility() == View.GONE) {
                dialpad_layout.setAnimation(bottomUp);
                dialpad_layout.setVisibility(View.VISIBLE);
                getView().clearFocus();
            }
        }
        //hide keyboard
        else {
            Animation bottpmdown = AnimationUtils.loadAnimation(ctx,
                    R.anim.bottom_down);
            if (dialpad_layout.getVisibility() == View.VISIBLE) {
                dialpad_layout.setAnimation(bottpmdown);
                dialpad_layout.setVisibility(View.GONE);

                getView().clearFocus();

            }
        }
    }

    void resetView() {

        Log.e("reset view called", "reset view called");
        source_state_layout.setVisibility(View.GONE);
        destination_state_layout.setVisibility(View.GONE);
        call_price_view.setVisibility(View.GONE);
        endcall_btn.setVisibility(View.GONE);
        call_btn.setVisibility(View.VISIBLE);

        if (timerTask_dest != null) {
            timerTask_dest.cancel();
        }
        if (timerTask_source != null) {
            timerTask_source.cancel();
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

    public void selectContact(int action) {


        Intent intent = new Intent(ctx, PhoneBookActivity.class);
        startActivityForResult(intent, action);
        getActivity().overridePendingTransition(R.anim.animation1, R.anim.animation2);

    }

    public class GetPricing extends AsyncTask<Void, Void, Void> {
        String response = "";
        Boolean iserror = false;
        String rate, currency;

        @Override
        protected void onPostExecute(Void result) {

            if (iserror) {

                showErrorMessage(true, response);

                if (number == REQUEST_SELECT_CONTACT_DEST) {
                    destination_rate = "0.0";
                } else if (number == REQUEST_SELECT_CONTACT_SOURCE) {
                    source_rate = "0.0";
                }

                try {
                    Double temp_rate = (Double.parseDouble(source_rate) + Double.parseDouble(destination_rate));
                    rate = "" + CommonUtility.round(temp_rate, 2);
                    call_price_rate.setText(source_rate + "+" + destination_rate + "=" + rate);
                } catch (Exception e) {
                    call_price_view.setVisibility(View.GONE);
                }


            } else {

                call_price_view.setVisibility(View.VISIBLE);
                if (number == REQUEST_SELECT_CONTACT_DEST) {
                    destination_rate = rate;
                } else if (number == REQUEST_SELECT_CONTACT_SOURCE) {
                    source_rate = rate;
                }
                try {
                    Double temp_rate = (Double.parseDouble(source_rate) + Double.parseDouble(destination_rate));
                    rate = "" + CommonUtility.round(temp_rate, 2);
                    call_price_rate.setText(source_rate + "+" + destination_rate + "=" + rate);
                    call_price_currecny.setText(currency + "/min");
                } catch (Exception e) {
                    call_price_view.setVisibility(View.GONE);
                }

            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String selected_number = null;
            if (number == REQUEST_SELECT_CONTACT_DEST) {
                selected_number = destination_string;
            } else if (number == REQUEST_SELECT_CONTACT_SOURCE) {
                selected_number = source_string;
            }

            response = Apis.getApisInstance(getActivity()).getTwoWayPricing(selected_number);
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
                        jarray = joparent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                        jochild = jarray.getJSONObject(0);
                        rate = jochild.getString(VariableClass.ResponseVariables.RATE);
                        currency = jochild.getString(VariableClass.ResponseVariables.CURRENCY_NAME);
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

    public class TwoWayCall extends AsyncTask<Void, Void, Void> {
        String response = "";
        Boolean iserror = false;
        ContactsDto cdto;

        @Override
        protected void onPostExecute(Void result) {
            CommonUtility.dialog.dismiss();
            if (iserror) {
                showErrorMessage(true, response);
            } else {
                call_btn.setVisibility(View.GONE);
                endcall_btn.setVisibility(View.VISIBLE);
                timer_source = new Timer(false);
                timerTask_source = new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                uniqueId_selected = uniqueId1;
                                ctx.runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        new TwoWayCallResponseSource().execute(null, null, null);
                                    }
                                });
                            }
                        });
                    }
                };
                timer_source.schedule(timerTask_source, new Date(), 3000); // 1000 = 1 second.
            }
            call_btn.setEnabled(true);

            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            call_btn.setEnabled(false);
            CommonUtility.show_PDialog(getActivity(), getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            response = Apis.getApisInstance(getActivity()).twoWayCall(source_string, destination_string);
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
                        cdto = new ContactsDto(CommonUtility.getContactDisplayNameByNumber(source_string, ctx), source_string, CommonUtility.getContactDisplayNameByNumber(destination_string, ctx), "+" + destination_string, ft.format(new Date()));
                        UserService.getUserServiceInstance(ctx).addTwoWayCall(cdto);

                        jarray = joparent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                        jochild = jarray.getJSONObject(0);
                        uniqueId1 = jochild.getString(VariableClass.ResponseVariables.MESSAGEID1);
                        uniqueId2 = jochild.getString(VariableClass.ResponseVariables.MESSAGEID2);
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

    public class TwoWayCallResponseSource extends AsyncTask<Void, Void, Void> {
        String response = "";
        Boolean iserror = false;
        String status = "";
        String time_elapsed = "";

        @Override
        protected void onPostExecute(Void result) {

            if (iserror) {
                showErrorMessage(true, response);
                resetView();
            } else {
                source_state_layout.setVisibility(View.VISIBLE);
                source_status_btn.setBackgroundResource(R.drawable.tw_status_connecting);
                source_text_status.setText(status);
                source_time_elapsed.setText(time_elapsed);
                if (status.equals(TWO_WAY_CALL_STATE_ANSWER)) {
                    //start second timer
                    if (timerTask_source != null)
                        timerTask_source.cancel();

                    source_status_btn.setBackgroundResource(R.drawable.tw_status_connected);
                    timer_destination = new Timer(false);
                    timerTask_dest = new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {

                                @Override
                                public void run() {

                                    uniqueId_selected = uniqueId2;
                                    ctx.runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            new TwoWayCallResponseDestination().execute(null, null, null);
                                        }
                                    });

                                }
                            });
                        }
                    };
                    timer_destination.schedule(timerTask_dest, new Date(), 3000); // 1000 = 1 second.
                    endcall_btn.setEnabled(true);

                } else if (status.equals(TWO_WAY_CALL_STATE_ANSWERED)) {
                    if (timerTask_source != null)
                        timerTask_source.cancel();
                    resetView();

                } else if (!status.equals(TWO_WAY_CALL_STATE_DIALING)) {
                    //show error
                    if (timerTask_source != null)
                        timerTask_source.cancel();
                    resetView();
                    showErrorMessage(true, status);
                    //reset view
                }
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            response = Apis.getApisInstance(getActivity()).twoWayCallResponse(uniqueId_selected);
            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;
                JSONArray jarry;
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
                        jarry = joparent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                        jochild = jarry.getJSONObject(0);

                        status = jochild.getString(VariableClass.ResponseVariables.STATUS);
                        time_elapsed = jochild.getString(VariableClass.ResponseVariables.TIME_ELAPSED);
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

    public class TwoWayCallResponseDestination extends AsyncTask<Void, Void, Void> {
        String response = "";
        Boolean iserror = false;
        String status = "";
        String time_elapsed = "";

        @Override
        protected void onPostExecute(Void result) {

            if (iserror) {
                showErrorMessage(true, response);
                resetView();

            } else {
                destination_state_layout.setVisibility(View.VISIBLE);
                dest_status_btn.setBackgroundResource(R.drawable.tw_status_connecting);
                destination_text_status.setText(status);
                destination_time_elapsed.setText(time_elapsed);

                if (status.equals(TWO_WAY_CALL_STATE_ANSWER)) {
                    //show connectivity
                    dest_status_btn.setBackgroundResource(R.drawable.tw_status_connected);
                } else if (status.equals(TWO_WAY_CALL_STATE_ANSWERED)) {
                    //endCall
                    if (timerTask_dest != null)
                        timerTask_dest.cancel();
                    resetView();

                } else if (!status.equals(TWO_WAY_CALL_STATE_DIALING)) {
                    //show error
                    //reset view
                    if (timerTask_dest != null)
                        timerTask_dest.cancel();
                    resetView();
                    showErrorMessage(true, status);
                }


            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            response = Apis.getApisInstance(getActivity()).twoWayCallResponse(uniqueId_selected);
            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;
                JSONArray jarry;
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
                        jarry = joparent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                        jochild = jarry.getJSONObject(0);
                        status = jochild.getString(VariableClass.ResponseVariables.STATUS);
                        time_elapsed = jochild.getString(VariableClass.ResponseVariables.TIME_ELAPSED);
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

    public class TwoWayCallEnd extends AsyncTask<Void, Void, Void> {
        String response = "";
        Boolean iserror = false;

        @Override
        protected void onPostExecute(Void result) {
            CommonUtility.dialog.dismiss();
            endcall_btn.setEnabled(true);
            resetView();
            if (iserror) {
                showErrorMessage(true, response);
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            endcall_btn.setEnabled(true);
            CommonUtility.show_PDialog(getActivity(), getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(getActivity()).twoWayCallEnd(uniqueId1);
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

    void showDestinationprice() {

        if (destination_timer != null) {
            destination_timer.cancel();
            destination_timer = null;
        }
        destination_timer = new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                //hit pricing api
                destination_string = CommonUtility.validateNumberForApi(destination_string);
                if (!destination_string.equals("")) {
                    number = REQUEST_SELECT_CONTACT_DEST;
                    new GetPricing().execute(null, null, null);
                }

                showErrorMessage(false, "");
            }
        }
                .start();


    }

    void showsourceprice() {

        if (source_timer != null) {
            source_timer.cancel();
            source_timer = null;
        }


        source_timer = new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {

                //hit pricing api
                source_string = CommonUtility.validateNumberForApi(source_string);
                if (!source_string.equals("")) {
                    number = REQUEST_SELECT_CONTACT_SOURCE;
                    new GetPricing().execute(null, null, null);
                }

                showErrorMessage(false, "");
            }
        }
                .start();


    }

    public void onBackPress() {
        if (dialpad_layout.getVisibility() == View.VISIBLE) {
            showKeyBoard(false);
            Log.e("hiding  keyboard", "hiding keyboard");

        } else {
            Intent menu = new Intent(getActivity(), MenuScreen.class);
            menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(menu);
            getActivity().finish();
        }

    }
}
