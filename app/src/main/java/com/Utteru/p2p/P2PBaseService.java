package com.Utteru.p2p;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.Utteru.commonUtilities.CommonUtility;

/**
 * Created by vikas on 24/02/15.
 */
public abstract class P2PBaseService extends Service implements ServiceConnection {

    private SinchService.SinchServiceInterface mSinchServiceInterface;

    @Override
    public void onCreate() {

        Log.e("P2P Base  on create ", "P2P base on Create");
        if(CommonUtility.isNetworkAvailable(this)) {

            getApplicationContext().bindService(new Intent(this, SinchService.class), this,
                    BIND_AUTO_CREATE);
        }
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }



    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.e("component name ", "" + componentName.getClassName());
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
            onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceConnected() {
        // for subclasses
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }

    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }




}
