package com.Utteru.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.Prefs;
import com.splunk.mint.Mint;

/**
 * Created by root on 11/29/14.
 */
public class SplashScreen extends Activity {

    private static int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        Mint.initAndStartSession(SplashScreen.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(SplashScreen.this));
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent localIntent = new Intent(SplashScreen.this, SignUpHome.class);
                SplashScreen.this.startActivity(localIntent);
                SplashScreen.this.finish();
            }
        }
                , SPLASH_TIME_OUT);
    }

}
