package com.Utteru.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.Utteru.R;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.dtos.TransactionLogsDto;

import java.util.ArrayList;

public class TransactionListAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<TransactionLogsDto> list;

    public TransactionListAdapter(ArrayList<TransactionLogsDto> paramArrayList, Context paramContext) {
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

        TransactionLogsDto dto = list.get(paramInt);
        if (paramView == null) {
            paramView = LayoutInflater.from(this.ctx).inflate(R.layout.transaction_row_view, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.amount = ((FontTextView) paramView.findViewById(R.id.t_amount));
            viewholder.date = ((FontTextView) paramView.findViewById(R.id.t_date));
            viewholder.balance = ((FontTextView) paramView.findViewById(R.id.t_balance));

            viewholder.transaction_detail_icon = ((ImageView) paramView.findViewById(R.id.trans_details_icon));
            viewholder.transaction_icon = ((ImageView) paramView.findViewById(R.id.transaction_icon));

            paramView.setTag(viewholder);
        }


        ViewHolder viewholder = (ViewHolder) paramView.getTag();
        viewholder.amount.setText("Talktime " + dto.getAmount() + " " + dto.getCurrency());
        viewholder.date.setText(dto.getDate());
        viewholder.balance.setText("Balance " + dto.getCurrentBalance() + " " + dto.getCurrency());

        if (dto.getType() == 0) {
            viewholder.transaction_icon.setBackgroundResource(R.drawable.round_red_shape_button);


        } else {
            viewholder.transaction_icon.setBackgroundResource(R.drawable.round_green_shape_button);

        }


        return paramView;
    }

    static class ViewHolder {
        public FontTextView amount;
        public FontTextView date;
        public FontTextView balance;
        public ImageView transaction_icon;
        public ImageView transaction_detail_icon;
    }
}