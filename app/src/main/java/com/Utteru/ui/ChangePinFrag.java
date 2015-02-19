package com.Utteru.ui;

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
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.CustomKeyboardOther;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.userService.UserService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChangePinFrag extends Fragment {
    View change_pin;
    RelativeLayout error_layout;
    FontTextView error_FontTextView;
    Button close_em_button, change_pin_button;
    String new_pin;
    Context ctx;
    EditText cp_new_pin_code;
    CustomKeyboardOther keyboard;
    EditText cp_confirm_pin_code;
    RelativeLayout root_layout;
    LinearLayout dialpad_layout;
    FontTextView lastupdateFontTextView;
    SimpleDateFormat ft;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        change_pin = inflater.inflate(R.layout.change_pin, container, false);
        init();
        return change_pin;
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
        lastupdateFontTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), CplogsActivity.class));
                getActivity().overridePendingTransition(R.anim.animation1, R.anim.animation2);
            }
        });

        root_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyBoard(false);
            }
        });
        cp_new_pin_code.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                showKeyBoard(true);
                return false;
            }
        });
        cp_confirm_pin_code.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                showKeyBoard(true);
                return false;
            }
        });
        change_pin_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showKeyBoard(false);
                return false;
            }
        });


        change_pin_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
                if (CommonUtility.isNetworkAvailable(ctx)) {

                    String confirm_password = cp_confirm_pin_code.getText().toString();
                    new_pin = cp_new_pin_code.getText().toString();

                    if (!new_pin.equals("") && new_pin.length() == 4) {
                        if (!confirm_password.equals("") && confirm_password.length() == 4) {
                            if (new_pin.equals(confirm_password)) {
                                new ChangePinTask().execute(null, null, null);
                            } else {
                                cp_confirm_pin_code.setText("");


                                showErrorMessage(true, getResources().getString(R.string.confirm_error));
                            }
                        } else
                            showErrorMessage(true, getResources().getString(R.string.empty_pin));
                    } else
                        showErrorMessage(true, getResources().getString(R.string.empty_pin));
                } else
                    showErrorMessage(true, getResources().getString(R.string.internet_error));

            }
        });


        super.onResume();
    }

    public void showKeyBoard(Boolean showKeyBoard) {
        //show keyboard
        if (showKeyBoard) {

            Animation bottomUp = AnimationUtils.loadAnimation(ctx,
                    R.anim.bottom_up);

            if (dialpad_layout.getVisibility() == View.GONE) {
                dialpad_layout.setAnimation(bottomUp);
                dialpad_layout.setVisibility(View.VISIBLE);
                getView().clearFocus();
            }
        }
        //hide keyboard
        else {
            Animation bottpmdown = AnimationUtils.loadAnimation(ctx,
                    R.anim.bottom_down);
            if (dialpad_layout.getVisibility() == View.VISIBLE) {
                dialpad_layout.setAnimation(bottpmdown);
                dialpad_layout.setVisibility(View.GONE);

                getView().clearFocus();

            }
        }
    }

    void init() {

        root_layout = (RelativeLayout) change_pin.findViewById(R.id.change_pin_root);
        ft = new SimpleDateFormat("E yyyy.MM.dd hh:mm:ss");
        lastupdateFontTextView = (FontTextView) change_pin.findViewById(R.id.cp_last_update);
        error_FontTextView = (FontTextView) change_pin.findViewById(R.id.error_text);
        error_layout = (RelativeLayout) change_pin.findViewById(R.id.error_layout);
        close_em_button = (Button) change_pin.findViewById(R.id.close_button);
        change_pin_button = (Button) change_pin.findViewById(R.id.reset_pin);
        ctx = getActivity().getBaseContext();
        cp_new_pin_code = (EditText) change_pin.findViewById(R.id.cp_new_pin_code);
        cp_confirm_pin_code = (EditText) change_pin.findViewById(R.id.cp_confirm_pin_code);
        dialpad_layout = (LinearLayout) change_pin.findViewById(R.id.dialpad_layout);
        keyboard = new CustomKeyboardOther(getActivity(), R.id.keyboardview, R.xml.numberic_key_only, change_pin);
        keyboard.registerEditText(cp_new_pin_code.getId(), change_pin);
        keyboard.registerEditText(cp_confirm_pin_code.getId(), change_pin);
        ctx = getActivity().getBaseContext();
        String lastupdate = UserService.getUserServiceInstance(ctx).getLastupdateCp();
        if (lastupdate != null && !lastupdate.equals(""))
            lastupdateFontTextView.setText(lastupdateFontTextView.getText() + " :  " + lastupdate);
        else
            lastupdateFontTextView.setVisibility(View.GONE);


    }

    void resetView() {
        cp_confirm_pin_code.setText("");


        cp_new_pin_code.setText("");


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

    public class ChangePinTask extends AsyncTask<Void, Void, Void> {
        String response = "";
        Boolean iserror = false;

        @Override
        protected void onPostExecute(Void result) {

            if (iserror) {
                showErrorMessage(true, response);

            } else {
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
                Prefs.setUserPassword(ctx, new_pin);
                CommonUtility.showCustomAlert(getActivity(), getString(R.string.success_pin)).show();
                cp_new_pin_code.setText("");
                cp_confirm_pin_code.setText("");
                cp_confirm_pin_code.setHint("XXXX");
                cp_new_pin_code.setHint("XXXX");

                String lastupdate = UserService.getUserServiceInstance(ctx).getLastupdateCp();
                if (lastupdate != null && !lastupdate.equals(""))
                    lastupdateFontTextView.setText(lastupdateFontTextView.getText() + " :  " + lastupdate);
                else
                    lastupdateFontTextView.setVisibility(View.GONE);

            }
            CommonUtility.dialog.dismiss();
            change_pin_button.setEnabled(true);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            CommonUtility.show_PDialog(getActivity(), getResources().getString(R.string.please_wait));
            change_pin_button.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            response = Apis.getApisInstance(getActivity()).changePassword(new_pin);
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
                        UserService.getUserServiceInstance(ctx).addCpLog(ft.format(new Date()));
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

    public void onBackPress() {
        if (dialpad_layout.getVisibility() == View.VISIBLE) {
            showKeyBoard(false);
            Log.e("hiding  keyboard", "hiding keyboard");

        } else {
            Intent menu = new Intent(getActivity(), MenuScreen.class);
            menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(menu);
            getActivity().finish();
        }

    }
}
