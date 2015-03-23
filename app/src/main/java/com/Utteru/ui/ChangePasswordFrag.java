package com.Utteru.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordFrag extends Fragment {
    View change_password;
    RelativeLayout error_layout;
    TextView error_textview;
    Button close_em_button, change_pass_button;
    String new_password;
    Context ctx;


    EditText old_pass_ed, new_pass_ed, confirm_pass_ed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        change_password = inflater.inflate(R.layout.change_password, container, false);
        init();
        return change_password;

    }

    @Override
    public void onResume() {


        error_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showErrorMessage(false, "");
            }
        });

        close_em_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showErrorMessage(false, "");
            }
        });

        old_pass_ed.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                    new_pass_ed.requestFocus();
                }
                return false;
            }
        });
        new_pass_ed.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    confirm_pass_ed.requestFocus();

                }
                return false;
            }
        });
        confirm_pass_ed.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                    new_pass_ed.performClick();
                }
                return false;
            }
        });
        new_pass_ed.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showErrorMessage(false, "");

                if (CommonUtility.isNetworkAvailable(ctx)) {

                    String old_password = old_pass_ed.getText().toString();
                    new_password = new_pass_ed.getText().toString();
                    String confirm_password = confirm_pass_ed.getText().toString();

                    if (!old_password.equals("") && !new_password.equals("") && !confirm_password.equals("")) {
                        if (old_password.equals(Prefs.getUserPassword(ctx))) {

                            if (new_password.equals(confirm_password)) {
                                new ChangePasswordTask().execute(null, null, null);
                            } else {
                                confirm_pass_ed.setText("");
                                showErrorMessage(true, getResources().getString(R.string.confirm_error));
                            }
                        } else {
                            old_pass_ed.setText("");
                            showErrorMessage(true, getResources().getString(R.string.old_pass_error));
                        }
                    } else
                        showErrorMessage(true, getResources().getString(R.string.fill_all));

                } else
                    showErrorMessage(true, getResources().getString(R.string.internet_error));

            }
        });

        super.onResume();
    }

    void resetView() {
        old_pass_ed.setText("");
        confirm_pass_ed.setText("");
        new_pass_ed.setText("");
    }

    void init() {
        ctx = getActivity().getBaseContext();
        error_textview = (TextView) change_password.findViewById(R.id.error_text);
        error_layout = (RelativeLayout) change_password.findViewById(R.id.error_layout);
        close_em_button = (Button) change_password.findViewById(R.id.close_button);

        old_pass_ed = (EditText) change_password.findViewById(R.id.cp_old_pwd);
        new_pass_ed = (EditText) change_password.findViewById(R.id.cp_new_pwd);
        confirm_pass_ed = (EditText) change_password.findViewById(R.id.cp_confirm_pwd);

        change_pass_button = (Button) change_password.findViewById(R.id.cp_change_password);

    }

    void showErrorMessage(Boolean showm, String message) {
        if (showm) {
            error_textview.setText(message);
            if (error_layout.getVisibility() == View.GONE)
                CommonUtility.expand(error_layout);
        } else {
            if (error_layout.getVisibility() == View.VISIBLE)
                CommonUtility.collapse(error_layout);

        }
    }

    public class ChangePasswordTask extends AsyncTask<Void, Void, Void> {
        String response = "";
        Boolean iserror = false;

        @Override
        protected void onPostExecute(Void result) {
            CommonUtility.dialog.dismiss();
            if (iserror) {
                showErrorMessage(true, response);
            } else {

                Prefs.setUserPassword(ctx, new_password);
                new SignInScreen().setPassword();
                resetView();

                CommonUtility.showCustomAlert(getActivity(), getString(R.string.success_password)).show();
            }

            change_pass_button.setEnabled(true);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            CommonUtility.show_PDialog(getActivity(), getResources().getString(R.string.please_wait));
            change_pass_button.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {


            response = Apis.getApisInstance(getActivity()).changePassword(new_password);
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

    }

}
