package com.Utteru.ui;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.Utteru.R;
import com.Utteru.adapters.EmailListAdapter;

import java.util.ArrayList;

/**
 * Created by root on 11/24/14.
 */
public class CustomListDialog extends Dialog implements AdapterView.OnItemClickListener {
    public Activity c;
    ArrayList<String> list;
    ListView listView;
    EmailListAdapter adapter;


    public CustomListDialog(Activity a, ArrayList<String> list) {
        super(a);
        this.c = a;
        this.list = list;
        adapter = new EmailListAdapter(list, c);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_list_layout);
        listView = (ListView) findViewById(R.id.alert_list);
        listView.setAdapter(adapter);


    }


    @Override
    protected void onStart() {

        super.onStart();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


    }
}