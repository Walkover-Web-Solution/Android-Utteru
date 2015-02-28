package com.Utteru.ui;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.Utteru.R;
import com.Utteru.adapters.LongCodesAdapter;
import com.Utteru.adapters.ManageVerifiedDataListAdapter;
import com.Utteru.adapters.SearchRateListAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessDataDto;
import com.Utteru.dtos.Country;
import com.Utteru.dtos.LongCodesDto;
import com.Utteru.dtos.RecentCallsDto;
import com.Utteru.dtos.VerifiedData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


public class CallForwardFrag extends Fragment {
    FontTextView tvMsg;
    Button mSelectCountry;
    //View manage_numbers;
    LongCodesAdapter adapter;
    LongCodesDto selectedto;
    Context ctx;

    RelativeLayout headerlistview;
    ListView longcode_listview;
    ProgressDialog dialog;
    ArrayList<LongCodesDto> datalist;
    // Store instance variables
    private String title;
    private int page;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.call_forward_frag, container, false);
        ctx = getActivity().getBaseContext();
        tvMsg = (FontTextView) v.findViewById(R.id.msg_call_fwd);
        mSelectCountry = (Button) v.findViewById(R.id.select_country_btn);
        dialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(true);
        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);

        ctx = getActivity().getBaseContext();
        longcode_listview = (ListView) v.findViewById(R.id.alllong_codes);
        headerlistview = (RelativeLayout) v.findViewById(R.id.lsthead);
        datalist = new ArrayList<>();


        new getAllLongCodes().execute();
        return v;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//

    }

    @Override
    public void onResume() {
        super.onResume();

        mSelectCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotocountry = new Intent(getActivity(), SelectCountryCallForward.class);
                startActivity(gotocountry);
            }
        });
        longcode_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedto = datalist.get(position);
                parent.getItemAtPosition(position).equals("null");
                if (selectedto.getLongCodeNo().equals("null") || selectedto.getLongCodeNo().equals("") && selectedto.getDestinationNo().equals("null")) {

                    Intent assignno = new Intent(getActivity(), CallForwardtoNumber.class);

                    startActivity(assignno);
                }
                if (!(selectedto.getLongCodeNo().equals("null"))) {
                    Intent details = new Intent(getActivity(), LongCodeLogsActivity.class);
                    details.putExtra(VariableClass.Vari.SELECTEDDATA, selectedto);
                    Log.e("setting data", "" + selectedto.getLongCodeNo());
                    startActivity(details);

                }

            }
        });
    }

    public static CallForwardFrag newInstance(int page, String title) {

        CallForwardFrag f = new CallForwardFrag();
        Bundle b = new Bundle();
        b.putString("someTitle", title);
        f.setArguments(b);

        return f;
    }

    class getAllLongCodes extends AsyncTask<Void, Void, Void> {

        String response = null;
        Boolean iserr = false;


        @Override
        protected void onPostExecute(Void result) {

            if (iserr)
                showErrorMessage(true, response);
            else {

                if (datalist.size() > 0) {
                    Log.e("search rate ", "search rate list updating ");
                    longcode_listview.setVisibility(View.VISIBLE);
                    adapter = new LongCodesAdapter(datalist, ctx);
                    longcode_listview.setAdapter(adapter);
                    adapter.notifyDataSetInvalidated();
                    longcode_listview.invalidateViews();

                } else {
                    longcode_listview.setVisibility(View.GONE);
                }


            }
//            CommonUtility.dialog.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            //  showErrorMessage(false, "");
            // CommonUtility.show_PDialog(ctx, getResources().getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {


            response = Apis.getApisInstance(ctx).getLongCodes();

            if (!response.equals("")) {
                JSONObject joparent = null;
                JSONObject jochild = null;
                JSONArray japarent = null;
                JSONArray jachild = null;
                try {
                    joparent = new JSONObject(response);
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserr = true;
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }
                    //success response
                    else if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                        japarent = joparent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                        //  joparent= japarent.getJSONObject();
                        LongCodesDto longCodesDto, senddto;

                        ArrayList<LongCodesDto> statelist;


                        for (int i = 0; i < japarent.length(); i++) {
                            longCodesDto = new LongCodesDto();
                            jochild = japarent.getJSONObject(i);
                            longCodesDto.setLongCodeNo(jochild.getString(VariableClass.ResponseVariables.LONG_CODE_NO));
                            longCodesDto.setExpiryDate(jochild.getString(VariableClass.ResponseVariables.EXPIRY_DATE));
                            longCodesDto.setAssignId(jochild.getString(VariableClass.ResponseVariables.ASSIGN_ID));
                            longCodesDto.setCountry(jochild.getString(VariableClass.ResponseVariables.COUNTRY));
                            longCodesDto.setState(jochild.getString(VariableClass.ResponseVariables.STATE));
                            longCodesDto.setDestinationNo(jochild.getString(VariableClass.ResponseVariables.DESTINATION_NUMBER));
                            Prefs.setAssignId(getActivity(), longCodesDto.getAssignId());
                            datalist.add(longCodesDto);
                            Log.e("list", "adding country" + longCodesDto.getLongCodeNo());


                        }


                    }
                } catch (JSONException e) {
                    iserr = true;
                    response = getResources().getString(R.string.parse_error);
                    e.printStackTrace();
                }

            } else {
                iserr = true;
                response = getResources().getString(R.string.server_error);
            }
            return null;
        }

    }

    void showErrorMessage(Boolean showm, String message) {
      /*  if (showm) {
            error_FontTextView.setText(message);
            if (error_layout.getVisibility() == View.GONE)
                CommonUtility.expand(error_layout);
        } else {
            if (error_layout.getVisibility() == View.VISIBLE)
                CommonUtility.collapse(error_layout);

        }*/
    }

    @Override
    public void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
    }
}