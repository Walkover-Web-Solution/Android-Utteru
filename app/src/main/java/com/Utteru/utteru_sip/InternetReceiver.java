package com.Utteru.utteru_sip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.service.PortSipService;

/**
 * Created by vikas on 24/02/15.
 */
public class InternetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(CommonUtility.isNetworkAvailable(context))
        {
            if(!CommonUtility.isMyServiceRunning(PortSipService.class, context))
            {
                context.startService(new Intent(context,PortSipService.class));
            }


        }

    }
}
