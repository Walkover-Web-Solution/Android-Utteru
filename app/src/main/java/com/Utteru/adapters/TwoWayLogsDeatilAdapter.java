package com.Utteru.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.Utteru.R;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.dtos.ContactsDto;


import java.util.ArrayList;

public class TwoWayLogsDeatilAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<ContactsDto> list;

    public TwoWayLogsDeatilAdapter(ArrayList<ContactsDto> paramArrayList, Context paramContext) {
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
            paramView = LayoutInflater.from(this.ctx).inflate(R.layout.two_way_recent_detailed_row, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.dest_name = ((FontTextView) paramView.findViewById(R.id.d_twc_dest_name));
            viewholder.dest_num = ((FontTextView) paramView.findViewById(R.id.d_twc_dest_number));
            viewholder.source_name_num = ((FontTextView) paramView.findViewById(R.id.d_twc_source_name_num));
            viewholder.date = ((FontTextView) paramView.findViewById(R.id.d_twc_date));

            paramView.setTag(viewholder);
        }
        ViewHolder viewholder = (ViewHolder) paramView.getTag();
        ContactsDto cdto = list.get(paramInt);

        if (!cdto.getDestination_name().trim().equals("")) {
            viewholder.dest_name.setText(cdto.getDestination_name().trim());
            viewholder.dest_num.setText(cdto.getDestination_number().trim());
        } else {
            viewholder.dest_name.setText(cdto.getDestination_number());
            viewholder.dest_num.setVisibility(View.GONE);
        }
        if (!cdto.getSource_name().equals(""))
            viewholder.source_name_num.setText("from " + cdto.getSource_name() + "(" + cdto.getSource_number() + ")");
        else
            viewholder.source_name_num.setText("from " + cdto.getSource_number());
        viewholder.date.setText(cdto.getDate());

        return paramView;
    }

    static class ViewHolder {
        public FontTextView source_name_num, dest_num, dest_name, date;
    }
}