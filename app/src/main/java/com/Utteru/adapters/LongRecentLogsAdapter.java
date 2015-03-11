
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
import com.Utteru.dtos.LongCodesDto;

import java.util.ArrayList;

public class LongRecentLogsAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<LongCodesDto> list;

    public LongRecentLogsAdapter(ArrayList<LongCodesDto> paramArrayList, Context paramContext) {
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
            paramView = LayoutInflater.from(ctx).inflate(R.layout.longlog_row, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.callerid = ((FontTextView) paramView.findViewById(R.id.caller_id));
            viewholder.duartion = (FontTextView) paramView.findViewById(R.id.duartion);
            viewholder.date_time = (FontTextView) paramView.findViewById(R.id.date_time);
            viewholder.bal_deduct = (FontTextView) paramView.findViewById(R.id.bal_deducted);
            viewholder.details = (ImageView) paramView.findViewById(R.id.details);

            paramView.setTag(viewholder);
        }

        ViewHolder viewholder = (ViewHolder) paramView.getTag();
        LongCodesDto ldto;
        ldto = list.get(paramInt);
        viewholder.callerid.setText(ldto.getCallerid());
        if (ldto.getDuration().equals("null")||ldto.getBal_deduct().equals("null")) {
            viewholder.duartion.setText("Missed");
            viewholder.bal_deduct.setText("0.00");
        } else {
            viewholder.duartion.setText(ldto.getDuration());
        }
        viewholder.date_time.setText(ldto.getDate_time());
        viewholder.bal_deduct.setText(ldto.getBal_deduct());

        return paramView;
    }

    static class ViewHolder {
        public FontTextView callerid;
        public FontTextView duartion;
        public FontTextView date_time;
        public FontTextView bal_deduct;
        ImageView details;


    }
}