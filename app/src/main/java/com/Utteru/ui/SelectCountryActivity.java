package com.Utteru.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
import com.Utteru.dtos.AccessContactDto;
import com.Utteru.dtos.AccessDataDto;
import com.Utteru.userService.UserService;
import com.splunk.mint.Mint;

import java.util.ArrayList;


public class  SelectCountryActivity extends ActionBarActivity {


    Context ctx;
    ArrayList<AccessDataDto> countryList;
    AccessDataAdapter adapter;
    ListView listView;
    AccessContactDto selected_dto;
    ImageView back_button,gotohome;
    FontTextView header;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_layout);
        init();
        Mint.initAndStartSession(SelectCountryActivity.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(SelectCountryActivity.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(ctx, SelectStateActivity.class);
                selected_dto.setCountry(countryList.get(position).getCountry());
                selected_dto.setCode(countryList.get(position).getCountryCode());
                intent.putExtra(VariableClass.Vari.SELECTEDDATA,selected_dto);

                startActivity(intent);

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
                startActivity(new Intent(SelectCountryActivity.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_detail_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    void init() {

        ctx = this;

        back_button = (ImageView)findViewById(R.id.contacts_back);
        gotohome =(ImageView)findViewById(R.id.contacts_home);
        header = (FontTextView)findViewById(R.id.contact_header);
        header.setText("Select Country");


        selected_dto=(AccessContactDto)getIntent().getSerializableExtra(VariableClass.Vari.SELECTEDDATA);

        listView = (ListView) findViewById(R.id.contacts_list);
        countryList = new ArrayList<>();
        countryList = UserService.getUserServiceInstance(ctx).getAllCountries();
        if (countryList.size() > 0) {

            adapter = new AccessDataAdapter(countryList, this, 0);
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(adapter);
        } else {
            listView.setVisibility(View.GONE);
            new IntialiseData(ctx).initAccessData();
        }
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


}
