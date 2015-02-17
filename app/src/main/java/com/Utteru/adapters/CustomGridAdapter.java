package com.Utteru.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.Utteru.R;
import com.Utteru.commonUtilities.FontTextView;

public class CustomGridAdapter extends BaseAdapter {
    private final String[] web;
    private final int[] Imageid;
    private Context mContext;

    public CustomGridAdapter(Context c, String[] web, int[] Imageid) {
        mContext = c;
        this.Imageid = Imageid;
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
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        grid = inflater.inflate(R.layout.gridview_cell, null);
        FontTextView FontTextView = (FontTextView) grid.findViewById(R.id.grid_text);
        ImageView imageView = (ImageView) grid.findViewById(R.id.grid_image);
        FontTextView.setText(web[position]);
        imageView.setImageResource(Imageid[position]);


        return grid;
    }
}