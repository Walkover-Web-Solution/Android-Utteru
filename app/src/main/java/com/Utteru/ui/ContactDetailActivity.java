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
import android.support.v7.app.ActionBarActivity;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessContactDto;
import com.Utteru.util.Utils;
import com.splunk.mint.Mint;


public class ContactDetailActivity extends ActionBarActivity {
    // Defines a tag for identifying the single fragment that this activity holds
    private static final String TAG = "ContactDetailActivity";

    @Override
    protected void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
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


        if (getIntent().getExtras().containsKey(VariableClass.Vari.SOURCECLASS)) {

            this.finish();

        } else {

//            startActivity(new Intent(ContactDetailActivity.this, ContactsListActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            super.onBackPressed();
            this.finish();
            overridePendingTransition(R.anim.animation3, R.anim.animation4);
        }
    }
}
