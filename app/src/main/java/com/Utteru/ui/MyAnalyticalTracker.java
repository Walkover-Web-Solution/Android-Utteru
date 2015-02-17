package com.Utteru.ui;

import android.app.Application;
import android.content.Context;

import com.Utteru.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

public class MyAnalyticalTracker  {

    public static MyAnalyticalTracker myAnalyticalTracker;

    // The following line should be changed to include the correct property id.
    private static final String PROPERTY_ID = "UA-57145578-1";

    //Logging TAG
    private static final String TAG = "Utteru";

    public static int GENERAL_TRACKER = 0;
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    private MyAnalyticalTracker() {
        super();

    }
    public static MyAnalyticalTracker  getTrackerInstance()
    {
        if(myAnalyticalTracker==null)
            myAnalyticalTracker= new MyAnalyticalTracker();
        return  myAnalyticalTracker;

    }

    synchronized Tracker getTracker(TrackerName trackerId,Context ctx) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(ctx);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml.app_tracker)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : analytics.newTracker(R.xml.ecommerce_tracker);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }
}