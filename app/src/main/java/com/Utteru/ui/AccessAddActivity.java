package com.Utteru.ui;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.Constants;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessContactDto;
import com.splunk.mint.Mint;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Arpit on 31-10-2014.
 */
public class AccessAddActivity extends ActionBarActivity {

    public String jsonStr, mName, mobileNumber = null, accessNumber = null, hash = null, extNo = null, access = null, contactId = null, countryCode, countryName, stateName;
    ArrayAdapter<String> adapter;
    Context ctx;
    Button assignButton;
    FontTextView textName, textAccess, textExt;
    LinearLayout relativeLayout;
    ImageButton typeSwitch;
    AccessContactDto selected_con;
    ImageView back_button,gotohome;
    FontTextView titile,subtitile;


    @Override
    protected void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:

                startActivity(new Intent(AccessAddActivity.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_detail_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type_list_layout);
        init();
        Mint.initAndStartSession(AccessAddActivity.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));



    }

    @Override
    protected void onResume() {

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        titile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        gotohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotohome = new Intent(ctx,MenuScreen.class);
                gotohome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(gotohome);
            }
        });
        super.onResume();
    }

    void parser() {
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                String response = jsonObj.getString("response");
                JSONObject message1 = jsonObj.getJSONObject("message");
                String message = message1.getString("message");
                // Getting JSON Array node
                if (response.equals(Apis.SuccessResponse)) {
                    CommonUtility.showCustomAlertForContacts(getApplication(),message);

                } else {
                    CommonUtility.showCustomAlertError(AccessAddActivity.this,ctx.getResources().getString(R.string.internet_error));
                  CommonUtility.showCustomAlertForContactsError(getApplication(),message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(AccessAddActivity.this, ContactsListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            overridePendingTransition(R.anim.animation3, R.anim.animation4);

        } else {
            CommonUtility.showCustomAlertError(AccessAddActivity.this,ctx.getResources().getString(R.string.internet_error));
            startActivity(new Intent(AccessAddActivity.this, ContactsListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            overridePendingTransition(R.anim.animation3, R.anim.animation4);
        }
        Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, ContactsContract.AUTHORITY, bundle);
    }

    void init() {
        ctx = this;
        Intent myIntent = getIntent();
        selected_con = (AccessContactDto)getIntent().getSerializableExtra(VariableClass.Vari.SELECTEDDATA);
        mName = selected_con.getDisplay_name();
        contactId = selected_con.getContact_id();
        mobileNumber = selected_con.getMobile_number();
        accessNumber = selected_con.getAccess_number();
        hash =selected_con.getExtension_number();
        countryCode = selected_con.getCode();
        countryName = selected_con.getCountry();
        access = selected_con.getAccess_number();
        extNo =selected_con.getExtension_number();
        stateName =selected_con.getState();

        back_button = (ImageView)findViewById(R.id.contacts_back);
        gotohome =(ImageView)findViewById(R.id.contacts_home);
        titile = (FontTextView)findViewById(R.id.contact_header);
        subtitile = (FontTextView)findViewById(R.id.contacts_subtitle);
        subtitile.setText(countryName + ", " + stateName);

        relativeLayout = (LinearLayout) findViewById(R.id.assign_layout);
        assignButton = (Button) findViewById(R.id.assign_button);
        typeSwitch = (ImageButton) findViewById(R.id.type_switch);
        textName = (FontTextView) findViewById(R.id.access_name);
        textAccess = (FontTextView) findViewById(R.id.access_number);
        textExt = (FontTextView) findViewById(R.id.acces_ext);
        assignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    new AssignAccessNumber().execute();

                } else
                    CommonUtility.showCustomAlertError(AccessAddActivity.this,ctx.getResources().getString(R.string.internet_error));

            }
        });
        relativeLayout.setVisibility(View.VISIBLE);
        typeSwitch.setVisibility(View.GONE);
        textName.setText(access);
        textAccess.setText(mName);


        if(!extNo.equals("100"))
        {

            textExt.setText(" " + extNo + " ");
        }
        else{
            textExt.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animation3, R.anim.animation4);
    }

    class AssignAccessNumber extends AsyncTask<Void, Void, Void> {
        Boolean iserror = false;
        String response = "";

        @Override
        protected void onPreExecute() {
            CommonUtility.show_PDialog(ctx, getString(R.string.please_wait));
            assignButton.setEnabled(false);

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String name = CommonUtility.validateText(selected_con.getDisplay_name());


           String number = CommonUtility.validateNumberForApi(selected_con.getMobile_number());



            if (number.startsWith("00"))
                number = number.replaceFirst("00", "");



            response = Apis.getApisInstance(ctx).addContact(name, "", number, selected_con.getAccess_number(), selected_con.getExtension_number());


            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;
                try {
                    joparent = new JSONObject(response);
                    //failed response
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                        iserror = true;
                    } else {
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }


                } catch (JSONException e) {
                    iserror = true;
                    response = getResources().getString(R.string.parse_error);
                    e.printStackTrace();
                }
            } else {
                iserror = true;
                response = getResources().getString(R.string.server_error);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            assignButton.setEnabled(true);
            if (iserror) {
               CommonUtility.showCustomAlertForContactsError(ctx,response);
            } else {


                Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
                Bundle bundle = new Bundle();
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                ContentResolver.requestSync(account, ContactsContract.AUTHORITY, bundle);
                CommonUtility.showCustomAlertForContacts(getApplicationContext(), response);
              /*  Intent start = new Intent(ctx, this.class);
                start.putExtra(VariableClass.Vari.SELECTEDDATA, selected_con);
                start.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(start);*/
                overridePendingTransition(R.anim.animation3, R.anim.animation4);
            }
            CommonUtility.dialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }
}
