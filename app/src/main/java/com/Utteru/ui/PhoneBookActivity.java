package com.Utteru.ui;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.Utteru.R;
import com.Utteru.adapters.PhoneBookAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessContactDto;
import com.Utteru.util.Utils;
import com.splunk.mint.Mint;
import com.stripe.android.compat.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by root on 12/23/14.
 */
public class  PhoneBookActivity extends ActionBarActivity {

    ListView all_contacts_listview;
    ArrayList<AccessContactDto> allcontacts;
    Context ctx = this;
    AccessContactDto selected_contact;
    private PhoneBookAdapter mAdapter;
    private boolean mIsSearchResultView = false;
    SwipeRefreshLayout refreshLayout;
    FontTextView nothing_found;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phonebook_layout);
        init();
        Mint.initAndStartSession(PhoneBookActivity.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));


        new loadData().execute();


    }

    @Override
    protected void onResume() {



        all_contacts_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selected_contact = (AccessContactDto) parent.getItemAtPosition(position);
                PhoneBookActivity.this.finish();
            }
        });

        super.onResume();
    }
    @Override
    public boolean onSearchRequested() {
        boolean isSearchResultView = false;
        return !isSearchResultView && super.onSearchRequested();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear();
        getMenuInflater().inflate(R.menu.contact_list_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        menu.setGroupVisible(R.id.main_menu_group, true);
        if (mIsSearchResultView) {
            searchItem.setVisible(false);
        }


        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.toString() != null && !newText.toString().equals("")) {
                    ArrayList<AccessContactDto> filterList = new ArrayList<AccessContactDto>();

                    for (AccessContactDto a : allcontacts) {
                        if (a.getDisplay_name().toLowerCase().startsWith(newText.toString().toLowerCase())) {
                            filterList.add(a);
                            continue;
                        }


                        all_contacts_listview.setAdapter(new PhoneBookAdapter(filterList, PhoneBookActivity.this, null));

                    }
                    return true;
                } else {
                    all_contacts_listview.setAdapter(new PhoneBookAdapter(allcontacts, PhoneBookActivity.this, null));
                    return true;
                }

            }

        });

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.menu_search:
                if (!Utils.hasHoneycomb()) {

                    onSearchRequested();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void init() {

        nothing_found = (FontTextView)findViewById(R.id.nothing_found_pb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Pick a contact");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setIcon(android.R.color.transparent);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_contacts_screen)));
        all_contacts_listview = (ListView) findViewById(R.id.pb_list);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (CommonUtility.isNetworkAvailable(PhoneBookActivity.this)) {

                    new loadData().execute();

                } else
                    CommonUtility.showCustomAlert(PhoneBookActivity.this, getString(R.string.internet_error));
                refreshLayout.setRefreshing(false);

            }
        });


    }

    public ArrayList<AccessContactDto> readContactsNew() {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        String SELECTION;
        SELECTION =
                ContactsContract.Contacts.DISPLAY_NAME
                        + "<>'' AND "
                        + ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1";

        ArrayList<AccessContactDto> list = new ArrayList<AccessContactDto>();
        AccessContactDto adto;
        Cursor phones = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, SELECTION, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            name = name.replaceAll("[^\\w\\s\\-_]", "");
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber = phoneNumber.replaceAll("-", "");
            phoneNumber=phoneNumber.replaceAll("\\s+", "");
            String label = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
            String photoUri = null;
            String contact_id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
            if (currentApiVersion >= Build.VERSION_CODES.HONEYCOMB) {
                photoUri = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            }
            Uri con_uri = ContactsContract.Contacts.getLookupUri(phones.getLong(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)), phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY)));
            adto = new AccessContactDto(contact_id, name, null, phoneNumber, null, photoUri, con_uri.toString(), null, null, null);
            if (label != null) {
                if (!label.equals("Utteru Number") && !label.equals("Dedicated Access Number") & !label.contains("Access Number Extension:"))
                    list.add(adto);
            } else {
                list.add(adto);
            }
        }
        phones.close();
        return list;
    }

    @Override
    public void finish() {

        Log.e("on finish  called ", "on finish ");
        Intent data = new Intent();
        data.putExtra(VariableClass.Vari.SELECTEDDATA, selected_contact);
        setResult(Activity.RESULT_OK, data);
        super.finish();
        overridePendingTransition(R.anim.animation3, R.anim.animation4);

    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.animation3, R.anim.animation4);
        super.onBackPressed();
    }

    public class loadData extends AsyncTask<Void, Void, Void>

    {
        @Override
        protected void onPreExecute() {

            CommonUtility.show_PDialog(ctx,getString(R.string.please_wait));
            CommonUtility.dialog.setCancelable(true);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if(allcontacts.size()>0) {

                mAdapter = new PhoneBookAdapter(allcontacts, PhoneBookActivity.this, null);
                nothing_found.setVisibility(View.GONE);
               all_contacts_listview.setVisibility(View.VISIBLE);
                all_contacts_listview.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
            else {
                all_contacts_listview.setVisibility(View.GONE);
                nothing_found.setVisibility(View.VISIBLE);
            }


             CommonUtility.dialog.dismiss();
             super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            allcontacts = readContactsNew();
            Collections.sort(allcontacts, new Comparator<AccessContactDto>() {
                @Override
                public int compare(AccessContactDto lhs, AccessContactDto rhs) {

                    return lhs.getDisplay_name().compareToIgnoreCase(rhs.getDisplay_name());
                }
            });
            return null;
        }
    }
}
