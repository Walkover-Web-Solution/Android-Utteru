package com.Utteru.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.Utteru.R;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessContactDto;

import java.util.ArrayList;

public class AccessNumberInfoAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<AccessContactDto> list;

    public AccessNumberInfoAdapter(ArrayList<AccessContactDto> paramArrayList, Context paramContext) {
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
            paramView = LayoutInflater.from(ctx).inflate(R.layout.access_info_rowview, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.country = ((FontTextView) paramView.findViewById(R.id.access_number));
            viewholder.price = (FontTextView) paramView.findViewById(R.id.extension);


            paramView.setTag(viewholder);
        }

        ViewHolder viewholder = (ViewHolder) paramView.getTag();
        AccessContactDto cdto = list.get(paramInt);


        viewholder.country.setText(cdto.getDisplay_name());
        if (!cdto.getExtension_number().equals(VariableClass.Vari.DEDICATED))
            viewholder.price.setText(cdto.getExtension_number());
        else
            viewholder.price.setText("Dedicated");


        return paramView;
    }

    static class ViewHolder {
        public FontTextView country;
        public FontTextView price;

    }
}