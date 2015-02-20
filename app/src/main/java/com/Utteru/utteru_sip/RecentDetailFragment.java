package com.Utteru.utteru_sip;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.Utteru.R;
import com.Utteru.adapters.RecentDetailsAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessContactDto;
import com.Utteru.dtos.RecentCallsDto;
import com.Utteru.ui.Apis;
import com.Utteru.ui.BaseActivity;
import com.Utteru.ui.MenuScreen;
import com.Utteru.ui.TwoWayHome;
import com.Utteru.userService.UserService;
import com.portsip.PortSipSdk;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class  RecentDetailFragment extends Fragment {
    ListView dialer_recent_list;
    RecentDetailsAdapter adapter;
    ArrayList<RecentCallsDto> list;
    Context ctx;
    RecentCallsDto selected_dto;
    FontTextView title;
    ImageView menu;
    CallDetails listener;
    UtteruSipCore utteruSipCore;
    PortSipSdk mSipSdk;
    ImageButton call,show_profile;
    ProgressDialog dialog;
    ImageView back_button;
    FontTextView nothing_found;



    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        utteruSipCore = ((UtteruSipCore) getActivity().getApplicationContext());
        mSipSdk = utteruSipCore.getPortSIPSDK();
        init();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("DetailFragment", "onCreateView()");
        View view = inflater.inflate(R.layout.dialer_calls_detailled_layout, container, false);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        try {
            listener = (CallDetails) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnURLSelectedListener");
        }
        super.onAttach(activity);
    }

    public void setData(RecentCallsDto dto) {


        selected_dto = dto;

    }
    @Override
    public void onResume() {



        menu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent startmenu = new Intent(ctx, MenuScreen.class);
                startmenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startmenu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(startmenu);
                getActivity().overridePendingTransition(R.anim.animation3, R.anim.animation4);
            }
        });

        dialer_recent_list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                     makecall(list.get(arg2));


            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makecall(selected_dto);
            }
        });

        show_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProfile  (selected_dto);
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               onBackPress();
            }
        });

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPress();
            }
        });


        super.onResume();
    }


    public interface CallDetails {

        public void CallFromDetails(UtteruSipCore myapp, PortSipSdk sdk, RecentCallsDto dto , int action );

    }
    void init() {

        nothing_found = (FontTextView)getView().findViewById(R.id.nothing_found);
        call = (ImageButton)getView().findViewById(R.id.call_current_number);
        show_profile = (ImageButton)getView().findViewById(R.id.open_profile);
        dialer_recent_list = (ListView) getView().findViewById(R.id.dialer_detail_list);
        title = (FontTextView)getView(). findViewById(R.id.dialer_con_name);
        back_button = (ImageView)getView().findViewById(R.id.back_button);
        list = new ArrayList<>();
        ctx = getActivity().getBaseContext();
        title.setText(selected_dto.getName());
        menu = (ImageView) getView().findViewById(R.id.menu_button);

        dialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);

        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);

        new getDetailsTask().execute(null,null,null);
    }

 public void makecall(RecentCallsDto dto)
 {

     Log.e("number to call ",""+dto.getNumber());
     listener.CallFromDetails(utteruSipCore,mSipSdk,dto,0);

 }

    public void showProfile(RecentCallsDto dto)
    {
        listener.CallFromDetails(utteruSipCore,mSipSdk,dto,1);

    }

    public void onBackPress()
    {
       getActivity(). getSupportFragmentManager().popBackStack();
    }

    class getDetailsTask extends AsyncTask<Void,Void,Void>
    {

        Boolean iserror=false;
        String response= null;

        @Override
        protected void onPreExecute() {
          dialog.show();

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

             response = Apis.getApisInstance(getActivity()).getCallLogsDetails(selected_dto.getSource_number(),selected_dto.getNumber());
             JSONObject joparent;
             JSONObject child;
             JSONArray jarray;

             if(response!=null)
             {
                 try {

                 joparent = new JSONObject(response);

                     if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                         iserror = true;
                         joparent = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                         response = joparent.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                     }
                     else{
                         jarray = joparent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                         int length =jarray.length();
                         RecentCallsDto dto ;
                         for(int i =0;i<length;i++)
                         {
                            child = jarray.getJSONObject(i);
                             dto = new RecentCallsDto();
                             dto.setName(selected_dto.getName());
                             dto.setSource_number(selected_dto.getSource_number());
                             dto.setNumber(selected_dto.getNumber());


                             dto.setPrice(child.getString(VariableClass.ResponseVariables.PRICE));
                             dto.setTime(child.getString(VariableClass.ResponseVariables.CALL_TIME));
                             dto.setDuration(child.getString(VariableClass.ResponseVariables.DURATION));
                             list.add(dto);
                         }
                     }

                 }catch (Exception e )
                 {
                     e.printStackTrace();
                     iserror = true;
                     response = getString(R.string.parse_error);
                 }

             }
            else{
                iserror=true;
                response = getString(R.string.server_error);

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


            dialog.dismiss();
            if(iserror)
            {
                CommonUtility.showCustomAlertForContactsError(ctx,response);
            }
            else {

                if(list.size()!=0)
                {
                    dialer_recent_list.setVisibility(View.VISIBLE);
                    adapter = new RecentDetailsAdapter(list,ctx);
                    dialer_recent_list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    nothing_found.setVisibility(View.GONE);
                }
                else
                    dialer_recent_list.setVisibility(View.GONE);
                nothing_found.setVisibility(View.VISIBLE);
            }
            super.onPostExecute(aVoid);
        }
    }
}


