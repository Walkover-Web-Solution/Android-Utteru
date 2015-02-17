package com.Utteru.ui;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.Utteru.R;
import com.Utteru.adapters.TwoWayLogsDeatilAdapter;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.ContactsDto;
import com.Utteru.userService.UserService;
import com.splunk.mint.Mint;

import java.util.ArrayList;

public class TwoWayCallDetailedActivity extends BaseActivity {
    ListView two_way_recent_list;
    TwoWayLogsDeatilAdapter adapter;
    ArrayList<ContactsDto> list;
    Context ctx;
    ContactsDto selected_dto;
    FontTextView title;
    ImageView menu,back_button;
    FontTextView nothing_found_txt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.two_way_recent_detailed_layout);
        init();
        Mint.initAndStartSession(TwoWayCallDetailedActivity.this, "395e969a");
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));


        super.onCreate(savedInstanceState);
    }


    @Override
    public void onResume() {

        new InitList().execute(null, null, null);
        menu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent startmenu = new Intent(ctx, MenuScreen.class);
                startmenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startmenu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(startmenu);
                overridePendingTransition(R.anim.animation3, R.anim.animation4);
            }
        });

        two_way_recent_list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent makecall = new Intent(ctx, TwoWayHome.class);
                makecall.putExtra(VariableClass.Vari.SELECTEDDATA, list.get(arg2));
                makecall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                makecall.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ctx.startActivity(makecall);
                overridePendingTransition(R.anim.animation1, R.anim.animation2);

            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        super.onResume();
    }


    void init() {
        nothing_found_txt = (FontTextView)findViewById(R.id.nothing_found_detail);
        two_way_recent_list = (ListView) findViewById(R.id.twc_detailed_list);
        title = (FontTextView) findViewById(R.id.twc_detailed_con_name);
        list = new ArrayList<ContactsDto>();
        ctx = this;
        selected_dto = (ContactsDto) getIntent().getExtras().get(VariableClass.Vari.SELECTEDDATA);
        title.setText(selected_dto.getDestination_name());
        menu = (ImageView) findViewById(R.id.menu_button);
        back_button = (ImageView)findViewById(R.id.tw_details_back);
    }

    class InitList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void result) {
            if (list.size() != 0) {
                two_way_recent_list.setVisibility(View.VISIBLE);
                nothing_found_txt.setVisibility(View.GONE);
                adapter = new TwoWayLogsDeatilAdapter(list, ctx);
                two_way_recent_list.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            else {
                two_way_recent_list.setVisibility(View.GONE);
                nothing_found_txt.setVisibility(View.VISIBLE);
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            list = UserService.getUserServiceInstance(ctx).getAllTwoRecentCallByName(selected_dto.getDestination_number());
            return null;
        }

    }


}
