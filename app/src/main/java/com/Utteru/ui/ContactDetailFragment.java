/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.Utteru.ui;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts.Photo;
import android.support.v4.BuildConfig;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.Utteru.R;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.Constants;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessContactDto;
import com.Utteru.util.ImageLoader;
import com.Utteru.util.Utils;
import com.Utteru.utteru_sip.CallData;
import com.Utteru.utteru_sip.CallingScreenActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ContactDetailFragment extends Fragment {

    public static final String EXTRA_CONTACT_URI = "com.Utteru.ui.EXTRA_CONTACT_URI";
    private static final String TAG = "ContactDetailFragment";
    private static AccessContactDto mAccessContactDto;
    private Uri mContactUri; // Stores the contact Uri for this fragment instance
    private ImageLoader mImageLoader; // Handles loading the contact image in a background thread
    private ImageView mImageView;


    public ContactDetailFragment() {
    }


    public static ContactDetailFragment newInstance(AccessContactDto accessContactDto) {
        final ContactDetailFragment fragment = new ContactDetailFragment();

        mAccessContactDto = accessContactDto;
        return fragment;
    }

    public void setContact() {

        if (mAccessContactDto.getIsAccess()) {
            if (mAccessContactDto.getContactUri() != null) {
                mContactUri = (Uri.parse(mAccessContactDto.getContactUri()));
            }


        }
        // Asynchronously loads the contact image
        mImageLoader.loadImage(mContactUri, mImageView);
        // Shows the contact photo ImageView and hides the empty view
        mImageView.setVisibility(View.VISIBLE);

        // Shows the edit contact action/menu item
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Let this fragment contribute menu items
        mImageLoader = new ImageLoader(getActivity(), getLargestScreenDimension()) {
            @Override
            protected Bitmap processBitmap(Object data) {
                // This gets called in a background thread and passed the data from
                // ImageLoader.loadImage().

                return loadContactPhoto((Uri) data, getImageSize());


            }
        };

        mImageLoader.setLoadingImage(R.drawable.con_profile);
        mImageLoader.setImageFadeIn(false);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstance) {

        final View detailView =
                inflater.inflate(R.layout.contact_detail_fragment, container, false);

        final FontTextView contact_data =
                (FontTextView) detailView.findViewById(R.id.contact_number);
        final FontTextView extension_text =
                (FontTextView) detailView.findViewById(R.id.extension);
        final FontTextView access_text =
                (FontTextView) detailView.findViewById(R.id.access_number);

        final ImageView call_access_number = (ImageView) detailView.findViewById(R.id.call_access_number);
        final ImageView gohome = (ImageView) detailView.findViewById(R.id.contact_detail_home);
        final FontTextView name_text = (FontTextView) detailView.findViewById(R.id.contact_detail_title);
        final LinearLayout access_details_layout = (LinearLayout) detailView.findViewById(R.id.access_number_details);
        final ImageView contact_detail_divide = (ImageView) detailView.findViewById(R.id.contact_detail_divider);
        final ImageView call_free_imgview = (ImageView) detailView.findViewById(R.id.call_free_img);
        call_free_imgview.setVisibility(View.GONE);

        Button assignAccessNumber = (Button) detailView.findViewById(R.id.assignButton);

        Button unassignAccessNumber = (Button) detailView.findViewById(R.id.unassignButton);
        ImageView contactdetailback = (ImageView) detailView.findViewById(R.id.contact_detail_back_button);

        name_text.setText(mAccessContactDto.getDisplay_name());


        if (mAccessContactDto.getMobile_number() != null) {


            contact_data.setText(mAccessContactDto.getMobile_number());

            contact_data.setText(mAccessContactDto.getMobile_number());

            if (mAccessContactDto.getAccess_number() != null) {

                access_details_layout.setVisibility(View.VISIBLE);
                assignAccessNumber.setText("EDIT ACCESS NUMBER");

                if (mAccessContactDto.getExtension_number().equals("100")) {
                    extension_text.setVisibility(View.GONE);
                } else {
                    extension_text.setVisibility(View.VISIBLE);
                    extension_text.setText(mAccessContactDto.getExtension_number());
                }

                access_text.setText(mAccessContactDto.getAccess_number());
                unassignAccessNumber.setVisibility(View.VISIBLE);


            } else {
                access_details_layout.setVisibility(View.GONE);
                contact_detail_divide.setVisibility(View.GONE);

                assignAccessNumber.setText("ASSIGN ACCESS NUMBER");
                unassignAccessNumber.setVisibility(View.GONE);
            }
        }
        contact_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialNumber(mobileNumber.toString());
//                CommonUtility.makeCall(getActivity(), mAccessContactDto.getMobile_number());
//
//                getActivity().finish();
                launchCallingActivity(CommonUtility.validateNumberForApi(mAccessContactDto.getMobile_number()), mAccessContactDto.getDisplay_name(), SystemClock.elapsedRealtime(), false, null, System.currentTimeMillis());
//                call_listener.onCall(0,mAccessContactDto);


            }
        });

        gohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        name_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        contactdetailback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        call_access_number.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Log.e("extension ", "" + mAccessContactDto.getExtension_number());
                if (mAccessContactDto.getExtension_number() != null && !mAccessContactDto.getAccess_number().equals("100")) {
                    Log.e("extension ", "" + mAccessContactDto.getExtension_number());
                    CommonUtility.makeCall(getActivity(), access_text.getText().toString() + "," + extension_text.getText());
                } else
                    CommonUtility.makeCall(getActivity(), access_text.getText().toString());
                getActivity().finish();
            }
        });


        assignAccessNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String number = CommonUtility.validateNumberForApi(mAccessContactDto.getMobile_number());


                number = CommonUtility.validateText(number);
                Log.e("number", "" + number);

                if (number.length() > 8 && number.length() < 18 && number.matches("[0-9+]*")) {
                    if (CommonUtility.isNetworkAvailable(getActivity().getBaseContext())) {
                        if (mAccessContactDto.getAccess_number() == null) {
                            mAccessContactDto.setDisplay_name(CommonUtility.validateText(mAccessContactDto.getDisplay_name()));
                            AccessContactDto con = new AccessContactDto(mAccessContactDto.getContact_id(), mAccessContactDto.getDisplay_name(), mAccessContactDto.getAccess_number(), mAccessContactDto.getMobile_number(), mAccessContactDto.getExtension_number(), null, null, null, null, null);
                            Intent myIntent = new Intent(getActivity(), SelectCountryActivity.class);
                            myIntent.putExtra(VariableClass.Vari.SELECTEDDATA, con);
                            getActivity().startActivity(myIntent);
                            getActivity().overridePendingTransition(R.anim.animation1, R.anim.animation2);
                        } else {
                            Intent myIntent = new Intent(getActivity(), AccessEditActivity.class);
                            myIntent.putExtra(VariableClass.Vari.SELECTEDDATA, mAccessContactDto);
                            getActivity().startActivity(myIntent);
                            getActivity().overridePendingTransition(R.anim.animation1, R.anim.animation2);
                        }
                    } else
                        CommonUtility.showCustomAlertError(getActivity(), getString(R.string.internet_error));
                } else {
                    CommonUtility.showCustomAlertError(getActivity(), getString(R.string.mobile_number_not_valid));

                }
            }
        });


        unassignAccessNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CommonUtility.isNetworkAvailable(getActivity())) {
                    new UnassignAccessNumberTask().execute();
                } else {
                    CommonUtility.showCustomAlertError(getActivity(), getString(R.string.internet_error));
                }
            }
        });
        mImageView = (ImageView) detailView.findViewById(R.id.contact_image);
        return detailView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setContact();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Saves the contact Uri
        outState.putParcelable(EXTRA_CONTACT_URI, mContactUri);
    }


    private int getLargestScreenDimension() {

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        return height > width ? height : width;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private Bitmap loadContactPhoto(Uri contactUri, int imageSize) {

        if (!isAdded() || getActivity() == null) {
            return null;
        }

        final ContentResolver contentResolver = getActivity().getContentResolver();

        AssetFileDescriptor afd = null;

        if (Utils.hasICS()) {

            try {
                // Constructs the content Uri for the image
                Uri displayImageUri = Uri.withAppendedPath(contactUri, Photo.DISPLAY_PHOTO);

                afd = contentResolver.openAssetFileDescriptor(displayImageUri, "r");
                if (afd != null) {
                    return ImageLoader.decodeSampledBitmapFromDescriptor(
                            afd.getFileDescriptor(), imageSize, imageSize);
                }
            } catch (FileNotFoundException e) {

                e.printStackTrace();
                if (BuildConfig.DEBUG) {

                    Log.d(TAG, "Contact photo not found for contact " + contactUri.toString()
                            + ": " + e.toString());
                }
            } finally {

                if (afd != null) {
                    try {
                        afd.close();
                    } catch (IOException ignored) {

                    }
                }
            }
        }

        try {

            Uri imageUri = Uri.withAppendedPath(contactUri, Photo.CONTENT_DIRECTORY);

            afd = getActivity().getContentResolver().openAssetFileDescriptor(imageUri, "r");

            if (afd != null) {

                return ImageLoader.decodeSampledBitmapFromDescriptor(
                        afd.getFileDescriptor(), imageSize, imageSize);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // Catches file not found exceptions
            if (BuildConfig.DEBUG) {
                // Log debug message, this is not an error message as this exception is thrown
                // when a contact is legitimately missing a contact photo (which will be quite
                // frequently in a long Utteru).
                Log.d(TAG, "Contact photo not found for contact " + contactUri.toString()
                        + ": " + e.toString());
            }
        } finally {

            if (afd != null) {
                try {
                    afd.close();
                } catch (IOException e) {

                }
            }
        }

        // If none of the case selectors match, returns null.
        return null;
    }

    public class UnassignAccessNumberTask extends AsyncTask<Void, Void, String> {
        String jsonStr;

        @Override
        protected String doInBackground(Void... voids) {

            mAccessContactDto.setMobile_number(CommonUtility.validateNumberForApi(mAccessContactDto.getMobile_number()));

            jsonStr = Apis.getApisInstance(getActivity().getApplicationContext()).editContact(mAccessContactDto.getDisplay_name(), mAccessContactDto.getContact_id(), null, mAccessContactDto.getMobile_number(), null, null);
            Log.e("", "UnAssign: " + jsonStr);

            return jsonStr;
        }

        protected void onPostExecute(String result) {
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String response = jsonObj.getString("response");
                    // Getting JSON Array node
                    if (response.equals("1")) {
                        CommonUtility.showCustomAlert(getActivity(), "Access number unassigned");

                        final Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
                        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                        ContentResolver.requestSync(account, ContactsContract.AUTHORITY, bundle);

                    } else {
                        CommonUtility.showCustomAlertError(getActivity(), getString(R.string.server_error));
                        getActivity().finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                CommonUtility.showCustomAlertError(getActivity(), getString(R.string.internet_error));
                getActivity().finish();
            }

        }
    }




    void launchCallingActivity(String number, String name, long time, boolean isongoing, String price, long date) {

        String calleename = name;
        if (calleename == null)
            calleename = CommonUtility.getContactDisplayNameByNumber(number, getActivity().getBaseContext());


        CallData calldata = CallData.getCallDateInstance();
        Log.e("setting variable ", "setting variable " + name);
        calldata.setCallee_number(number);
        calldata.setCallee_name(calleename);
        calldata.setTime_elapsed(time);
        calldata.setCallType(isongoing);
        calldata.setCall_price(price);
        calldata.setDate(date);

        startActivity(new Intent(getActivity(), CallingScreenActivity.class));
        getActivity().overridePendingTransition(R.anim.animation1, R.anim.animation2);
    }


}
