package com.Utteru.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.Utteru.R;
import com.Utteru.commonUtilities.FontTextView;

import java.util.ArrayList;

public class AcessInfoOptionsAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<String> list;

    public AcessInfoOptionsAdapter(ArrayList<String> paramArrayList, Context paramContext) {
        this.list = paramArrayList;
        this.ctx = paramContext;
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int paramInt) {

        return this.list.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
        if (paramView == null) {
            paramView = LayoutInflater.from(this.ctx).inflate(R.layout.list_options_row_view, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.name = ((FontTextView) paramView.findViewById(R.id.options_text));
            paramView.setTag(viewholder);
        }
        ViewHolder viewholder = (ViewHolder) paramView.getTag();
        viewholder.name.setText(list.get(paramInt));
        return paramView;
    }

    static class ViewHolder {
        public FontTextView name;
    }
}