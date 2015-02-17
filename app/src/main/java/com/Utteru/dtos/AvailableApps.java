package com.Utteru.dtos;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * Created by root on 11/13/14.
 */
public class AvailableApps {

    Drawable icon;
    String activityName;
    String activityFullName;
    String packageName;
    Intent intent;

    public AvailableApps(String activityName, String activityFullName, String packageName, Intent intent, Drawable icon) {
        this.icon = icon;
        this.activityFullName = activityFullName;
        this.activityName = activityName;
        this.packageName = packageName;
        this.intent = intent;

    }

    public Drawable getIcon() {
        return icon;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getActivityFullName() {
        return activityFullName;
    }

    public String getPackageName() {
        return packageName;
    }

    public Intent getIntent() {
        return intent;
    }


}
