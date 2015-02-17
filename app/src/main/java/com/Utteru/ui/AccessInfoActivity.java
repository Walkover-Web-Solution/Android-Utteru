package com.Utteru.ui;

import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.Utteru.R;
import com.Utteru.adapters.AccessNumberInfoAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.Constants;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessContactDto;
import com.Utteru.dtos.AccessDataDto;
import com.Utteru.userService.UserService;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.splunk.mint.Mint;
import com.stripe.android.compat.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by root on 12/15/14.
 */
public class AccessInfoActivity extends Activity {

    final int REQUEST_PICK_CONTACT = 002;
    Button assign_to_contact;
    FontTextView access_number;
    ArrayList<AccessContactDto> list;
    AccessNumberInfoAdapter adapter;
    Context ctx;
    Button unassign;
    AccessDataDto selectedcon;
    int action = 02;
    RelativeLayout error_layout;
    FontTextView error_FontTextView;
    FontTextView access_info_text_view;
    Button close_em_button;
    ArrayList<String> option_list;
    SwipeMenuListView listview;
    FontTextView tittle;

    ImageView backButton, gotohome;
    RoundedImageView call_accessNumber;
    Button chooseOtherCountry;

    int count = 0;
    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            // create "open" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(R.color.red);

            // set item width
            deleteItem.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            // set item title
            deleteItem.setTitle("  Unassign   ");
            // set item title fontsize
            deleteItem.setTitleSize(18);
            // set item title font color
            deleteItem.setTitleColor(Color.WHITE);


            // add to menu
            menu.addMenuItem(deleteItem);


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.access_info);
        init();
        Mint.initAndStartSession(AccessInfoActivity.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));



    }

    @Override
    protected void onResume() {
        call_accessNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtility.makeCall(ctx,selectedcon.getAccessNumber());
            }
        });

        assign_to_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CommonUtility.isNetworkAvailable(ctx))
                    selectContact();
                else
                    showErrorMessage(true, getString(R.string.internet_error));
            }
        });

        unassign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call unassign
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    showErrorMessage(false, "");
                    new Unassign().execute();
                } else
                    showErrorMessage(true, getString(R.string.internet_error));
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

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CommonUtility.makeCall(ctx, list.get(position).getAccess_number() + "," + list.get(position).getExtension_number());


            }
        });
        listview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        new Unassign().execute();
                        break;
                }

                return false;
            }
        });
        gotohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(AccessInfoActivity.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tittle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        chooseOtherCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chooseCountry = new Intent(AccessInfoActivity.this, AllCountryActivity.class);
                chooseCountry.putExtra(VariableClass.Vari.SOURCECLASS, "");
                startActivity(chooseCountry);
                AccessInfoActivity.this.finish();
            }
        });
        access_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtility.makeCall(ctx,selectedcon.getAccessNumber());
            }
        });

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.animation3, R.anim.animation4);
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent
            data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            AccessContactDto selected_con = (AccessContactDto) data.getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
            if (selected_con != null) {
                String con_number = CommonUtility.validateNumberForUI(selected_con.getMobile_number(),ctx);
                String con_name = CommonUtility.validateText(selected_con.getDisplay_name());


                if (con_number != null && !con_number.equals("")) {



                    //send activity to select extension number
                    Intent selectextIntent = new Intent(ctx, SelectExtensionAI.class);
                    selectextIntent.putExtra(VariableClass.Vari.SELECTEDDATA, selectedcon);
                    selectextIntent.putExtra(VariableClass.Vari.SELECTEDNUMBER, con_number);
                    if (con_number == null || con_name.equals("")) {
                        con_name = "Not Available";
                    }
                    selectextIntent.putExtra(VariableClass.Vari.SELECTEDNAME, con_name);
                    overridePendingTransition(R.anim.animation1, R.anim.animation2);

                    startActivity(selectextIntent);
                }
            } else {

                CommonUtility.showCustomAlert(AccessInfoActivity.this, getString(R.string.no_contact_found));

            }

        }
    }


    void init() {

        ctx = this;

        backButton = (ImageView) findViewById(R.id.access_infor_back);
        gotohome = (ImageView) findViewById(R.id.access_info_gotohome);
        chooseOtherCountry = (Button) findViewById(R.id.choose_another_country);

        call_accessNumber = (RoundedImageView) findViewById(R.id.call_access_number);

        option_list = new ArrayList<>();
        option_list.add(getString(R.string.make_call_via_access_number));
        option_list.add(getString(R.string.unassign_access_number));
        option_list.add(getString(R.string.cancel_string));
        access_info_text_view = (FontTextView) findViewById(R.id.access_info);
tittle= (FontTextView) findViewById(R.id.change_password_title);

        Bundle bundle = getIntent().getExtras();
        selectedcon = (AccessDataDto) bundle.getSerializable(VariableClass.Vari.SELECTEDDATA);
        unassign = (Button) findViewById(R.id.unassign);

        listview = (SwipeMenuListView) findViewById(R.id.access_info_listview);
        listview.setMenuCreator(creator);
        assign_to_contact = (Button) findViewById(R.id.assigncontact);
        access_number = (FontTextView) findViewById(R.id.access_number);
        error_FontTextView = (FontTextView) findViewById(R.id.error_text);
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        close_em_button = (Button) findViewById(R.id.close_button);
        access_number.setText(selectedcon.getAccessNumber());
        access_info_text_view.setText("(" + selectedcon.getCountry() + "," + selectedcon.getState() + ")");
        list = new ArrayList<AccessContactDto>();
        list = UserService.getUserServiceInstance(ctx).getAllAccessContactsByAccessNumber(selectedcon.getAccessNumber());
        if(list.size()!=0) {


            call_accessNumber.setVisibility(View.VISIBLE);
            if (list.size() == 1 && list.get(count).getExtension_number().equals(VariableClass.Vari.DEDICATED)) {
                assign_to_contact.setVisibility(View.GONE);
                unassign.setVisibility(View.VISIBLE);

            } else {
                assign_to_contact.setVisibility(View.VISIBLE);
                unassign.setVisibility(View.GONE);

            }


        }
        else {
            call_accessNumber.setVisibility(View.VISIBLE);
        }
        adapter = new AccessNumberInfoAdapter(list, ctx);
        listview.setAdapter(adapter);

        listview.setCloseInterpolator(new BounceInterpolator());
        listview.setOpenInterpolator(new BounceInterpolator());

    }

    public void selectContact() {
        Intent intent = new Intent(ctx, PhoneBookActivity.class);
        startActivityForResult(intent, action);
        overridePendingTransition(R.anim.animation1, R.anim.animation2);

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

    class Unassign extends AsyncTask<Void, Void, Void> {
        String response = null;
        Boolean iserror = false;
        AccessContactDto dto = null;

        @Override
        protected void onPostExecute(Void aVoid) {
            unassign.setEnabled(true);

            if (iserror) {
                showErrorMessage(true, response);
            } else {

                list.remove(dto);
                adapter = new AccessNumberInfoAdapter(list, ctx);
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                assign_to_contact.setVisibility(View.VISIBLE);
                unassign.setVisibility(View.GONE);


                Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
                Bundle bundle = new Bundle();
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                ContentResolver.requestSync(account, ContactsContract.AUTHORITY, bundle);


                CommonUtility.showCustomAlert(AccessInfoActivity.this, response);


            }
            count = 0;

            CommonUtility.dialog.dismiss();


            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {


            response = Apis.getApisInstance(ctx).editContact(dto.getDisplay_name(), dto.getContact_id(), "", CommonUtility.validateNumberForApi(dto.getMobile_number()), "", "");

            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;
                try {
                    joparent = new JSONObject(response);
                    //failed response
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                        iserror = true;
                    } else {

                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
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

            CommonUtility.show_PDialog(ctx, getString(R.string.please_wait));
            unassign.setEnabled(true);
            dto = list.get(count);
            super.onPreExecute();
        }
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


}
