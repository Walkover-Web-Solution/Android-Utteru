package com.Utteru.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.Utteru.R;
import com.Utteru.adapters.ExtentionAdapter;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessContactDto;
import com.Utteru.userService.UserService;
import com.splunk.mint.Mint;

import java.util.ArrayList;

/**
 * Created by root on 12/16/14.
 */
public class  SelectTypeActivity extends ActionBarActivity {

    GridView gridView;

    ExtentionAdapter adapter;
    ArrayList<String> extension;
    Context ctx = this;
    Boolean is_dedicated = false;
    Button next;
    ImageButton swithtype;
    AccessContactDto accessContactDto;
    ImageView back_button,gotohome;
    FontTextView titile,subtitile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectextension_ai);
        init();
        Mint.initAndStartSession(SelectTypeActivity.this, "395e969a");
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(ctx));

    }

    @Override
    protected void onResume() {

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                accessContactDto.setExtension_number(extension.get(position));


                Intent intent = new Intent(ctx, AccessAddActivity.class);
                intent.putExtra(VariableClass.Vari.SELECTEDDATA, accessContactDto);
                startActivity(intent);
                overridePendingTransition(R.anim.animation1, R.anim.animation2);


            }
        });
        swithtype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_dedicated) {
                    is_dedicated = false;

                    swithtype.setBackgroundResource(R.drawable.extension);
                    next.setVisibility(View.GONE);
                    gridView.setVisibility(View.VISIBLE);


                } else {
                    is_dedicated = true;
                    swithtype.setBackgroundResource(R.drawable.dedicated);
                    next.setVisibility(View.VISIBLE);
                    gridView.setVisibility(View.GONE);
                    accessContactDto.setExtension_number(VariableClass.Vari.DEDICATED);
                }

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                accessContactDto.setExtension_number(VariableClass.Vari.DEDICATED);


                Intent intent = new Intent(ctx, AccessAddActivity.class);
                intent.putExtra(VariableClass.Vari.SELECTEDDATA, accessContactDto);
                startActivity(intent);
                overridePendingTransition(R.anim.animation1, R.anim.animation2);
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        titile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        subtitile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        gotohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectTypeActivity.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        super.onResume();
    }

    void init() {
        back_button = (ImageView)findViewById(R.id.contacts_back);
        gotohome =(ImageView)findViewById(R.id.contacts_home);
        titile = (FontTextView)findViewById(R.id.contact_header);
        subtitile = (FontTextView)findViewById(R.id.contacts_subtitle);
        accessContactDto = new AccessContactDto();
        gridView = (GridView) findViewById(R.id.grid_view_ai);
        next = (Button) findViewById(R.id.assign_button);

        next.setText("Proceed");
        swithtype = (ImageButton) findViewById(R.id.type_switch);
        Bundle bundle = getIntent().getExtras();
        accessContactDto = (AccessContactDto) bundle.getSerializable(VariableClass.Vari.SELECTEDDATA);

        extension = new ArrayList<>();
        for (int i = 11; i < 100; i++) {
            extension.add("" + i);
        }

        ArrayList<String> usedextension = UserService.getUserServiceInstance(ctx).getAllExtensionByAccessNumber(accessContactDto.getAccess_number());

        if (usedextension.size() > 0) {

            extension.removeAll(usedextension);

            //disable button
            swithtype.setBackgroundResource(R.drawable.extension);
            swithtype.setEnabled(false);
            is_dedicated = false;
            next.setVisibility(View.GONE);


        } else {


            is_dedicated = true;
            gridView.setVisibility(View.GONE);
            next.setVisibility(View.VISIBLE);
            swithtype.setBackgroundResource(R.drawable.dedicated);
            swithtype.setEnabled(true);
            accessContactDto.setExtension_number(VariableClass.Vari.DEDICATED);
        }

        adapter = new ExtentionAdapter(ctx, extension.toArray(new String[extension.size()]));
        gridView.setAdapter(adapter);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animation4, R.anim.animation4);
    }
}
