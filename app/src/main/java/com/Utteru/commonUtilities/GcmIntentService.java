package com.Utteru.commonUtilities;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.widget.RemoteViews;

import com.Utteru.R;
import com.Utteru.dtos.AccessDataDto;
import com.Utteru.ui.IntialiseData;
import com.Utteru.ui.MenuScreen;
import com.Utteru.ui.SelectExtensionAI;
import com.Utteru.ui.SignUpHome;
import com.Utteru.utteru_sip.DialerActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by root on 11/25/14.
 */
public class
        GcmIntentService extends IntentService {
    private NotificationManagerCompat mNotificationManager;
    private int notificationID = 108;
    String TAG = "utteru gcm service";


    public GcmIntentService() {
        super("GcmIntentService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle

            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {

            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {

            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {


                Log.i(TAG, "Received: " + extras.get("default").toString());
                try {
                    JSONObject jobj = new JSONObject(extras.get("default").toString());
                    jobj = jobj.getJSONObject("GCM");

                    String alert = jobj.getString("alert");


                    String dataType=jobj.getString("dataType");


                    jobj = new JSONObject(jobj.get("data").toString());

                    int type = Integer.parseInt(jobj.getString("contentType"));

                    switch (type) {
                        case 0:
                            //show message
                            updateNotification(this, alert, dataType);
                            break;
                        case 1:
                            //assign access number
                            jobj = jobj.getJSONObject("content");
                            assginAccessNumberNotification(alert,dataType, new AccessDataDto(jobj.getString("country"), jobj.getString("code"), jobj.getString("state"), jobj.getString("accessNo")), jobj.getString("calleeNo"), jobj.getString("calleeName"), this);
                            break;
                        case 2:
                            new IntialiseData(this).initAccessData();
                            break;
                        case 3:

                            jobj = jobj.getJSONObject("content");
                            JSONArray jarry;

                            if (jobj.has("perse")) {
                                jarry = jobj.getJSONArray("perse");
                                Prefs.PERSE_SIP = jarry.getString(0);
                                Prefs.PERSE_PORT = jarry.getString(1);

                            }
                            if (jobj.has("udp")) {
                                jarry = jobj.getJSONArray("udp");
                                Prefs.SIP_SERVER = jarry.getString(0);
                                Prefs.SIP_PORT = jarry.getString(1);

                            }


                            break;
                    }


                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message

    public  void assginAccessNumberNotification(String title,String subtitle, AccessDataDto dto, String selectedNumber, String selectedName, Context context) {


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context);

        mBuilder.setContentTitle(title);
        mBuilder.setContentText(subtitle);
        mBuilder.setTicker("Utteru Alert");
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setAutoCancel(true);
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_launcher);
        mBuilder.setLargeIcon(icon);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        Intent notificationIntent = new Intent(context, SelectExtensionAI.class);

        notificationIntent.putExtra(VariableClass.Vari.SELECTEDDATA, dto);
        notificationIntent.putExtra(VariableClass.Vari.SELECTEDNAME, selectedName);
        notificationIntent.putExtra(VariableClass.Vari.SELECTEDNUMBER, selectedNumber);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_ONE_SHOT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager =
                NotificationManagerCompat.from(context);
        mNotificationManager.notify(notificationID, mBuilder.build());


    }
    public void updateNotification(Context context, String title,String subtitle) {

      /* Invoking the default notification service */

        Log.e("mesages",""+title+"      "+subtitle);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);


        mBuilder.setContentTitle(getString(R.string.app_name));
        mBuilder.setContentText(title);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setOngoing(false);
        mBuilder.setGroupSummary(true);
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.logo);
        mBuilder.setLargeIcon(icon);
        mBuilder.setAutoCancel(true);
        Intent intent = new Intent(context, MenuScreen.class); //create intent for notifcation
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
        mBuilder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Dismiss", pIntent);
        mBuilder.setContentIntent(pIntent);
        Notification builder =
                new NotificationCompat.BigTextStyle(mBuilder).bigText(title).build();
        mNotificationManager =
                NotificationManagerCompat.from(context);
        mNotificationManager.notify(notificationID, builder);
    }
}
