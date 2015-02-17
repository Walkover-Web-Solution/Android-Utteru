package com.Utteru.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.Utteru.R;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.dtos.Country;

import java.util.ArrayList;

public class SearchRateListAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<Country> list;

    public SearchRateListAdapter(ArrayList<Country> paramArrayList, Context paramContext) {
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
            paramView = LayoutInflater.from(ctx).inflate(R.layout.search_rate_row_view, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.country = ((FontTextView) paramView.findViewById(R.id.searchrate_country));
            viewholder.price = (FontTextView) paramView.findViewById(R.id.searchrate_price);
            viewholder.country_name_header = (FontTextView) paramView.findViewById(R.id.searchrate_country_header);
            viewholder.coutry_rate_header = (FontTextView) paramView.findViewById(R.id.searchrate_price_header);
            viewholder.header_view = (RelativeLayout) paramView.findViewById(R.id.search_rate_header_view);
            viewholder.row_view = (RelativeLayout) paramView.findViewById(R.id.search_rate_row_view);
            paramView.setTag(viewholder);
        }

        ViewHolder viewholder = (ViewHolder) paramView.getTag();
        Country cdto = list.get(paramInt);
        if (cdto.getIsSection()) {

            viewholder.header_view.setVisibility(View.VISIBLE);
            viewholder.row_view.setVisibility(View.GONE);
            viewholder.country_name_header.setText(cdto.getCountryName());
            viewholder.coutry_rate_header.setText(cdto.getCurrency() + "/min");
        } else {
            viewholder.header_view.setVisibility(View.GONE);
            viewholder.row_view.setVisibility(View.VISIBLE);
            viewholder.country.setText(cdto.getCountryName());
            viewholder.price.setText(cdto.getPrice());
        }
        return paramView;
    }

    static class ViewHolder {
        public FontTextView country;
        public FontTextView price;
        public FontTextView country_name_header;
        public FontTextView coutry_rate_header;

        public RelativeLayout header_view, row_view;
    }
}