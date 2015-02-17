package com.Utteru.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.Utteru.R;
import com.Utteru.adapters.SearchListDialerAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.Country;
import com.splunk.mint.Mint;

import java.util.ArrayList;
import java.util.Collections;

public class SearchListDialer extends BaseActivity {
    public static ArrayList<Country> country_list;
    ListView listview;
    EditText searchFontTextView;
    SearchListDialerAdapter listadapter;
    Context ctx;
    LinearLayout searchListLayout;

    Boolean changecolor = false;
    String countryCode, countryName, countryIso = null;

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.search_list_dialer);
        super.onCreate(savedInstanceState);
        init();
        Mint.initAndStartSession(SearchListDialer.this, "395e969a");
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));

    }

    @Override
    protected void onStart() {
        listadapter = new SearchListDialerAdapter(country_list, ctx);
        listview.setAdapter(listadapter);
        super.onStart();
    }

    @Override
    protected void onResume() {
        searchFontTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = searchFontTextView.getText().toString().toLowerCase().replace("+", "");
                if (isNumeric(text))
                    text = "+" + text;
                ArrayList<Country> filterList1 = new ArrayList<Country>();
                ArrayList<Country> filterList2 = new ArrayList<Country>();
                for (Country a : country_list) {
                    if (a.getCountryName().toLowerCase().startsWith(text) || a.getCountryCode().toLowerCase().startsWith(text)) {
                        filterList1.add(a);
                        continue;
                    }

                    if (a.getCountryName().toLowerCase().contains(text) || a.getCountryCode().toLowerCase().contains(text)) {
                        filterList2.add(a);
                    }
                }
                filterList1.addAll(filterList2);
                listview.setAdapter(new SearchListDialerAdapter(filterList1, ctx));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Country cdto = (Country) arg0.getItemAtPosition(arg2);
                countryCode = cdto.getCountryCode();
                countryName = cdto.getCountryName();
                countryIso = cdto.getCountryIso();

                Log.e("country in searchlist", "" + cdto.getCountryName());
                SearchListDialer.this.finish();

            }
        });

        super.onResume();
    }

    @Override
    public void finish() {
        Log.e("onfinsh", "on finish");

        Log.e("if from signup", "if from signup");
        Intent data = new Intent();
        data.putExtra(VariableClass.Vari.COUNTRYCODE, countryCode);
        data.putExtra(VariableClass.Vari.COUNTRYNAME, countryName);

        setResult(Activity.RESULT_OK, data);

        super.finish();
        overridePendingTransition(R.anim.animation3, R.anim.animation4);

    }

    public void init() {
        ctx = SearchListDialer.this;
        searchListLayout = (LinearLayout) findViewById(R.id.country_list_search_layout);
        listview = (ListView) findViewById(R.id.search_list);
        searchFontTextView = (EditText) findViewById(R.id.search_bar);
        if (country_list.size() == 0)
            Collections.sort(country_list);


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animation3, R.anim.animation4);
    }
}
