package com.Utteru.utteru_sip;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Utteru.R;
import com.Utteru.commonUtilities.CustomKeyboardOther;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.Country;
import com.Utteru.dtos.RecentCallsDto;
import com.Utteru.ui.Apis;
import com.Utteru.userService.UserService;
import com.Utteru.util.Line;
import com.Utteru.util.Session;
import com.portsip.PortSipEnumDefine;
import com.portsip.PortSipSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;


public class CallingScreenActivity extends Activity {
    View call;
    PortSipSdk mSipSdk;
    UtteruSipCore utteruSipCore;
    public static int _CurrentlyLine = 0;
    Line[] lines;

    RelativeLayout hangup;
    Chronometer chronometer;
    ImageButton speaker, mute_button, end_call_button, open_dialpad;
    CustomKeyboardOther keyboard;
    Boolean speaker_state = false;
    Boolean mute_state = false;
    SimpleDateFormat sdf;
    TextView call_status, callee_name_txt, callee_number_txt;
    ImageView pricedivider;
    TextView price;
    LinearLayout dialpad_layout;
    public static CallData calldata;
    EditText edCalling;
    Context ctx = this;


    AudioManager audiomanager = null;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private int field = 0x00000020;
    IntentFilter mIntentFilter_call;
    BroadcastReceiver callReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {


            if (intent.getAction().equals(UtteruSipCore.CALL_STATE_CHANGE)) {
                Log.e("call status in receiver", "" + intent.getExtras().getString("message") + "" + intent.getExtras().getInt("code"));
                int code = intent.getExtras().getInt("code");
                String message = intent.getExtras().getString("message");

                 onCallStateChange(message,code);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.dialer_calling_screen);
        utteruSipCore = ((UtteruSipCore) getApplicationContext());
        mSipSdk = utteruSipCore.getPortSIPSDK();
        audiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);//to silent ring
        mSipSdk.clearAudioCodec();
        mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_G729);
        mSipSdk.enableAEC(true);

        //what is the  use
        isCallActive(ctx);
        lines = utteruSipCore.getLines();
        audiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);


        try {

            field = PowerManager.class.getClass().getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        } catch (Throwable ignored) {


         }

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(field,getLocalClassName());
        init();


        super.onCreate(savedInstanceState);
    }


    public boolean isCallActive(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (manager.getMode() == AudioManager.MODE_IN_CALL) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onResume() {

        mIntentFilter_call = new IntentFilter();
        mIntentFilter_call.addAction(UtteruSipCore.CALL_STATE_CHANGE);
        ctx.registerReceiver(callReceiver, mIntentFilter_call);


        if (UtteruSipCore.isCallConnected) {
            Log.e("setting chronometer ", "setting chronometer ");
            chronometer.setVisibility(View.VISIBLE);
            chronometer.setBase(calldata.getTime_elapsed());
            chronometer.start();


            if (calldata.getCall_price() != null && !calldata.getCall_price().equals("")) {
                price.setText(calldata.getCall_price());
            }
        } else
            Log.e("not setting chronometer ", " not setting chronometer ");

        utteruSipCore.cancelNotification(ctx);

        if (calldata.getCallee_name() != null && !calldata.getCallee_name().equals(""))
            callee_name_txt.setText(calldata.getCallee_name());
        else {
            callee_name_txt.setText("Unknown");

        }

        callee_number_txt.setText(calldata.getCallee_number());


        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
        }
        edCalling.setOnTouchListener(new View.OnTouchListener() {
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

    @Override
    public void onPause() {

        if (wakeLock.isHeld()) {
           wakeLock.release();
        }
        Log.e("is call disconnected ",""+UtteruSipCore.isCallDisconnected);
        if (!UtteruSipCore.isCallDisconnected)
            utteruSipCore.updateNotification(ctx);

        super.onPause();
    }



    void hangupCall() {


        UtteruSipCore.isCallInProgress = false;
        UtteruSipCore.isCallDisconnected = true;
        UtteruSipCore.isCallConnected = false;
        utteruSipCore.cancelNotification(ctx);
        Session currentLine = utteruSipCore.findSessionByIndex(_CurrentlyLine);
        Log.e("on hangup", "" + currentLine.getRecvCallState());
        if (currentLine.getRecvCallState()) {

            mSipSdk.rejectCall(currentLine.getSessionId(), 486);
            currentLine.reset();
            showTips(lines[_CurrentlyLine].getLineName()
                    + ": Rejected call");
            return;
        }

        Log.e("on hangup", "" + currentLine.getSessionId());
        if (currentLine.getSessionState()) {
            mSipSdk.hangUp(currentLine.getSessionId());
            currentLine.reset();

            showTips(lines[_CurrentlyLine].getLineName() + ": Hang up");
        }
    }


    void muteUnMuteCall(Boolean state) {
        Session currentLine = utteruSipCore.findSessionByIndex(_CurrentlyLine);
        mSipSdk.muteSession(currentLine.getSessionId(), state,
                state, state, state);


    }

    void holdCall() {

        Session currentLine = utteruSipCore.findSessionByIndex(_CurrentlyLine);
        if (!currentLine.getSessionState()
                || currentLine.getHoldState()) {
            return;
        }
        int rt = mSipSdk.hold(currentLine.getSessionId());
        if (rt != 0) {
            showTips("hold operation failed.");
            return;
        }
        currentLine.setHoldState(true);
    }

    void unholdCall() {

        Session currentLine = utteruSipCore.findSessionByIndex(_CurrentlyLine);
        if (!currentLine.getSessionState()
                || !currentLine.getHoldState()) {
            return;
        }
        int rt = mSipSdk.unHold(currentLine.getSessionId());
        if (rt != 0) {
            currentLine.setHoldState(false);
            showTips(lines[_CurrentlyLine].getLineName()
                    + ": Un-Hold Failure.");
            return;
        }

        currentLine.setHoldState(false);
        showTips(lines[_CurrentlyLine].getLineName() + ": Un-Hold");
    }

    void showTips(String text) {


    }

    void startSpeaker(Boolean speaker) {
        mSipSdk.setLoudspeakerStatus(speaker);

    }

    public void sendDtmf(String dtmfdigit) {

        Session currentLine = utteruSipCore.findSessionByIndex(_CurrentlyLine);
        char number = dtmfdigit.charAt(0);
        if (utteruSipCore.isOnline()
                && currentLine.getSessionState()) {
            if (number == '*') {
                mSipSdk.sendDtmf(currentLine.getSessionId(),
                        PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, 10,
                        160, true);
                return;
            }
            if (number == '#') {
                mSipSdk.sendDtmf(currentLine.getSessionId(),
                        PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, 11,
                        160, true);
                return;
            }
            int sum = Integer.valueOf(dtmfdigit);// 0~9
            mSipSdk.sendDtmf(currentLine.getSessionId(),
                    PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, sum,
                    160, true);
        }

    }

    private Boolean dialNumber(String number) {


        String callTo = number;
        Log.e("number", "" + number);

        callTo = callTo.replaceAll("-", "").replaceAll("\\s+", "");
//
//        if (!callTo.startsWith("0") && !callTo.startsWith("+")) {
//            callTo = Prefs.getUserCountryCode(ctx) + callTo;
//        }
//
//        callTo = callTo.replaceAll("-", "").replaceAll("\\s+", "");


        if (callTo == null || callTo.length() <= 0) {
            showTips("The phone number is empty.");
            return false;
        }


        Session currentLine = utteruSipCore.findSessionByIndex(_CurrentlyLine);
        if (currentLine.getSessionState()
                || currentLine.getRecvCallState()) {
            showTips("Current line is busy now, please switch a line.");
            return false;
        }

        // Ensure that we have been added one audio codec at least
        if (mSipSdk.isAudioCodecEmpty()) {
            showTips("Audio Codec Empty,add audio codec at first");
            return false;
        }

        // Usually for 3PCC need to make call without SDP
        long sessionId = mSipSdk.call(callTo,
                true, false);

        Log.e("on call", "" + currentLine.getSessionId());
        if (sessionId <= 0) {
            showTips("Call failure");
            return false;
        }

        currentLine.setSessionId(sessionId);
        currentLine.setSessionState(true);
        currentLine.setVideoState(false);
        utteruSipCore.setCurrentLine(lines[_CurrentlyLine]);
        showTips(lines[_CurrentlyLine].getLineName() + ": Calling...");

        return true;


    }

    public void showKeyBoard(Boolean showKeyBoard) {
        //show keyboard
        if (showKeyBoard) {

            Animation bottomUp = AnimationUtils.loadAnimation(ctx,
                    R.anim.bottom_up);

            if (dialpad_layout.getVisibility() == View.GONE) {
                dialpad_layout.setAnimation(bottomUp);
                dialpad_layout.setVisibility(View.VISIBLE);
                edCalling.setVisibility(View.VISIBLE);
                open_dialpad.setBackgroundResource(R.drawable.dial_pad_on);
//                clearFocus();
            }
        }
        //hide keyboard
        else {
            Animation bottpmdown = AnimationUtils.loadAnimation(ctx,
                    R.anim.bottom_down);
            if (dialpad_layout.getVisibility() == View.VISIBLE) {
                dialpad_layout.setAnimation(bottpmdown);
                dialpad_layout.setVisibility(View.GONE);
                edCalling.setVisibility(View.GONE);
                open_dialpad.setBackgroundResource(R.drawable.dial_pad_off);
//               clearFocus();

            }
        }
    }

    void init() {

        ctx=this;
        calldata = CallData.getCallDateInstance();
        price = (TextView)findViewById(R.id.call_price);
        pricedivider = (ImageView) findViewById(R.id.call_price_divider);
        chronometer = (Chronometer) findViewById(R.id.time);
        callee_name_txt = (TextView) findViewById(R.id.calle_name);
        callee_number_txt = (TextView)findViewById(R.id.callee_number);
        call_status = (TextView) findViewById(R.id.call_status);
        speaker = (ImageButton) findViewById(R.id.switch_speaker);
        hangup = (RelativeLayout) findViewById(R.id.call_end);
        open_dialpad = (ImageButton) findViewById(R.id.open_dialpad);
        mute_button = (ImageButton) findViewById(R.id.microphone);
        dialpad_layout = (LinearLayout)findViewById(R.id.dialpad_layout);
        edCalling = (EditText) findViewById(R.id.ed_calling_screen);
        end_call_button = (ImageButton)findViewById(R.id.endcall_btn);
        open_dialpad.setEnabled(false);
        keyboard = new CustomKeyboardOther(this, R.id.keyboardview, R.xml.numberic_key_only, call);
        keyboard.registerEditText(edCalling.getId(), call);

        new SearchRate().execute();
        sdf = new SimpleDateFormat("d MMM , hh:mm aaa");


        //check if from notification
        if (!calldata.getCallType()) {
            Log.e("making call", "making call ");
            if (!dialNumber(calldata.getCallee_number())) {

                finishCall();
            }
        }

        startSpeaker(false);

        hangup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("clicked hangup", "clicked hangup");
                finishCall();
                CallingScreenActivity.this.finish();
                return false;
            }
        });
        end_call_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finishCall();
                CallingScreenActivity.this.finish();
                return false;
            }
        });
        speaker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (speaker_state) {
                    speaker_state = false;
                    startSpeaker(speaker_state);
                    speaker.setBackgroundResource(R.drawable.speaker_inactive);
                } else {
                    speaker_state = true;
                    startSpeaker(speaker_state);
                    speaker.setBackgroundResource(R.drawable.speaker_active);

                }
                return false;
            }
        });
        open_dialpad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!(dialpad_layout.getVisibility() == View.VISIBLE))
                    showKeyBoard(true);
                else
                    showKeyBoard(false);

            }
        });
        mute_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (mute_state) {
                    mute_state = false;
                    muteUnMuteCall(mute_state);
                    mute_button.setBackgroundResource(R.drawable.mute_inactive);
                } else {
                    mute_state = true;
                    muteUnMuteCall(mute_state);
                    mute_button.setBackgroundResource(R.drawable.mute_active);
                }
                return false;
            }
        });
    }

    void finishCall() {

        Log.e("call finish", " call finish with proper end call");
        if (chronometer != null)
            chronometer.stop();
        hangupCall();


        new AddCallInRecentCallList().execute(null, null, null);


    }


    private class AddCallInRecentCallList extends AsyncTask<Void, Void, Void> {
        RecentCallsDto call = null;

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            call = new RecentCallsDto();
            call.setDuration(chronometer.getText().toString());
            call.setName(calldata.getCallee_name());
            call.setNumber(calldata.getCallee_number());
            call.setTime(sdf.format(new Date(calldata.getDate())));
            call.setSource_number(Prefs.getUserDefaultNumber(ctx));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (call != null) {
                UserService.getUserServiceInstance(ctx).addRecentCall(call);
            }
            return null;
        }

    }


    public void onCallStateChange(String message, int code) {
        switch (code) {
            case UtteruSipCore.CALL_STATE_TRYING:

                call_status.setText("Dialing...");
                break;
            case UtteruSipCore.CALL_STATE_PROGRESS:

                break;
            case UtteruSipCore.CALL_STATE_CONNECTED:
                open_dialpad.setEnabled(true);
                break;
            case UtteruSipCore.CALL_STATE_ESTATBLISHED:
                open_dialpad.setEnabled(true);
                call_status.setVisibility(View.GONE);

                chronometer.setVisibility(View.VISIBLE);
                calldata.setTime_elapsed(SystemClock.elapsedRealtime());

                chronometer.setBase(calldata.getTime_elapsed());
                chronometer.start();
                break;
            case UtteruSipCore.CALL_STATE_RINGING:

                break;
            case UtteruSipCore.CALL_STATE_CLOSED:

                finishCall();
                this.finish();
                break;
            case UtteruSipCore.CALL_STATE_FAILED:
                call_status.setText("Call Failed..." + "(" + message + ")");
                finishCall();
                this.finish();
                break;
        }

    }




    class SearchRate extends AsyncTask<Void, Void, Void> {

        String response = null;
        Boolean iserr = false;


        @Override
        protected void onPostExecute(Void result) {

            if (!iserr) {
                price.setVisibility(View.VISIBLE);
                pricedivider.setVisibility(View.VISIBLE);
                price.setText(calldata.getCall_price());
            } else {
                price.setVisibility(View.GONE);
                pricedivider.setVisibility(View.GONE);
            }
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... params) {

            String callTo = calldata.getCallee_number().replaceAll("-", "").replaceAll("\\s+", "");


            callTo = callTo.replace("+", "");

            response = Apis.getApisInstance(ctx).getPricing(callTo);
            if (!response.equals("")) {
                JSONObject joparent = null;
                JSONObject jochild = null;
                JSONArray japarent = null;
                JSONArray jachild = null;
                try {
                    joparent = new JSONObject(response);
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserr = true;
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }
                    //success response
                    else if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                        japarent = joparent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                        joparent = japarent.getJSONObject(0);

                        Country header, row;

                        //getting currency
                        String currency = null;
                        jachild = joparent.getJSONArray(VariableClass.ResponseVariables.STATUS);
                        jochild = jachild.getJSONObject(0);
                        currency = jochild.getString(VariableClass.ResponseVariables.CURRENCY);
                        joparent.remove(VariableClass.ResponseVariables.STATUS);

                        //get keys and pricing
                        Iterator keys = joparent.keys();
                        String key_string = null;
                        int count = 0;

                        while (keys.hasNext()) {
                            key_string = (String) keys.next();
                            jachild = joparent.getJSONArray(key_string);

                            header = new Country();
                            header.setCountryName(key_string);
                            header.setCurrency(currency);
                            header.setIsSection(true);
                            jochild = jachild.getJSONObject(count);
                            row = new Country();
                            String country = jochild.getString(VariableClass.ResponseVariables.OPERATOR);
                            if (country.equals(""))
                                country = "Other";
                            row.setCountryName(country);
                            row.setPrice(jochild.getString(VariableClass.ResponseVariables.RATE) + " " + jochild.getString(VariableClass.ResponseVariables.AMOUNTTYPE));
                            String price = jochild.getString(VariableClass.ResponseVariables.RATE) + " " + jochild.getString(VariableClass.ResponseVariables.AMOUNTTYPE) + "/min";

                            calldata.setCall_price(price);

                            row.setIsSection(false);
                        }

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


    @Override
    protected void onStop() {
        if (callReceiver != null)
            ctx.unregisterReceiver(callReceiver);
        super.onStop();
    }

    public void onDestroy() {
        Log.e("on destroy ", "on destroy ");
        audiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        UtteruSipCore.isCallInProgress = false;
        UtteruSipCore.isCallDisconnected = false;
        UtteruSipCore.isCallConnected = false;
        utteruSipCore.cancelNotification(ctx);
        finishCall();
    super.onDestroy();

    }

    @Override
    public void onBackPressed() {
       onPause();
        startActivity(new Intent(ctx,DialerActivity.class));
        overridePendingTransition(R.anim.animation3,R.anim.animation4);
    }
}
