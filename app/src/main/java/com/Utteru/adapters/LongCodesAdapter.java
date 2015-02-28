package com.Utteru.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.Utteru.R;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.dtos.LongCodesDto;

import java.util.ArrayList;

/**
 * Created by walkover on 27/2/15.
 */

public class LongCodesAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<LongCodesDto> list;

    public LongCodesAdapter(ArrayList<LongCodesDto> paramArrayList, Context paramContext) {
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
            paramView = LayoutInflater.from(ctx).inflate(R.layout.longcode_row, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.number = ((FontTextView) paramView.findViewById(R.id.longcode_no));
            viewholder.expiry = (FontTextView) paramView.findViewById(R.id.expiry);


            paramView.setTag(viewholder);
        }

        ViewHolder viewholder = (ViewHolder) paramView.getTag();
        LongCodesDto ldto;
        ldto = list.get(paramInt);
        if (ldto.getLongCodeNo() == null || ldto.getLongCodeNo().equals("null")) {

            String countryandstate = ldto.getCountry() + " > " + ldto.getState();
            viewholder.number.setText(countryandstate);
            viewholder.expiry.setText("coming soon");

        } else {
            viewholder.number.setText(ldto.getLongCodeNo());
            Log.e("longcodes", ldto.getLongCodeNo());
            viewholder.expiry.setText(ldto.getExpiryDate());
            Log.e("expiry", ldto.getExpiryDate());

        }
        return paramView;
    }

    static class ViewHolder {
        public FontTextView number;
        public FontTextView expiry;


    }
}