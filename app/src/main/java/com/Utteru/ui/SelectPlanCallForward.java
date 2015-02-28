package com.Utteru.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessDataDto;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by walkover on 26/2/15.
 */


public class SelectPlanCallForward extends ActionBarActivity {


    Context ctx;

    AccessDataDto selectedDto;
    ImageView back_button, gotohome;
    FontTextView titile, subtitile, tvCurrency, tvCost;
    Button mGetPlan;
    String currency, planrate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_plan_callforward);
        init();
        Mint.initAndStartSession(SelectPlanCallForward.this, CommonUtility.BUGSENSEID);
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
        subtitile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        gotohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectPlanCallForward.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        mGetPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Double.parseDouble(planrate) < Double.parseDouble(Prefs.getUserBalanceAmount(ctx))) {
                    DialogConfirm d = new DialogConfirm(SelectPlanCallForward.this);
                    d.show();
                    Window window = d.getWindow();
                    window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                } else {
                    DialogGotoRecharge d = new DialogGotoRecharge(SelectPlanCallForward.this);
                    d.show();
                    Window window = d.getWindow();
                    window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
            }
        });
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.contact_detail_menu, menu);

        super.onCreateOptionsMenu(menu);
        return true;
    }

    void init() {

        ctx = this;
        selectedDto = (AccessDataDto) getIntent().getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
        back_button = (ImageView) findViewById(R.id.contacts_back);
        gotohome = (ImageView) findViewById(R.id.contacts_home);
        titile = (FontTextView) findViewById(R.id.contact_header);
        subtitile = (FontTextView) findViewById(R.id.contacts_subtitle);
        tvCurrency = (FontTextView) findViewById(R.id.user_currency);
        tvCost = (FontTextView) findViewById(R.id.user_call_rate);
        titile.setText("Call Rates");
        subtitile.setVisibility(View.VISIBLE);
        subtitile.setText(selectedDto.getCountry() + " , " + selectedDto.getState());

        mGetPlan = (Button) findViewById(R.id.btn_getplan);
        new AllPlans().execute();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animation3, R.anim.animation4);
    }

    @Override
    protected void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onDestroy();
    }

    class AllPlans extends AsyncTask<Void, Void, Void> {

        String response = null;
        Boolean iserr = false;


        @Override
        protected void onPostExecute(Void result) {

            if (iserr)
                showErrorMessage(true, response);
            else {


                tvCurrency.setText(currency);
                tvCost.setText(planrate);


            }
            if (CommonUtility.dialog != null)
                CommonUtility.dialog.dismiss();

            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            showErrorMessage(false, "");
            CommonUtility.show_PDialog(ctx, getResources().getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {


            response = Apis.getApisInstance(ctx).getPlan(selectedDto.getCountry(), selectedDto.getState());

            if (!response.equals("")) {
                JSONObject joparent = null;
                JSONObject jochild = null;
                JSONArray japarent = null;
                JSONArray jachild = null;
                try {
                    joparent = new JSONObject(response);
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserr = true;
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }
                    //success response
                    else if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                        jachild = joparent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                        jochild = jachild.getJSONObject(0);
                        currency = null;
                        planrate = null;
                        String planid = null;
                        currency = jochild.getString(VariableClass.ResponseVariables.CURRENCY);
                        planrate = jochild.getString(VariableClass.ResponseVariables.PLAN_RATE);
                        planid = jochild.getString(VariableClass.ResponseVariables.PLAN_ID);
                        Prefs.setPlanId(ctx, planid);
                        Prefs.setPlanRate(ctx, planrate);

                    }
                } catch (JSONException e) {
                    iserr = true;
                    response = getResources().getString(R.string.parse_error);
                    e.printStackTrace();
                }

            } else {
                iserr = true;
                response = getResources().getString(R.string.server_error);
            }
            return null;
        }

    }

    void showErrorMessage(Boolean showm, String message) {
      /*  if (showm) {
            error_FontTextView.setText(message);
            if (error_layout.getVisibility() == View.GONE)
                CommonUtility.expand(error_layout);
        } else {
            if (error_layout.getVisibility() == View.VISIBLE)
                CommonUtility.collapse(error_layout);

        }*/
    }


    @Override
    protected void onStop() {

        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onStop();
    }
}

