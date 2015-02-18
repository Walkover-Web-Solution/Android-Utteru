package com.Utteru.utteru_sip;


import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Utteru.R;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.TimeView;
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


public class CallingScreenFragment extends Fragment {

    PortSipSdk mSipSdk;
    UtteruSipCore utteruSipCore;
    int _CurrentlyLine = 0;
    Line[] lines;
    Context ctx;
    RelativeLayout hangup;
    Chronometer chronometer;
    ImageButton speaker, mute_button ,end_call_button;
    Boolean speaker_state = false;
    Boolean mute_state = false;
    SimpleDateFormat sdf;
    TextView call_status, callee_name_txt, callee_number_txt;
    ImageView pricedivider;
    TextView price;
    public static CallData calldata;


    AudioManager audiomanager = null;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private int field = 0x00000020;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e("Calling fragment", "on activity created");
        super.onActivityCreated(savedInstanceState);
        utteruSipCore = ((UtteruSipCore) getActivity().getApplicationContext());
        mSipSdk = utteruSipCore.getPortSIPSDK();
        audiomanager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);//to silent ring
        mSipSdk.clearAudioCodec();
        mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_G729);

        mSipSdk.enableAEC(true);
        isCallActive(getActivity());
        lines = utteruSipCore.getLines();
        int result = audiomanager.requestAudioFocus(null,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
        audiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        audiomanager.setStreamMute(AudioManager.STREAM_MUSIC, true);


        try {
            // Yeah, this is hidden field.
            field = PowerManager.class.getClass().getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        } catch (Throwable ignored) {
        }

        powerManager = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(field, getActivity().getLocalClassName());
        init();

        //CheckMusic();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("Calling fragment", "onCreateView()");
        View view = inflater.inflate(R.layout.dialer_calling_screen, container, false);

        return view;
    }

    @Override
    public void onStart() {
        //check if call running

        super.onStart();
    }

    @Override
    public void onResume() {

        if (UtteruSipCore.isCallConnected) {
            Log.e("setting chronometer ", "setting chronometer ");
            chronometer.setVisibility(View.VISIBLE);

            long time =calldata.getTime_elapsed();

            chronometer.setBase(time);
            chronometer.start();


            if (calldata.getCall_price() != null && !calldata.getCall_price().equals("")) {
                price.setText(calldata.getCall_price());
            }
        } else
            Log.e("not setting chronometer ", " not setting chronometer ");

        utteruSipCore.cancelNotification(ctx);

        if (calldata.getCallee_name() != null && !calldata.getCallee_name().equals(""))
            callee_name_txt.setText(calldata.getCallee_name());
        else
            callee_name_txt.setText("Unknown");

        callee_number_txt.setText(calldata.getCallee_number());






        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
        }

        super.onResume();
    }

    @Override
    public void onPause() {

        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
        if (!UtteruSipCore.isCallDisconnected)
            utteruSipCore.updateNotification(ctx, calldata.getCallee_number(), calldata.getCallee_name(), calldata.getTime_elapsed(), calldata.getCall_price(),calldata.getDate());

        super.onPause();
    }

    public void setNumber(String number, String name, long basetime, Boolean iscallongoing, String price_call,Context c,long date) {

        Log.e("number settings",""+number);

        Log.e("setting data", "setting data" +basetime);
        String callee_name = name;
        if (callee_name == null || callee_name.equals(""))
            callee_name = number;

        ctx = c;
        String callTo = number;

        calldata = new CallData(callee_name, callTo, price_call, basetime, iscallongoing, date);


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

    void sendDtmf(String dtmfdigit) {

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
        Log.e("number",""+number);

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

    void init() {

        ctx = getActivity().getBaseContext();
        price = (TextView) getView().findViewById(R.id.call_price);
        pricedivider = (ImageView) getView().findViewById(R.id.call_price_divider);
        chronometer = (Chronometer) getView().findViewById(R.id.time);
        callee_name_txt = (TextView) getView().findViewById(R.id.calle_name);
        callee_number_txt = (TextView) getView().findViewById(R.id.callee_number);
        call_status = (TextView) getView().findViewById(R.id.call_status);
        speaker = (ImageButton) getView().findViewById(R.id.switch_speaker);
        hangup = (RelativeLayout) getView().findViewById(R.id.call_end);
        mute_button = (ImageButton) getView().findViewById(R.id.microphone);
        end_call_button = (ImageButton)getView().findViewById(R.id.endcall_btn);

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
                return false;
            }
        });
        end_call_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finishCall();
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
        getActivity().onBackPressed();
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
                break;
            case UtteruSipCore.CALL_STATE_ESTATBLISHED:

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
                break;
            case UtteruSipCore.CALL_STATE_FAILED:
                call_status.setText("Call Failed..." + "(" + message + ")");
                finishCall();
                break;
        }

    }

    @Override
    public void onStop() {
        audiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audiomanager.setStreamMute(AudioManager.STREAM_MUSIC, false);

        Log.e("on stop ", "on stop ");
        super.onStop();
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

                            calldata.setPrice(price);

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

    public void onBackPress(Boolean fromNotification) {

        getActivity().getSupportFragmentManager().popBackStack();




    }


    public void onDestroyFrag() {
        Log.e("on destroy ", "on destroy ");
        UtteruSipCore.isCallInProgress = false;
        UtteruSipCore.isCallDisconnected = false;
        UtteruSipCore.isCallConnected = false;
        finishCall();
        utteruSipCore.cancelNotification(ctx);
        super.onDestroy();
    }
}
