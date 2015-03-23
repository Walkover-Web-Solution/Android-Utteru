package com.Utteru.utteru_sip;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.util.Line;
import com.Utteru.util.Network;
import com.Utteru.util.SettingConfig;
import com.Utteru.util.UserInfo;
import com.portsip.PortSipEnumDefine;
import com.portsip.PortSipErrorcode;
import com.portsip.PortSipSdk;

import java.util.Random;

/**
 * Created by vikas on 24/02/15.
 */
public class SipRegisterService extends Service


{

    PortSipSdk mSipSdk;
    String statuString;
    UtteruSipCore utteruSipCore;
    Context context = this;
    String LogPath = null;
    String licenseKey = "1Xh01QTg3OTEyNUZEOTQwOTM4QzlFRDg3NDNFOTQyQjIzNkBGRTc2NzcwNDBCNzcwRDdGOUNCQzhGMzM4MjdDOTY1OUA3QjlFMzQxQkYzMUY0RTM3QTUyNjgzM0IzRjFENjQ4RkBGMjg4MDU0OTBFMDAxRDIyNTVDNkMwQTFFNDk0RTZFMg";

    @Override
    public void onCreate() {

        utteruSipCore = ((UtteruSipCore) context.getApplicationContext());
        mSipSdk = utteruSipCore.getPortSIPSDK();
        if (!utteruSipCore.isOnline())
            online();

        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }

    void updateStatus() {

        if (utteruSipCore.isOnline()) {
            statuString = null;
            CommonUtility.showCustomAlertForContacts(this, statuString);
        } else {
            if (statuString != null) {
                CommonUtility.showCustomAlertForContactsError(this, statuString);
            } else {
                CommonUtility.showCustomAlertForContactsError(this, "User not registered");
            }
        }
    }

    private UserInfo saveUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(Prefs.getUserSipName(context));
        userInfo.setUserPwd(Prefs.getUserSipPassword(context));
        if (Prefs.getUserCountryCode(this).equals("971")) {
            userInfo.setTranType(PortSipEnumDefine.ENUM_TRANSPORT_PERS);
            SettingConfig.setTransType(context, PortSipEnumDefine.ENUM_TRANSPORT_PERS);
            SettingConfig.setSrtpType(context, PortSipEnumDefine.ENUM_SRTPPOLICY_NONE, mSipSdk);
            userInfo.setSipServer(Prefs.getPerse_ip(context));
            userInfo.setSipPort(Integer.parseInt(Prefs.getPerse_port(context)));
        } else {
            userInfo.setTranType(PortSipEnumDefine.ENUM_TRANSPORT_UDP);
            SettingConfig.setTransType(context, PortSipEnumDefine.ENUM_TRANSPORT_UDP);
            SettingConfig.setSrtpType(context, PortSipEnumDefine.ENUM_SRTPPOLICY_NONE, mSipSdk);
            userInfo.setSipServer(Prefs.getUdp_ip(context));
            userInfo.setSipPort(Integer.parseInt(Prefs.getUdp_port(context)));
        }
        //userInfo.setStunPort(Integer.parseInt(Prefs.getstun_port(context)));
        //userInfo.setStunServer(Prefs.getstun_ip(context));

        SettingConfig.setUserInfo(context, userInfo);
        return userInfo;
    }

    private int online() {
        int result = setUserInfo();

        Log.e("online status result ", "" + result);
        if (result == PortSipErrorcode.ECoreErrorNone) {

            result = mSipSdk.registerServer(90, 3);
            Log.e("online registration result ", "" + result);
            if (result != PortSipErrorcode.ECoreErrorNone) {
                statuString = "register server failed";
                updateStatus();
            }
        } else {

            updateStatus();
        }
        return result;

    }

    int setUserInfo() {
        Environment.getExternalStorageDirectory();
        LogPath = Environment.getExternalStorageDirectory().getAbsolutePath() + '/';

        String localIP = new Network(context).getLocalIP(false);// ipv4
        int localPort = new Random().nextInt(4940) + 5060;
        UserInfo info = saveUserInfo();

        if (info.isAvailable()) {
            Log.e("info available", "info available");
            mSipSdk.CreateCallManager(context.getApplicationContext());// step 1

            int result = mSipSdk.initialize(info.getTransType(),
                    PortSipEnumDefine.ENUM_LOG_LEVEL_NONE, LogPath,
                    Line.MAX_LINES, " UTTERU ANDROID ",
                    0, 0);// step 2
            Log.e("sdk state", "" + result);
            if (result != PortSipErrorcode.ECoreErrorNone) {

                return result;
            }


            Log.e("setting codec", "setting codec ");


            int nSetKeyRet = mSipSdk.setLicenseKey(licenseKey);// step 3
            if (nSetKeyRet == PortSipErrorcode.ECoreTrialVersionLicenseKey) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Prompt").setMessage(R.string.trial_version_tips);
                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            } else if (nSetKeyRet == PortSipErrorcode.ECoreWrongLicenseKey) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Prompt").setMessage(R.string.wrong_lisence_tips);
                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                return -1;
            }
            Log.e("all is well ", "going for registration");

            result = mSipSdk.setUser(info.getUserName(), info.getUserDisplayName(), info.getAuthName(), info.getUserPassword(),

                    localIP, localPort, info.getUserdomain(), info.getSipServer(), info.getSipPort(),
                    info.getStunServer(), info.getStunPort(), null, 5060);// step 4

            Log.e("sent registration state", "" + result);
            if (result != PortSipErrorcode.ECoreErrorNone) {
                statuString = "setUser resource sucess";
                return result;
            } else {


            }
        } else {
            Log.e("info not available", "info not available");
            return -1;
        }

        SettingConfig.setAVArguments(context, mSipSdk);
        return PortSipErrorcode.ECoreErrorNone;
    }
}
