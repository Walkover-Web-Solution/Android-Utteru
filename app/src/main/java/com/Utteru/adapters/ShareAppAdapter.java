package com.Utteru.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.Utteru.R;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.dtos.AvailableApps;

import java.util.ArrayList;

public class ShareAppAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<AvailableApps> list;

    public ShareAppAdapter(ArrayList<AvailableApps> paramArrayList, Context paramContext) {
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
            paramView = LayoutInflater.from(ctx).inflate(R.layout.share_row_layout, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.app_name = ((FontTextView) paramView.findViewById(R.id.app_name));
            viewholder.icon = (ImageView) paramView.findViewById(R.id.app_icon);
            paramView.setTag(viewholder);
        }
        ViewHolder viewholder = (ViewHolder) paramView.getTag();
        AvailableApps cdto = list.get(paramInt);
        viewholder.icon.setImageDrawable(cdto.getIcon());
        viewholder.app_name.setText(cdto.getActivityName());
        return paramView;
    }

    static class ViewHolder {
        public ImageView icon;
        public FontTextView app_name;
    }
}