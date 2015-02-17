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
package com.Utteru.platform;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Groups;

public class ContactManager {
    public static final String Utteru_GROUP_NAME = "Utteru Contacts";

    public static long ensureSampleGroupExists(Context context, Account account) {
        final ContentResolver resolver = context.getContentResolver();
        long groupId = 0;
        final Cursor cursor = resolver.query(Groups.CONTENT_URI, new String[]{Groups._ID},
                Groups.ACCOUNT_NAME + "=? AND " + Groups.ACCOUNT_TYPE + "=? AND " +
                        Groups.TITLE + "=?",
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

        if (groupId == 0) {
            // Utteru Contacts doesn't exist yet, so create it
            final ContentValues contentValues = new ContentValues();
            contentValues.put(Groups.ACCOUNT_NAME, account.name);
            contentValues.put(Groups.ACCOUNT_TYPE, account.type);
            contentValues.put(Groups.TITLE, Utteru_GROUP_NAME);
            contentValues.put(Groups.GROUP_IS_READ_ONLY, true);
            final Uri newGroupUri = resolver.insert(Groups.CONTENT_URI, contentValues);
            if (newGroupUri != null)
                groupId = ContentUris.parseId(newGroupUri);
        }
        return groupId;
    }


}
