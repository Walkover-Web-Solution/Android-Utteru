package com.Utteru.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.Utteru.R;
import com.Utteru.adapters.AccessDataAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessDataDto;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SelectCountryCallForward extends ActionBarActivity {


    Context ctx;
    static ArrayList<AccessDataDto> countryList;
    AccessDataAdapter adapter;
    ListView listView;
    AccessDataDto selected_dto;
    ImageView back_button, gotohome;
    FontTextView header;
    FontTextView error_FontTextView;
    String country = null;
    RelativeLayout error_layout;
    Button close_em_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_layout);
        init();
        Mint.initAndStartSession(SelectCountryCallForward.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(SelectCountryCallForward.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(ctx, SelectStateCallForward.class);
                selected_dto = countryList.get(position);

                intent.putExtra(VariableClass.Vari.SELECTEDDATA, selected_dto);

                startActivity(intent);
             /*   error_layout.setOnClickListener(new View.OnClickListener() {

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
*/
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        gotohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectCountryCallForward.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        super.onResume();
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_detail_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }*/

    void init() {

        ctx = this;

        back_button = (ImageView) findViewById(R.id.contacts_back);
        gotohome = (ImageView) findViewById(R.id.contacts_home);
        header = (FontTextView) findViewById(R.id.contact_header);
        header.setText("Select Country");
        listView = (ListView) findViewById(R.id.contacts_list);
        countryList = new ArrayList<>();
        Log.e("calling allCountries", "Calling");
        error_FontTextView = (FontTextView) findViewById(R.id.error_text);
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        close_em_button = (Button) findViewById(R.id.close_button);
        new AllCountries().execute();

    }

    class AllCountries extends AsyncTask<Void, Void, Void> {

        String response = null;
        Boolean iserr = false;


        @Override
        protected void onPostExecute(Void result) {

            if (iserr)
                showErrorMessage(true, response);
            else {

                if (countryList.size() > 0) {
                    Log.e("Country dta ", "geting country list ");
                    listView.setVisibility(View.VISIBLE);
                    adapter = new AccessDataAdapter(countryList, SelectCountryCallForward.this, 0);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetInvalidated();
                    listView.invalidateViews();

                } else {
                    listView.setVisibility(View.GONE);
                }


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


            response = Apis.getApisInstance(ctx).getCountrylist();

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

                        AccessDataDto countrydto, statedto;

                        ArrayList<AccessDataDto> statelist;

                        for (int i = 0; i < japarent.length(); i++) {
                            countrydto = new AccessDataDto();
                            jochild = japarent.getJSONObject(i);
                            countrydto.setCountry(jochild.getString(VariableClass.ResponseVariables.COUNTRY));

                            jachild = jochild.getJSONArray(VariableClass.ResponseVariables.STATE);
                            statelist = new ArrayList<AccessDataDto>();

                            for (int j = 0; j < jachild.length(); j++) {

                                statedto = new AccessDataDto();

                                statedto.setState(jachild.getString(j));
                                statedto.setCountry(countrydto.getCountry());
                                statelist.add(statedto);
                            }

                            countrydto.setStatelist(statelist);
                            Log.e("state", countrydto.getStatelist().toString());
                            countryList.add(countrydto);
                            Log.e("list", "adding country" + countrydto.getCountry());
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

    @Override
    protected void onStop() {
        super.onStop();
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
    }
}
