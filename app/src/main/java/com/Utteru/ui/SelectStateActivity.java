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


public class SelectStateActivity extends ActionBarActivity {


    Context ctx;
    ArrayList<AccessDataDto> stateList;
    AccessDataAdapter adapter;
    ListView listView;
    AccessContactDto selectedDto;
    ImageView back_button, gotohome;
    FontTextView titile, subtitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.list_layout);
        init();
        Mint.initAndStartSession(SelectStateActivity.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(SelectStateActivity.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(ctx, SelectAccessActivity.class);

                selectedDto.setState(stateList.get(position).getState());
                intent.putExtra(VariableClass.Vari.SELECTEDDATA, selectedDto);
                startActivity(intent);

            }
        });
//        if(stateList!=null&&stateList.size()==1)
//            listView.performItemClick(listView.getAdapter().getView(0, null, null), 0, listView.getAdapter().getItemId(0));
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
        subtitle.setOnClickListener(new View.OnClickListener() {
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

        selectedDto = (AccessContactDto) getIntent().getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
        ctx = this;
        back_button = (ImageView) findViewById(R.id.contacts_back);
        gotohome = (ImageView) findViewById(R.id.contacts_home);
        titile = (FontTextView) findViewById(R.id.contact_header);
        subtitle = (FontTextView) findViewById(R.id.contacts_subtitle);
        subtitle.setVisibility(View.VISIBLE);
        subtitle.setText(selectedDto.getCountry());
        titile.setText("Select Access Number");
        listView = (ListView) findViewById(R.id.contacts_list);
        stateList = new ArrayList<>();


        stateList = UserService.getUserServiceInstance(ctx).getAllStates(selectedDto.getCountry());
        if (stateList.size() > 0) {
            adapter = new AccessDataAdapter(stateList, this, 1);
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
