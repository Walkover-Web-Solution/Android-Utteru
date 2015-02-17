package com.Utteru.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.Utteru.R;
import com.Utteru.adapters.ViewPagerManageVerifiedDataAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.Country;
import com.splunk.mint.Mint;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ManageNumbersHome extends FragmentActivity {
    public static final int REQUEST_CODE = 11;
    public static final int RESULT_OK = 11;
    public static final int ISNUMBER = 0;
    public static final int ISEMAIL = 1;
    public static final int ISDEFAULT = 2;
    public static final int ISVERIFIED = 1;
    public static final int ISUNVERIFIED = 0;

    RelativeLayout error_layout;
    FontTextView error_FontTextView;
    Button close_em_button;

    FontTextView title;
    ViewPagerManageVerifiedDataAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;
    FontTextView show_add_layout;
    Button countrycode, add_number, add_email;
    String data, countrycode_string;
    LinearLayout addnew_layout;
    Boolean isaddlayoutvisible = false;
    EditText number_ed, email_ed;
    Context ctx;
    LinearLayout add_num_layout, add_email_id_layout;
    ProgressDialog dialog;

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    @Override
    public void onBackPressed() {

        if (addnew_layout.getVisibility() == View.VISIBLE) {
            showAddNewLayout(false);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.animation3, R.anim.animation4);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_numbers_home);

        init();
        new IntialiseData(ctx).initVerifiedData();

    }

    @Override
    protected void onResume() {
        show_add_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


                if (mPager.getCurrentItem() == 0) {
                    add_num_layout.setVisibility(View.VISIBLE);
                    add_email_id_layout.setVisibility(View.GONE);
                    title.setText(getResources().getText(R.string.my_num));
                    email_ed.setText("");
                } else if (mPager.getCurrentItem() == 1) {
                    add_num_layout.setVisibility(View.GONE);
                    number_ed.setText("");
                    add_email_id_layout.setVisibility(View.VISIBLE);
                    title.setText(getResources().getText(R.string.my_emails));
                } else {
                    add_num_layout.setVisibility(View.GONE);
                    add_email_id_layout.setVisibility(View.GONE);
                    title.setText(getResources().getText(R.string.my_profile));
                }
                if (isaddlayoutvisible) {
                    isaddlayoutvisible = false;
                    showAddNewLayout(isaddlayoutvisible);
                } else {
                    isaddlayoutvisible = true;
                    showAddNewLayout(isaddlayoutvisible);

                }
            }


        });

        countrycode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(ctx, SearchListDialer.class);

                startActivityForResult(i, REQUEST_CODE);
            }
        });


        add_number.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (CommonUtility.isNetworkAvailable(ctx)) {
                    data = number_ed.getText().toString();
                    if (!data.equals("")) {
                        countrycode_string = countrycode_string.replaceAll("[+]", "");
                        String check = countrycode_string + data;
                        if (check.length() < 18 && check.length() > 8) {
                            new addData().execute(null, null, null);
                        } else
                            showErrorMessage(true, getResources().getString(R.string.number_not_valid));
                    } else
                        showErrorMessage(true, getResources().getString(R.string.fill_all));
                } else {
                    showErrorMessage(true, getResources().getString(R.string.internet_error));
                }


            }
        });
        add_email.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    data = email_ed.getText().toString();
                    if (isValidEmail(data)) {
                        if (!data.equals("")) {
                            new addData().execute(null, null, null);
                        } else
                            showErrorMessage(true, getResources().getString(R.string.fill_all));

                    } else
                        showErrorMessage(true, getResources().getString(R.string.email_error));

                } else {
                    showErrorMessage(true, getResources().getString(R.string.internet_error));
                }


            }
        });

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {

                showAddNewLayout(false);
                if (arg0 == 0) {
                    title.setText(getResources().getText(R.string.my_num));
                    show_add_layout.setVisibility(View.VISIBLE);
                } else if (arg0 == 1) {
                    title.setText(getResources().getText(R.string.my_emails));
                    show_add_layout.setVisibility(View.VISIBLE);
                } else if (arg0 == 2) {
                    title.setText(getResources().getText(R.string.my_profile));
                    show_add_layout.setVisibility(View.GONE);
                }


                mIndicator.setCurrentItem(arg0);

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

        });
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
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("activity " + requestCode, "activity" + resultCode);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {

            showAddNewLayout(true);
            add_email_id_layout.setVisibility(View.GONE);
            title.setText(getResources().getText(R.string.my_num));
            add_num_layout.setVisibility(View.VISIBLE);
            show_add_layout.setVisibility(View.VISIBLE);
            String countryCode = data.getExtras().getString(VariableClass.Vari.COUNTRYCODE);
            Log.e("country code ", countryCode);
            if (countryCode != null && !countryCode.equals("")) {
                Log.e("country code in manage number", "" + countryCode);
                Prefs.setUserCountryCode(ctx, countryCode);
                countrycode.setText(countryCode);
                countrycode_string = countryCode;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void init() {

        ctx = this;

        Mint.initAndStartSession(ManageNumbersHome.this, "395e969a");
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));
        error_FontTextView = (FontTextView) findViewById(R.id.error_text);
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        close_em_button = (Button) findViewById(R.id.close_button);
        mAdapter = new ViewPagerManageVerifiedDataAdapter(getSupportFragmentManager());

        mPager = (ViewPager) findViewById(R.id.mn_pager);
        title = (FontTextView) findViewById(R.id.manage_numbers_title);
        mPager.setAdapter(mAdapter);
        mIndicator = (CirclePageIndicator) findViewById(R.id.mn_indicator);
        mIndicator.setViewPager(mPager);
        show_add_layout = (FontTextView) findViewById(R.id.mn_show_add_layout);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            int selected_page = bundle.getInt(VariableClass.Vari.ACCESS_TYPE);
            mPager.setCurrentItem(selected_page);
            if (selected_page == 0) {
                title.setText(getResources().getText(R.string.my_num));
            } else if (selected_page == 1) {
                title.setText(getResources().getText(R.string.my_emails));
            } else {
                if (selected_page == 2) {
                    title.setText(getResources().getText(R.string.my_profile));
                    show_add_layout.setVisibility(View.GONE);
                }

            }


        }


        addnew_layout = (LinearLayout) findViewById(R.id.add_new_layout);
        countrycode = (Button) findViewById(R.id.add_new_countrycodebtn);
        countrycode_string = Prefs.getUserCountryCode(ctx);
        countrycode.setText("+" + countrycode_string);
        number_ed = (EditText) findViewById(R.id.add_new_number);
        email_ed = (EditText) findViewById(R.id.add_new_email);
        add_number = (Button) findViewById(R.id.add_new_number_button);
        add_email = (Button) findViewById(R.id.add_new_email_button);
        add_num_layout = (LinearLayout) findViewById(R.id.add_num_layout);
        add_email_id_layout = (LinearLayout) findViewById(R.id.add_email_layout);
        SearchListDialer.country_list = new ArrayList<Country>();
        SearchListDialer.country_list = new CsvReader().readCsv(ctx, new CsvReader().getUserCountryIso(ctx), false);

        dialog = new ProgressDialog(ctx, R.style.MyTheme);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);

        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
    }

    void showAddNewLayout(Boolean showm) {
        if (showm) {
            show_add_layout.setText(getResources().getString(R.string.cancel_add_new_data));
            CommonUtility.expand(addnew_layout);
        } else {
            show_add_layout.setText(getResources().getString(R.string.add_new_data));
            CommonUtility.collapse(addnew_layout);
        }
    }

    @Override
    protected void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
        }
        super.onDestroy();
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

    @Override
    protected void onStop() {

        if (dialog != null)
            dialog.dismiss();
        super.onStop();
    }

    class addData extends AsyncTask<Void, Void, Void> {
        String response = "";
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            add_email.setEnabled(true);
            add_number.setEnabled(true);

            if (iserr) {

                showErrorMessage(true, response);
            } else {
                showAddNewLayout(false);
                showErrorMessage(true, getResources().getString(R.string.success_add));
                startActivity(new Intent(ctx, ManageNumbersHome.class));
                ManageNumbersHome.this.finish();
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {


            dialog.show();


            add_email.setEnabled(false);
            add_number.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            response = Apis.getApisInstance(ctx).addNumberEmail(mPager.getCurrentItem(), data, countrycode_string, "1");
            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;
                try {
                    joparent = new JSONObject(response);
                    //failed response
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserr = true;
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
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


}



