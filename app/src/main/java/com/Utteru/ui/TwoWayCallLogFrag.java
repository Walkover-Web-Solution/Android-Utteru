package com.Utteru.ui;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.Utteru.R;
import com.Utteru.adapters.TwcListAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.ContactsDto;
import com.Utteru.userService.UserService;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class TwoWayCallLogFrag extends Fragment {
    View tw_call_log_recent;
    ListView two_way_recent_list;
    TwcListAdapter adapter;
    ArrayList<ContactsDto> list;
    Context ctx;
    ProgressDialog dialog;
    FontTextView nothing_found;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("creating view", "two way call");
        tw_call_log_recent = inflater.inflate(R.layout.two_way_recent_layout, container, false);
        init();
        new InitList().execute(null, null, null);

        return tw_call_log_recent;
    }

    @Override
    public void onResume() {

        new InitList().execute(null, null, null);
        two_way_recent_list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                //make call
                Intent makecall = new Intent(ctx, TwoWayHome.class);
                makecall.putExtra(VariableClass.Vari.SELECTEDDATA, list.get(arg2));
                makecall.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                makecall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(makecall);
            }
        });

        super.onResume();
    }


    void init() {
        two_way_recent_list = (ListView) tw_call_log_recent.findViewById(R.id.twc_list);
        list = new ArrayList<ContactsDto>();
        ctx = getActivity().getBaseContext();
        nothing_found = (FontTextView)tw_call_log_recent.findViewById(R.id.nothing_found);

    }

    class InitList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void result) {

            dialog.dismiss();
            Log.e("in post", "in post");
            if (list.size() != 0) {
                nothing_found.setVisibility(View.GONE);
                adapter = new TwcListAdapter(list, ctx);
                two_way_recent_list.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                two_way_recent_list.setVisibility(View.VISIBLE);
            }
            else {
                two_way_recent_list.setVisibility(View.GONE);
                nothing_found.setVisibility(View.VISIBLE);
            }
            super.onPostExecute(result);
        }


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog = new ProgressDialog(ctx, R.style.MyTheme);
            dialog.setMessage(getString(R.string.please_wait));
            dialog.setCancelable(false);

            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        }

        @Override
        protected Void doInBackground(Void... params) {
            list = UserService.getUserServiceInstance(ctx).getAllTwoRecentCallByGroup();
            return null;
        }

    }


}
