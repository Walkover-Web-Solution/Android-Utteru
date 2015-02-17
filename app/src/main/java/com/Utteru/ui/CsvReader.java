package com.Utteru.ui;

import android.content.Context;
import android.content.res.AssetManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.Utteru.commonUtilities.Prefs;
import com.Utteru.dtos.Country;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

public class CsvReader {
    AssetManager assetManager;
    InputStream is = null;
    BufferedReader reader = null;
    String line = "";
    String cvsSplitBy = ",", currencyCode;


    public ArrayList<Country> readCsv(Context ctx, String isoCode, Boolean setTarrif) {

        assetManager = ctx.getAssets();
        ArrayList<Country> list = new ArrayList<Country>();
        Country dto;
        try {
            is = assetManager.open("country_csv/iso.csv");
            reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            while ((line = reader.readLine()) != null) {

                dto = new Country();
                try {
                    String[] country = line.split(cvsSplitBy);
                    dto.setCountryName(country[0]);
                    dto.setCountryCode("+" + country[1]);
                    dto.setCountryIso(country[2]);

                    if (dto.getCountryIso().equalsIgnoreCase(isoCode)) {
                        Prefs.setUserCountryCode(ctx, dto.getCountryCode());
                        Prefs.setUserCountryName(ctx, dto.getCountryName());
                        if (setTarrif) {
                            if (dto.getCountryCode().equals("+91"))
                                Prefs.setUserTarrif(ctx, "7");
                        }
                    }
                    list.add(dto);
                } catch (Exception e) {

                }

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block

        }
        return list;
    }


    public String getUserCountryIso(Context context) {
        try {

            //from network
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();

            if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();

                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
            //from sim
            else if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            }
            //from device
            else if (!(context.getResources().getConfiguration().locale.getCountry().equals(""))) {
                return context.getResources().getConfiguration().locale.getCountry().toLowerCase();
            } else
                return "IN";

        } catch (Exception e) {
        }
        return "IN";
    }

    String getUserCurrecncy(String countryCode) {
        try {
            currencyCode = String.valueOf(Currency.getInstance(new Locale("", countryCode)));
        } catch (Exception e) {
            currencyCode = "84";
        }
        return currencyCode;

    }



}


