package com.Utteru.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.Utteru.R;
import com.Utteru.commonUtilities.FontTextView;
import com.Utteru.commonUtilities.Prefs;
import com.Utteru.commonUtilities.VariableClass;
import com.Utteru.dtos.AccessDataDto;
import com.splunk.mint.Mint;

/**
 * Created by vikas on 23/01/15.
 */
public class   AutoCountryDetect extends Activity {

    Button choose_country,choose_state;
    ImageView backpress,gototohome;
    LinearLayout auto_detected_country,country_not_detected;
    AccessDataDto dto;
    FontTextView or_message;
    FontTextView tittleback;
    FontTextView choose_another_country_message;
    FontTextView country_not_found;
String msgNotFound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.autocountrydetectlayout);
        Mint.initAndStartSession(AutoCountryDetect.this, "395e969a");
        Mint.setUserIdentifier(Prefs.getUserDefaultNumber(AutoCountryDetect.this));

        init();

    }

    @Override
    protected void onResume() {


        super.onResume();
    }

    void init()
    {
        choose_another_country_message = (FontTextView)findViewById(R.id.choose_another_country_message);
        auto_detected_country = (LinearLayout)findViewById(R.id.auto_detect_country_middle);
        country_not_detected = (LinearLayout)findViewById(R.id.country_not_found_body);
        choose_country = (Button)findViewById(R.id.choose_another_country);
        choose_state  = (Button)findViewById(R.id.auto_detect_country_button_choose_state);
        backpress = (ImageView)findViewById(R.id.auto_detect_country_back);
        gototohome = (ImageView)findViewById(R.id.auto_detect_country_home);
        tittleback = (FontTextView)findViewById(R.id.auto_detect_coutry_header);
        country_not_found= (FontTextView) findViewById(R.id.country_not_found_message);
        or_message = (FontTextView)findViewById(R.id.or_message);
        Bundle data = getIntent().getExtras();
        msgNotFound=country_not_found.getText().toString();


        if(data!=null&&data.containsKey(VariableClass.Vari.SELECTEDNAME))
        {
            //no access number from all country
             choose_another_country_message.setTextColor(getResources().getColor(R.color.black));
            dto = (AccessDataDto)getIntent().getSerializableExtra(VariableClass.Vari.SELECTEDNAME);
            choose_country.setBackgroundResource(R.drawable.contact_details_button_shape);
            auto_detected_country.setVisibility(View.GONE);
            country_not_detected.setVisibility(View.VISIBLE);
            or_message.setVisibility(View.GONE);


            country_not_found.setText(msgNotFound+dto.getCountry());

        }
        else if(data!=null&& data.containsKey(VariableClass.Vari.SELECTEDDATA)){

//            found access  number from all country
            choose_another_country_message.setTextColor(getResources().getColor(R.color.blue_dark));
            dto = (AccessDataDto)getIntent().getSerializableExtra(VariableClass.Vari.SELECTEDDATA);
            choose_country.setBackgroundResource(R.drawable.auto_detect_country_button_2);
            auto_detected_country.setVisibility(View.VISIBLE);
            country_not_detected.setVisibility(View.GONE);
            or_message.setVisibility(View.VISIBLE);

        }



        choose_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chooseCountry = new Intent(AutoCountryDetect.this,AllStateActivity.class);
                chooseCountry.putExtra(VariableClass.Vari.SELECTEDDATA,dto);
                startActivity(chooseCountry);
                AutoCountryDetect.this.finish();

            }
        });
        choose_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chooseCountry = new Intent(AutoCountryDetect.this,AllCountryActivity.class);
                chooseCountry.putExtra(VariableClass.Vari.SOURCECLASS,"");
                startActivity(chooseCountry);
                AutoCountryDetect.this.finish();

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
                startActivity(new Intent(AutoCountryDetect.this, MenuScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent menu = new Intent(this , MenuScreen.class);
        menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(menu);
        this.finish();
      //  super.onBackPressed();
        overridePendingTransition(R.anim.animation3, R.anim.animation4);
    }
}
