package com.Utteru.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.Utteru.R;
import com.Utteru.adapters.AccessDataAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessDataDto;
import com.Utteru.userService.UserService;
import com.splunk.mint.Mint;

import java.util.ArrayList;


public class AllCountryActivity extends ActionBarActivity {


    Context ctx;
    ArrayList<AccessDataDto> countryList;
    AccessDataAdapter adapter;
    ListView listView;
    ImageView back_button,gotohome;
    FontTextView titile;
    SwipeRefreshLayout refreshLayout;
    IntentFilter access_updated_filter;

    BroadcastReceiver access_updated = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //show unregister layout
            if (intent.getAction().equals(IntialiseData.ACCESS_UPDATED)) {

                Log.e("refresh access number ","refresh access number ");
                showList();

            }

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.country_list_layout);
        init();
        Mint.initAndStartSession(AllCountryActivity.this, "395e969a");

        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));



    }
    @Override
    protected void onPause() {
        super.onPause();
        Prefs.setLastActivity(ctx, getClass().getName());

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(AllCountryActivity.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(ctx, AllStateActivity.class);
                intent.putExtra(VariableClass.Vari.SELECTEDDATA, countryList.get(position));
                startActivity(intent);

            }
        });

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
                startActivity(new Intent(AllCountryActivity.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (CommonUtility.isNetworkAvailable(ctx)) {


                new IntialiseData(ctx).initAccessData();

                refreshLayout.setRefreshing(false);


                } else {
                    CommonUtility.showCustomAlertError(AllCountryActivity.this, getString(R.string.internet_error));

                }


                refreshLayout.setRefreshing(false);

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
        access_updated_filter = new IntentFilter();
        access_updated_filter.addAction(IntialiseData.ACCESS_UPDATED);
        back_button = (ImageView)findViewById(R.id.contacts_back);
        gotohome =(ImageView)findViewById(R.id.contacts_home);

        titile = (FontTextView)findViewById(R.id.contact_header);
        titile.setText("Select Country");
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);




        listView = (ListView) findViewById(R.id.contacts_list);
        countryList = new ArrayList<>();
        registerReceiver(access_updated, access_updated_filter);
        showList();

    }


    @Override
    public void onBackPressed() {
        Intent menu =new Intent(this,MenuScreen.class);
        menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(menu);
       // super.onBackPressed();
        overridePendingTransition(R.anim.animation3, R.anim.animation4);
    }

    @Override
    protected void onDestroy() {
        try {
            if(access_updated!=null)
                unregisterReceiver(access_updated);
        }
        catch (Exception e)
        {
    e.printStackTrace();
        }

        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onDestroy();
    }


    @Override
    protected void onStop() {

        try {
            unregisterReceiver(access_updated);
        }catch (Exception e )
        {
            e.printStackTrace();

        }

        super.onStop();
    }

    void showList()
    {
        countryList = UserService.getUserServiceInstance(ctx).getAllCountries();
        Boolean found_country=false;
        if (countryList.size() > 0) {


            AccessDataDto dto = new AccessDataDto();
            dto.setCountry(Prefs.getUserCountryName(ctx));
            dto.setCountryCode(Prefs.getUserCountryCode(ctx));

            if(getIntent().getExtras()==null ) {

                for (AccessDataDto c : countryList) {

                    if (c.equals(dto)) {

                          found_country =true;


                        break;
                    }
                }
                   Log.e("Out of loop ","out of loop");

                if(found_country){

                Intent detect_country = new Intent(ctx, AutoCountryDetect.class);

                detect_country.putExtra(VariableClass.Vari.SELECTEDDATA, dto);

                ctx.startActivity(detect_country);

                this.finish();

                }

                else {

                    //no access number found for this country
                    Intent detect_country = new Intent(ctx, AutoCountryDetect.class);
                    detect_country.putExtra(VariableClass.Vari.SELECTEDNAME, dto);
                    ctx.startActivity(detect_country);
                    this.finish();
                }

            }
            adapter = new AccessDataAdapter(countryList, this, 0);
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(adapter);


        } else {
            listView.setVisibility(View.GONE);
            new IntialiseData(ctx).initAccessData();
        }
    }
}
