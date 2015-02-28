package com.Utteru.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.Utteru.R;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessDataDto;

import com.Utteru.ui.AccessInfoActivity;
import com.Utteru.ui.SelectCountryCallForward;

import java.util.ArrayList;

public class AccessDataAdapter extends BaseAdapter {
    Activity ctx;
    ArrayList<AccessDataDto> list;

    int dataType;

    public AccessDataAdapter(ArrayList<AccessDataDto> paramArrayList, Activity paramContext, int dataType) {
        this.list = paramArrayList;
        this.ctx = paramContext;
        this.dataType = dataType;
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

    public View getView(final int paramInt, View paramView, ViewGroup paramViewGroup) {
        if (paramView == null) {
            paramView = LayoutInflater.from(this.ctx).inflate(R.layout.access_data_row_view, null);

            ViewHolder holder = new ViewHolder();
            holder.text = (FontTextView) paramView.findViewById(R.id.access_text);

            holder.icon = (ImageView) paramView.findViewById(R.id.call_image);
            holder.info_button = (ImageView) paramView.findViewById(R.id.info_button);

            paramView.setTag(holder);
        }


        ViewHolder viewholder = (ViewHolder) paramView.getTag();

        switch (dataType) {
            case 0:
                viewholder.text.setText(list.get(paramInt).getCountry());
                viewholder.icon.setVisibility(View.GONE);
                viewholder.info_button.setVisibility(View.GONE);
                break;

            case 1:
                viewholder.text.setText(list.get(paramInt).getState());
                viewholder.icon.setVisibility(View.GONE);
                viewholder.info_button.setVisibility(View.GONE);
                break;

            case 2:
                viewholder.text.setText(list.get(paramInt).getAccessNumber());
                viewholder.icon.setVisibility(View.GONE);
                viewholder.info_button.setVisibility(View.VISIBLE);
                break;
            case 3:
                viewholder.text.setText(list.get(paramInt).getAccessNumber());
                viewholder.icon.setVisibility(View.GONE);
                viewholder.info_button.setVisibility(View.GONE);
                break;


        }
        viewholder.info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent showdetails = new Intent(ctx, AccessInfoActivity.class);
                showdetails.putExtra(VariableClass.Vari.SELECTEDDATA, list.get(paramInt));
                ctx.startActivity(showdetails);
                ctx.overridePendingTransition(R.anim.animation1, R.anim.animation2);
            }
        });


        return paramView;
    }

    private class ViewHolder {
        FontTextView text;
        ImageView icon;
        ImageView info_button;

    }
}