package com.Utteru.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.Utteru.R;
import com.Utteru.adapters.CplistAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.userService.UserService;
import com.splunk.mint.Mint;

import java.util.ArrayList;

public class CplogsActivity extends Activity {

    ListView cplogslistview;
    CplistAdapter adapter;
    ImageView backpress, gototohome;
    ArrayList<String> cplist;
    Context ctx = this;
    LayoutInflater inflater;
    ViewGroup header;
    FontTextView tittleback,nothing_found;


    @Override
    protected void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.two_way_recent_layout);

        cplogslistview = (ListView) findViewById(R.id.twc_list);

        cplist = new ArrayList<String>();

        new getAllLogs().execute(null, null, null);
        Mint.initAndStartSession(CplogsActivity.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));
        inflater = getLayoutInflater();
        header = (ViewGroup) inflater.inflate(R.layout.cp_log_header, cplogslistview,
                false);
        backpress = (ImageView) header.findViewById(R.id.auto_detect_country_back);
        tittleback = (FontTextView)header.findViewById(R.id.auto_detect_coutry_header);
        gototohome = (ImageView)header.findViewById(R.id.auto_detect_country_home);
        nothing_found = (FontTextView)findViewById(R.id.nothing_found);
        cplogslistview.addHeaderView(header, null, false);
        cplogslistview.setDivider(new ColorDrawable(getResources().getColor(R.color.yellow)));
        cplogslistview.setDividerHeight(1);

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
                startActivity(new Intent(CplogsActivity.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });


    }


    class getAllLogs extends AsyncTask<Void, Void, Void>

    {

        @Override
        protected Void doInBackground(Void... params) {

            cplist = UserService.getUserServiceInstance(ctx).getCpLogs();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (cplist.size() > 0) {

                Log.e("size is not zero", "size is not zero ");
                nothing_found.setVisibility(View.GONE);
                cplogslistview.setVisibility(View.VISIBLE);
                adapter = new CplistAdapter(cplist, ctx);
                cplogslistview.setAdapter(adapter);
            } else {
                Log.e("size is  zero", "size is zero ");
                cplogslistview.setVisibility(View.GONE);
                nothing_found.setVisibility(View.VISIBLE);
            }
            CommonUtility.dialog.dismiss();


            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            CommonUtility.show_PDialog(CplogsActivity.this, getResources().getString(R.string.please_wait));
            super.onPreExecute();
        }


    }

}

