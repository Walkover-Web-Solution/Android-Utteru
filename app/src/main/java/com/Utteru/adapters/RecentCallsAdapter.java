package com.Utteru.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;

import java.util.ArrayList;

import com.Utteru.R;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.VariableClass;

import com.Utteru.dtos.RecentCallsDto;

import com.Utteru.utteru_sip.DialerFragment;
import com.Utteru.utteru_sip.RecentDetailFragment;
import com.Utteru.utteru_sip.UtteruSipCore;
import com.portsip.PortSipSdk;


public class RecentCallsAdapter extends BaseAdapter {
    public ArrayList<RecentCallsDto> call_info;
    Context ctx;
    PopupWindow pwindo;
    DialerFragment.OnCallListener listener;
    UtteruSipCore myapp;
    PortSipSdk sipSdk;


    public RecentCallsAdapter(ArrayList<RecentCallsDto> paramArrayList, Context paramContext ,DialerFragment.OnCallListener listener , UtteruSipCore myapp, PortSipSdk sdk) {
        this.call_info = paramArrayList;
        this.ctx = paramContext;
        this.listener = listener;
        this.myapp = myapp;
        sipSdk = sdk;
    }

    public int getCount() {
        return this.call_info.size();
    }

    public Object getItem(int paramInt) {
        return this.call_info.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    public View getView(final int paramInt, View paramView, ViewGroup paramViewGroup) {
        if (paramView == null) {

            paramView = LayoutInflater.from(this.ctx).inflate(R.layout.recent_call_row_view, null);
            ViewHolder localViewHolder2 = new ViewHolder();
            localViewHolder2.name = ((FontTextView) paramView.findViewById(R.id.call_name));
            localViewHolder2.duration = ((FontTextView) paramView.findViewById(R.id.call_duration));
            localViewHolder2.time = ((FontTextView) paramView.findViewById(R.id.call_time));
            localViewHolder2.detail = (ImageView) paramView.findViewById(R.id.call_times);

            paramView.setTag(localViewHolder2);
        }
        final RecentCallsDto rdto = call_info.get(paramInt);
        ViewHolder localViewHolder1 = (ViewHolder) paramView.getTag();


        if(rdto.getName()!=null&&!rdto.getName().equals(""))
        localViewHolder1.name.setText(rdto.getName());
        else
        localViewHolder1.name.setText(rdto.getNumber());


         String duration =rdto.getDuration();
        if(duration.startsWith("00:00")){
            localViewHolder1.duration.setText(" Not Answered ");
        }
        else
        localViewHolder1.duration.setText(duration);

        localViewHolder1.duration.setVisibility(View.GONE);

        localViewHolder1.time.setText(rdto.getTime());

        localViewHolder1.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("clicked detail", "clicked detail");

              listener.onCall(myapp, sipSdk, null, 1, call_info.get(paramInt));

            }
        });
        return paramView;
    }

    static class ViewHolder {
        public FontTextView duration;
        public FontTextView name;
        public FontTextView time;
        public ImageView detail;

    }
}