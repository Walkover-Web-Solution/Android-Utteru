package com.Utteru.adapters;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.dtos.AccessContactDto;
import com.Utteru.util.ImageLoader;
import com.Utteru.util.RoundedQuickContactBadge;

import java.util.ArrayList;

public class AccessContactAdapter extends BaseAdapter {
    //testing 
    Activity ctx;
    ArrayList<AccessContactDto> list;
    ImageLoader imgloader;

    public AccessContactAdapter(ArrayList<AccessContactDto> paramArrayList, Activity paramContext, ImageLoader imgloader) {
        this.list = paramArrayList;
        this.ctx = paramContext;
        this.imgloader = imgloader;
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
            paramView = LayoutInflater.from(this.ctx).inflate(R.layout.contact_list_item, paramViewGroup, false);

            ViewHolder holder = new ViewHolder();
            holder.text1 = (FontTextView) paramView.findViewById(android.R.id.text1);
            holder.text2 = (FontTextView) paramView.findViewById(android.R.id.text2);
            holder.icon = (RoundedQuickContactBadge) paramView.findViewById(android.R.id.icon);
            paramView.setTag(holder);
        }

        ViewHolder viewholder = (ViewHolder) paramView.getTag();
        viewholder.text1.setText(list.get(paramInt).getDisplay_name());
        viewholder.text2.setText(list.get(paramInt).getMobile_number());
        if (imgloader != null) {
            viewholder.icon.setVisibility(View.VISIBLE);
            if (list.get(paramInt).getThumbUri() != null)
                imgloader.loadImage(list.get(paramInt).getThumbUri(), viewholder.icon);
            else {
                viewholder.icon.setBackgroundColor(ctx.getResources().getColor(android.R.color.transparent));
                Log.e("getname", "" + list.get(paramInt).getDisplay_name());
                viewholder.icon.setImageBitmap(CommonUtility.drawImage(list.get(paramInt).getDisplay_name(), ctx));
            }
            if (list.get(paramInt).getContactUri() != null)
                viewholder.icon.assignContactUri(Uri.parse(list.get(paramInt).getContactUri()));
        } else {
            viewholder.icon.setVisibility(View.GONE);
        }
        return paramView;
    }

    private class ViewHolder {
        FontTextView text1, text2;
        RoundedQuickContactBadge icon;
    }
}