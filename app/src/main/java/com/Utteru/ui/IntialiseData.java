package com.Utteru.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.Utteru.R;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessDataDto;
import com.Utteru.dtos.VerifiedData;
import com.Utteru.userService.UserService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by root on 12/11/14.
 */
public class IntialiseData {

    ArrayList<AccessDataDto> accessdataList;
    ArrayList<AccessDataDto> accessCountries;

    public final static String ACCESS_UPDATED="access_updated";
    Apis apis;
    Context context;
    UserService userService;


    public IntialiseData(Context c) {
        context = c;
        apis = Apis.getApisInstance(context);
        userService = UserService.getUserServiceInstance(context);

    }


    public void initAccessData() {
        accessdataList = new ArrayList<>();

        if(!Prefs.getUserType(context).equals("4")&&Prefs.getResellerID(context).equals("2")) {
            new GetAllCountries().execute();
        }

    }


    public void initVerifiedData() {

        if(!Prefs.getUserType(context).equals("4")&&Prefs.getResellerID(context).equals("2")) {
            new GetAllVerifiedData().execute();
        }
    }

    private void insertAccessDataIntoDb() {


        long delete_status = userService.deleteAllAccessData();
        Log.e("delete status", "delete" + delete_status);

        for (AccessDataDto accessDataDto : accessdataList) {
            Log.e("inserting state", "" + accessDataDto.getState());

            userService.addAccessData(accessDataDto);

        }

    }

    class GetAllCountries extends AsyncTask<Void, Void, Void> {
        String response;
        Boolean iserror;

        @Override
        protected void onPostExecute(Void aVoid) {

            if (accessCountries != null && accessCountries.size() > 0) {

                new GetStatedAccessNumber().execute();
            }
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {

            response = apis.getAllCountry();

            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;
                JSONArray japarent = null;
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

                        japarent = joparent.getJSONArray(VariableClass.ResponseVariables.CONTENT);

                        int count = japarent.length();

                        AccessDataDto adto;
                        accessCountries = new ArrayList<AccessDataDto>();

                        for (int i = 0; i < count; i++) {
                            jochild = japarent.getJSONObject(i);
                            adto = new AccessDataDto();
                            String countryname = jochild.getString(VariableClass.ResponseVariables.COUNTRYNAME);
                            String countrycode = jochild.getString(VariableClass.ResponseVariables.COUNTRY_CODE);
                            adto.setCountryCode(countrycode);
                            adto.setCountry(countryname);
                            accessCountries.add(adto);


                        }
                    }
                } catch (JSONException e) {
                    iserror = true;
                    e.printStackTrace();
                }
            } else {
                iserror = true;

            }

            return null;
        }
    }

    class GetStatedAccessNumber extends AsyncTask<Void, Void, Void> {
        String response = "";
        Boolean iserror = false;

        @Override
        protected void onPostExecute(Void aVoid) {

            if(!iserror) {
                Intent intent = new Intent();
                intent.setAction(ACCESS_UPDATED);
                context.sendBroadcast(intent);
            }

            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {


            AccessDataDto newobject = null;
            for (AccessDataDto adto : accessCountries) {

                response = apis.getAllState(adto.getCountryCode(), adto.getCountry());

                if (!response.equalsIgnoreCase("")) {
                    JSONObject joparent, jochild, subchild;
                    JSONArray japarent = null;
                    JSONArray jachild = null;
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


                            japarent = joparent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                            int count = japarent.length();

                            for (int i = 0; i < count; i++) {
                                jochild = japarent.getJSONObject(i);

                                String stateName = jochild.getString(VariableClass.ResponseVariables.STATENAME);
                                Log.e("Loop  " + i, "state name" + stateName);

                                adto.setState(stateName);
                                jachild = jochild.getJSONArray(VariableClass.ResponseVariables.ACESSNUMBER);
                                int count_access = jachild.length();

                                for (int j = 0; j < count_access; j++) {
                                    subchild = jachild.getJSONObject(j);
                                    adto.setAccessNumber(subchild.getString(VariableClass.ResponseVariables.accessNumber));
                                    Log.e("adto access number", "" + adto.getAccessNumber() + " " + adto
                                            .getState());
                                    newobject = new AccessDataDto();
                                    newobject.setCountryCode(adto.getCountryCode());
                                    newobject.setCountry(adto.getCountry());
                                    newobject.setState(adto.getState());
                                    newobject.setAccessNumber("+"+adto.getAccessNumber());
                                    accessdataList.add(newobject);


                                }
                            }


                        }
                    } catch (JSONException e) {

                        iserror = true;

                        e.printStackTrace();
                    }
                } else {
                    iserror = true;

                }
            }
            if (!iserror)
                insertAccessDataIntoDb();

            return null;
        }
    }

    class GetAllVerifiedData extends AsyncTask<Void, Void, Void> {

        String response;
        Boolean iserr = false;
        ArrayList<VerifiedData> verifiedNumberlist = new ArrayList<>();
        ArrayList<VerifiedData> verifiedEmailList = new ArrayList<>();

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {

            response = Apis.getApisInstance(context).getNumbermail(2);
            if (!response.equals("")) {
                JSONObject joparent = null;
                JSONObject jochild = null;
                JSONArray japarent = null;
                String defalut_number = null;
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
                        int count = japarent.length();
                        VerifiedData dto;
                        verifiedNumberlist.clear();
                        verifiedEmailList.clear();
                        for (int i = 0; i < count; i++) {
                            jochild = japarent.getJSONObject(i);
                            dto = new VerifiedData();
                            dto.setParticualr(jochild.getString(VariableClass.ResponseVariables.DATA));
                            if (dto.getParticualr().contains("@"))
                                dto.setType(ManageNumbersHome.ISEMAIL);
                            else {
                                dto.setType(ManageNumbersHome.ISNUMBER);
                                dto.setCountryCode(jochild.getString(VariableClass.ResponseVariables.COUNTRY_CODE));
                            }
                            if (1 == (jochild.getInt(VariableClass.ResponseVariables.ISDATADEFAULT)))
                                dto.setState(ManageNumbersHome.ISDEFAULT);

                            else if (1 == (jochild.getInt(VariableClass.ResponseVariables.ISDATAVERIFIED)))
                                dto.setState(ManageNumbersHome.ISVERIFIED);
                            else
                                dto.setState(ManageNumbersHome.ISUNVERIFIED);

                            if (dto.getType() == ManageNumbersHome.ISNUMBER) {
                                verifiedNumberlist.add(dto);
                                if (dto.getState() == ManageNumbersHome.ISDEFAULT) {
                                    defalut_number = dto.getCountryCode() + dto.getParticualr();
                                }
                            } else
                                verifiedEmailList.add(dto);


                            Prefs.setVerifiedEmaillist(context, verifiedEmailList);
                            Prefs.setVerifiedNumberlist(context, verifiedNumberlist);
                            Prefs.setUserDefalutNumber(context, defalut_number);
                        }

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    iserr = true;
                    response = context.getResources().getString(R.string.parse_error);
                    e.printStackTrace();
                }

            } else {
                iserr = true;
                response = context.getResources().getString(R.string.server_error);
            }
            return null;
        }
    }


}


