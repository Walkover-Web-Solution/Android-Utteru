package com.Utteru.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.Utteru.R;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.dtos.RecentCallsDto;

import java.util.ArrayList;

public class RecentDetailsAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<RecentCallsDto> list;

    public RecentDetailsAdapter(ArrayList<RecentCallsDto> paramArrayList, Context paramContext) {
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
            paramView = LayoutInflater.from(this.ctx).inflate(R.layout.dialer_recent_detailed_row, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.call_time = ((FontTextView) paramView.findViewById(R.id.call_times));
            viewholder.call_duration = ((FontTextView) paramView.findViewById(R.id.call_durations));
            viewholder.call_price = ((FontTextView) paramView.findViewById(R.id.call_price));


            paramView.setTag(viewholder);
        }
        ViewHolder viewholder = (ViewHolder) paramView.getTag();
        RecentCallsDto rdto = list.get(paramInt);



        viewholder.call_time.setText(rdto.getTime().trim());
        String duration = rdto.getDuration().trim();


            viewholder.call_duration.setText(duration);


            if(rdto .getPrice()!=null) {
                viewholder.call_price.setText(rdto.getPrice());
            }
            else {
                viewholder.call_price.setVisibility(View.GONE);
            }


        return paramView;
    }

    static class ViewHolder {
        public FontTextView call_time, call_duration,call_price;
    }
}