/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.Utteru.syncadapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;

import com.Utteru.commonUtilities.Constants;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessContactDto;
import com.Utteru.platform.ContactManager;
import com.Utteru.ui.Apis;
import com.Utteru.userService.UserService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.provider.ContactsContract.Data.MIMETYPE;
import static android.provider.ContactsContract.Data.RAW_CONTACT_ID;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String Utteru_GROUP_NAME = "Utteru Contacts";
    public final Context mContext;
    public long groupId;
    public final static String CONTACTS_UPDATED = "contacts_updated";

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        //
        if (Prefs.getUserType(mContext).equals("4") || !Prefs.getResellerID(mContext).equals("2")) {
            return;
        }

        try {
            String jsonStr = Apis.getApisInstance(mContext).fetchAccessContacts();

            JSONArray contacts;

            ArrayList<ContentProviderOperation> opes = new ArrayList<ContentProviderOperation>();
            ContentResolver cr = getContext().getContentResolver();

            String[] params = new String[]{Constants.ACCOUNT_NAME};
            opes.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                    .withSelection(
                            ContactsContract.RawContacts.ACCOUNT_NAME + " = ? ", params)
                    .build());
            try {
                cr.applyBatch(ContactsContract.AUTHORITY, opes);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }


            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    if (jsonObj.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {

                        UserService.getUserServiceInstance(mContext).deleteAllAccessContacts();

                        return;
                    }

                    contacts = jsonObj.getJSONArray("content");
                    // looping through All Contacts
                    AccessContactDto dto;


                    if (contacts.length() > 0) {

                        UserService.getUserServiceInstance(mContext).deleteAllAccessContacts();
                    }

                    for (int i = 0; i < contacts.length(); i++) {

                        String hashi;
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("contactId");
                        String number = c.getString("contactNumber");
                        String name = c.getString("contactName");
                        hashi = c.getString("hash");
                        String email = c.getString("email");
                        String code = c.getString("code");
                        String access = c.getString("accessNumber");
                        String country = c.getString("countryName");
                        String state = c.getString("stateName");

                        number = "+" + code + number;
                        access = "+" + access;
                        //adding in database
                        dto = new AccessContactDto(id, name, access, number, hashi, null, null, country, state, null);
                        UserService.getUserServiceInstance(mContext).addAccessContacts(dto);

                        //adding in database

                        try {
                            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                            int rawindex = ops.size();
                            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, account.type)
                                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, Constants.ACCOUNT_NAME)
                                    .build());

                            // ------------------------------------------------------ Names
                            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(RAW_CONTACT_ID, rawindex)
                                    .withValue(MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                                    .build());

                            ops.add(ContentProviderOperation
                                    .newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(RAW_CONTACT_ID, rawindex)
                                    .withValue(MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                            ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.LABEL,
                                            "Utteru Number")
                                    .build());

                            if (hashi != null && !hashi.equals("")) {


                                if (hashi.equals("100")) {
                                    ops.add(ContentProviderOperation
                                            .newInsert(ContactsContract.Data.CONTENT_URI)
                                            .withValueBackReference(RAW_CONTACT_ID, rawindex)
                                            .withValue(MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
                                            .withValue(ContactsContract.CommonDataKinds.Im.DATA, "Dedicated Access Number")
                                            .withValue(ContactsContract.CommonDataKinds.Im.TYPE, ContactsContract.CommonDataKinds.Im.TYPE_CUSTOM)
                                            .withValue(ContactsContract.CommonDataKinds.Im.LABEL, "Utteru Access Number Type")
                                            .build());
                                } else {
                                    ops.add(ContentProviderOperation
                                            .newInsert(ContactsContract.Data.CONTENT_URI)
                                            .withValueBackReference(RAW_CONTACT_ID, rawindex)
                                            .withValue(MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
                                            .withValue(ContactsContract.CommonDataKinds.Im.DATA, "Access Number Extension: " + hashi)
                                            .withValue(ContactsContract.CommonDataKinds.Im.TYPE, ContactsContract.CommonDataKinds.Im.TYPE_CUSTOM)
                                            .withValue(ContactsContract.CommonDataKinds.Im.LABEL, "Utteru Extension No.")
                                            .build());
                                }
                            }
                            ops.add(ContentProviderOperation
                                    .newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(RAW_CONTACT_ID, rawindex)
                                    .withValue(MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Im.DATA, id)
                                    .withValue(ContactsContract.CommonDataKinds.Im.TYPE, ContactsContract.CommonDataKinds.Im.TYPE_CUSTOM)
                                    .withValue(ContactsContract.CommonDataKinds.Im.LABEL, "Phone91 Contact ID")
                                    .build());

                            if (hashi != null && !hashi.equals("")) {

                                if (hashi.equals("100")) {
                                    ops.add(ContentProviderOperation
                                            .newInsert(ContactsContract.Data.CONTENT_URI)
                                            .withValueBackReference(RAW_CONTACT_ID, rawindex)
                                            .withValue(MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, access)
                                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                                    ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
                                            .withValue(ContactsContract.CommonDataKinds.Phone.LABEL,
                                                    "Dedicated Access Number")
                                            .build());
                                } else {
                                    ops.add(ContentProviderOperation
                                            .newInsert(ContactsContract.Data.CONTENT_URI)
                                            .withValueBackReference(RAW_CONTACT_ID, rawindex)
                                            .withValue(MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, access)
                                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                                    ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
                                            .withValue(ContactsContract.CommonDataKinds.Phone.LABEL,
                                                    "Access Number Extension: " + hashi)
                                            .build());
                                }
                            }
                            if (email != null) {
                                ops.add(ContentProviderOperation
                                        .newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(RAW_CONTACT_ID, rawindex)
                                        .withValue(MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
                                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME)
                                        .build());
                            }

                            final ContentResolver resolver = mContext.getContentResolver();
                            long groupId = 0;
                            final Cursor cursor = resolver.query(ContactsContract.Groups.CONTENT_URI, new String[]{ContactsContract.Groups._ID},
                                    ContactsContract.Groups.ACCOUNT_NAME + "=? AND " + ContactsContract.Groups.ACCOUNT_TYPE + "=? AND " +
                                            ContactsContract.Groups.TITLE + "=?",
                                    new String[]{account.name, account.type, Utteru_GROUP_NAME}, null);
                            if (cursor != null) {
                                try {
                                    if (cursor.moveToFirst()) {
                                        groupId = cursor.getLong(0);
                                    }
                                } finally {
                                    cursor.close();
                                }
                            }

                            Log.e("", "Group ID: " + groupId);
                            if (groupId != 0) {
                                Prefs.setGroupId(mContext, String.valueOf(groupId));
                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(RAW_CONTACT_ID, rawindex)
                                        .withValue(ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE,
                                                ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, groupId)
                                        .build());

                                getContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                            }
                        } catch (RemoteException | OperationApplicationException e) {
                            e.printStackTrace();
                        }
                    }

                    mContext.sendBroadcast(new Intent().setAction(CONTACTS_UPDATED));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (NullPointerException e) {
            Log.e("", "Unable to create Account on this phone: " + e);
        }
        try {
            ContactManager.ensureSampleGroupExists(mContext, account);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
}