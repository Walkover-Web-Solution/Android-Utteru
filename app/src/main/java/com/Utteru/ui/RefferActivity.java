package com.Utteru.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.Utteru.R;
import com.Utteru.adapters.ShareAppAdapter;
import com.Utteru.commonUtilities.CommonUtility;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.dtos.AvailableApps;
import com.splunk.mint.Mint;

import java.util.ArrayList;
import java.util.List;


public class RefferActivity extends ActionBarActivity {

    ListView appListview;
    ShareAppAdapter adapter;
    Intent sendIntent;
    ArrayList<AvailableApps> applist;
    String refer_link = "https://www.utteru.com/signup.php?";
    Context ctx;
    FontTextView tittleback;
    ImageView backpress, gototohome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_layout);
        init();
        Mint.initAndStartSession(RefferActivity.this, "395e969a");
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));

    }

    @Override
    protected void onResume() {

        appListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if (applist.get(position).getIntent() != null)
                    sharetext(applist.get(position), ctx);
                else
                    clipData("Refferal link", "" + refer_link);
            }
        });
        tittleback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        gototohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RefferActivity.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        super.onResume();
    }

    @Override
    public void onBackPressed() {
       super.onBackPressed();
        overridePendingTransition(R.anim.animation3, R.anim.animation4);
    }

    public void init() {
        ctx = this;


        backpress = (ImageView) findViewById(R.id.auto_detect_country_back);
        tittleback = (FontTextView) findViewById(R.id.auto_detect_coutry_header);
        gototohome = (ImageView) findViewById(R.id.auto_detect_country_home);
        refer_link = refer_link + "token=" + Integer.toHexString(Integer.parseInt(Prefs.getUserName(this)));
        appListview = (ListView) findViewById(R.id.share_list);
        sendIntent = new Intent(Intent.ACTION_SEND, null);
        sendIntent.setType("text/plain");
        applist = getAvailableAppsForIntent(sendIntent, getApplicationContext());
        Log.e("list size", "list size" + applist.size());
        adapter = new ShareAppAdapter(applist, getApplicationContext());
        appListview.setAdapter(adapter);



    }


    public ArrayList<AvailableApps> getAvailableAppsForIntent(Intent intent, Context context) {
        ArrayList<AvailableApps> availables = new ArrayList<AvailableApps>();

        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER);
        Log.e("infos list", "" + infos.size());

        availables.add(new AvailableApps("Copy Link", "Copy Link", "Copy Link", null, getResources().getDrawable(android.R.drawable.ic_menu_save)));


        for (ResolveInfo info : infos) {
            ActivityInfo activityInfo = info.activityInfo;
            IntentFilter filter = info.filter;
            if (filter != null && filter.hasAction(intent.getAction())) {

                // This activity resolves my Intent with the filter I'm looking for
                String activityPackageName = activityInfo.packageName;
                String activityName = activityInfo.loadLabel(manager).toString();
                String activityFullName = activityInfo.name;
                Drawable icon = activityInfo.loadIcon(manager);
                AvailableApps available = new AvailableApps(activityName, activityFullName, activityPackageName, intent, icon);

                availables.add(available);
            }


        }


        return availables;
    }


    void sharetext(AvailableApps apps, Context ctx) {
        String shareBody = ctx.getString(R.string.share_message) + " " + refer_link;
        Intent intent = apps.getIntent();
        intent.setPackage(apps.getPackageName());
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Utteru Refferal Link");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        ctx.startActivity(intent);

        RefferActivity.this.finish();
        overridePendingTransition(R.anim.animation1, R.anim.animation2);
    }

    void clipData(String label, String text) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText("text to clip");
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("text label", "text to clip");
            clipboard.setPrimaryClip(clip);
            CommonUtility.showCustomAlertCopy(this, text);
        }
    }

}







