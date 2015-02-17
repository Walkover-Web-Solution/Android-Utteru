package com.Utteru.ui;

import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.Constants;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessContactDto;
import com.splunk.mint.Mint;
import com.stripe.android.compat.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;


public class AssignAccessAI extends Activity {

    FontTextView country_txt, state_txt, access_txt, extension_txt;
    Button assign_button;
    Context ctx = this;
    AccessContactDto contactDto;
    FontTextView header;
    RelativeLayout error_layout;
    FontTextView error_FontTextView;
    Button close_em_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assign_acess_ai);
        init();
        Mint.initAndStartSession(AssignAccessAI.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));



    }

    @Override
    protected void onResume() {

        assign_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtility.isNetworkAvailable(ctx)) {

                    showErrorMessage(false, "");
                    new AssignAccessNumber().execute();


                } else {
                    showErrorMessage(true, getResources().getString(R.string.internet_error));
                }


            }
        });
        error_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
            }
        });
        close_em_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showErrorMessage(false, "");
            }
        });
        super.onResume();
    }

    void init() {
        country_txt = (FontTextView) findViewById(R.id.country_info);
        header = (FontTextView) findViewById(R.id.title);
        state_txt = (FontTextView) findViewById(R.id.state_info);
        access_txt = (FontTextView) findViewById(R.id.access_info);
        extension_txt = (FontTextView) findViewById(R.id.extention_info);
        assign_button = (Button) findViewById(R.id.assign_access_button);
        error_FontTextView = (FontTextView) findViewById(R.id.error_text);
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        close_em_button = (Button) findViewById(R.id.close_button);
        contactDto = (AccessContactDto) getIntent().getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);

        country_txt.setText(contactDto.getCountry());
        state_txt.setText(contactDto.getState());
        access_txt.setText(contactDto.getAccess_number());
        if (!contactDto.getExtension_number().equals(VariableClass.Vari.DEDICATED))
            extension_txt.setText(extension_txt.getText() + " : " + contactDto.getExtension_number());
        else
            extension_txt.setText("Dedicated");

        header.setText(contactDto.getDisplay_name());


    }

    void showErrorMessage(Boolean showm, String message) {
        if (showm) {
            error_FontTextView.setText(message);
            if (error_layout.getVisibility() == View.GONE)
                CommonUtility.expand(error_layout);
        } else {
            if (error_layout.getVisibility() == View.VISIBLE)
                CommonUtility.collapse(error_layout);

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
            assign_button.setEnabled(false);

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String name = CommonUtility.validateText(contactDto.getDisplay_name());

            String number = CommonUtility.validateNumberForApi(contactDto.getMobile_number());




            response = Apis.getApisInstance(ctx).addContact(name, "", number, contactDto.getAccess_number(), contactDto.getExtension_number());


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

            assign_button.setEnabled(true);
            if (iserror) {
                showErrorMessage(true, response);
            } else {


                Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
                Bundle bundle = new Bundle();
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                ContentResolver.requestSync(account, ContactsContract.AUTHORITY, bundle);


                CommonUtility.showCustomAlertForContacts(getApplicationContext(), response);
//
//                Intent start = new Intent(ctx, AllCountryActivity.class);
//
//                start.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(start);
//                overridePendingTransition(R.anim.animation3, R.anim.animation4);
            }
            CommonUtility.dialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }
}
