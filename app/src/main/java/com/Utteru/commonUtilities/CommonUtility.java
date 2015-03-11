package com.Utteru.commonUtilities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;

import com.Utteru.R;
import com.Utteru.dtos.MultipleVerifiedNumber;
import com.Utteru.parse.ContactsDto;
import com.Utteru.ui.Apis;
import com.Utteru.ui.FundTransferActivity;
import com.Utteru.ui.MenuScreen;
import com.Utteru.ui.SignUpHome;
import com.Utteru.userService.UserService;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.OnClickWrapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;


public class CommonUtility {

    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public final static String BUGSENSEID = "395e969a";
    public final static String BUGSENSELIVE = "395e969a";
    public final static String BUGSENSEID_TEST = "1a1d2717";

    public static final String APP_KEY = "21384a7d-f111-400d-8202-ff29b5b6df56";
    public static final String APP_SECRET = "OFbB7aMIJ0auppPm2I11Uw==";
    public static final String ENVIRONMENT = "sandbox.sinch.com";


    public final static  String PARSE_APP_ID = "nkUvfH1hYs7e3u8dU0N6DMhqyYO47zAe8W3v3y5G";
    public final static  String PARSE_CLIENT_ID="WaDxKh5iq8nfGsRucBCcfDW4tATuFFYGqoilTbk3";
    public static ArrayList<MultipleVerifiedNumber> c_list;
    public static ProgressDialog dialog;
    public static HashMap<String, String> currency_list;

    public static boolean isNetworkAvailable(Context context) {

        boolean bool;
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        State mobile = conMan.getNetworkInfo(0).getState();
        State wifi = conMan.getNetworkInfo(1).getState();
        if (mobile == State.CONNECTED) {
            bool = true;
            return bool;
        } else if (wifi == State.CONNECTED) {
            bool = true;
            return bool;
        } else {
            bool = false;
            return bool;
        }
    }

    public static ArrayList splitCodeFromNumber(String number) {
        number = number.replace("+", "");
        String code = "", extractNum = "";
        String[] tempList = {"93", "355", "213", "1684", "376", "244", "1264", "672", "1268", "54", "374", "297", "61", "43", "994", "1242", "973", "880", "1246", "375", "32", "501", "229", "1441", "975", "591", "387", "267", "55", "246", "1284", "673", "359", "226", "95", "257", "855", "237", "1", "238", "1345", "236", "235", "56", "86", "61", "61", "57", "269", "682", "506", "385", "53", "357", "420", "243", "45", "253", "1767", "1809", "593", "20", "503", "240", "291", "372", "251", "500", "298", "679", "358", "33", "689", "241", "220", "970", "995", "49", "233", "350", "30", "299", "1473", "1671", "502", "224", "245", "592", "509", "39", "504", "852", "36", "354", "91", "62", "98", "964", "353", "44", "972", "39", "225", "1876", "81", "962", "7", "254", "686", "381", "965", "996", "856", "371", "961", "266", "231", "218", "423", "370", "352", "853", "389", "261", "265", "60", "960", "223", "356", "692", "222", "230", "262", "52", "691", "373", "377", "976", "382", "1664", "212", "258", "264", "674", "977", "31", "599", "687", "64", "505", "227", "234", "683", "672", "850", "1670", "47", "968", "92", "680", "507", "675", "595", "51", "63", "870", "48", "351", "974", "242", "40", "7", "250", "590", "290", "1869", "1758", "1599", "508", "1784", "685", "378", "239", "966", "221", "381", "248", "232", "65", "421", "386", "677", "252", "27", "82", "34", "94", "249", "597", "268", "46", "41", "963", "886", "992", "255", "66", "670", "228", "690", "676", "1868", "216", "90", "993", "1649", "688", "256", "380", "971", "44", "1", "598", "1340", "998", "678", "58", "84", "681", "970", "212", "967", "260", "263", "1868", "95"};
        ArrayList<String> codeList = new ArrayList<String>(Arrays.asList(tempList));
        ArrayList<String> result = new ArrayList<>();
        //codeList.addAll(tempList);
        String first = number.substring(0, 1), second = number.substring(0, 2), third = number.substring(0, 3), fourth = number.substring(0, 4);
        if (codeList.contains(fourth)) {
            Log.e("fourth", fourth);
            code = fourth;
            extractNum = number.substring(4);
        } else if (codeList.contains(third)) {
            Log.e("third", third);
            code = third;
            extractNum = number.substring(3);
        } else if (codeList.contains(second)) {
            Log.e("second", second);
            code = second;
            extractNum = number.substring(2);
        } else if (codeList.contains(first)) {
            Log.e("first", first);
            code = first;
            extractNum = number.substring(1);
        }

        if (code.equals("")) {

        }
        Log.e("number_code_common", extractNum);
        Log.e("number_common", code);
        result.add(extractNum);
        result.add(code);
        return result;
    }

    //signin screen ,login screen forgot password

    public static String validateNumberForApi(String number) {

        if (number.startsWith("00")) {

            number = number.replaceFirst("00", "");

        }

        if (number.startsWith("0")) {
            number = number.replaceFirst("0", "");
        }

        number = number.replace("//s+", "");//space
        number = number.replaceAll("[-+.^:,]", "");//specific special character
        number = number.replaceAll("-", ""); //dash
        number = number.replace("+", "");
        number = number.replaceAll("\\s+", "");


        return number;

    }


    public static String validateText(String text) {

        text = text.replaceAll("[^\\w\\s\\-_]", "");//all special
        text = text.replaceAll("\\s+", "");
        text = text.replace("+", "");
        text = text.replaceAll("-", ""); //dash
        text = text.trim();
        return text;
    }

    public static String validateNumberForUI(String number, Context ctx) {

//        number = number.replaceAll("[^\\w\\s\\-_]", "");//all special
        number = number.replace("//s+", "");//space
        number = number.replaceAll("-", ""); //dash
        number = number.trim();




        if (number.startsWith("+") || number.startsWith("00")) {

            return number;
        }
        else
         if (number.startsWith("0")) {

             number = number.replaceFirst("0", "");
             number = "+" + Prefs.getUserCountryCode(ctx) + number;
             return number;
         }
        else{
             number = "+" + Prefs.getUserCountryCode(ctx) + number;
             return number;
         }

    }


    //signin screen ,login screen forgot password


    public static void getUserBalance(final Activity ctx) {


        final MenuScreen c = (MenuScreen) ctx;
        if (isNetworkAvailable(c)) {
            new AsyncTask<Void, Void, Void>() {
                String response;
                Boolean iserr = false;

                @Override
                protected void onPostExecute(Void result) {
                    if (iserr)
                        showCustomAlert(ctx, response);
                    else {
                        if (!c.isFinishing())
                            c.setBalance();
                    }

                    super.onPostExecute(result);
                }


                @Override
                protected Void doInBackground(Void... params) {

                    response = Apis.getApisInstance(c).checkBalance(true);

                    if (!response.equalsIgnoreCase("")) {
                        try {
                            JSONObject jobj;
                            JSONArray jarray;
                            jobj = new JSONObject(response);
                            //if response of failed

                            if (jobj.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                                jobj = jobj.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                                response = jobj.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                                iserr = true;

                            } else if (jobj.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                                //get balance  and set in pref
                                jarray = jobj.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                                jobj = jarray.getJSONObject(0);
                                String balance = jobj.getString(VariableClass.ResponseVariables.BALANCE);
                                Double bal = Double.parseDouble(balance);
                                bal = CommonUtility.round(bal, 2);
                                balance = bal + " " + jobj.getString(VariableClass.ResponseVariables.CURRENCY);
                                Prefs.setUserBalance(c, balance);
                                Prefs.setUserCurrency(c, jobj.getString(VariableClass.ResponseVariables.CURRENCY));

                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                            iserr = true;
                            response = c.getResources().getString(R.string.parse_error);
                        } catch (Exception e) {
                            e.printStackTrace();
                            iserr = true;
                            response = c.getResources().getString(R.string.parse_error);
                        }
                    } else {
                        iserr = true;
                        response = c.getResources().getString(R.string.server_error);
                    }
                    return null;
                }
            }.execute(null, null, null);
        } else {
            showCustomAlert(c, c.getResources().getString(R.string.internet_error));
        }
    }

    public static void hideKeyboard(View view, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void getUserBalanceFund(final Context ctx) {


        final FundTransferActivity c = (FundTransferActivity) ctx;
        if (isNetworkAvailable(c)) {
            new AsyncTask<Void, Void, Void>() {
                String response;
                Boolean iserr = false;

                @Override
                protected void onPostExecute(Void result) {
                    if (iserr)
                        showCustomAlert(c, response);
                    else {
                        if (!c.isFinishing())
                            c.setBalance();
                    }

                    super.onPostExecute(result);
                }


                @Override
                protected Void doInBackground(Void... params) {

                    response = Apis.getApisInstance(c).checkBalance(true);

                    if (!response.equalsIgnoreCase("")) {
                        try {
                            JSONObject jobj;
                            JSONArray jarray;
                            jobj = new JSONObject(response);
                            //if response of failed

                            if (jobj.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                                jobj = jobj.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                                response = jobj.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                                iserr = true;

                            } else if (jobj.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                                //get balance  and set in pref
                                jarray = jobj.getJSONArray(VariableClass.ResponseVariables.CONTENT);
                                jobj = jarray.getJSONObject(0);
                                String balance = jobj.getString(VariableClass.ResponseVariables.BALANCE);
                                Double bal = Double.parseDouble(balance);
                                bal = CommonUtility.round(bal, 2);
                                balance = bal + " " + jobj.getString(VariableClass.ResponseVariables.CURRENCY);
                                Prefs.setUserBalance(c, balance);
                                Prefs.setUserCurrency(c, jobj.getString(VariableClass.ResponseVariables.CURRENCY));

                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                            iserr = true;
                            response = c.getResources().getString(R.string.parse_error);
                        } catch (Exception e) {
                            e.printStackTrace();
                            iserr = true;
                            response = c.getResources().getString(R.string.parse_error);
                        }
                    } else {
                        iserr = true;
                        response = c.getResources().getString(R.string.server_error);
                    }
                    return null;
                }
            }.execute(null, null, null);
        } else {
            showCustomAlertError(c, c.getResources().getString(R.string.internet_error));
        }
    }

    public static void expand(final View v) {

        Boolean b = false;
        if (v == null) {
            b = true;
        }
        Log.e("check if view is null", "" + b);
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LayoutParams.WRAP_CONTENT
                        : (int) (targtetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targtetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static void show_PDialog(final Context context,
                                    String message) {
        dialog = new ProgressDialog(context, R.style.MyTheme);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        dialog.show();
    }

    public static String getContactDisplayNameByNumber(String number, Context ctx) {
        String name = "";
        try {

            Uri uri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(number));
            ContentResolver contentResolver = ctx.getContentResolver();
            Cursor contactLookup = contentResolver.query(uri, new String[]{
                            BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME},
                    null, null, null);
            try {
                if (contactLookup != null && contactLookup.getCount() > 0) {
                    contactLookup.moveToNext();
                    name = contactLookup.getString(contactLookup
                            .getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                    if (name == null)
                        name = "";
                }
            } finally {
                if (contactLookup != null) {
                    contactLookup.close();
                }
            }

        } catch (Exception e) {

        }
        return name;
    }

    public static void setCurrency(Context c) {

        final Context ctx = c;
        currency_list = new HashMap<String, String>();

        if (isNetworkAvailable(ctx)) {

            new AsyncTask<Void, Void, Void>() {
                String response;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(Void result) {
                    super.onPostExecute(result);
                }

                @Override
                protected Void doInBackground(Void... params) {
                    response = Apis.getApisInstance(ctx).getCurrency();

                    JSONObject jobj;
                    JSONArray jarray;
                    if (!response.equals("")) {
                        //if response of success
                        try {
                            jobj = new JSONObject(response);

                            if (jobj.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                                //get balance  and set in pref
                                jarray = jobj.getJSONArray(VariableClass.ResponseVariables.CONTENT);

                                int count = jarray.length();
                                for (int i = 0; i < count; i++) {
                                    jobj = jarray.getJSONObject(i);
                                    currency_list.put(jobj.getString(VariableClass.ResponseVariables.CURRENCY), jobj.getString(VariableClass.ResponseVariables.TARRIFFID));

                                }

                            }
                        } catch (Exception e) {

                        }
                    }

                    return null;
                }

            }.execute(null, null, null);
        } else {

        }

    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static SuperActivityToast showCustomAlert(Activity ctx, String text) {

        final SuperActivityToast superActivityToast = new SuperActivityToast(ctx, SuperToast.Type.BUTTON);
        superActivityToast.setDuration(SuperToast.Duration.SHORT);
        superActivityToast.setText(text);
        superActivityToast.setButtonIcon(SuperToast.Icon.Dark.EXIT, "");

        OnClickWrapper onClickWrapper = new OnClickWrapper("superactivitytoast", new SuperToast.OnClickListener() {

            @Override
            public void onClick(View view, Parcelable token) {

                superActivityToast.dismiss();

            }

        });
        superActivityToast.setOnClickWrapper(onClickWrapper);
        superActivityToast.show();

        return superActivityToast;

    }

    public static SuperActivityToast showCustomAlertCopy(Activity ctx, String text) {

        final SuperActivityToast superActivityToast = new SuperActivityToast(ctx, SuperToast.Type.BUTTON);
        superActivityToast.setDuration(SuperToast.Duration.SHORT);
        superActivityToast.setText("Copied " + text + ".Ready to paste");
        superActivityToast.setButtonIcon(SuperToast.Icon.Dark.EXIT, "");

        OnClickWrapper onClickWrapper = new OnClickWrapper("superactivitytoast", new SuperToast.OnClickListener() {

            @Override
            public void onClick(View view, Parcelable token) {

        /* On click event */
                superActivityToast.dismiss();

            }

        });
        superActivityToast.setOnClickWrapper(onClickWrapper);
        superActivityToast.show();

        return superActivityToast;

    }

    public static SuperToast showCustomAlertForContacts(Context ctx, String text) {

        SuperToast superToast = new SuperToast(ctx);
        superToast.setDuration(SuperToast.Duration.LONG);
        superToast.setText(text);
        superToast.setIcon(SuperToast.Icon.Dark.INFO, SuperToast.IconPosition.LEFT);
        superToast.show();

        return superToast;

    }

    public static SuperToast showCustomAlertForContactsError(Context ctx, String text) {

        SuperToast superToast = new SuperToast(ctx);
        superToast.setDuration(SuperToast.Duration.LONG);
        superToast.setText(text);
        superToast.setBackground(SuperToast.Background.RED);
        superToast.setIcon(SuperToast.Icon.Dark.INFO, SuperToast.IconPosition.LEFT);
        superToast.show();

        return superToast;

    }

    public static SuperActivityToast showCustomAlertError(Activity ctx, String text) {

        final SuperActivityToast superActivityToast = new SuperActivityToast(ctx, SuperToast.Type.BUTTON);
        superActivityToast.setDuration(SuperToast.Duration.SHORT);
        superActivityToast.setText(text);
        superActivityToast.setBackground(SuperToast.Background.RED);
        superActivityToast.setButtonIcon(SuperToast.Icon.Dark.EXIT, "");
        OnClickWrapper onClickWrapper = new OnClickWrapper("superactivitytoast", new SuperToast.OnClickListener() {

            @Override
            public void onClick(View view, Parcelable token) {
                superActivityToast.dismiss();
            }
        });
        superActivityToast.setOnClickWrapper(onClickWrapper);
        superActivityToast.show();

        return superActivityToast;

    }

    public static boolean checkPlayServices(Activity c) {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(c);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, c,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("Utteru", "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    public static int getListPreferredItemHeight(Activity ctx) {
        final TypedValue typedValue = new TypedValue();

        // Resolve list item preferred height theme attribute into typedValue
        ctx.getTheme().resolveAttribute(
                android.R.attr.listPreferredItemHeight, typedValue, true);

        // Create a new DisplayMetrics object
        final DisplayMetrics metrics = new DisplayMetrics();

        // Populate the DisplayMetrics
        ctx.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // Return theme value based on DisplayMetrics
        return (int) typedValue.getDimension(metrics);
    }


    public static Bitmap drawImage(String text, Activity ctx) {
        int colorsarray[] = {ctx.getResources().getColor(R.color.red_contacts), ctx.getResources().getColor(R.color.blue_contacts), ctx.getResources().getColor(R.color.purple_contacts), ctx.getResources().getColor(R.color.green_contacts), ctx.getResources().getColor(R.color.grey_contacts), ctx.getResources().getColor(R.color.violet_contacts), ctx.getResources().getColor(R.color.yellow_contacts)};

        Bitmap image = null;
        Paint paint = new Paint();
        paint.setTextSize(120);
        // Random rnd = new Random();
        //int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        paint.setColor(ctx.getResources().getColor(android.R.color.white));

        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.
        paint.setTextAlign(Paint.Align.CENTER);
        Typeface tf = Typeface.createFromAsset(ctx.getAssets(), "fonts/mc_handwriting.ttf");
        paint.setTypeface(tf);

        String temp;

        text = validateText(text);
        try {

            if (text.contains(" ")) {
                String split[] = text.split("\\s+");

                temp = split[0].substring(0, 1).toUpperCase() + split[split.length - 1].substring(0, 1).toUpperCase();


            } else {
                temp = text.substring(0, 1).toUpperCase();
            }
        } catch (Exception e) {
            temp = "NA";

        }


        int width = getListPreferredItemHeight(ctx);
        //float baseline = width - (width / 4); // ascent() is negative
        int height = getListPreferredItemHeight(ctx);
        image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawColor(colorsarray[new Random().nextInt(colorsarray.length)]);
        final Rect rect = new Rect(0, 0, image.getWidth(), image.getHeight());
        canvas.drawBitmap(image, rect, rect, paint);
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
        canvas.drawText(temp, xPos, yPos, paint);
        return image;

    }

    public static void logOut(Context ctx) {
        Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        Log.e("loggin out", "loggin out");
        Intent start_screen;
        Prefs.deletePrefs(ctx);
        //delete database
        UserService us = UserService.getUserServiceInstance(ctx);
        us.deleteAllAccessContacts();
        us.deleteAllCpLogs();
        us.deleteAllTwowayLogs();
        us.deleteTransaction();
        us.deleteRecentCalls();


        ctx.stopService(new Intent(ctx, GPSTracker.class));
        start_screen = new Intent(ctx, SignUpHome.class);
        start_screen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ctx.startActivity(start_screen);
        AccountManager myAccountManager = AccountManager.get(ctx);
        myAccountManager.removeAccount(account, new AccountManagerCallback<Boolean>() {
            @Override
            public void run(AccountManagerFuture<Boolean> future) {
                try {

                    boolean wasAccountDeleted = future.getResult();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (AuthenticatorException e) {
                    e.printStackTrace();
                } catch (android.accounts.OperationCanceledException e) {
                    e.printStackTrace();
                }
            }
        }, null);

    }

    public static void clearData(Context ctx) {
        //delete database
        Prefs.deletePrefs(ctx);
        UserService us = UserService.getUserServiceInstance(ctx);
        us.deleteAllAccessContacts();
        us.deleteAllCpLogs();
        us.deleteAllTwowayLogs();
        ctx.stopService(new Intent(ctx, GPSTracker.class));

//        try {
//            GoogleCloudMessaging.getInstance(ctx).unregister();
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }


    }

    public static void makeCall(Context c, String number) {


        Intent callIntent = new Intent(Intent.ACTION_CALL);

        if (!number.startsWith("+"))
            number = "+" + number;


        callIntent.setData(Uri.parse("tel:" + number));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(callIntent);

    }


    public static void printMe(String tag, String message) {
        Log.e(tag, message);

    }

    public static boolean isMyServiceRunning(Class<?> serviceClass,Context ctx) {
        ActivityManager manager = (ActivityManager)ctx. getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public  static ArrayList<ContactsDto> readContactsNew(Context ctx) {
        int currentApiVersion = Build.VERSION.SDK_INT;
        String SELECTION;
        SELECTION =
                ContactsContract.Contacts.DISPLAY_NAME
                        + "<>'' AND "
                        + ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1";

        ArrayList<ContactsDto> list = new ArrayList<ContactsDto>();
        ContactsDto adto;
        Cursor phones = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, SELECTION, null, null);
        while (phones.moveToNext()) {

            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber = phoneNumber.replaceAll("-", "").replaceAll("\\s+", "");
            String label = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));

            adto = new ContactsDto();

            adto.setNumber(CommonUtility.validateNumberForUI(phoneNumber,ctx));
            adto.setStatus(false);
            adto.setState(false);
            adto.setUserNumber(Prefs.getUserDefaultNumber(ctx));

                list.add(adto);

        }
        phones.close();
        return list;
    }

}
