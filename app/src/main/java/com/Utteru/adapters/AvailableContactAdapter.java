package com.Utteru.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.parse.ContactsDto;
import com.Utteru.ui.RoundedImageView;
import com.Utteru.util.RoundedQuickContactBadge;

import java.util.ArrayList;

public class AvailableContactAdapter extends BaseAdapter {
    //testing
    Activity ctx;
    ArrayList<ContactsDto> list;

    public AvailableContactAdapter(ArrayList<ContactsDto> paramArrayList, Activity paramContext) {
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
            paramView = LayoutInflater.from(this.ctx).inflate(R.layout.contact_list_item, paramViewGroup, false);

            ViewHolder holder = new ViewHolder();
            holder.text1 = (FontTextView) paramView.findViewById(android.R.id.text1);
            holder.text2 = (FontTextView) paramView.findViewById(android.R.id.text2);
            holder.icon = (RoundedQuickContactBadge) paramView.findViewById(android.R.id.icon);
            holder.user_status=(RoundedImageView)paramView.findViewById(R.id.user_status);
            paramView.setTag(holder);
        }

        ContactsDto dto =list.get(paramInt);

        ViewHolder viewholder = (ViewHolder) paramView.getTag();
        String name =dto.getName();
        if(name!=null&&!name.equals(""))
        viewholder.text1.setText(dto.getName());
        else viewholder.text1.setVisibility(View.GONE);

        String number =dto.getNumber();



        viewholder.text2.setText(number);

            viewholder.icon.setVisibility(View.VISIBLE);

                viewholder.icon.setBackgroundColor(ctx.getResources().getColor(android.R.color.transparent));
                Log.e("getname", "" + list.get(paramInt).getName());
                viewholder.icon.setImageBitmap(CommonUtility.drawImage(dto.getName(), ctx));

        viewholder.user_status.setVisibility(View.VISIBLE);
        if(dto.getState())
        viewholder.user_status.setBackgroundResource(R.color.green);
        else
        viewholder.user_status.setBackgroundResource(R.color.light_gray);



        return paramView;
    }

    private class ViewHolder {
        FontTextView text1, text2;
        RoundedQuickContactBadge icon ;
        RoundedImageView user_status;
    }
}