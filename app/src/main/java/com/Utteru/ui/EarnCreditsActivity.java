package com.Utteru.ui;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import com.Utteru.R;
import com.Utteru.adapters.ManageVerifiedDataListAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.CustomKeyboardOther;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.VerifiedData;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class EarnCreditsActivity extends Activity {

    Button invite_now_button, edit_code_btn,edit_done_button, close_em_button;
    EditText uniqu_refferal_code;
    ListView verified_data_listview;
    ArrayList<VerifiedData> verifieddatalist;
    RelativeLayout error_layout;
    ManageVerifiedDataListAdapter adapter;
    Tracker tracker;
    Context ctx = this;
    LinearLayout  promo_tittle,dialpad_layout;
    ImageView backpress;
    CustomKeyboardOther keyboard;
    FontTextView highlightedtext1,total_earn_txt,error_FontTextView,tittleback;
    FrameLayout parent_layout;
    ScrollView earn_scroll;
    String temp="";
    String total_earnings;
    String promoCode = "";
    @Override
    protected void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earn_credits_layout);
        init();
        Mint.initAndStartSession(EarnCreditsActivity.this, "395e969a");
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));
        tracker = MyAnalyticalTracker.getTrackerInstance().getTracker(MyAnalyticalTracker.TrackerName.APP_TRACKER, this);


        tittleback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });


    }

    @Override
    protected void onStart() {
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        // Set screen name.
        tracker.setScreenName("Earn Credit Android");
        // Send a screen view.
        tracker.send(new HitBuilders.AppViewBuilder().build());
        super.onStart();
    }

    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        if (CommonUtility.dialog != null)
            CommonUtility.dialog.dismiss();
        super.onStop();
    }

    @Override
    protected void onPause() {

        Prefs.setLastActivity(ctx, getClass().getName());
        super.onPause();

    }

    @Override
    protected void onResume() {

              //hit api & save promo code in String
              edit_code_btn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                if (!uniqu_refferal_code.getText().equals(""))
                    enableEditing(true);
                 }
                });
        
        edit_done_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //hit update promocode api
                temp = uniqu_refferal_code.getText().toString();

                if (temp != null && !temp.equals("")) {

                    if (!promoCode.equals(temp)) {
                        new UpdatePromoCode().execute();

                    } else {
                        CommonUtility.showCustomAlert(EarnCreditsActivity.this, "Nothing to update").show();
                        enableEditing(false);
                    }
                } else {
                     CommonUtility.showCustomAlert(EarnCreditsActivity.this, "Enter valid promocode").show();

                }


            }
        });



        uniqu_refferal_code.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    uniqu_refferal_code.setEnabled(false);
                    if (!promoCode.equals(""))
                        uniqu_refferal_code.setText(promoCode);
                    else
                        uniqu_refferal_code.setText("");
                    uniqu_refferal_code.setEnabled(false);
                    edit_done_button.setVisibility(View.GONE);
                    edit_code_btn.setVisibility(View.VISIBLE);
                    showKeyBoard(false);


                }
            }
        });

        invite_now_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uniqu_refferal_code.clearFocus();
                startActivity(new Intent(ctx, RefferActivity.class));
                overridePendingTransition(R.anim.animation1, R.anim.animation2);

            }
        });
  
        verified_data_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override

            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                uniqu_refferal_code.clearFocus();

                if (verifieddatalist.get(position).getCountryCode() != null)
                    clipData("", verifieddatalist.get(position).getCountryCode() + verifieddatalist.get(position).getParticualr());
                else
                    clipData("", verifieddatalist.get(position).getParticualr());
                return true;

            }
        });

        verified_data_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout layout = (LinearLayout) view.findViewById(R.id.manange_veri_row);
                layout.setBackgroundColor(getResources().getColor(R.color.whitish_gray));
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

        parent_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(!isPointInsideView(v.getX(), v.getY(), edit_code_btn)){

                  enableEditing(false);

                }
                return false;
            }
        });
        earn_scroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!isPointInsideView(v.getX(), v.getY(), edit_code_btn)){

                    enableEditing(false);

                }
                return false;
            }
        });

        promo_tittle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyBoard(false);
            }
        });

        uniqu_refferal_code.setOnTouchListener(new View.OnTouchListener() {
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

            }
        }
        //hide keyboard
        else {
            Animation bottpmdown = AnimationUtils.loadAnimation(ctx,
                    R.anim.bottom_down);
            if (dialpad_layout.getVisibility() == View.VISIBLE) {
                dialpad_layout.setAnimation(bottpmdown);
                dialpad_layout.setVisibility(View.GONE);


            }
        }
    }

    void init() {
        parent_layout=(FrameLayout)findViewById(R.id.earn_credit_parent);
        total_earn_txt = (FontTextView)findViewById(R.id.total_earn_txt);
        dialpad_layout = (LinearLayout) findViewById(R.id.dialpad_layout);
        promo_tittle = (LinearLayout) findViewById(R.id.promo_tittle);
        invite_now_button = (Button) findViewById(R.id.invite_now_button);
        edit_code_btn = (Button) findViewById(R.id.edit_unique_code_button);
        uniqu_refferal_code = (EditText) findViewById(R.id.unique_id_ed);
        verified_data_listview = (ListView) findViewById(R.id.verified_numbers_list);
        edit_done_button = (Button) findViewById(R.id.edit_unique_code_done_button);
        error_FontTextView = (FontTextView) findViewById(R.id.error_text);
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        close_em_button = (Button) findViewById(R.id.close_button);


        verifieddatalist = new ArrayList<VerifiedData>();

        backpress = (ImageView) findViewById(R.id.auto_detect_country_back);
        tittleback = (FontTextView) findViewById(R.id.earn_credit_title);
        keyboard = new CustomKeyboardOther(EarnCreditsActivity.this, R.id.keyboardview, R.xml.numberic_keypad_other, null);
        keyboard.registerEditText(uniqu_refferal_code.getId(), null);
        earn_scroll=(ScrollView)findViewById(R.id.earn_scroll);
        highlightedtext1 = (FontTextView)findViewById(R.id.highlighted_text1);


        uniqu_refferal_code.setText(Prefs.getPromocode(ctx));
        total_earn_txt.setText(Prefs.getTotalearn(ctx)+" "+Prefs.getUserCurrency(ctx));

        String currency ="";

        if(Prefs.getUserCurrency(ctx).equalsIgnoreCase("INR"))
        {
            currency = "12 INR";

        }else if (Prefs.getUserCurrency(ctx).equalsIgnoreCase("USD")){
            currency = "0.2 USD";

        }else if (Prefs.getUserCurrency(ctx).equalsIgnoreCase("AED")){
            currency = "0.73 AED";

        }




        String text1="<p><b><font size=\"21\" color=\"#f7941e\"> You earn</font> "+currency+" </b> for every friend that joins + <b>10%</b> of their recharge amount</p>\n" +
                "\n" +
                "<p><b><font size=\"21\" color=\"#00aeef\"> Your friend earns </font> 10% extra talktime</b> of their recharge amount.</p>\n";


        highlightedtext1.setText(Html.fromHtml(text1));
        new UpdatePromoCode().execute();
        new getAllNumbers().execute();




    }


    @Override
    public void onBackPressed() {
        if (dialpad_layout.getVisibility() == View.VISIBLE) {
            showKeyBoard(false);


        } else {
            Intent menu = new Intent(this, MenuScreen.class);
            menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(menu);
            // super.onBackPressed();
            overridePendingTransition(R.anim.animation3, R.anim.animation4);
        }

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


    void clipData(String label, String text) {
        int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.HONEYCOMB) {
            ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText("text to clip");
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("text label", "text to clip");
            clipboard.setPrimaryClip(clip);
        }
        CommonUtility.showCustomAlertCopy(this, text);
    }

    public class UpdatePromoCode extends AsyncTask<Void, Void, Void> {
        String response = "";
        Boolean iserror = false;
        @Override
        protected void onPostExecute(Void result) {

            edit_done_button.setEnabled(true);
            if (iserror) {

                showErrorMessage(true, response);

            } else {

                total_earn_txt.setText(total_earnings+" "+Prefs.getUserCurrency(ctx));
                uniqu_refferal_code.setText(promoCode);
                enableEditing(false);
            }
            CommonUtility.dialog.dismiss();
            super.onPostExecute(result);
        }
        @Override
        protected void onPreExecute() {

            CommonUtility.show_PDialog(ctx, getString(R.string.please_wait));
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params) {

            response = Apis.getApisInstance(ctx).earnCreditPromo(temp);
            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;
                JSONArray japarent;
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
                        jochild = japarent.getJSONObject(0);
                        promoCode = jochild.getString(VariableClass.ResponseVariables.PROMOCODE);
                        total_earnings=jochild.getString(VariableClass.ResponseVariables.TOTALEARN);
                        Prefs.setPromocode(ctx, promoCode);
                        Prefs.setTotalearn(ctx,total_earnings);
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

    class getAllNumbers extends AsyncTask<Void, Void, Void> {

        String response;
        Boolean iserr = false;
        ArrayList<VerifiedData> templist = new ArrayList<>();


        @Override
        protected void onPostExecute(Void result) {

            templist = Prefs.getVerifiedNumberList(ctx);

            if (templist != null && templist.size() > 0) {
                templist.addAll(Prefs.getVerifiedEmailList(ctx));
                verified_data_listview.setVisibility(View.VISIBLE);
                verifieddatalist.clear();

                for (VerifiedData vdto : templist) {
                    if (vdto.getState() == ManageNumbersHome.ISVERIFIED || vdto.getState() == ManageNumbersHome.ISDEFAULT) {
                        verifieddatalist.add(vdto);
                    }
                }
                adapter = new ManageVerifiedDataListAdapter(verifieddatalist, ctx);
                verified_data_listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                verified_data_listview.invalidateViews();

            } else {
                verified_data_listview.setVisibility(View.GONE);
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {


            templist = Prefs.getVerifiedNumberList(ctx);
            if (templist != null && templist.size() > 0) {


                templist.addAll(Prefs.getVerifiedEmailList(ctx));
                verified_data_listview.setVisibility(View.VISIBLE);
                verifieddatalist.clear();

                for (VerifiedData vdto : templist) {
                    if (vdto.getState() == ManageNumbersHome.ISVERIFIED || vdto.getState() == ManageNumbersHome.ISDEFAULT) {
                        verifieddatalist.add(vdto);
                    }

                }
                adapter = new ManageVerifiedDataListAdapter(verifieddatalist, ctx);
                verified_data_listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                verified_data_listview.invalidateViews();

            } else {
                verified_data_listview.setVisibility(View.GONE);
            }
            new IntialiseData(ctx).initVerifiedData();

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

    }
    public static boolean isPointInsideView(float x, float y, View view){
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        //point is inside view bounds
        if(( x > viewX && x < (viewX + view.getWidth())) &&
                ( y > viewY && y < (viewY + view.getHeight()))){
            return true;
        } else {
            return false;
        }
    }

    void enableEditing(Boolean enable)
    {

        if(enable)
        {
            uniqu_refferal_code.setEnabled(enable);
            uniqu_refferal_code.requestFocus();
            edit_done_button.setVisibility(View.VISIBLE);
            edit_code_btn.setVisibility(View.GONE);
            uniqu_refferal_code.setSelection(uniqu_refferal_code.getText().length());


        }
        else{
            uniqu_refferal_code.setText(promoCode);
            uniqu_refferal_code.setEnabled(enable);
            uniqu_refferal_code.clearFocus();
            edit_done_button.setVisibility(View.GONE);
            edit_code_btn.setVisibility(View.VISIBLE);

        }
    }



}
