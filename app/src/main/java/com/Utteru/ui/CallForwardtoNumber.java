package com.Utteru.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Utteru.R;
import com.Utteru.adapters.ManageVerifiedDataListAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.RecentCallsDto;
import com.Utteru.dtos.VerifiedData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by walkover on 26/2/15.
 */
public class CallForwardtoNumber extends Activity {
    //View manage_numbers;
    ManageVerifiedDataListAdapter adapter;
    Context ctx;
    ListView listview;
    String number;
    ArrayList<VerifiedData> datalist;
    FontTextView tvNotIntrested, tvAddotherNum;
    ImageView gotomenu;
    ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callforwardtonumber);
        init();
        new getAllNumbers().execute();
    }

    @Override
    public void onResume() {


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //number = parent.getItemAtPosition(position).toString();
                number = ((TextView) parent
                        .findViewById(R.id.data_text)).getText().toString();

                new AsignForwarding().execute();


            }
        });
        tvAddotherNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menu = new Intent(CallForwardtoNumber.this, ManageNumbersHome.class);
                startActivity(menu);
                menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                CallForwardtoNumber.this.finish();

            }
        });
        tvNotIntrested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menu = new Intent(CallForwardtoNumber.this, CallForwardActivity.class);
                startActivity(menu);
                menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                CallForwardtoNumber.this.finish();
            }
        });
        gotomenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menu = new Intent(CallForwardtoNumber.this, MenuScreen.class);
                startActivity(menu);
                menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                CallForwardtoNumber.this.finish();
            }
        });
        super.onResume();
    }


    void init() {
        dialog = new ProgressDialog(CallForwardtoNumber.this, R.style.MyTheme);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(true);

        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
      /*  nothing_found_text = (FontTextView) findViewById(R.id.nothing_found_text);
        nothing_found_text.setText(getResources().getString(R.string.no_number_found));*/
        ctx = CallForwardtoNumber.this.getBaseContext();
        listview = (ListView) findViewById(R.id.twc_list);
        datalist = new ArrayList<VerifiedData>();
        tvAddotherNum = (FontTextView) findViewById(R.id.tv_add_number);
        tvNotIntrested = (FontTextView) findViewById(R.id.donot_wish);
        gotomenu = (ImageView) findViewById(R.id.contacts_home);
    }

    @Override
    public void onStop() {


        super.onStop();
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
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

    class getAllNumbers extends AsyncTask<Void, Void, Void> {

        String response;
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {


            if (datalist != null && datalist.size() > 0) {
                listview.setVisibility(View.VISIBLE);
                adapter = new ManageVerifiedDataListAdapter(datalist, ctx);
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                listview.invalidateViews();
                Log.e("setting data", "setting data");
            } else
                listview.setVisibility(View.GONE);
            Log.e("got  list", "got list");

            dialog.dismiss();

            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.e("getting list", "getting list");
            datalist = Prefs.getVerifiedNumberList(ctx);

            return null;
        }

    }

    class AsignForwarding extends AsyncTask<Void, Void, Void> {

        String response = null;
        Boolean iserr = false;


        @Override
        protected void onPostExecute(Void result) {

            if (iserr)
                showErrorMessage(true, response);
            else {


            }
            if (CommonUtility.dialog != null)
                CommonUtility.dialog.dismiss();
            number = number.replace("+", "");
            DialogSuccess d = new DialogSuccess(CallForwardtoNumber.this,number);
            d.show();
            Window window = d.getWindow();
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            showErrorMessage(false, "");
//            CommonUtility.show_PDialog(ctx, getResources().getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String type = String.valueOf(1);
            number = number.replace("+", "");
            response = Apis.getApisInstance(ctx).assigndestinationnumber(number, type);

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
}



