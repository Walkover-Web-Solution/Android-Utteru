package com.Utteru.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.Utteru.R;
import com.Utteru.adapters.SearchListDialerAdapter;
import com.Utteru.adapters.SearchRateListAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.CustomKeyboardSearchRate;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.Country;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class SearchRateActivity extends BaseActivity {

    public static final int REQUEST_CODE = 01;
    ListView search_rate_listview;
    SearchRateListAdapter adapter;
    ArrayList<Country> list;
    Context ctx;
    FontTextView tittleback;
    String country;
    CustomKeyboardSearchRate keyboard;
    AutoCompleteTextView search_country_ed;
    FontTextView nothing_found_text;
    LinearLayout dialpad_layout;
    FontTextView error_FontTextView;
    RelativeLayout error_layout;
    Button close_em_button;
    CountDownTimer timer;
    ImageView backpress, gototohome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_rate_layout);
        init();
        Mint.initAndStartSession(SearchRateActivity.this, "395e969a");
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));



    }

    @Override
    protected void onStart() {
        Log.e("on start", "on start");
        super.onStart();
    }

    @Override
    protected void onResume() {
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
                startActivity(new Intent(SearchRateActivity.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        search_rate_listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (dialpad_layout.getVisibility() == View.VISIBLE) {
                    showKeyBoard(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        search_rate_listview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
                return false;
            }
        });
        search_country_ed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showKeyBoard(true);
                return false;
            }
        });

        search_country_ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {


                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                timer = new CountDownTimer(1500, 1000) {

                    public void onTick(long millisUntilFinished) {


                    }

                    public void onFinish() {

                        searchRate(s.toString());
                    }
                }
                        .start();

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

    public void showKeyBoard(Boolean showKeyBoard) {
        //show keyboard
        if (showKeyBoard) {

            Animation bottomUp = AnimationUtils.loadAnimation(this,
                    R.anim.bottom_up);
            Animation bottom_down = AnimationUtils.loadAnimation(this,
                    R.anim.abc_slide_in_top);

            dialpad_layout.setAnimation(bottomUp);
            dialpad_layout.setVisibility(View.VISIBLE);


            search_rate_listview.setClickable(false);
            search_rate_listview.setFocusable(false);


        }
        //hide keyboard
        else {
            Animation bottpmdown = AnimationUtils.loadAnimation(this,
                    R.anim.bottom_down);
            Animation bottomup = AnimationUtils.loadAnimation(this,
                    R.anim.abc_slide_out_top);

            dialpad_layout.setAnimation(bottpmdown);
            dialpad_layout.setVisibility(View.GONE);


            search_rate_listview.setClickable(true);
            search_rate_listview.setFocusable(true);

            search_rate_listview.requestFocus();

            //     calllogs_listview.addHeaderView(listHeaderView);


        }
    }


    void init() {
        tittleback = (FontTextView) findViewById(R.id.auto_detect_coutry_header);
        backpress = (ImageView) findViewById(R.id.auto_detect_country_back);
        gototohome = (ImageView) findViewById(R.id.auto_detect_country_home);
        gototohome.setVisibility(View.GONE);
        error_FontTextView = (FontTextView) findViewById(R.id.error_text);
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        close_em_button = (Button) findViewById(R.id.close_button);

        dialpad_layout = (LinearLayout) findViewById(R.id.dialpad_layout);
        nothing_found_text = (FontTextView) findViewById(R.id.search_nothing_found_text);
        nothing_found_text.setVisibility(View.GONE);
        Log.e("search rate init", "search rate init");
        SearchListDialer.country_list = new ArrayList<Country>();
        SearchListDialer.country_list = new CsvReader().readCsv(SearchRateActivity.this, new CsvReader().getUserCountryIso(SearchRateActivity.this), false);

        search_rate_listview = (ListView) findViewById(R.id.search_rate_list);
        list = new ArrayList<Country>();
        ctx = this;
        search_country_ed = (AutoCompleteTextView) findViewById(R.id.search_rate_country_ed);
        SearchListDialerAdapter adapter = new SearchListDialerAdapter(SearchListDialer.country_list, this);
        search_country_ed.setAdapter(adapter);

        keyboard = new CustomKeyboardSearchRate(SearchRateActivity.this, R.id.keyboardview, R.xml.numberic_keypad_other, null);
        keyboard.registerEditText(search_country_ed.getId(), null);
//        SearchListActivity.country_list = new ArrayList<Country>();
//        SearchListActivity.country_list = new CsvReader().readCsv(ctx, new CsvReader().getUserCountry(ctx), false);
//        country = Prefs.getUserCountryName(ctx);


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
    protected void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Prefs.setLastActivity(ctx, getClass().getName());

    }

    @Override
    public void onBackPressed() {
        if (dialpad_layout.getVisibility() == View.VISIBLE) {
            showKeyBoard(false);
            Log.e("hiding  keyboard", "hiding keyboard");

        } else {
            Intent menu = new Intent(this, MenuScreen.class);
            menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(menu);
            // super.onBackPressed();
            overridePendingTransition(R.anim.animation3, R.anim.animation4);
        }
    }

    class SearchRate extends AsyncTask<Void, Void, Void> {

        String response = null;
        Boolean iserr = false;


        @Override
        protected void onPostExecute(Void result) {

            if (iserr)
                showErrorMessage(true, response);
            else {

                if (list.size() > 0) {
                    Log.e("search rate ", "search rate list updating ");
                    search_rate_listview.setVisibility(View.VISIBLE);
                    adapter = new SearchRateListAdapter(list, ctx);
                    search_rate_listview.setAdapter(adapter);
                    adapter.notifyDataSetInvalidated();
                    search_rate_listview.invalidateViews();

                } else {
                    search_rate_listview.setVisibility(View.GONE);
                }


            }
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



            response = Apis.getApisInstance(ctx).getPricing(country);

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
                        joparent = japarent.getJSONObject(0);

                        Country header, row;

                        //getting currency
                        String currency = null;
                        jachild = joparent.getJSONArray(VariableClass.ResponseVariables.STATUS);
                        jochild = jachild.getJSONObject(0);
                        currency = jochild.getString(VariableClass.ResponseVariables.CURRENCY);
                        joparent.remove(VariableClass.ResponseVariables.STATUS);

                        //get keys and pricing
                        Iterator keys = joparent.keys();
                        String key_string = null;
                        int count = 0;
                        list.clear();
                        while (keys.hasNext()) {
                            key_string = (String) keys.next();
                            jachild = joparent.getJSONArray(key_string);
                            count = jachild.length();
                            header = new Country();
                            header.setCountryName(key_string);
                            header.setCurrency(currency);
                            header.setIsSection(true);
                            list.add(header);
                            for (int i = 0; i < count; i++) {
                                jochild = jachild.getJSONObject(i);
                                row = new Country();
                                String country = jochild.getString(VariableClass.ResponseVariables.OPERATOR);
                                if (country.equals(""))
                                    country = "Other";
                                row.setCountryName(country);
                                row.setPrice(jochild.getString(VariableClass.ResponseVariables.RATE) + " " + jochild.getString(VariableClass.ResponseVariables.AMOUNTTYPE));
                                row.setIsSection(false);
                                list.add(row);

                            }


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

    void searchRate(String s) {
        if (CommonUtility.isNetworkAvailable(ctx)) {

            if (!(s.toString().equals(""))) {

                country = s;
                if (!(CommonUtility.isNumeric(country)) && !country.equals("")) {
                    country=CommonUtility.validateText(country);

                    if(!country.equals(""))
                    new SearchRate().execute();

                } else if (country.startsWith("+") || country.startsWith("00")||country.startsWith("0")) {


                  country = CommonUtility.validateNumberForApi(country);

                    if ((!country.equals("") && country.length() > 0) && CommonUtility.isNumeric(country)) {

                        new SearchRate().execute();
                    } else {
                        if (search_rate_listview != null)
                            search_rate_listview.invalidateViews();
                    }

                } else {
                    if (search_rate_listview != null)
                        search_rate_listview.invalidateViews();
                    showErrorMessage(true, getResources().getString(R.string.number_validation));
                }


            } else {
                if (search_rate_listview != null)
                    search_rate_listview.invalidateViews();
            }

        } else
            showErrorMessage(true, getResources().getString(R.string.internet_error));
    }


}





