package com.Utteru.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.Utteru.R;
import com.Utteru.adapters.ManageVerifiedDataListAdapter;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.VerifiedData;

import java.util.ArrayList;

public class ManageEmailsFrag extends Fragment {
    View manage_numbers;
    ManageVerifiedDataListAdapter adapter;
    Context ctx;
    ListView listview;
    ArrayList<VerifiedData> datalist;
    FontTextView nothing_found_text;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        manage_numbers = inflater.inflate(R.layout.manage_email_number, container, false);
        init();

        return manage_numbers;
    }

    @Override
    public void onResume() {

        new getAllEmails().execute();

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                if (datalist.get(arg2).getState() != ManageNumbersHome.ISDEFAULT) {

                    Intent gotodatail = new Intent(ctx, EditNumberEmails.class);
                    gotodatail.putExtra(VariableClass.Vari.SELECTEDDATA, datalist.get(arg2));
                    getActivity().startActivity(gotodatail);

                    getActivity().overridePendingTransition(R.anim.animation1, R.anim.animation2);
                    getActivity().finish();

                }

            }
        });

        super.onResume();
    }


    void init() {
        ctx = getActivity().getBaseContext();
        listview = (ListView) manage_numbers.findViewById(R.id.twc_list);
        nothing_found_text = (FontTextView) manage_numbers.findViewById(R.id.nothing_found_text);
        nothing_found_text.setText(getResources().getString(R.string.no_email_found));
        datalist = new ArrayList<VerifiedData>();
    }

    @Override
    public void onStop() {

        super.onStop();
    }

    class getAllEmails extends AsyncTask<Void, Void, Void> {

        String response;
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {


            if (datalist != null && datalist.size() > 0) {
                listview.setVisibility(View.VISIBLE);
                adapter = new ManageVerifiedDataListAdapter(datalist, ctx);
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                listview.invalidateViews();

            } else
                listview.setVisibility(View.GONE);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {


            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {


            datalist = Prefs.getVerifiedEmailList(ctx);
            return null;
        }

    }
}
