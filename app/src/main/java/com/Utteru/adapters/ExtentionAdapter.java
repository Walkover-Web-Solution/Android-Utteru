package com.Utteru.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.Utteru.R;
import com.Utteru.commonUtilities.FontTextView;

public class ExtentionAdapter extends BaseAdapter {
    private final String[] web;
    private Context mContext;

    public ExtentionAdapter(Context c, String[] web) {
        mContext = c;
        this.web = web;

        Log.e("List size", "" + web.length);
    }

    @Override
    public int getCount() {
        return web.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        grid = inflater.inflate(R.layout.extension_cells, null);
        FontTextView FontTextView = (FontTextView) grid.findViewById(R.id.extenstions_text);
        FontTextView.setTextColor(mContext.getResources().getColor(android.R.color.black));


        FontTextView.setText(web[position]);


        return grid;
    }
}