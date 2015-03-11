/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.Utteru.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.BuildConfig;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessContactDto;
import com.Utteru.p2p.CallScreenActivity;
import com.Utteru.p2p.SinchService;
import com.Utteru.util.Utils;
import com.sinch.android.rtc.calling.Call;
import com.splunk.mint.Mint;


public class ContactDetailActivity extends com.Utteru.p2p.BaseActivity implements  ContactDetailFragment.callUser {
    // Defines a tag for identifying the single fragment that this activity holds
    private static final String TAG = "ContactDetailActivity";

    @Override
    protected void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }

//        stopButtonClicked();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Mint.initAndStartSession(ContactDetailActivity.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ContactDetailActivity.this));

        if (BuildConfig.DEBUG) {
            // Enable strict mode checks when in debug modes
            Utils.enableStrictMode();
        }
        if (getIntent() != null) {
            final AccessContactDto accessContactDto = (AccessContactDto) getIntent().getSerializableExtra(VariableClass.Vari.SELECTEDDATA);
            if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
                final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(android.R.id.content, ContactDetailFragment.newInstance(accessContactDto), TAG);
                ft.commit();
            }
        } else {
            finish();
        }

    }

    @Override
    public void onBackPressed() {


        if(getIntent().getExtras().containsKey(VariableClass.Vari.SOURCECLASS)){

           this.finish();

        }
        else {

            super.onBackPressed();
            this.finish();
            overridePendingTransition(R.anim.animation3, R.anim.animation4);
        }
    }
    public void stopButtonClicked() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        finish();
    }

    public void callButtonClicked(String number ) {
        String userName = number.replace("+","");
        userName = userName.replace("\\s+","");
        userName = userName.replace(" ","");

       Log.e("calling number ",""+number);
        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a user to call", Toast.LENGTH_LONG).show();
            return;
        }

        Call call = getSinchServiceInterface().callUser(userName);
        if(call!=null) {
            String callId = call.getCallId();

            Intent callScreen = new Intent(this, CallScreenActivity.class);
            callScreen.putExtra(SinchService.CALL_ID, callId);
            startActivity(callScreen);
        }
        else{
            CommonUtility.showCustomAlertForContactsError(this, "Not able to initiate call");
        }
    }

    @Override
    public void onCall(int action, AccessContactDto dto) {

        if(action==0)
        {
            callButtonClicked(dto.getMobile_number());
        }
    }

}
