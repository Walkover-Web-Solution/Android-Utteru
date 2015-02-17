package com.Utteru.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.Utteru.R;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.dtos.VerifiedData;
import com.Utteru.ui.ManageNumbersHome;

import java.util.ArrayList;


public class ManageVerifiedDataListAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<VerifiedData> list;

    public ManageVerifiedDataListAdapter(ArrayList<VerifiedData> paramArrayList, Context paramContext) {
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
            paramView = LayoutInflater.from(this.ctx).inflate(R.layout.manage_date_row_view, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.data_text = ((FontTextView) paramView.findViewById(R.id.data_text));
            viewholder.staFontTextView = ((FontTextView) paramView.findViewById(R.id.state_text));

            paramView.setTag(viewholder);
        }
        ViewHolder viewholder = (ViewHolder) paramView.getTag();
        final VerifiedData dto = list.get(paramInt);

        switch (dto.getType()) {
            case ManageNumbersHome.ISEMAIL:
                viewholder.data_text.setText(dto.getParticualr());
                break;
            case ManageNumbersHome.ISNUMBER:
                viewholder.data_text.setText("+" + dto.getCountryCode() + dto.getParticualr());
                break;
        }


        switch (dto.getState()) {
            case ManageNumbersHome.ISUNVERIFIED:
                viewholder.staFontTextView.setText("UNVERIFIED");
                break;
            case ManageNumbersHome.ISVERIFIED:
                viewholder.staFontTextView.setText("VERIFIED");
                break;
            case ManageNumbersHome.ISDEFAULT:
                viewholder.staFontTextView.setText("DEFAULT");
                break;
        }


        return paramView;
    }

    static class ViewHolder {
        FontTextView data_text, staFontTextView;

    }
}