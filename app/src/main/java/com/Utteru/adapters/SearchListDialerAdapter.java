
package com.Utteru.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.Utteru.R;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.dtos.Country;

import java.util.ArrayList;
import java.util.Collections;

public class SearchListDialerAdapter extends BaseAdapter implements Filterable {
    Context ctx;
    ArrayList<Country> list;
    ArrayList<Country>suggestions,countryAllClone;
    public static ArrayList<Country>selected;

    public SearchListDialerAdapter(ArrayList<Country> paramArrayList, Context paramContext) {
        this.list = paramArrayList;
        this.ctx = paramContext;
        countryAllClone=(ArrayList<Country>)list.clone();
        suggestions=new ArrayList<Country>();
        Collections.sort(list);
        selected= new ArrayList<Country>();

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
            paramView = LayoutInflater.from(this.ctx).inflate(R.layout.searchlist_row_dialer, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.name = ((FontTextView) paramView.findViewById(R.id.search_text));
            paramView.setTag(viewholder);
        }
        ViewHolder viewholder = (ViewHolder) paramView.getTag();
        Country cdto = list.get(paramInt);

        viewholder.name.setText(cdto.getCountryName() + " ( " + cdto.getCountryCode() + " )");


        return paramView;
    }

    static class ViewHolder {
        public FontTextView name;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {

        public String convertResultToString(Object resultValue) {
            String str = ((Country) (resultValue)).getCountryName();

            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {

                suggestions.clear();
                for (Country contacts : countryAllClone) {
                    if (contacts.getCountryName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(contacts);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {


            if (results != null && results.count > 0) {
//                clear();
//                for (Contacts c : filteredList) {
//                    add(c);
//                }
                list = (ArrayList<Country>) results.values;


                notifyDataSetChanged();
            }
        }
    };

    }