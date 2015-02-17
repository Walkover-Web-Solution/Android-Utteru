/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.Utteru.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.Utteru.R;
import com.Utteru.adapters.EmailListAdapter;
import com.Utteru.commonUtilities.Prefs;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.splunk.mint.Mint;

import java.util.ArrayList;
import java.util.Arrays;


public class HelloActivity extends Activity {
    //    https://www.googleapis.com/auth/userinfo.profile
//    https://www.googleapis.com/auth/userinfo.email
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;
    static final String TOKEN = "token";
    static final String ERROR_MESSAGE = "error_message";
    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.email";
    IntentFilter filter;
    ListView emailListview;
    EditText search_bar;
    ArrayList<String> emailList;
    EmailListAdapter listadapter;
    String token;
    LayoutInflater inflate;
    String error_messge_string;
    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchable_list);
        Mint.initAndStartSession(HelloActivity.this, "395e969a");
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(HelloActivity.this));
        emailListview = (ListView) findViewById(R.id.search_list);

        search_bar = (EditText) findViewById(R.id.search_bar);
        search_bar.setVisibility(View.GONE);
        inflate = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup header_view = (ViewGroup) inflate.inflate(R.layout.email_list_header, emailListview,
                false);
        emailListview.addHeaderView(header_view, null, false);
        emailList = getAccountNames();

        if (emailList.size() == 0)
            this.finish();
        listadapter = new EmailListAdapter(emailList, HelloActivity.this);
        emailListview.setAdapter(listadapter);
        filter = new IntentFilter("ACTION_TOKEN_RECEIVED");

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        emailListview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                mEmail = emailList.get(arg2 - 1);//because of header
                new GetNameInForeground(HelloActivity.this, mEmail, SCOPE).execute();
            }
        });
        super.onResume();
    }

    public void show(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void handleException(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    error_messge_string = "Install Google Play Service";
                } else if (e instanceof UserRecoverableAuthException) {
                    Intent intent = ((UserRecoverableAuthException) e).getIntent();
                    startActivityForResult(intent,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    overridePendingTransition(R.anim.animation1, R.anim.animation2);
                }
            }
        });
    }

    private ArrayList<String> getAccountNames() {
        AccountManager mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);

        String[] names = new String[accounts.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = accounts[i].name;
        }
        emailList = new ArrayList<String>(Arrays.asList(names));
        return emailList;
    }

    @Override
    public void finish() {
        Log.e("token in on finish", "" + token);
        Intent data = new Intent();
        if (token != null && !token.equals(""))
            data.putExtra(TOKEN, token);
        if (error_messge_string != null && !error_messge_string.equals(""))
            data.putExtra(ERROR_MESSAGE, error_messge_string);
        setResult(SignInScreen.RESULT_OK, data);
        super.finish();
    }

}