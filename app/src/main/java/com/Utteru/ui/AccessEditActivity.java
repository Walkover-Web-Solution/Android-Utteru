package com.Utteru.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessContactDto;
import com.splunk.mint.Mint;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class AccessEditActivity extends ActionBarActivity {
    public String jsonStr, jsonStr1, jsonStr2;
    Button buttonCountry, buttonState, buttonAccess, buttonExtension;
    ArrayList<HashMap<String, ArrayList<String>>> accessList;
    ArrayList<String> extensionList;
    Context ctx;
    ArrayList<String> arrayList;
    HashMap<String, ArrayList<String>> temp;
    HashMap<String, JSONArray> accessNumberList;
    AccessContactDto selected_contact;
    int i = 1;
    ImageView back_button,gotohome;
    FontTextView titile,subtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_edit);
        init();
        Mint.initAndStartSession(AccessEditActivity.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));






        buttonAccess.setText(selected_contact.getAccess_number());
        if (selected_contact.getExtension_number() != null) {
            if (selected_contact.getExtension_number().equals("100")) {
                buttonExtension.setText("Dedicated");
            } else {
                buttonExtension.setText("Extension No.: " + selected_contact.getExtension_number());
            }
        }

        buttonCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(AccessEditActivity.this, SelectCountryActivity.class);
                myIntent.putExtra(VariableClass.Vari.SELECTEDDATA, selected_contact);
                startActivity(myIntent);
                overridePendingTransition(R.anim.animation1, R.anim.animation2);

            }
        });

        buttonState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(AccessEditActivity.this, SelectStateActivity.class);
                myIntent.putExtra(VariableClass.Vari.SELECTEDDATA, selected_contact);
                startActivity(myIntent);
                overridePendingTransition(R.anim.animation1, R.anim.animation2);
            }
        });

        buttonAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent myIntent = new Intent(AccessEditActivity.this, SelectAccessActivity.class);
                myIntent.putExtra(VariableClass.Vari.SELECTEDDATA, selected_contact);

                startActivity(myIntent);
                overridePendingTransition(R.anim.animation1, R.anim.animation2);
            }
        });
        buttonExtension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(AccessEditActivity.this, SelectTypeActivity.class);
                myIntent.putExtra(VariableClass.Vari.SELECTEDDATA, selected_contact);

                startActivity(myIntent);
                overridePendingTransition(R.anim.animation1, R.anim.animation2);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(AccessEditActivity.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
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

    void init() {
        ctx = this;
        back_button = (ImageView)findViewById(R.id.contacts_back);
        gotohome =(ImageView)findViewById(R.id.contacts_home);
        titile = (FontTextView)findViewById(R.id.contact_header);
        subtitle = (FontTextView)findViewById(R.id.contacts_subtitle);

        titile.setText("Edit Access Number");

        selected_contact = (AccessContactDto)getIntent().getSerializableExtra(VariableClass.Vari.SELECTEDDATA);
        temp = new HashMap<String, ArrayList<String>>();
        accessList = new ArrayList<HashMap<String, ArrayList<String>>>();
        arrayList = new ArrayList<String>();
        accessNumberList = new HashMap<String, JSONArray>();
        buttonCountry = (Button) findViewById(R.id.selectCountry);
        buttonState = (Button) findViewById(R.id.selectState);
        buttonAccess = (Button) findViewById(R.id.selectAccessNumber);
        buttonExtension = (Button) findViewById(R.id.selectExtension);
        buttonCountry.setText(selected_contact.getCountry());
        buttonState.setText(selected_contact.getState());
        buttonAccess.setText(selected_contact.getAccess_number());
        buttonExtension.setText(selected_contact.getExtension_number());


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
