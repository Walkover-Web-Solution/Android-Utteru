package com.Utteru.ui;

import android.accounts.Account;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.Constants;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Apis {
    public static Apis apis;
    public static String ErrorResponse = "0";
    public static String SuccessResponse = "1";
    public Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
    Context ct;
    String serverName;
    String completeUrl;
    String response = "";
    Boolean logoutavail = false;
    String UserIdType = "1";

    private Apis(Context c) {
        ct = c;

    }

    public static Apis getApisInstance(Context ctx) {
            apis = new Apis(ctx);
        return apis;
    }

    //API FOR VALIDATION CHECK
    public String signInApi(String userId, String userPassword, String resellerId, String token, String accessType, String[] country_code) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "login";

        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();

        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, userId.replace("+", "")));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, userPassword));
       nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, resellerId));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.SYSTEMDETAIL, "1"));


        if (token != null && !token.equals("")) {
            nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.ACCESSTOKEN, token));
            nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.ACCESSTYPE, accessType));
        }
        if (country_code != null)
            for (String code : country_code) {

                nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COUNTRY_CODE_ARRY, code));
            }

        Log.e("signin api namevalue", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }

        logoutavail = false;
        response = HitUrl(httpPost);
        Log.e("login response", "" + response);

        return response;
    }

    //API FOR SIGNUP WITH NUMBER

    public String signupWithNumber(String userNumber, String countryCode, String resellerId, String tempId, String carrierType) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "signUpWithNumber";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERNUMBER, userNumber));
        countryCode = countryCode.replace("+", "");
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COUNTRY_CODE, countryCode));
        Log.e("country code", "" + countryCode);
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, resellerId));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.CARRIERTYPE, carrierType));
        if (!tempId.equals("")) {
            nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.TEMPID, tempId));
        }
        Log.e("signupwithNumber", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        response = HitUrl(httpPost);

        Log.e("signupwithNumber response", "" + response);
        return response;
    }



    //API FOR FORGOTPASSWORD
    public String forgotPassword(String userInfo, String[] countryCode, String carrierType, String resellerId) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "forgotPasswordApiNew";

        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        userInfo = userInfo.replace("+", "");
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERNUMBER, userInfo.replace("\\+s", "")));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.CARRIERTYPE, carrierType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, resellerId));
        if (countryCode != null)
            for (String code : countryCode) {

                nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COUNTRY_CODE_ARRY, code.replace("+", "")));
            }
        Log.e("forgot password", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        response = HitUrl(httpPost);
        Log.e("forgotPassword response", "" + response);

        return response;
    }

    //API FOR VERIFYCODE
    public String verifyCode(String userNumber, String countryCode, String resellerId, String confirmCode, String smsCall) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "checkVerificationCode";

        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERNUMBER, userNumber.replace("+", "")));//userNumber,imei,gcmId,code,contacts,action,auth

        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COUNTRY_CODE, countryCode.replace("+", "")));

        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, resellerId));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.CARRIERTYPE, smsCall));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.CONFIRM_CODE, confirmCode));
        Log.e("verification code parameter", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        response = HitUrl(httpPost);
        Log.e("verification code", "" + response);
        return response;
    }

    //API FOR VERIFIED SIGNUP
    public String verifyCodeSignup(String userNumber, String countryCode, String resellerId, String confirmCode, String smsCall, String tempID, String tarrifID) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "checkVerificationCodeWithSignup";

        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERNUMBER, userNumber.replace("+", "")));//userNumber,imei,gcmId,code,contacts,action,auth

        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COUNTRY_CODE, countryCode.replace("+", "")));

        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, resellerId));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.CARRIERTYPE, smsCall));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.CONFIRM_CODE, confirmCode));
        if (SignInScreen.accessToken != null && !SignInScreen.accessToken.equals(""))
            nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.SIGNUPFROM, "1"));

        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, resellerId));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.TEMPID, tempID));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.CURRENCY, tarrifID));
        Log.e("verification code parameter", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        response = HitUrl(httpPost);
        Log.e("verification code", "" + response);
        return response;
    }

    //API FOR SIGNUP WITH GOOGLE
    public String signupWithGoogleFacebook(String accessToken, String accessType, String resellerId) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "fbGlLogin";

        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.ACCESSTOKEN, accessToken));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.ACCESSTYPE, accessType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, resellerId));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("signupWithGoogleFacebook", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("signup with google fb", "" + response);
        return response;
    }


    //API FOR CREATE PIN

    public String createPin(String number, String confirmCode, String resellerId, String pin) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "createPinPassword";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERNUMBER, number.replace("+", "")));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.CONFIRM_CODE, confirmCode));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, resellerId));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PIN, pin));

        Log.e("create pin ", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        response = HitUrl(httpPost);
        Log.e("create pin", "" + response);
        return response;
    }


    //API FOR BALANCE
    public String checkBalance(Boolean logout) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "balance";
        Log.e("balance url", "" + completeUrl);

        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserID(ct)));

        Log.e("balce name valluse", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return ErrorResponse;
        }
        logoutavail = logout;
        response = HitUrl(httpPost);
        Log.e("balance response", "" + response);

        return response;

    }

    //get two way pricing
    public String getTwoWayPricing(String number) {

        number = CommonUtility.validateNumberForApi(number);


        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "seeCallRateNew";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERNUMBER, number));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));


        Log.e("gettwowayprice ", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("getTwoCallPricing", "" + response);
        return response;
    }


    public String getCallLogsDetails(String sourceNumber,String destinationNumber) {

      sourceNumber =CommonUtility.validateNumberForApi(sourceNumber);


        destinationNumber = CommonUtility.validateNumberForApi(destinationNumber);



        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "getcallLogsDetail";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.SOURCE_NUMBER_CALL, sourceNumber));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DESTINATION_NUMBER_CALL, destinationNumber));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));


        Log.e("get call details  ", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("get call details", "" + response);
        return response;
    }


    //two way call api
    public String twoWayCall(String source, String destination) {
        serverName = Prefs.getServerUrl(ct);


        source = CommonUtility.validateNumberForApi(source);
        destination = CommonUtility.validateNumberForApi(destination);

        completeUrl = serverName + "twowaycalling";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.SOURCE_NUMBER, source));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DESTINATION_NUMBER1, destination));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));


        Log.e("two way call response ", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("two way call", "" + response);
        return response;
    }

    //two way call response
    public String twoWayCallResponse(String uniqueId) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "checkCallStatus";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.UNIQUEID, uniqueId));


        Log.e("two way call state ", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("two way call state", "" + response);
        return response;
    }

    //two way call response
    public String twoWayCallEnd(String uniqueId) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "cutCall";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.CALLID, uniqueId));


        Log.e("two way call  end  ", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("two way call end respom", "" + response);
        return response;
    }
    //API FOR CRECHARGE BY PIN

    public String RechargeByPin(String pin) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "rechargePin";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PIN, pin));

        Log.e("create pin ", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("create pin", "" + response);
        return response;
    }

    //API FOR CHANGEPASSWORD/PIN
    public String changePassword(String newpassword) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "changepassword";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.NEWPASSWORD, newpassword));


        Log.e("create pin ", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }

        response = HitUrl(httpPost);

        Log.e("create pin", "" + response);
        return response;
    }

    //API FOR CHANGE STATUS FOR LISTEN VOICE
    public String listenVoice(int listen_voice, int gender, String name, Boolean isupdate) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "getAndsetProfileInfo";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();

        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserActualName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));

        if (isupdate) {

            nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.LISTENVOICE, "" + listen_voice));
            nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.GENDER, "" + gender));

            if (!name.equals(""))
                nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER_DISPLAY_NAME, "" + name));
        }

        Log.e("listen voice  request", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("listen voice response", "" + response);
        return response;
    }

    //API For Get Currency
    public String getCurrency() {

        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "currencyList";
        Log.e("url", "" + completeUrl);
        HttpPost httpPost = new HttpPost(completeUrl);
        response = HitUrl(httpPost);
        Log.e("get currency", "" + response);
        return response;
    }

    //API For Get Pricing
    public String getPricing(String country) {

        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "searchPrice";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COUNTRY, country));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.CURRENCY, Prefs.getUserTariff(ct)));

        Log.e("get pricing ", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("get pricing", "" + response);
        return response;
    }

    //API For Validate User
    public String validateUser(String user) {

        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "checkUserAvail";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserType(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERNAME, user));

        Log.e("get checkuseravail ", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("get checkuseravail", "" + response);
        return response;
    }

    //API For Fund Transfer
    public String transferFund(String user, String fund) {

        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "transferFund";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserType(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERNAME, user));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.CURRENCY, Prefs.getUserTariff(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AMOUNT, fund));

        Log.e("transfer fund  ", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("transfer fund ", "" + response);
        return response;
    }

    //API Adding number/email
    public String addNumberEmail(int dataType, String data, String countrycode, String carrierType) {

        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "addNumberOrEmail";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserType(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DATATYPE, "" + dataType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DATA, data));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COUNTRY_CODE, countrycode));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.CARRIERTYPE, carrierType));

        Log.e("add number email", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("addNumber email ", "" + response);
        return response;
    }

    //API deleting number/Email
    public String deleteNumberEmail(int dataType, String data, String countryCode) {

        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "deleteNumberOrEmail";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserType(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DATATYPE, "" + dataType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DATA, data));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COUNTRY_CODE, countryCode));
        Log.e("delete number", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("delete number", "" + response);
        return response;
    }

    //API For making email/number default
    public String makeDefaultNumberEmail(int dataType, String data, String countryCode) {

        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "makeVerifiedDataDefault";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserType(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DATATYPE, "" + dataType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DATA, data));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COUNTRY_CODE, countryCode));

        Log.e("nake  number defalut", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("nake number default", "" + response);
        return response;
    }

    //API For getting numbers/emails
    public String getNumbermail(int dataType) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "getAllNumberOrEmail";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserType(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DATATYPE, "" + dataType));

        Log.e("getNumber email list", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("getNumber email list", "" + response);
        return response;
    }

    //Api for verifying number email
    public String verifyEmailNumber(int dataType, String data, String countryCode, String code) {

        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "verifycontact";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserType(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DATATYPE, "" + dataType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COUNTRY_CODE, countryCode));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DATA, data));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.CONFIRM_CODE, code));

        Log.e("verify email Number", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("verify email ", "" + response);
        return response;
    }

    //Api forresend code manage number
    public String resendCodeEmailNumber(int dataType, String data, String countryCode, String carrierType) {

        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "resendVerification";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserType(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DATATYPE, "" + dataType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COUNTRY_CODE, countryCode));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DATA, data));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.CARRIERTYPE, carrierType));

        Log.e("resend add contacts ", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("resend add contacts ", "" + response);
        return response;
    }

    //fetchAccessContacts
    public String fetchAccessContacts() {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "listOfAllContacts";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.TIMESTAMP, "timestamp"));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.CONTACTSWITHACCESS, "1"));

        try {
            Log.e("fetch access", "" + nameValuePair);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("fetch access response ", "" + response);

        return response;
    }

    public String getAllCountry() {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "getAllCountry";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();

        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        Log.e("getAllCountry", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("get all country response", "" + response);
        return response;
    }

    public String getAllState(String countryCode, String countryName) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "getAllState";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();

        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COUNTRY_CODE, countryCode));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COUNTRY, countryName));
        Log.e("getAll state", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("get all state response", "" + response);
        return response;
    }

    public String addContact(String name, String eMail, String mobileNumber, String access, String extNo) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "addContact";
        HttpPost httpPost = new HttpPost(completeUrl);
        name = name.replaceAll("[^\\w\\s\\-_]", "");
        List<NameValuePair> nameValuePair = new ArrayList<>();
        mobileNumber = mobileNumber.replaceAll("[-+.^:,]", "");
        access=access.replaceAll("[-+.^:,]", "");

        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.NAME, name));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.EMAIL, eMail));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.CONTACT_NUMBER, mobileNumber));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.ADD_ACCESS_NUMBER, access));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.Extension_NUMBER, extNo));
        Log.e("addContact", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        return response;
    }

    public String editContact(String name, String contactId, String eMail, String mobileNumber, String access, String extNo) {

        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "editContactNumber";
        Log.e("url",completeUrl);
        HttpPost httpPost = new HttpPost(completeUrl);
        name = name.replaceAll("[^\\w\\s\\-_]", "");
        mobileNumber = mobileNumber.replaceAll("[-+.^:,]", "");
        if(access!=null)
        access=access.replaceAll("[-+.^:,]", "");

        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.CONTACTID, contactId));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.NAME, name));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.EMAIL, eMail));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.CONTACT_NUMBER, mobileNumber));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.EDIT_ACCESS_NUMBER, access));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.Extension_NUMBER, extNo));
        Log.e("editContact", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("", "Edit Contact: " + response);
        return response;
    }

    public String getCountryAndStateByAccess(String accessNumber) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "getCountryAndStateByAccess";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERIDTYPE, UserIdType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERID, Prefs.getUserID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.EDIT_ACCESS_NUMBER, accessNumber));
        Log.e("getCountryAndStateByAccess response ", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("get country and state respone", "" + response);
        return response;
    }

    public String signupPromo(String promoCode) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "assignPromoCode";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserActualName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));

        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PROMOCODE, promoCode));
        Log.e("update promocode api", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        return response;
    }

    public String earnCreditPromo(String promoCode) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "addPromocode";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserActualName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        if (!promoCode.equals(""))
            nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PROMOCODE, promoCode));
        Log.e("add Promo code request ", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("add Promo code response ", "" + response);
        return response;
    }

    public String gcmRegistration(String deviceId, int appid, String user) {

        TelephonyManager tel = (TelephonyManager) ct.getSystemService(Context.TELEPHONY_SERVICE);

//        serverName =  Prefs.getServerUrl(ct);

        completeUrl = "https://voice.utteru.com/newapi/registerapp.php";
        Log.e("GCM API", "" + completeUrl);
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USERNAMEP, user));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DEVICETOKEN, deviceId));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DEVICEID, tel.getDeviceId()));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.APPID, "" + appid));


        Log.e("gcm request", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("gcm  response ", "" + response);
        return response;
    }

    public String stripePayment(String token, String talktime, String fingerprint, String countryCode) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "stripePayment";
        Log.e("stripe API", "" + completeUrl);
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserActualName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.TOKEN, token));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.TALKTIME, talktime));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.FINGERPRINT, fingerprint));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COUNTRY_CODE, countryCode));
        Log.e("stripe request", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("stripe api response ", "" + response);
        return response;
    }

    public String paypalInfo(String orderID, String payId, String trackId) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "paypalResponseHandle";
        Log.e("paypal API", "" + completeUrl);
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserActualName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.ORDERID, orderID));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PAYID, payId));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.TRACKID, trackId));

        Log.e("paypal request", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("paypal response ", "" + response);
        return response;
    }

    //    http://geo.taskb.in/api/store.php

    public String sendGeo(String lat, String lon) {

        completeUrl = "http://geo.taskb.in/api/store.php";
        Log.e("geo coordinates", "" + completeUrl);
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserActualName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.LATITUDE, lat));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.LONGITUDE, lon));

        Log.e("geo coordinates", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("geo coordinates ", "" + response);
        return response;
    }

    public String paypalpreviousInfo(String amount) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "savePaypalOrder";
        Log.e("paypal prevous info", "" + completeUrl);
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserActualName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AMOUNT, amount));


        Log.e("paypal previous request", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("stripe api response ", "" + response);
        return response;
    }

    public String getTokenApi() {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "getLoginAsToken";
        Log.e("get token url ", "" + completeUrl);
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserActualName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        Log.e("get token  request", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("get token response ", "" + response);
        return response;
    }

    public String getTransactionApi(int pageno) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "getTransactionLog";
        Log.e("get token url ", "" + completeUrl);
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.USER, Prefs.getUserActualName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PASSWORD, Prefs.getUserPassword(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.PAGENO, "" + pageno));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.RESELLER_ID, Prefs.getResellerID(ct)));
        Log.e("get token  request", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        response = HitUrl(httpPost);
        Log.e("get token response ", "" + response);
        return response;
    }

    public String HitUrl(HttpPost postrequest) {
        Log.e("hitting url", "hitting url ");
        if (CommonUtility.isNetworkAvailable(ct)) {

            long backoff;
            final int MAX_ATTEMPTS = 1;
            final int BACKOFF_MILLI_SECONDS = 2000;
            final Random random = new Random();
            StringBuilder total = null;
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 6*1000);
            HttpConnectionParams.setSoTimeout(httpParameters, 6*1000);
            DefaultHttpClient httpClient;
            InputStream inputstream;

            backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
            for (int i = 0; i < MAX_ATTEMPTS; i++) {
                try {
                    httpClient = new DefaultHttpClient();
                    httpClient.setParams(httpParameters);
                    HttpResponse httpresponse = httpClient.execute(postrequest);
                    String line;
                    inputstream = httpresponse.getEntity().getContent();
                    total = new StringBuilder();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(inputstream));
                    while ((line = rd.readLine()) != null) {
                        total.append(line);
                    }
                    break;
                }
                catch (SocketTimeoutException e)
                {
                    e.printStackTrace();
                    CommonUtility.showCustomAlertForContacts(ct,ct.getString(R.string.server_error));

                    return "";


                }
                catch (Exception e) {
                    e.printStackTrace();

                    if (i == MAX_ATTEMPTS) {
                        return "";
                    }
                    try {
                        Thread.sleep(backoff);
                    } catch (InterruptedException e1) {
                        e.printStackTrace();

                        Thread.currentThread().interrupt();
                    }
                    backoff *= 2;
                }
            }
            if (total != null)//if response is null
            {

                if (total.toString().contains("1014")) {
                    Log.e("logout", "logout" + logoutavail);

                    if (logoutavail)
                        CommonUtility.logOut(ct);
                }

                return total.toString();
            } else return "";

        }
        return "";

    }


}
