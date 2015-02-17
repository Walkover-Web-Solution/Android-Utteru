package com.Utteru.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.Utteru.R;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.dtos.TransactionLogsDto;


public class TransactionDetailsFragment extends Fragment {

    TransactionLogsDto dto;
    FontTextView date, amount, current_balance, payment_mode, description;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("DetailFragment", "onCreateView()");
        View view = inflater.inflate(R.layout.transactions_detail, container, false);


        return view;
    }

    public void setURLContent(TransactionLogsDto dto) {
        this.dto = dto;
    }

    void init() {
        date = (FontTextView) getView().findViewById(R.id.trans_detail_date);

        amount = (FontTextView) getView().findViewById(R.id.trans_detail_amount);

        current_balance = (FontTextView) getView().findViewById(R.id.trans_detail_current_balance);
        payment_mode = (FontTextView) getView().findViewById(R.id.trans_detail_mode);
        description = (FontTextView) getView().findViewById(R.id.trans_detail_description);
        getActivity().setTitle("Transaction Details");

        date.setText(dto.getDate());
        if (dto.getType() == 0) {
            amount.setText("Talktime shared " + dto.getAmount() + " " + dto.getCurrency());
        } else {
            amount.setText("Talktime received " + dto.getAmount() + " " + dto.getCurrency());
        }

        current_balance.setText("Remaining talktime " + dto.getCurrentBalance() + " " + dto.getCurrency());
        payment_mode.setText(dto.getPayment_mode());
        description.setText(dto.getDescription());

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }


}