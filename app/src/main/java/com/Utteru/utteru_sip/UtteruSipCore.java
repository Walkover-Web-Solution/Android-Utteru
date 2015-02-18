package com.Utteru.utteru_sip;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.service.PortSipService;
import com.Utteru.util.Line;
import com.Utteru.util.Session;
import com.Utteru.util.SipContact;
import com.portsip.OnPortSIPEvent;
import com.portsip.PortSipErrorcode;
import com.portsip.PortSipSdk;
import com.portsip.Renderer;

import java.util.ArrayList;
import java.util.List;

import intercom.intercomsdk.Intercom;


public class UtteruSipCore extends Application implements OnPortSIPEvent {
    public static Boolean isCallDisconnected = false;
    public static Boolean isCallConnected = false;
    public static Boolean isCallInProgress = false;
    //  private  BroadcastReceiver registrationReceiver;
    Intent srvIntent = null;
    // IntentFilter filter;
    PortSipService portSrv = null;
    MyServiceConnection connection = null;
    PortSipSdk sdk;
    boolean conference = false;
    private boolean _SIPLogined = false;// record register status
    DialerActivity mainActivity;
    private SurfaceView remoteSurfaceView = null;
    private SurfaceView localSurfaceView = null;
    AudioManager audiomanager;
    static final private Line[] _CallSessions = new Line[Line.MAX_LINES];// record
    // all
    // audio-video
    // sessions
    static final private ArrayList<SipContact> contacts = new ArrayList<SipContact>();
    private Line _CurrentlyLine = _CallSessions[Line.LINE_BASE];// active line

    public static final String SESSION_CHANG = UtteruSipCore.class
            .getCanonicalName() + "session_change";
    public static final String CONTACT_CHANG = UtteruSipCore.class
            .getCanonicalName() + "Contact change";

    public static final String REGISTRATION_STATE_CHANGE = UtteruSipCore.class
            .getCanonicalName() + "registration_state_change";
    public static final String CALL_STATE_CHANGE = UtteruSipCore.class
            .getCanonicalName() + "call_state_change";

    public static final int CALL_STATE_TRYING = 1001;
    public static final int CALL_STATE_PROGRESS = 1002;
    public static final int CALL_STATE_RINGING = 1003;
    public static final int CALL_STATE_ESTATBLISHED = 1004;
    public static final int CALL_STATE_FAILED = 1005;
    public static final int CALL_STATE_CONNECTED = 1006;
    public static final int CALL_STATE_CLOSED = 1007;


    public void sendSessionChangeMessage(String message, String action) {
        Intent broadIntent = new Intent(action);
        broadIntent.putExtra("description", message);
        sendBroadcast(broadIntent);
    }

    public void sendCallStates(String message, String action, int code) {
        Intent broadIntent = new Intent(action);
        broadIntent.putExtra("message", message);
        broadIntent.putExtra("code", code);
        sendBroadcast(broadIntent);
    }

    public void sendRegistrationStates(String message, String action, int code) {

        Log.e("registration status", "" + code);
        Intent broadIntent = new Intent(action);
        broadIntent.putExtra("message", message);
        broadIntent.putExtra("code", code);
        sendBroadcast(broadIntent);
    }


    Line[] getLines() {
        return _CallSessions;
    }

    List<SipContact> getSipContacts() {
        return contacts;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Intercom.initialize(getApplicationContext());
        sdk = new PortSipSdk();

        srvIntent = new Intent(this, PortSipService.class);
        connection = new MyServiceConnection();

        sdk.setOnPortSIPEvent(this);
        localSurfaceView = Renderer.CreateLocalRenderer(this);
        remoteSurfaceView = Renderer.CreateRenderer(this, true);

        bindService(srvIntent, connection, BIND_AUTO_CREATE);
        for (int i = 0; i < _CallSessions.length; i++) {
            _CallSessions[i] = new Line(i);
        }

        // Setup handler for uncaught exceptions.
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(thread, e);
            }
        });
        audiomanager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);//to silent ring
       /* registrationReceiver = new MyCallReceiver();
         filter = new IntentFilter();*/
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        unbindService(connection);
        connection = null;
    }

    public SurfaceView getRemoteSurfaceView() {
        return remoteSurfaceView;
    }

    public SurfaceView getLocalSurfaceView() {
        return localSurfaceView;
    }

    public PortSipSdk getPortSIPSDK() {
        return sdk;
    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PortSipService.MyBinder binder = (PortSipService.MyBinder) service;

            portSrv = binder.getService();


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            portSrv = null;
        }
    }

    public boolean isOnline() {
        return _SIPLogined;
    }

    void setConfrenceMode(boolean state) {
        conference = state;
    }

    public boolean isConference() {
        return conference;
    }

    void setOnlineState(boolean state) {
        _SIPLogined = state;
    }

    public void showTipMessage(String text) {

    }

    public int answerSessionCall(Line sessionLine, boolean videoCall) {
        long seesionId = sessionLine.getSessionId();
        int rt = PortSipErrorcode.INVALID_SESSION_ID;
        if (seesionId != PortSipErrorcode.INVALID_SESSION_ID) {
            rt = sdk.answerCall(sessionLine.getSessionId(), videoCall);
        }
        if (rt == 0) {
            sessionLine.setSessionState(true);
            setCurrentLine(sessionLine);
            if (videoCall) {
                sessionLine.setVideoState(true);
            } else {
                sessionLine.setVideoState(false);
            }

            if (conference) {
                sdk.joinToConference(sessionLine.getSessionId());
            }
            showTipMessage(sessionLine.getLineName()
                    + ": Call established");
        } else {
            sessionLine.reset();
            showTipMessage(sessionLine.getLineName()
                    + ": failed to answer call !");
        }

        return rt;
    }


    // register event listener
    @Override
    public void onRegisterSuccess(String reason, int code) {
        _SIPLogined = true;

        sendRegistrationStates(reason, REGISTRATION_STATE_CHANGE, code);
    }

    @Override
    public void onRegisterFailure(String reason, int code) {
        _SIPLogined = false;

        if (mainActivity != null) {

            sendRegistrationStates(reason, REGISTRATION_STATE_CHANGE, code);

        }

    }

    // call event listener
    @Override
    public void onInviteIncoming(long sessionId, String callerDisplayName, String caller,
                                 String calleeDisplayName, String callee,
                                 String audioCodecs, String videoCodecs, boolean existsAudio,
                                 boolean existsVideo) {

        Line tempSession = findIdleLine();

        if (tempSession == null)// all sessions busy
        {
            sdk.rejectCall(sessionId, 486);
            return;
        } else {
            tempSession.setRecvCallState(true);
        }


        if (existsAudio) {
            // If more than one codecs using, then they are separated with "#",
            // for example: "g.729#GSM#AMR", "H264#H263", you have to parse them
            // by yourself.
        }

        tempSession.setSessionId(sessionId);
        tempSession.setVideoState(existsVideo);
        String comingCallTips = "Call incoming: " + callerDisplayName + "<" + caller + ">";
        tempSession.setDescriptionString(comingCallTips);
        sendSessionChangeMessage(comingCallTips, SESSION_CHANG);
        setCurrentLine(tempSession);


        final Line curSession = tempSession;
        AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).create();
        alertDialog.setTitle("Incoming Audio Call");
        alertDialog.setMessage(comingCallTips);

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Answer",
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Answer Audio call
                        answerSessionCall(curSession, false);
                    }
                });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Reject",
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Reject call
                        sdk.rejectCall(curSession.getSessionId(), 486);
                        curSession.reset();

                        showTipMessage("Rejected call");
                    }
                });

        // Showing Alert Message
        alertDialog.show();

        bringToFront();
        CommonUtility.showCustomAlertForContacts(this.getApplicationContext(), comingCallTips);
        // You should write your own code to play the wav file here for alert
        // the incoming call(incoming tone);
    }

    @Override
    public void onInviteTrying(long sessionId) {

        isCallInProgress = true;
        Line tempSession = findLineBySessionId(sessionId);
        if (tempSession == null) {
            return;
        }

        tempSession.setDescriptionString("Call is trying...");
        Log.e("call state", "Call is trying...");


        sendCallStates("Trying to connect..", CALL_STATE_CHANGE, CALL_STATE_TRYING);
        audiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

    }

    @Override
    public void onInviteSessionProgress(long sessionId, String audioCodecs,
                                        String videoCodecs, boolean existsEarlyMedia, boolean existsAudio,
                                        boolean existsVideo) {
        Line tempSession = findLineBySessionId(sessionId);
        if (tempSession == null) {
            return;
        }

        if (existsVideo) {
            // If more than one codecs using, then they are separated with "#",
            // for example: "g.729#GSM#AMR", "H264#H263", you have to parse them
            // by yourself.
        }
        if (existsAudio) {
            // If more than one codecs using, then they are separated with "#",
            // for example: "g.729#GSM#AMR", "H264#H263", you have to parse them
            // by yourself.
        }

        tempSession.setSessionState(true);

        tempSession.setDescriptionString("Call session progress.");
        Log.e("call state", "Call session progress.");
        tempSession.setEarlyMeida(existsEarlyMedia);
        sendCallStates("In Progress...", CALL_STATE_CHANGE, CALL_STATE_PROGRESS);

    }

    @Override
    public void onInviteRinging(long sessionId, String statusText,
                                int statusCode) {
        Line tempSession = findLineBySessionId(sessionId);
        if (tempSession == null) {
            return;
        }

        if (!tempSession.hasEarlyMeida()) {
            // Hasn't the early media, you must play the local WAVE file for
            // ringing tone
        }

        tempSession.setDescriptionString("Ringing...");
        Log.e("call state", "Ringing...");
        sendCallStates("Ringing...", CALL_STATE_CHANGE, CALL_STATE_RINGING);
    }

    @Override
    public void onInviteAnswered(long sessionId, String callerDisplayName, String caller,
                                 String calleeDisplayName, String callee,
                                 String audioCodecs, String videoCodecs, boolean existsAudio,
                                 boolean existsVideo) {
        Line tempSession = findLineBySessionId(sessionId);
        if (tempSession == null) {
            return;
        }

        if (existsVideo) {
            sdk.sendVideo(tempSession.getSessionId(), true);
            // If more than one codecs using, then they are separated with "#",
            // for example: "g.729#GSM#AMR", "H264#H263", you have to parse them
            // by yourself.
        }
        if (existsAudio) {
            // If more than one codecs using, then they are separated with "#",
            // for example: "g.729#GSM#AMR", "H264#H263", you have to parse them
            // by yourself.
        }
        tempSession.setVideoState(existsVideo);
        tempSession.setSessionState(true);
        tempSession.setDescriptionString("call established");
        Log.e("call state", "call established");
        sendCallStates("Call Established...", CALL_STATE_CHANGE, CALL_STATE_ESTATBLISHED);

        if (isConference()) {
            sdk.joinToConference(tempSession.getSessionId());
            tempSession.setHoldState(false);

        }

        // If this is the refer call then need set it to normal
        if (tempSession.isReferCall()) {
            tempSession.setReferCall(false, 0);
        }


    }

    @Override
    public void onInviteFailure(long sessionId, String reason, int code) {
        Line tempSession = findLineBySessionId(sessionId);
        if (tempSession == null) {
            return;
        }
        isCallConnected = false;
        isCallDisconnected = true;
        isCallInProgress = false;
        cancelNotification(this);

        tempSession.setDescriptionString("call failure" + reason);
        Log.e("call state", "call failure" + reason);
        sendCallStates("Failed to connect", CALL_STATE_CHANGE, CALL_STATE_FAILED);

        if (tempSession.isReferCall()) {
            // Take off the origin call from HOLD if the refer call is failure
            Line originSession = findLineBySessionId(tempSession
                    .getOriginCallSessionId());
            if (originSession != null) {
                sdk.unHold(originSession.getSessionId());
                originSession.setHoldState(false);

                // Switch the currently line to origin call line
                setCurrentLine(originSession);

                tempSession.setDescriptionString("refer failure:" + reason
                        + "resume orignal call");
                Log.e("call state", "refer failure:" + reason
                        + "resume orignal call");

            }
        }

        tempSession.reset();
        audiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);


    }

    @Override
    public void onInviteUpdated(long sessionId, String audioCodecs,
                                String videoCodecs, boolean existsAudio, boolean existsVideo) {
        Line tempSession = findLineBySessionId(sessionId);
        if (tempSession == null) {
            return;
        }


        tempSession.setDescriptionString("Call is updated");
        Log.e("call state", "Call is updated");
    }

    @Override
    public void onInviteConnected(long sessionId) {
        isCallConnected = true;
        isCallDisconnected = false;
        isCallInProgress = false;
        Line tempSession = findLineBySessionId(sessionId);
        if (tempSession == null) {
            return;
        }

        tempSession.setDescriptionString("Call is connected");
        Log.e("call state", "Call is connected");
        sendCallStates("Call connected", CALL_STATE_CHANGE, CALL_STATE_CONNECTED);
        //filter.addAction("android.intent.action.PHONE_STATE");
        //  registerReceiver(registrationReceiver, filter);

    }

    @Override
    public void onInviteBeginingForward(String forwardTo) {
        sendSessionChangeMessage("An incoming call was forwarded to: "
                + forwardTo, SESSION_CHANG);
    }

    @Override
    public void onInviteClosed(long sessionId) {

        isCallConnected = false;
        isCallDisconnected = true;
        isCallInProgress = false;
        cancelNotification(this);
        Line tempSession = findLineBySessionId(sessionId);
        if (tempSession == null) {
            return;
        }

        tempSession.reset();
        tempSession.setDescriptionString(": Call closed.");
        Log.e("call state", "Call closed.");
        sendCallStates("Call Closed", CALL_STATE_CHANGE, CALL_STATE_CLOSED);
        audiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);


    }

    @Override
    public void onRemoteHold(long sessionId) {
        Line tempSession = findLineBySessionId(sessionId);
        if (tempSession == null) {
            return;
        }

        tempSession.setDescriptionString("Placed on hold by remote.");
        sendSessionChangeMessage("Placed on hold by remote.", SESSION_CHANG);
    }

    @Override
    public void onRemoteUnHold(long sessionId, String audioCodecs,
                               String videoCodecs, boolean existsAudio, boolean existsVideo) {
        Line tempSession = findLineBySessionId(sessionId);
        if (tempSession == null) {
            return;
        }

        tempSession.setDescriptionString("Take off hold by remote.");
        sendSessionChangeMessage("Take off hold by remote.", SESSION_CHANG);
    }

    @Override
    public void onRecvDtmfTone(long sessionId, int tone) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReceivedRefer(long sessionId, final long referId, String to,
                                String from, final String referSipMessage) {
        final Line tempSession = findLineBySessionId(sessionId);
        if (tempSession == null) {
            sdk.rejectRefer(referId);
            return;
        }

        final Line referSession = findIdleLine();

        if (referSession == null)// all sessions busy
        {
            sdk.rejectRefer(referId);
            return;
        } else {
            referSession.setSessionState(true);
        }

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE: {

                        sdk.rejectRefer(referId);
                        referSession.reset();
                    }
                    break;
                    case DialogInterface.BUTTON_POSITIVE: {

                        sdk.hold(tempSession.getSessionId());// hold current session
                        tempSession.setHoldState(true);

                        tempSession
                                .setDescriptionString("Place currently call on hold on line: ");

                        long referSessionId = sdk.acceptRefer(referId,
                                referSipMessage);
                        if (referSessionId <= 0) {
                            referSession
                                    .setDescriptionString("Failed to accept REFER on line");

                            referSession.reset();

                            // Take off hold
                            sdk.unHold(tempSession.getSessionId());
                            tempSession.setHoldState(false);
                        } else {
                            referSession.setSessionId(referSessionId);
                            referSession.setSessionState(true);
                            referSession.setReferCall(true,
                                    tempSession.getSessionId());

                            referSession
                                    .setDescriptionString("Accepted the refer, new call is trying on line ");

                            _CurrentlyLine = referSession;

                            tempSession
                                    .setDescriptionString("Now current line is set to: "
                                            + _CurrentlyLine.getLineName());
                        }
                    }
                }

            }
        };
        showGloableDialog("Received REFER", "accept", listener, "reject",
                listener);

    }

    @Override
    public void onReferAccepted(long sessionId) {
        Line tempSession = findLineBySessionId(sessionId);
        if (tempSession == null) {
            return;
        }

        tempSession.setDescriptionString("the REFER was accepted.");
        sendSessionChangeMessage("the REFER was accepted.", SESSION_CHANG);
    }

    @Override
    public void onReferRejected(long sessionId, String reason, int code) {
        Line tempSession = findLineBySessionId(sessionId);
        if (tempSession == null) {
            return;
        }

        tempSession.setDescriptionString("the REFER was rejected.");
        sendSessionChangeMessage("the REFER was rejected.", SESSION_CHANG);
    }

    @Override
    public void onTransferTrying(long sessionId) {
        Line tempSession = findLineBySessionId(sessionId);
        if (tempSession == null) {
            return;
        }

        tempSession.setDescriptionString("Transfer Trying.");
        sendSessionChangeMessage("Transfer Trying.", SESSION_CHANG);
    }

    @Override
    public void onTransferRinging(long sessionId) {
        Line tempSession = findLineBySessionId(sessionId);
        if (tempSession == null) {
            return;
        }

        tempSession.setDescriptionString("Transfer Ringing.");
        sendSessionChangeMessage("Transfer Ringing.", SESSION_CHANG);
    }

    @Override
    public void onACTVTransferSuccess(long sessionId) {
        Line tempSession = findLineBySessionId(sessionId);
        if (tempSession == null) {
            return;
        }
        tempSession.setDescriptionString("Transfer succeeded.");
    }

    @Override
    public void onACTVTransferFailure(long sessionId, String reason, int code) {
        Line tempSession = findLineBySessionId(sessionId);
        if (tempSession == null) {
            return;
        }

        tempSession.setDescriptionString("Transfer failure");


    }

    public Line findLineBySessionId(long sessionId) {
        for (int i = Line.LINE_BASE; i < Line.MAX_LINES; ++i) {
            if (_CallSessions[i].getSessionId() == sessionId) {
                return _CallSessions[i];
            }
        }

        return null;
    }

    public Line findSessionByIndex(int index) {

        if (Line.LINE_BASE <= index && index < Line.MAX_LINES) {
            return _CallSessions[index];
        }

        return null;
    }

    static Line findIdleLine() {

        for (int i = Line.LINE_BASE; i < Line.MAX_LINES; ++i)// get idle session
        {
            if (!_CallSessions[i].getSessionState()
                    && !_CallSessions[i].getRecvCallState()) {
                return _CallSessions[i];
            }
        }

        return null;
    }

    public void setCurrentLine(Line line) {
        if (line == null) {
            _CurrentlyLine = _CallSessions[Line.LINE_BASE];
        } else {
            _CurrentlyLine = line;
        }

    }

    public Session getCurrentSession() {


        return _CurrentlyLine;
    }


    @Override
    public void onReceivedSignaling(long sessionId, String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSendingSignaling(long sessionId, String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onWaitingVoiceMessage(String messageAccount,
                                      int urgentNewMessageCount, int urgentOldMessageCount,
                                      int newMessageCount, int oldMessageCount) {
        String text = messageAccount;
        text += " has voice message.";

        showMessage(text);


    }

    @Override
    public void onWaitingFaxMessage(String messageAccount,
                                    int urgentNewMessageCount, int urgentOldMessageCount,
                                    int newMessageCount, int oldMessageCount) {
        String text = messageAccount;
        text += " has FAX message.";

        showMessage(text);


    }

    @Override
    public void onPresenceRecvSubscribe(long subscribeId,
                                        String fromDisplayName, String from, String subject) {

        String fromSipUri = "sip:" + from;

        final long tempId = subscribeId;
        OnClickListener onClick;
        SipContact contactRefrence = null;
        boolean contactExist = false;

        for (int i = 0; i < contacts.size(); ++i) {
            contactRefrence = contacts.get(i);
            String SipUri = contactRefrence.getSipAddr();

            if (SipUri.equals(fromSipUri)) {
                contactExist = true;
                if (contactRefrence.isAccept()) {
                    long nOldSubscribID = contactRefrence.getSubId();
                    sdk.presenceAcceptSubscribe(tempId);
                    String status = "Available";
                    sdk.presenceOnline(tempId, status);

                    if (contactRefrence.isSubscribed() && nOldSubscribID >= 0) {
                        sdk.presenceSubscribeContact(fromSipUri, subject);
                    }
                    return;
                } else {
                    break;
                }
            }
        }

        //
        if (!contactExist) {
            contactRefrence = new SipContact();
            contacts.add(contactRefrence);
            contactRefrence.setSipAddr(fromSipUri);
        }
        final SipContact contact = contactRefrence;
        onClick = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        sdk.presenceAcceptSubscribe(tempId);
                        contact.setSubId(tempId);
                        contact.setAccept(true);
                        String status = "Available";
                        sdk.presenceOnline(tempId, status);
                        contact.setSubstatus(true);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        contact.setAccept(false);// reject subscribe
                        contact.setSubId(0);
                        contact.setSubstatus(false);// offline

                        sdk.presenceRejectSubscribe(tempId);
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            }
        };
        showGloableDialog(from, "Accept", onClick, "Reject", onClick);

    }

    @Override
    public void onPresenceOnline(String fromDisplayName, String from,
                                 String stateText) {

        String fromSipUri = "sip:" + from;
        SipContact contactRefernce;
        for (int i = 0; i < contacts.size(); ++i) {
            contactRefernce = contacts.get(i);
            String SipUri = contactRefernce.getSipAddr();
            if (SipUri.endsWith(fromSipUri)) {
                contactRefernce.setSubDescription(stateText);
                contactRefernce.setSubstatus(true);// online
            }
        }
        sendSessionChangeMessage("contact status change.", CONTACT_CHANG);
    }

    @Override
    public void onPresenceOffline(String fromDisplayName, String from) {

        String fromSipUri = "sip:" + from;
        SipContact contactRefernce;
        for (int i = 0; i < contacts.size(); ++i) {
            contactRefernce = contacts.get(i);
            String SipUri = contactRefernce.getSipAddr();
            if (SipUri.endsWith(fromSipUri)) {
                contactRefernce.setSubstatus(false);// "Offline";
                contactRefernce.setSubId(0);
            }
        }
        sendSessionChangeMessage("contact status change.", CONTACT_CHANG);
    }

    @Override
    public void onRecvOptions(String optionsMessage) {

    }

    @Override
    public void onRecvInfo(String infoMessage) {


    }

    @Override
    public void onRecvMessage(long sessionId, String mimeType,
                              String subMimeType, byte[] messageData, int messageDataLength) {

    }

    @Override
    public void onRecvOutOfDialogMessage(String fromDisplayName, String from,
                                         String toDisplayName, String to, String mimeType,
                                         String subMimeType, byte[] messageData, int messageDataLength) {
        String text = "Received a " + mimeType + "message(out of dialog) from ";
        text += from;

        if (mimeType.equals("text") && subMimeType.equals("plain")) {
            // String receivedMsg = GetString(messageData);
            showMessage(text);
        } else if (mimeType.equals("application")
                && subMimeType.equals("vnd.3gpp.sms")) {
            // The messageData is binary data

            showMessage(text);
        } else if (mimeType.equals("application")
                && subMimeType.equals("vnd.3gpp2.sms")) {
            // The messageData is binary data
            showMessage(text);

        }
    }

    @Override
    public void onPlayAudioFileFinished(long sessionId, String fileName) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPlayVideoFileFinished(long sessionId) {
        // TODO Auto-generated method stub
    }

    public void setMainActivity(DialerActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onSendMessageSuccess(long sessionId, long messageId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSendMessageFailure(long sessionId, long messageId,
                                     String reason, int code) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSendOutOfDialogMessageSuccess(long messageId,
                                                String fromDisplayName, String from, String toDisplayName, String to) {
    }

    @Override
    public void onSendOutOfDialogMessageFailure(long messageId,
                                                String fromDisplayName, String from, String toDisplayName,
                                                String to, String reason, int code) {
    }

    @Override
    public void onReceivedRTPPacket(long sessionId, boolean isAudio,
                                    byte[] RTPPacket, int packetSize) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSendingRTPPacket(long sessionId, boolean isAudio,
                                   byte[] RTPPacket, int packetSize) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAudioRawCallback(long sessionId, int enum_audioCallbackMode,
                                   byte[] data, int dataLength, int samplingFreqHz) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onVideoRawCallback(long sessionId, int enum_videoCallbackMode,
                                   int width, int height, byte[] data, int dataLength) {
        // TODO Auto-generated method stub

    }

    void showMessage(String message) {
        OnClickListener listener = null;
        showGloableDialog(message, null, listener, "Cancel",
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
    }

    void showGloableDialog(String message, String strPositive,
                           OnClickListener positiveListener, String strNegative,
                           OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        if (positiveListener != null) {
            builder.setPositiveButton(strPositive, positiveListener);
        }

        if (negativeListener != null) {
            builder.setNegativeButton(strNegative, negativeListener);
        }

        AlertDialog ad = builder.create();
        ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        ad.setCanceledOnTouchOutside(false);
        ad.show();
    }

    public void bringToFront() {

        try {
            Intent startActivity = new Intent();
            startActivity.setClass(this, DialerActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pi = PendingIntent.getActivity(this, 0, startActivity, 0);

            pi.send(this, 0, null);
        } catch (CanceledException e) {
            e.printStackTrace();
        }
    }

    public void handleUncaughtException(Thread thread, Throwable e) {
        Log.e("crash handled", "crash handled ");
        e.printStackTrace(); // not all Android versions will print the stack trace automatically

//        Intent intent = new Intent ();
//        intent.setAction ("com.mydomain.SEND_LOG"); // see step 5.
//        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
//        startActivity (intent);

        System.exit(1); // kill off the crashed app
    }

    private NotificationManagerCompat mNotificationManager;
    private int notificationID = 107;


    public void cancelNotification(Context context) {
        Log.e("Cancel", "notification");
        mNotificationManager =
                NotificationManagerCompat.from(context);
        mNotificationManager.cancel(notificationID);
    }


    public void updateNotification(Context context, String number, String name, long duration, String price,long date) {

        Intent resultIntent = new Intent(context, DialerActivity.class).putExtra("fragment", "notification");
        resultIntent.putExtra(VariableClass.Vari.SELECTEDNUMBER, number);
        resultIntent.putExtra(VariableClass.Vari.SELECTEDNAME, name);
        resultIntent.putExtra(VariableClass.Vari.CALL_ELAPSED_TIME, duration);

        Log.e("elapsed time ",""+duration);
        resultIntent.putExtra(VariableClass.Vari.CALLDATE, date);
        resultIntent.putExtra(VariableClass.Vari.CALLPRICE, price);

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        resultIntent.setFlags(Notification.FLAG_ONGOING_EVENT);


        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context);

        mBuilder.setContentTitle(name);
        mBuilder.setContentText("Calling");
        mBuilder.setTicker("Call in progress");
        mBuilder.addAction(android.R.drawable.ic_menu_call, "HANG UP", pendingIntent);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setOngoing(true);
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_launcher);


        mBuilder.setLargeIcon(icon);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);

        Intent clickonnotice = new Intent(context, DialerActivity.class).putExtra("fragment", "notification");
        clickonnotice.putExtra(VariableClass.Vari.SELECTEDNUMBER, number);
        clickonnotice.putExtra(VariableClass.Vari.SELECTEDNAME, name);
        resultIntent.putExtra(VariableClass.Vari.CALL_ELAPSED_TIME, duration);
        resultIntent.putExtra(VariableClass.Vari.CALLDATE, date);
        clickonnotice.putExtra(VariableClass.Vari.CALLPRICE, price);
        clickonnotice.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        clickonnotice.setFlags(Notification.FLAG_ONGOING_EVENT);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addNextIntent(clickonnotice);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager =
                NotificationManagerCompat.from(context);
        mNotificationManager.notify(notificationID, mBuilder.build());
    }


}
