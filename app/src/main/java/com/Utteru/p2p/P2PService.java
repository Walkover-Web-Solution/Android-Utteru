package com.Utteru.p2p;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.Utteru.commonUtilities.Prefs;
import com.sinch.android.rtc.SinchError;

/**
 * Created by vikas on 24/02/15.
 */
public class P2PService extends P2PBaseService implements SinchService.StartFailedListener {


    @Override
    public void onCreate() {
        Log.e("P2P   on create ","P2P  on Create");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStarted() {
        //registered on p2p
        Log.e("service started ", "service started");




    }
    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        Log.e("service start fail   ","service start fail ");

    }
    @Override
    protected void onServiceConnected() {

        Log.e("service connected ","service connected");
        getSinchServiceInterface().setStartListener(this);
        loginClicked();


    }

    private void loginClicked() {

        String userName = Prefs.getUserDefaultNumber(this);

        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
            return;
        }

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(userName);

        } else {
            Log.e("already user registered ","user already registered");
        }
    }

}
