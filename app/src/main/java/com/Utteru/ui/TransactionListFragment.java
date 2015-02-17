package com.Utteru.ui;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.Utteru.R;
import com.Utteru.adapters.TransactionListAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.TransactionLogsDto;
import com.Utteru.userService.UserService;
import com.stripe.android.compat.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class TransactionListFragment extends Fragment {

    OnURLSelectedListener mListener;
    SwipeRefreshLayout refreshLayout;
    ListView listView;
    ArrayList<TransactionLogsDto> list;
    TransactionListAdapter adapter;
    Context ctx;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        displayListView();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View view = inflater.inflate(R.layout.transaction_list_view, container, false);

        return view;
    }

    @Override
    public void onResume() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    new getTransactions().execute();
                } else
                    CommonUtility.showCustomAlert(getActivity(), getString(R.string.internet_error));

            }
        });

        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnURLSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnURLSelectedListener");
        }
    }

    private void displayListView() {

        ctx = getActivity();
        getActivity().setTitle("Transactions");
        refreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        listView = (ListView) getView().findViewById(R.id.transaction_list);

        list = UserService.getUserServiceInstance(ctx).getAllTransaction();
        if (list != null && list.size() != 0) {
            adapter = new TransactionListAdapter(list, ctx);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } else {
            if (CommonUtility.isNetworkAvailable(ctx))
                new getTransactions().execute();
            else
                CommonUtility.showCustomAlertError(getActivity(), getString(R.string.internet_error)).show();

        }

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // Send the URL to the host activity
                mListener.onURLSelected(list.get(position));

            }
        });

    }

    public interface OnURLSelectedListener {
        public void onURLSelected(TransactionLogsDto dto);
    }

    public class getTransactions extends AsyncTask<Void, Void, Void> {
        String response = null;
        Boolean iserror = false;

        @Override
        protected void onPostExecute(Void aVoid) {

            if (iserror) {
                CommonUtility.showCustomAlertError(getActivity(), response);
            } else {
                if (list.size() != 0) {
                    adapter = new TransactionListAdapter(list, ctx);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }
            }
            refreshLayout.setEnabled(true);
            refreshLayout.setRefreshing(false);
            listView.setEnabled(true);
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {

            response = Apis.getApisInstance(ctx).getTransactionApi(1);
            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;

                try {
                    joparent = new JSONObject(response);
                    //failed response
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                        iserror = true;
                    }
                    //success response
                    else if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                        JSONArray array = joparent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                        int count = array.length();
                        TransactionLogsDto dto;
                        list.clear();
                        UserService.getUserServiceInstance(ctx).deleteTransaction();
                        for (int i = 0; i < count; i++) {
                            dto = new TransactionLogsDto();
                            jochild = array.getJSONObject(i);
                            dto.setCurrent_balance("" + jochild.getString(VariableClass.ResponseVariables.TRAN_CURRENTBAL));
                            dto.setDate(jochild.getString(VariableClass.ResponseVariables.TRAN_DATE));
                            dto.setTransaction_id(jochild.getString(VariableClass.ResponseVariables.TRAN_ID));
                            dto.setPayment_mode(jochild.getString(VariableClass.ResponseVariables.TRAN_MODE));
                            dto.setCurrency(jochild.getString(VariableClass.ResponseVariables.TRAN_CURRECNY));
                            dto.setDescription(jochild.getString(VariableClass.ResponseVariables.TRAN_DESCRIPTION));
                            dto.setAmount("" + (jochild.getDouble(VariableClass.ResponseVariables.TRAN_AMOUNT)));
                            dto.setUser_name("" + (jochild.getString(VariableClass.ResponseVariables.TRAN_NAME)));
                           if(jochild.has(VariableClass.ResponseVariables.TRAN_ADMINNAME))
                           dto.setAdmin_name("" + (jochild.getString(VariableClass.ResponseVariables.TRAN_ADMINNAME)));
                           else
                           dto.setAdmin_name("");

                            String des = dto.getDescription();
                            if (des != null) {
                                des = des.toLowerCase();
                                if (des.contains("to")) {
                                    dto.setType(0);
                                } else {
                                    dto.setType(1);
                                }

                            }

                            String paymentDescription=null;
                            switch(dto.getPayment_mode())
                            {
                                case "UtterU" :
                                    paymentDescription ="Added "+dto.getAmount()+" "+dto.getCurrency()+" in account by admin "+dto.getAdmin_name()+".";
                                    break;
                                case "signUp" :
                                    paymentDescription = "Free " + dto.getAmount() + " " + dto.getCurrency() + " added as Sign up talktime.";
                                    break;
                                case "Pin" :
                                    paymentDescription = " "+dto.getAmount()+" "+dto.getCurrency()+" talktime added on recharge done via PIN.";
                                    break;
                                case "Earn Credit" :
                                    paymentDescription = dto.getAmount()+" "+dto.getCurrency()+" earned through recharge done by a referral.";
                                    break;
                                case "Calling Card" :
                                    paymentDescription = "Recharge done via calling card. Talktime added: "+dto.getAmount()+" "+dto.getCurrency()+".";
                                    break;
                                case "stripe" :
                                    paymentDescription = "Recharge done via debit/credit card. Talktime added: "+dto.getAmount()+" "+dto.getCurrency()+".";
                                    break;
                                case "Share Talktime" :
                                    if(dto.getDescription().contains("to"))
                                        paymentDescription = "Given "+dto.getAmount()+" "+dto.getCurrency()+" to "+dto.getUser_name()+" via Share talktime.";
                                    else
                                        paymentDescription = "Received "+dto.getAmount()+" "+dto.getCurrency()+" from "+dto.getUser_name()+" via share talktime. ";
                                    break;
                                case "paypal" :
                                    paymentDescription = "Recharge done via paypal. Talktime added: "+dto.getAmount()+" "+dto.getCurrency()+".";
                                    break;
                                case "cashu" :
                                    paymentDescription = "Recharge done via cashu. Talktime added: "+dto.getAmount()+" "+dto.getCurrency()+".";
                                    break;
                                default :
                                    paymentDescription = "Recharge done. Talktime added: "+dto.getAmount()+" "+dto.getCurrency()+".";
                                    break;
                            }

                            dto.setDescription(paymentDescription);

                            UserService.getUserServiceInstance(ctx).addTransaction(dto);
                            list.add(dto);


                        }


                    }
                } catch (JSONException e) {
                    iserror = true;
                    response = getResources().getString(R.string.parse_error);
                    e.printStackTrace();
                }
            } else {
                iserror = true;
                response = getResources().getString(R.string.server_error);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            refreshLayout.setEnabled(false);
            listView.setEnabled(false);
            super.onPreExecute();
        }
    }
}