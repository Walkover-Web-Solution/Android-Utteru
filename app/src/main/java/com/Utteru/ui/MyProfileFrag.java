package com.Utteru.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyProfileFrag extends Fragment {
    View my_profile_view;
    Button listen_voice, gender_switch;

    int listen_voice_state;
    EditText profile_name;

    Context context;
    int gender;
    String name;
    Boolean isupdate = false;
    Button update_profile_button;
    ProgressDialog dialog;

    FontTextView gender_status, listen_status;

    RelativeLayout error_layout;
    FontTextView error_FontTextView;
    Button closer_err;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        my_profile_view = inflater.inflate(R.layout.my_profile, container, false);
        init();

        return my_profile_view;
    }

    @Override
    public void onResume() {


        listen_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (listen_voice_state == 1) {


                    listen_voice_state = 0;
                    listen_status.setText("Off");
                    listen_voice.setBackgroundResource(R.drawable.off);
                } else if (listen_voice_state == 0) {
                    listen_voice_state = 1;
                    listen_status.setText("On");
                    listen_voice.setBackgroundResource(R.drawable.on);
                }
            }
        });
        gender_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (gender == 1) {

                    gender = 0;
                    gender_status.setText("Male");
                    gender_switch.setBackgroundResource(R.drawable.off);
                } else if (gender == 0) {

                    gender = 1;
                    gender_status.setText("Female");
                    gender_switch.setBackgroundResource(R.drawable.on);
                }

            }
        });


        profile_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    name = profile_name.getText().toString();
                }
            }
        });
        update_profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showErrorMessage(false, "");
                name = profile_name.getText().toString();
                if (!name.equals("")) {
                    isupdate = true;
                    new ListenPin().execute();
                } else {
                    showErrorMessage(true, "Name can not be blank");
                }

            }
        });
        error_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
            }
        });

        closer_err.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
            }
        });


        super.onResume();
    }


    void showErrorMessage(Boolean showm, String message) {
        if (showm) {
            error_FontTextView.setText(message);
            if (error_layout.getVisibility() == View.GONE)
                CommonUtility.expand(error_layout);
        } else {
            if (error_layout.getVisibility() == View.VISIBLE)
                CommonUtility.collapse(error_layout);

        }
    }

    void init() {

        context = getActivity().getBaseContext();
        error_FontTextView = (FontTextView) my_profile_view.findViewById(R.id.error_text);
        error_layout = (RelativeLayout) my_profile_view.findViewById(R.id.error_layout);
        closer_err = (Button) my_profile_view.findViewById(R.id.close_button);
        gender_status = (FontTextView) my_profile_view.findViewById(R.id.gender_status);
        listen_status = (FontTextView) my_profile_view.findViewById(R.id.listen_status);
        update_profile_button = (Button) my_profile_view.findViewById(R.id.update_profile);
        listen_voice = (Button) my_profile_view.findViewById(R.id.switch_listner);
        gender_switch = (Button) my_profile_view.findViewById(R.id.gender_switch);
        listen_voice_state = Prefs.getListenVoice(context);

        gender = Prefs.getGender(context);

        profile_name = (EditText) my_profile_view.findViewById(R.id.profile_name);

        name = Prefs.getUserDisplay(context);

        if (!name.equals("")) {

            profile_name.setText(name);
            profile_name.setSelection(profile_name.getText().length());
        }


        if (gender == 1) {
            gender_switch.setBackgroundResource(R.drawable.on);
            gender_status.setText("Female");
        } else {
            gender_switch.setBackgroundResource(R.drawable.off);
            gender_status.setText("Male");
        }

        if (listen_voice_state == 1) {
            listen_voice.setBackgroundResource(R.drawable.on);
            listen_status.setText("On");

        } else {
            listen_voice.setBackgroundResource(R.drawable.off);
            listen_status.setText("Off");
        }

    }

    @Override
    public void onStop() {


        super.onStop();
    }

    class ListenPin extends AsyncTask<Void, Void, Void> {

        String response;
        Boolean iserror = false;

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();

            Log.e("gender fro api ", "gender");
            if (iserror) {
                showErrorMessage(true, response);
            }
            if (!name.equals("")) {

                profile_name.setText(name);
                profile_name.setSelection(profile_name.getText().length());
            }
            if (gender == 1) {

                gender_switch.setBackgroundResource(R.drawable.on);
                gender_status.setText("Female");
            } else {

                gender_switch.setBackgroundResource(R.drawable.off);
                gender_status.setText("Male");
            }

            if (listen_voice_state == 1) {
                listen_voice.setBackgroundResource(R.drawable.on);
                listen_status.setText("On");
            } else {
                listen_voice.setBackgroundResource(R.drawable.off);
                listen_status.setText("Off");
            }

            Prefs.setListenVoice(context, listen_voice_state);
            Prefs.setGender(context, gender);
            Prefs.setUserDisplay(context, name);


            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity(), R.style.MyTheme);
            dialog.setMessage(getString(R.string.please_wait));
            dialog.setCancelable(false);

            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject joparent, jochild;
            JSONArray jarray;
            response = Apis.getApisInstance(context).listenVoice(listen_voice_state, gender, name, isupdate);
            if (!response.equals("")) {
                try {
                    joparent = new JSONObject(response);
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserror = true;
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    } else {
                        jarray = joparent.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                        jochild = jarray.getJSONObject(0);
                        name = jochild.getString(VariableClass.ResponseVariables.USER_DISPLAY_NAME);
                        listen_voice_state = jochild.getInt(VariableClass.ResponseVariables.LISTENVOICE);
                        gender = jochild.getInt(VariableClass.ResponseVariables.GENDER);


                    }
                } catch (JSONException e) {
                    iserror = true;
                    response = getResources().getString(R.string.parse_error);
                }
            } else {
                iserror = true;
                response = getResources().getString(R.string.server_error);
            }
            return null;
        }
    }

}
