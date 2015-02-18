package com.Utteru.ui;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Photo;
import android.support.v4.BuildConfig;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.Utteru.R;
import com.Utteru.adapters.AccessContactAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.Constants;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessContactDto;
import com.Utteru.syncadapter.SyncAdapter;
import com.Utteru.userService.UserService;
import com.Utteru.util.ImageLoader;
import com.Utteru.util.Utils;
import com.stripe.android.compat.AsyncTask;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class ContactsListFragment extends ListFragment {

    private static final String TAG = "ContactsAccessFragment";
    private static final String STATE_PREVIOUSLY_SELECTED_KEY =
            "com.Utteru.ui.SELECTED_ITEM";
    public Context mContext;
    ArrayList<AccessContactDto> allcontacts;
    ArrayList<AccessContactDto> databasecontacts;
    FontTextView textempty;
    SwipeRefreshLayout refreshLayout;
    private AccessContactAdapter mAdapter;
    ProgressDialog dialog;

    private ImageLoader mImageLoader; // Handles loading the contact image in a background thread



    // can be reselected again
    private int mPreviouslySelectedSearchItem = 0;

    // Whether or not this fragment is showing in a two-pane layout
    private boolean mIsTwoPaneLayout;

    // Whether or not this is a search result view of this fragment, only used on pre-honeycomb
    // OS versions as search results are shown in-line via Action Bar search from honeycomb onward
    private boolean mIsSearchResultView = false;

    /**
     * Fragments require an empty constructor.
     */
    public ContactsListFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIsTwoPaneLayout = getResources().getBoolean(R.bool.has_two_panes);

        getActivity().setTitle("");
        if (savedInstanceState != null) {
            // If we're restoring state after this fragment was recreated then
            // retrieve previous search term and previously selected search
            // result.
            mPreviouslySelectedSearchItem =
                    savedInstanceState.getInt(STATE_PREVIOUSLY_SELECTED_KEY, 0);
        }
        mImageLoader = new ImageLoader(getActivity(), CommonUtility.getListPreferredItemHeight(getActivity())) {
            @Override
            protected Bitmap processBitmap(Object data) {
                // This gets called in a background thread and passed the data from
                // ImageLoader.loadImage().
                return loadContactPhotoThumbnail((String) data, getImageSize());
            }
        };

        // Set a placeholder loading image for the image loader
        mImageLoader.setLoadingImage(R.drawable.ic_contact_picture_holo_light);

        // Add a cache to the image loader
        mImageLoader.addImageCache(getActivity().getSupportFragmentManager(), 0.1f);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the list fragment layout

        View all_contacts_view = inflater.inflate(R.layout.contact_list_fragment, container, false);
        textempty = (FontTextView) all_contacts_view.findViewById(android.R.id.empty);
        textempty.setText("No contacts found");
        mContext = getActivity().getBaseContext();
        dialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(true);

        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        new loadData().execute();
        return all_contacts_view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        AccessContactDto cdto = (AccessContactDto) l.getItemAtPosition(position);
        Log.e("number on click", "" + cdto.getMobile_number());
        cdto = UserService.getUserServiceInstance(mContext).getAccessConDataByNumber(cdto.getMobile_number());


        if (cdto == null)
            cdto = (AccessContactDto) l.getItemAtPosition(position);
        else
            Log.e("mumber not mull from db", "number not null from db");


        Intent detailsActivity = new Intent(mContext, ContactDetailActivity.class);
        detailsActivity.putExtra(VariableClass.Vari.SELECTEDDATA, cdto);
        startActivity(detailsActivity);
        getActivity().overridePendingTransition(R.anim.animation1, R.anim.animation2);
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Let this fragment contribute menu items
        setHasOptionsMenu(true);
        // Set up ListView, assign adapter and set some listeners. The adapter was previously
        // created in onCreate().
        setListAdapter(mAdapter);

        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause image loader to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    mImageLoader.setPauseWork(true);
                } else {
                    mImageLoader.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        });
        refreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (CommonUtility.isNetworkAvailable(mContext)) {
//                  refresh list
                    final Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                    ContentResolver.requestSync(account, ContactsContract.AUTHORITY, bundle);
                } else {
                    CommonUtility.showCustomAlertError(getActivity(), getString(R.string.internet_error));
                }
                refreshLayout.setRefreshing(false);
            }
        });

        if (mIsTwoPaneLayout) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageLoader.setPauseWork(false);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.contact_list_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        menu.setGroupVisible(R.id.main_menu_group, true);
        if (mIsSearchResultView) {
            searchItem.setVisible(false);
        }
        final SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                Log.e("on query text change ", "on query text change");
                if (newText != null && !newText.equals("")) {
                    ArrayList<AccessContactDto> filterList = new ArrayList<AccessContactDto>();

                    for (AccessContactDto a : allcontacts) {
                        if (a.getDisplay_name().toLowerCase().startsWith(newText.toLowerCase())) {
                            filterList.add(a);
                            continue;
                        }
                        getListView().setAdapter(new AccessContactAdapter(filterList, getActivity(), mImageLoader));
                    }
                    return true;
                } else {
                    getListView().setAdapter(new AccessContactAdapter(allcontacts, getActivity(), mImageLoader));
                    return true;
                }
            }
        });
        super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent menu = new Intent(getActivity(), MenuScreen.class);
                startActivity(menu);
                break;
            case R.id.menu_search:
                if (!Utils.hasHoneycomb()) {
                    getActivity().onSearchRequested();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private Bitmap loadContactPhotoThumbnail(String photoData, int imageSize) {

        if (!isAdded() || getActivity() == null) {
            return null;
        }
        AssetFileDescriptor afd = null;

        try {
            Uri thumbUri;
            if (Utils.hasHoneycomb()) {
                thumbUri = Uri.parse(photoData);
            } else {
                final Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_URI, photoData);
                thumbUri = Uri.withAppendedPath(contactUri, Photo.CONTENT_DIRECTORY);
            }
            afd = getActivity().getContentResolver().openAssetFileDescriptor(thumbUri, "r");
            FileDescriptor fileDescriptor = afd.getFileDescriptor();
            if (fileDescriptor != null) {

                return ImageLoader.decodeSampledBitmapFromDescriptor(
                        fileDescriptor, imageSize, imageSize);
            }
        } catch (FileNotFoundException e) {

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Contact photo thumbnail not found for contact " + photoData
                        + ": " + e.toString());
            }
        } finally {

            if (afd != null) {
                try {
                    afd.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public ArrayList<AccessContactDto> readContactsNew() {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        String SELECTION;
        SELECTION =
                Contacts.DISPLAY_NAME
                        + "<>'' AND "
                        + ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1";

        ArrayList<AccessContactDto> list = new ArrayList<>();
        AccessContactDto adto;
        Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, SELECTION, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            name = CommonUtility.validateText(name);
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber =CommonUtility.validateNumberForUI(phoneNumber,mContext);
            String label = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
            String photoUri = null;
            String contact_id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
            if (currentApiVersion >= Build.VERSION_CODES.HONEYCOMB) {
                photoUri = phones.getString(phones.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            }
            Uri con_uri = Contacts.getLookupUri(phones.getLong(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)), phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY)));
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


    public class loadData extends AsyncTask<Void, Void, Void>

    {
        @Override
        protected void onPostExecute(Void aVoid) {

            mAdapter = new AccessContactAdapter(allcontacts, getActivity(), mImageLoader);
            if (getActivity() != null)
                getListView().setAdapter(mAdapter);
            dialog.dismiss();

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

        @Override
        protected void onPreExecute() {
            dialog.show();
            super.onPreExecute();
        }
    }



}
