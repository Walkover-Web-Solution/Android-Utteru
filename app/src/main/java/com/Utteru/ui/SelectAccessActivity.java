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


public class SelectAccessActivity extends ActionBarActivity {


    Context ctx;
    ArrayList<AccessDataDto> accessList;
    AccessDataAdapter adapter;
    ListView listView;
    AccessContactDto selectedDto;
    ImageView back_button,gotohome;
    FontTextView titile,subtitile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_layout);
        init();
        Mint.initAndStartSession(SelectAccessActivity.this, "395e969a");
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(SelectAccessActivity.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedDto.setAccess_number(accessList.get(position).getAccessNumber());

               if(!UserService.getUserServiceInstance(ctx).isAccessNumberDedicated(selectedDto.getAccess_number())) {
                   Intent assignNumber = new Intent(ctx, SelectTypeActivity.class);
                   assignNumber.putExtra(VariableClass.Vari.SELECTEDDATA, selectedDto);
                   ctx.startActivity(assignNumber);
               }
                else{
                   CommonUtility.showCustomAlertError(SelectAccessActivity.this," This access number is already dedicated ");
               }



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
        subtitile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        gotohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectAccessActivity.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
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
        selectedDto = (AccessContactDto) getIntent().getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
        back_button = (ImageView)findViewById(R.id.contacts_back);
        gotohome =(ImageView)findViewById(R.id.contacts_home);
        titile = (FontTextView)findViewById(R.id.contact_header);
        subtitile = (FontTextView)findViewById(R.id.contacts_subtitle);

        titile.setText("Select Access Number");
        subtitile.setVisibility(View.VISIBLE);
        subtitile.setText(selectedDto.getCountry()+","+selectedDto.getState());
        listView = (ListView) findViewById(R.id.contacts_list);
        accessList = new ArrayList<>();


        accessList = UserService.getUserServiceInstance(ctx).getAllAccess(selectedDto.getState());
        if (accessList.size() > 0) {
            adapter = new AccessDataAdapter(accessList, this, 3);
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(adapter);
        } else {
            listView.setVisibility(View.GONE);
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
