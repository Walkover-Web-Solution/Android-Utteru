package com.Utteru.ui;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.Utteru.R;
import com.Utteru.adapters.LongRecentLogsAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.LongCodesDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class LongCodeLogsActivity extends Activity {


    LongRecentLogsAdapter adapter;
    LongCodesDto selectedto;
    Context ctx;
    ImageView backpress, gototohome;
    FontTextView tittleback;

    ListView longlogs_listview;
    ProgressDialog dialog;
    ArrayList<LongCodesDto> datalist;
    // Store instance variables
    private String title;
    private int page;


    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = LongCodeLogsActivity.this;
        setContentView(R.layout.recent_longcode);
        longlogs_listview = (ListView) findViewById(R.id.search_list);
        backpress = (ImageView) findViewById(R.id.auto_detect_country_back);
        gototohome = (ImageView) findViewById(R.id.auto_detect_country_home);
        tittleback = (FontTextView) findViewById(R.id.auto_detect_coutry_header);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedto = (LongCodesDto) bundle.getSerializable(VariableClass.Vari.SELECTEDDATA);
            Log.e("set selected data  dto ", "" + selectedto.getLongCodeNo());
        }
        new getAllLogs().execute();


    }

    @Override
    public void onResume() {
        super.onResume();
        tittleback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();


            }
        });
        gototohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }


    class getAllLogs extends AsyncTask<Void, Void, Void> {

        String response = null;
        Boolean iserr = false;


        @Override
        protected void onPostExecute(Void result) {

            if (iserr)
                showErrorMessage(true, response);
            else {

                if (datalist.size() > 0) {
                    Log.e("logs ", "logs list updating ");
                    longlogs_listview.setVisibility(View.VISIBLE);
                    adapter = new LongRecentLogsAdapter(datalist, ctx);
                    longlogs_listview.setAdapter(adapter);
                    adapter.notifyDataSetInvalidated();
                    longlogs_listview.invalidateViews();

                } else {
                    longlogs_listview.setVisibility(View.GONE);
                }


            }
//            CommonUtility.dialog.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            //  showErrorMessage(false, "");
            // CommonUtility.show_PDialog(ctx, getResources().getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.e("LONGCODE", selectedto.getLongCodeNo());

            response = Apis.getApisInstance(ctx).getlonglogs(selectedto.getLongCodeNo());

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
                        japarent = joparent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                        //  joparent= japarent.getJSONObject();
                        LongCodesDto longCodesDto, senddto;

                        ArrayList<LongCodesDto> statelist;
                        datalist = new ArrayList<>();

                        for (int i = 0; i < japarent.length(); i++) {
                            longCodesDto = new LongCodesDto();
                            jochild = japarent.getJSONObject(i);
                            longCodesDto.setDuration(jochild.getString(VariableClass.ResponseVariables.DURATION_LOGS));
                            longCodesDto.setDate_time(jochild.getString(VariableClass.ResponseVariables.DATE_TIME));
                            longCodesDto.setBal_deduct(jochild.getString(VariableClass.ResponseVariables.BALANCE_DEDUCT));
                            longCodesDto.setCallerid(jochild.getString(VariableClass.ResponseVariables.CALLER_ID));
                            Prefs.setAssignId(ctx, longCodesDto.getAssignId());

                            datalist.add(longCodesDto);
                            //  Log.e("list", "adding country" + longCodesDto.getLongCodeNo());


                        }


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
    public void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
    }
}