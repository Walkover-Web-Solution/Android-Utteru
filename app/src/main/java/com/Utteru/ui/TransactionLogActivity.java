package com.Utteru.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.Utteru.R;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.dtos.TransactionLogsDto;
import com.splunk.mint.Mint;

public class TransactionLogActivity extends ActionBarActivity
        implements TransactionListFragment.OnURLSelectedListener {

    FontTextView title;
    ImageView backpress,gototohome;
    FontTextView   tittleback;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_layout);
        Mint.initAndStartSession(TransactionLogActivity.this, "395e969a");
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(TransactionLogActivity.this));
        tittleback = (FontTextView)findViewById(R.id.auto_detect_coutry_header);
        backpress = (ImageView)findViewById(R.id.auto_detect_country_back);
        gototohome = (ImageView)findViewById(R.id.auto_detect_country_home);
        title = (FontTextView) findViewById(R.id.auto_detect_coutry_header);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        TransactionListFragment listFragment = new TransactionListFragment();
        ft.add(R.id.displayList, listFragment, "List_Fragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        tittleback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menu =new Intent(TransactionLogActivity.this,MenuScreen.class);
                menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(menu);

            }
        });
        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menu =new Intent(TransactionLogActivity.this,MenuScreen.class);
                menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(menu);

            }
        });
        gototohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransactionLogActivity.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }


    @Override
    public void onURLSelected(TransactionLogsDto dto) {
        Log.v("AndroidFragmentActivity", dto.toString());

        TransactionDetailsFragment detailFragment = new TransactionDetailsFragment();
        detailFragment.setURLContent(dto);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.displayList, detailFragment, "Detail_Fragment2");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
        overridePendingTransition(R.anim.animation1, R.anim.animation2);
    }

    public void setTitle(String message) {
        title.setText("" + message);
    }
}
