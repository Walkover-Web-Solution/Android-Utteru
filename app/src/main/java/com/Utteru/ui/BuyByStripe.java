package com.Utteru.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.splunk.mint.Mint;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;


/**
 * Created by root on 11/27/14.
 */
public class BuyByStripe extends Activity {

    static final int VALID_CARD = 1;
    static final int VALID_CVC = 2;
    static final int VALID_DATE = 3;
    static final int DEFAULT = 0;
    static final Pattern CARD_PATTERN = Pattern.compile("([0-9]{0,4})|([0-9]{4}-)+|([0-9]{4}-[0-9]{0,4})+");
    final String APIKEY = "pk_live_rzmIEcHzKbPrdBvLv9VFUWWb";
    Stripe stripe;
    Card card;
    Context ctx = this;
    String token_id;
    FontTextView amount_text;
    String amount, currency;
    RelativeLayout error_layout;
    FontTextView error_FontTextView;
    Button close_em_button;
    EditText card_number, expmm, expyy, cvc;
    String card_num_string, expdate_string, cvc_string;
    Button proceed_button;
    int exp_month;
    String fingerprint, countrycode;
    int exp_year;
    Tracker tracker;
    ImageView backpress,gototohome;
    FontTextView tittleback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.stripe_card_details);
        tracker = MyAnalyticalTracker.getTrackerInstance().getTracker(MyAnalyticalTracker.TrackerName.APP_TRACKER,this);
        init();
        Mint.initAndStartSession(BuyByStripe.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));



    }

    @Override
    protected void onResume() {
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
        card_number.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                Log.w("", "input" + s.toString());

                if (s.length() > 0 && !CARD_PATTERN.matcher(s).matches()) {
                    String input = s.toString();
                    String numbersOnly = keepNumbersOnly(input);
                    String code = formatNumbersAsCode(numbersOnly);

                    Log.w("", "numbersOnly" + numbersOnly);
                    Log.w("", "code" + code);

                    card_number.removeTextChangedListener(this);
                    card_number.setText(code);
                    // You could also remember the previous position of the cursor
                    card_number.setSelection(code.length());
                    card_number.addTextChangedListener(this);
                }


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            private String keepNumbersOnly(CharSequence s) {
                return s.toString().replaceAll("[^0-9]", ""); // Should of course be more robust
            }

            private String formatNumbersAsCode(CharSequence s) {
                int groupDigits = 0;
                String tmp = "";
                for (int i = 0; i < s.length(); ++i) {
                    tmp += s.charAt(i);
                    ++groupDigits;
                    if (groupDigits == 4) {
                        tmp += "-";
                        groupDigits = 0;
                    }
                }
                return tmp;
            }
        });


        proceed_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!card_number.getText().toString().equals("") && !expmm.getText().equals("") && !expyy.getText().toString().equals("") && !cvc.getText().equals("")) {
                    card_num_string = card_number.getText().toString().replace("-", "");
                    exp_month = Integer.parseInt(expmm.getText().toString());
                    exp_year = Integer.parseInt(expyy.getText().toString());

                    cvc_string = cvc.getText().toString();

                    if (CommonUtility.isNetworkAvailable(ctx)) {
                        int i = validateCard();
                        Log.e("validate card result ", "" + i);
                        switch (validateCard()) {
                            case DEFAULT:
                                try {
                                    proceed_button.setEnabled(false);
                                    stripe = new Stripe(APIKEY);
                                    stripe.createToken(
                                            card,
                                            new TokenCallback() {


                                                public void onSuccess(Token token) {
                                                    // Send token to your server
                                                    Log.e("token in suceess", "" + token);

                                                    token_id = token.getId().toString();
                                                    if (token_id != null && !token_id.equals("")) {
                                                        fingerprint = token.getCard().getFingerprint();
                                                        countrycode = token.getCard().getCountry();

                                                        new MakePayment().execute();
                                                        showErrorMessage(false, "");
                                                    } else {
                                                        showErrorMessage(true, getString(R.string.auth_error));
                                                    }
                                                }

                                                public void onError(Exception error) {
                                                    proceed_button.setEnabled(true);
                                                    // Show localized error message
                                                    showErrorMessage(true, error.getMessage());
                                                    error.printStackTrace();

                                                }
                                            }
                                    );

                                } catch (AuthenticationException ex) {
                                    showErrorMessage(true, getString(R.string.auth_error));
                                    ex.printStackTrace();

                                }
                                card_number.setTextColor(getResources().getColor(android.R.color.black));
                                cvc.setTextColor(getResources().getColor(android.R.color.black));
                                expyy.setTextColor(getResources().getColor(android.R.color.black));
                                expmm.setTextColor(getResources().getColor(android.R.color.black));
                                break;
                            case VALID_CARD:
                                showErrorMessage(true, getString(R.string.fill_all));
                                card_number.setTextColor(getResources().getColor(R.color.red));
                                break;
                            case VALID_CVC:
                                showErrorMessage(true, getString(R.string.fill_all));
                                cvc.setTextColor(getResources().getColor(R.color.red));
                                break;
                            case VALID_DATE:
                                showErrorMessage(true, getString(R.string.fill_all));
                                expmm.setTextColor(getResources().getColor(R.color.red));
                                expyy.setTextColor(getResources().getColor(R.color.red));
                                break;


                        }
                    } else
                        showErrorMessage(true, getString(R.string.internet_error));
                } else {
                    showErrorMessage(true, getString(R.string.fill_all));
                }


            }
        });
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
        gototohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });


        super.onResume();
    }

    @Override
    protected void onStart() {
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        // Set screen name.
        tracker.setScreenName("Stripe Screen Android");
        // Send a screen view.
        tracker.send(new HitBuilders.AppViewBuilder().build());
        super.onStop();
    }

    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);

        super.onStop();
    }

    void init() {

        backpress = (ImageView)findViewById(R.id.auto_detect_country_back);
        tittleback = (FontTextView)findViewById(R.id.auto_detect_coutry_header);
        gototohome = (ImageView)findViewById(R.id.auto_detect_country_home);
        amount_text = (FontTextView) findViewById(R.id.amount);
        proceed_button = (Button) findViewById(R.id.save);
        card_number = (EditText) findViewById(R.id.card_number);
        expmm = (EditText) findViewById(R.id.exp_month);
        expyy = (EditText) findViewById(R.id.exp_year);
        cvc = (EditText) findViewById(R.id.cvc);

        error_FontTextView = (FontTextView) findViewById(R.id.error_text);
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        close_em_button = (Button) findViewById(R.id.close_button);
        amount = getIntent().getExtras().getString(VariableClass.ResponseVariables.AMOUNT);
        currency = Prefs.getUserCurrency(ctx);
        amount_text.setText(amount_text.getText() + " " + amount + " " + currency);


    }

    int validateCard() {
        int iscard_valid = DEFAULT;


        card = new Card(card_num_string, exp_month, exp_year, cvc_string);
        if (card.validateNumber()) {
            if (card.validateExpiryDate()) {
                if (card.validateCVC()) {

                } else
                    iscard_valid = VALID_CVC;

            } else
                iscard_valid = VALID_DATE;


        } else
            iscard_valid = VALID_CARD;

        return iscard_valid;
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

    public class MakePayment extends AsyncTask<Void, Void, Void> {
        String response = "";
        Boolean iserror = false;

        @Override
        protected void onPostExecute(Void result) {
            proceed_button.setEnabled(true);
            if (iserror) {
                showErrorMessage(true, response);
            } else {

                CommonUtility.showCustomAlert(BuyByStripe.this, response).show();

                overridePendingTransition(R.anim.animation3, R.anim.animation4);
            }
            CommonUtility.dialog.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            CommonUtility.show_PDialog(ctx, getResources().getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            response = Apis.getApisInstance(ctx).stripePayment(token_id, amount, fingerprint, countrycode);
            if (!response.equalsIgnoreCase("")) {
                JSONObject joparent, jochild;
                try {
                    joparent = new JSONObject(response);

                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                        iserror = true;
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
