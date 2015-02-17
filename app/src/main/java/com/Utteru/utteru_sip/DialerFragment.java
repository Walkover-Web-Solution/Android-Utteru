package com.Utteru.utteru_sip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Utteru.R;
import com.Utteru.adapters.RecentCallsAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.CustomKeyboard;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessContactDto;
import com.Utteru.dtos.Country;
import com.Utteru.dtos.RecentCallsDto;
import com.Utteru.ui.CsvReader;
import com.Utteru.ui.MenuScreen;
import com.Utteru.ui.PhoneBookActivity;
import com.Utteru.ui.SearchListDialer;
import com.Utteru.userService.UserService;
import com.Utteru.util.Line;
import com.Utteru.util.Network;
import com.Utteru.util.SettingConfig;
import com.Utteru.util.UserInfo;
import com.portsip.PortSipEnumDefine;
import com.portsip.PortSipErrorcode;
import com.portsip.PortSipSdk;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;


public class DialerFragment extends Fragment {
    Context context;
    PortSipSdk mSipSdk;

    public static final int REQUEST_CODE_SC = 1118;
    final int REQUEST_SELECT_CONTACT = 1119;

    UtteruSipCore utteruSipCore;
    String statuString;
    String LogPath = null;
    TextView mCountryName;
    ListView calllogs_listview;
    LinearLayout dialpad_layout;
    LinearLayout header_layout;
    RelativeLayout header_menu;
    ImageButton gotocontact;
    String countryCode, countryName;
    ImageButton showdialpadbutton;
    ImageButton delete_button;
    ImageButton hide_keyboard;
    EditText number_text;
    String licenseKey = "1Xh01QTg3OTEyNUZEOTQwOTM4QzlFRDg3NDNFOTQyQjIzNkBGRTc2NzcwNDBCNzcwRDdGOUNCQzhGMzM4MjdDOTY1OUA3QjlFMzQxQkYzMUY0RTM3QTUyNjgzM0IzRjFENjQ4RkBGMjg4MDU0OTBFMDAxRDIyNTVDNkMwQTFFNDk0RTZFMg";
    OnCallListener listener;
    ArrayList<RecentCallsDto> logs_list;
    RecentCallsAdapter adapter;

    CustomKeyboard keyboard;
    //    LinearLayout sip_error_layout;
    RelativeLayout sip_online_layout;
    Button mSelectCountry;
    ImageView backpress, gototohome;
    FontTextView tittleback;
    FontTextView country_arrow;
    FontTextView errorDetailMsg,no_logs_found;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        if (CommonUtility.isNetworkAvailable(context))
            online();
        else
            CommonUtility.showCustomAlertError(getActivity(), getString(R.string.internet_error));


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnCallListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnURLSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("DetailFragment", "onCreateView()");
        View view = inflater.inflate(R.layout.dialer_layout, container, false);
        return view;
    }


    @Override
    public void onResume() {


        Log.e("on Resume", "on Resume");

//        if (utteruSipCore != null && utteruSipCore.isOnline()) {
//            sip_error_layout.setVisibility(View.GONE);
//            if (showdialpadbutton.getVisibility() != View.VISIBLE)

        if (utteruSipCore.isOnline())
            showKeyBoard(true);
//            sip_online_layout.setEnabled(true);


//        } else {
//            online();
////            sip_error_layout.setVisibility(View.VISIBLE);
//            sip_online_layout.setEnabled(false);
//            if (showdialpadbutton.getVisibility() == View.VISIBLE)
//                showKeyBoard(false);
//        }
        this.delete_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Log.e("inside click listner", "click listner");


                Editable editable = number_text.getText();
                int start = number_text.getSelectionStart();
                if (editable != null && start > 0) editable.delete(start - 1, start);
                int length = number_text.getText().toString().length();
                if (!((length) > 0))
                    delete_button.setVisibility(View.INVISIBLE);
                else
                    delete_button.setVisibility(View.VISIBLE);


            }
        });

        this.delete_button.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                number_text.setText("");
                delete_button.setVisibility(View.GONE);
                return true;
            }
        });

        mSelectCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SearchListDialer.class);
                startActivityForResult(i, REQUEST_CODE_SC);
                getActivity().overridePendingTransition(R.anim.animation1, R.anim.animation2);

            }
        });
        mCountryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SearchListDialer.class);
                startActivityForResult(i, REQUEST_CODE_SC);
                getActivity().overridePendingTransition(R.anim.animation1, R.anim.animation2);

            }
        });

        country_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SearchListDialer.class);
                startActivityForResult(i, REQUEST_CODE_SC);
                getActivity().overridePendingTransition(R.anim.animation1, R.anim.animation2);
            }
        });
        showdialpadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialpad_layout.getVisibility() == View.GONE) {
                    showKeyBoard(true);
                } else {
                    if (number_text != null && number_text.getText().toString().length() > 0) {
                        Prefs.setLastCallNo(context, number_text.getText().toString());
                        makeCall(mSelectCountry.getText().toString() + number_text.getText().toString());
                        number_text.setText("");
                    } else {
                        if (number_text.getText().toString().equals("")) {
                            number_text.setText(Prefs.getLastCallNo(context).toString());
                            Prefs.setLastCallNo(context, "");

                        }

                    }


                }
            }

        });

        hide_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showKeyBoard(false);
            }
        });


        calllogs_listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (dialpad_layout.getVisibility() == View.VISIBLE) {
                    showKeyBoard(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


        calllogs_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                RecentCallsDto dto = (RecentCallsDto) parent.getItemAtPosition(position);
                makeCall(dto.getNumber());

            }
        });

        gotocontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectContact(REQUEST_SELECT_CONTACT);
            }
        });

        number_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Log.e("on ext change", "" + s.toString());
                if (s.toString() != null && !s.toString().equals("")) {
                    delete_button.setVisibility(View.VISIBLE);
                    ArrayList<RecentCallsDto> filterList = new ArrayList<RecentCallsDto>();
                    String searchBy = s.toString();

                    if (CommonUtility.isNumeric(searchBy)) {
                        Log.e("Number search ", "Number search ");
                        for (RecentCallsDto a : logs_list) {

                            if (a.getNumber().contains(searchBy)) {
                                filterList.add(a);
                                continue;
                            }


                        }
                        if (filterList.size() > 0) {
                            calllogs_listview.setVisibility(View.VISIBLE);
                            calllogs_listview.setAdapter(new RecentCallsAdapter(filterList, context, listener, utteruSipCore, mSipSdk));
                        } else
                            calllogs_listview.setVisibility(View.GONE);
                    } else {

                        Log.e("Name search ", "Name search ");
                        for (RecentCallsDto a : logs_list) {
                            Log.e("search by", "" + searchBy);
                            if (a.getName().toLowerCase().startsWith(searchBy.toLowerCase())) {
                                filterList.add(a);
                                continue;
                            }


                        }
                        if (filterList.size() > 0) {
                            calllogs_listview.setVisibility(View.VISIBLE);
                            calllogs_listview.setAdapter(new RecentCallsAdapter(filterList, context, listener, utteruSipCore, mSipSdk));
                        } else
                            calllogs_listview.setVisibility(View.GONE);


                    }
                } else {

                    calllogs_listview.setVisibility(View.VISIBLE);
                    calllogs_listview.setAdapter(new RecentCallsAdapter(logs_list, context, listener, utteruSipCore, mSipSdk)
                    );

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        sip_error_layout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                return true;
//            }
//        });
        tittleback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menu = new Intent(getActivity(), MenuScreen.class);
                menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(menu);
                // getActivity(). onBackPressed();

            }
        });
        gototohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        super.onResume();
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent
            data) {


        Log.e("inisie activity result", "inside activity result " + reqCode + " " + resultCode);

        {

            if (resultCode == Activity.RESULT_OK) {
                if (reqCode == REQUEST_SELECT_CONTACT) {

                    AccessContactDto selected_con = (AccessContactDto) data.getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);


                    if (selected_con != null) {

                        Log.e("number from phone", "" + selected_con.getMobile_number());
                        String con_number = selected_con.getMobile_number();

                        con_number = con_number.replace("//s+", "");

                        Log.e("number after replace", "" + con_number);
                        if (con_number != null && !con_number.equals("")) {



                            ArrayList<String> pass_number = new ArrayList<>();

                            pass_number = CommonUtility.splitCodeFromNumber(con_number);
                            String only_number = pass_number.get(0);
                            String only_code = pass_number.get(1);
                            Log.e("number",only_number);
                            Log.e("number_code",only_code);
                            String countryName = "";
                           if (!only_code.equals("") && !only_code.equals("")) {


                               if(SearchListDialer.country_list==null&&SearchListDialer.country_list.size()==0)
                               {
                                   SearchListDialer.country_list= new CsvReader().readCsv(getActivity(), new CsvReader().getUserCountryIso(getActivity()), false);
                               }
                               for(Iterator<Country> i = SearchListDialer.country_list.iterator(); i.hasNext(); ) {
                                   Country item = i.next();
                                   if (item.getCountryCode().equals("+"+only_code))
                                   {
                                       countryName = item.getCountryName();
                                       break;
                                   }
                               }
                                mSelectCountry.setText("+"+only_code);
                                number_text.setText(only_number);
                                mCountryName.setText(countryName);


                            }
                            else
                            {
                                if(con_number.startsWith("0"))
                                con_number = con_number.replaceFirst("0","");


                                number_text.setText(con_number);
                            }

                            // makeCall(con_number);

                        } else {

                            //CommonUtility.showCustomAlertError(getActivity(), getString(R.string.no_contact_found));
                        }
                    }

//                    else {
//                        CommonUtility.showCustomAlertError(getActivity(), getString(R.string.no_contact_found));
//
//
//                    }
                } else if (reqCode == REQUEST_CODE_SC) {


                    countryCode = data.getExtras().getString(VariableClass.Vari.COUNTRYCODE);
                    countryName = data.getExtras().getString(VariableClass.Vari.COUNTRYNAME);

                    Prefs.setUserDialerCountryCode(context, countryCode);
                    Prefs.setUserDialerCountryName(context, countryName);
                    Log.e("country code and name ", "data " + countryCode + countryName);
                    if (countryCode != null && !countryCode.equals("")) {

                        mSelectCountry.setText(countryCode);
                        mCountryName.setText("(" + countryCode + ")" + " " + countryName);

                    }

                }
            }

        }

        super.onActivityResult(reqCode, resultCode, data);

    }

    public interface OnCallListener {

        public void onCall(UtteruSipCore myapp, PortSipSdk sdk, String number, int action, RecentCallsDto dto);

    }


    public void setbuttonview(View v) {
        mSelectCountry.setVisibility(View.GONE);
    }

    void init() {


        context = getActivity().getBaseContext();

        utteruSipCore = ((UtteruSipCore) context.getApplicationContext());
        mSipSdk = utteruSipCore.getPortSIPSDK();

        number_text = (EditText) getView().findViewById(R.id.callee);
        errorDetailMsg = (FontTextView) getView().findViewById(R.id.error_detail_message);
        dialpad_layout = (LinearLayout) getView().findViewById(R.id.dialpad_layout);
        header_layout = (LinearLayout) getView().findViewById(R.id.header_layout);
        header_menu = (RelativeLayout) getView().findViewById(R.id.header_menu);
        showdialpadbutton = (ImageButton) getView().findViewById(R.id.show_dial_pad);
        calllogs_listview = (ListView) getView().findViewById(R.id.call_logs_list);
        mCountryName = (TextView) getView().findViewById(R.id.country_name);
        no_logs_found=(FontTextView)getView().findViewById(R.id.no_logs_found);
        country_arrow = (FontTextView)getView().findViewById(R.id.down_arrow);

        SearchListDialer.country_list = new ArrayList<Country>();
        SearchListDialer.country_list = new CsvReader().readCsv(getActivity(), new CsvReader().getUserCountryIso(getActivity()), false);

        mSelectCountry = (Button) getView().findViewById(R.id.select_country);


        if (!Prefs.getUserDialerCountryCode(context).equals("")) {
            countryName = Prefs.getUserDialerCountryName(context);
            countryCode = Prefs.getUserDialerCountryCode(context);
        } else {
            countryName = Prefs.getUserCountryName(context);
            countryCode = Prefs.getUserCountryCode(context);
        }

        mSelectCountry.setText("+" + countryCode);
        mCountryName.setText("(" + "+" + countryCode + ")" + countryName + "");
        logs_list = new ArrayList<>();

        logs_list = UserService.getUserServiceInstance(context).getAllRecentCallByGroup();
        if (logs_list.size() > 0) {
            adapter = new RecentCallsAdapter(logs_list, context, listener, utteruSipCore, mSipSdk);
            calllogs_listview.setAdapter(adapter);
            no_logs_found.setVisibility(View.GONE);
        } else {
            calllogs_listview.setVisibility(View.GONE);
            no_logs_found.setVisibility(View.VISIBLE);
        }
        gotocontact = (ImageButton) getView().findViewById(R.id.goto_contacts);

        delete_button = (ImageButton) getView().findViewById(R.id.clear_text);
        keyboard = new CustomKeyboard(getActivity(), R.id.keyboardview, R.xml.numberic_keypad);
        keyboard.registerEditText(number_text.getId());
        hide_keyboard = (ImageButton) getView().findViewById(R.id.hide_keyboard);
//        sip_error_layout = (LinearLayout) getView().findViewById(R.id.sip_error_layout);
        sip_online_layout = (RelativeLayout) getView().findViewById(R.id.sip_online_layout);
        sip_online_layout.setEnabled(false);
        //add header in listview
       /* inflater =getActivity().getLayoutInflater();
        listHeaderView = (RelativeLayout)inflater.inflate(
                R.layout.calllog_list_header, null);*/


        backpress = (ImageView) getView().findViewById(R.id.auto_detect_country_back);
        gototohome = (ImageView) getView().findViewById(R.id.auto_detect_country_home);
        tittleback = (FontTextView) getView().findViewById(R.id.auto_detect_coutry_header);
        if (VariableClass.Vari.CALL_SHOP_USER == true) {

            Log.e("var_value", "is true");
            hideIcons();
        }

    }

    private void hideIcons() {
        backpress.setVisibility(View.GONE);
        gototohome.setVisibility(View.GONE);
        tittleback.setClickable(false);
    }


    void showTips(String text) {

        CommonUtility.showCustomAlert(getActivity(), text);
    }

    void updateStatus() {

        if (utteruSipCore.isOnline()) {
            statuString = null;
            CommonUtility.showCustomAlert(getActivity(), statuString);
        } else {
            if (statuString != null) {
                CommonUtility.showCustomAlertError(getActivity(), statuString);
            } else {
                CommonUtility.showCustomAlertError(getActivity(), "User not registered");
            }
        }
    }

    private UserInfo saveUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(Prefs.getUserSipName(context));
        userInfo.setUserPwd(Prefs.getUserSipPassword(context));
        if (Prefs.getUserCountryCode(getActivity()).equals("971")) {
            userInfo.setTranType(PortSipEnumDefine.ENUM_TRANSPORT_PERS);
            SettingConfig.setTransType(context, PortSipEnumDefine.ENUM_TRANSPORT_PERS);
            SettingConfig.setSrtpType(context, PortSipEnumDefine.ENUM_SRTPPOLICY_NONE, mSipSdk);
            userInfo.setSipServer(Prefs.PERSE_SIP);
            userInfo.setSipPort(Integer.parseInt(Prefs.PERSE_PORT));
        } else {
            userInfo.setTranType(PortSipEnumDefine.ENUM_TRANSPORT_UDP);
            SettingConfig.setTransType(context, PortSipEnumDefine.ENUM_TRANSPORT_UDP);
            SettingConfig.setSrtpType(context, PortSipEnumDefine.ENUM_SRTPPOLICY_NONE, mSipSdk);
            userInfo.setSipServer(Prefs.SIP_SERVER);
            userInfo.setSipPort(Integer.parseInt(Prefs.SIP_PORT));
        }

        SettingConfig.setUserInfo(context, userInfo);
        return userInfo;
    }

    private int online() {
        int result = setUserInfo();

        Log.e("online status result ", "" + result);
        if (result == PortSipErrorcode.ECoreErrorNone) {

            result = mSipSdk.registerServer(90, 3);
            Log.e("online registration result ", "" + result);
            if (result != PortSipErrorcode.ECoreErrorNone) {
                statuString = "register server failed";
                updateStatus();
            }
        } else {

            updateStatus();
        }
        return result;

    }

    private void offline() {

        Line[] mLines = utteruSipCore.getLines();
        for (int i = Line.LINE_BASE; i < Line.MAX_LINES; ++i) {
            if (mLines[i].getRecvCallState()) {
                mSipSdk.rejectCall(mLines[i].getSessionId(), 486);
            } else if (mLines[i].getSessionState()) {
                mSipSdk.hangUp(mLines[i].getSessionId());
            }

            mLines[i].reset();
        }
        utteruSipCore.setOnlineState(false);
        updateStatus();
        mSipSdk.unRegisterServer();
        mSipSdk.DeleteCallManager();
    }


    int setUserInfo() {
        Environment.getExternalStorageDirectory();
        LogPath = Environment.getExternalStorageDirectory().getAbsolutePath() + '/';

        String localIP = new Network(context).getLocalIP(false);// ipv4
        int localPort = new Random().nextInt(4940) + 5060;
        UserInfo info = saveUserInfo();

        if (info.isAvailable()) {
            Log.e("info available", "info available");
            mSipSdk.CreateCallManager(context.getApplicationContext());// step 1

            int result = mSipSdk.initialize(info.getTransType(),
                    PortSipEnumDefine.ENUM_LOG_LEVEL_NONE, LogPath,
                    Line.MAX_LINES, " UTTERU ANDROID ",
                    0, 0);// step 2
            Log.e("sdk state", "" + result);
            if (result != PortSipErrorcode.ECoreErrorNone) {

                return result;
            }


            Log.e("setting codec","setting codec ");



            int nSetKeyRet = mSipSdk.setLicenseKey(licenseKey);// step 3
            if (nSetKeyRet == PortSipErrorcode.ECoreTrialVersionLicenseKey) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Prompt").setMessage(R.string.trial_version_tips);
                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            } else if (nSetKeyRet == PortSipErrorcode.ECoreWrongLicenseKey) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Prompt").setMessage(R.string.wrong_lisence_tips);
                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                return -1;
            }
            Log.e("all is well ", "going for registration");

            result = mSipSdk.setUser(info.getUserName(), info.getUserDisplayName(), info.getAuthName(), info.getUserPassword(),

                    localIP, localPort, info.getUserdomain(), info.getSipServer(), info.getSipPort(),
                    info.getStunServer(), info.getStunPort(), null, 5060);// step 4

            Log.e("sent registration state", "" + result);
            if (result != PortSipErrorcode.ECoreErrorNone) {
                statuString = "setUser resource sucess";
                return result;
            } else {


            }
        } else {
            Log.e("info not available", "info not available");
            return -1;
        }

        SettingConfig.setAVArguments(context, mSipSdk);
        return PortSipErrorcode.ECoreErrorNone;
    }


    public void selectContact(int action) {


        Intent intent = new Intent(context, PhoneBookActivity.class);
        startActivityForResult(intent, action);
        getActivity().overridePendingTransition(R.anim.animation1, R.anim.animation2);

    }

    public void showKeyBoard(Boolean showKeyBoard) {
        //show keyboard
        if (showKeyBoard) {
            if (number_text != null && number_text.getText().toString().length() > 0) {
                delete_button.setVisibility(View.VISIBLE);
            }
            Animation bottomUp = AnimationUtils.loadAnimation(context,
                    R.anim.bottom_up);
            Animation bottom_down = AnimationUtils.loadAnimation(context,
                    R.anim.abc_slide_in_top);

            dialpad_layout.setAnimation(bottomUp);
            dialpad_layout.setVisibility(View.VISIBLE);

            header_layout.setAnimation(bottom_down);
            header_layout.setVisibility(View.VISIBLE);
            header_menu.setVisibility(View.GONE);
            showdialpadbutton.setBackgroundResource(R.drawable.call_btn);
            calllogs_listview.setClickable(false);
            calllogs_listview.setFocusable(false);
            hide_keyboard.setVisibility(View.VISIBLE);

            number_text.requestFocus();
            // calllogs_listview.removeHeaderView(listHeaderView);


        }
        //hide keyboard
        else {
            Animation bottpmdown = AnimationUtils.loadAnimation(context,
                    R.anim.bottom_down);
            Animation bottomup = AnimationUtils.loadAnimation(context,
                    R.anim.abc_slide_out_top);

            dialpad_layout.setAnimation(bottpmdown);
            dialpad_layout.setVisibility(View.GONE);

            header_layout.setAnimation(bottomup);
            header_layout.setVisibility(View.GONE);
            header_menu.setVisibility(View.VISIBLE);
            showdialpadbutton.setBackgroundResource(R.drawable.dialpad_upw);
            calllogs_listview.setClickable(true);
            calllogs_listview.setFocusable(true);
            hide_keyboard.setVisibility(View.GONE);
            number_text.setText("");
            getView().clearFocus();
            calllogs_listview.requestFocus();

            //     calllogs_listview.addHeaderView(listHeaderView);


        }
    }

    void makeCall(String number) {

        if (!(number.equals("")) && number.length() > 8 && number.length() < 18 && CommonUtility.isNumeric(number)) {


            if (utteruSipCore.isOnline())
                listener.onCall(utteruSipCore, mSipSdk, number, 0, null);
            else
                CommonUtility.showCustomAlertError(getActivity(), "You are not connected");
        } else {
            CommonUtility.showCustomAlertError(getActivity(), "Please enter valid Number ");
        }
    }

    public void showDetails(RecentCallsDto dto) {


        listener.onCall(utteruSipCore, mSipSdk, null, 1, dto);

    }

    public void onRegisterStatusReceive(String message, int code) {
        Log.e("registration response ", "" + code);
        if (code == 200) {

//            sip_error_layout.setVisibility(View.GONE);
            sip_online_layout.setEnabled(true);

            showKeyBoard(true);

        } else {


            errorDetailMsg.setVisibility(View.VISIBLE);
            errorDetailMsg.setText(errorDetailMsg.getText() + " " + message);
            online();
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