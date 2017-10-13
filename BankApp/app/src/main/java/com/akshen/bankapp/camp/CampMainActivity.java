package com.akshen.bankapp.camp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.akshen.bankapp.LoginActivity;
import com.akshen.bankapp.R;
import com.akshen.bankapp.misc.Utils;
import com.akshen.bankapp.misc.VolleyRequestQueue;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class CampMainActivity extends AppCompatActivity {
    String updateUrl = "";
    String getUrl = "";
    final static String TAG = "CAMP";
    ProgressDialog pd;
    VolleyRequestQueue queue;

    String bankid;

    public String getBankId() {
        return bankid;
    }

    public enum Status {
        COMPLETED(0), ONGOING(1), PENDING(2);
        int val;

        Status(int val) {
            this.val = val;
        }

        public int getStatus() {
            return val;
        }
        public int statusString(){
            if(val==0){
                return R.string.completed;
            }else if(val==1){
                return R.string.ongoing;
            }else if(val==2){
                return R.string.pending;
            }
            return -1;
        }
    }

    PlacePicker.IntentBuilder builder;
    private final int PLACE_PICKER_REQUEST = 201;
    private ViewPager viewPager;
    private TabFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camp_main_activity);

        updateUrl = getString(R.string.protocol) + getString(R.string.domain)
                + getString(R.string.baseUrl) + getString(R.string.campUpdateUrl);

        getUrl = getString(R.string.protocol) + getString(R.string.domain)
                + getString(R.string.baseUrl) + getString(R.string.campGetUrl);

        viewPager = (ViewPager) findViewById(R.id.camp_viewpager);
        adapter = new TabFragmentAdapter(CampMainActivity.this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.camp_tabs);
        tabLayout.setupWithViewPager(viewPager);

        //place picker
        builder = new PlacePicker.IntentBuilder();

        pd = new ProgressDialog(this);
        pd.setCancelable(false);

        SharedPreferences sp = new Utils().getApplicationPreference(this);
        bankid = sp.getString(LoginActivity.BANK_ID_TAG, null);
    }

    public void pickPlace(View v) {
        try {
            if (new Utils().isGpsEnabled(this)) {
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            } else {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e("error", e.toString());
            Toast.makeText(this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PLACE_PICKER_REQUEST:
                if (resultCode == RESULT_OK) {
                    Place selectedPlace = PlacePicker.getPlace(this, data);
                    ((CampNewFragment)adapter.getItem(0)).placeUpdated(selectedPlace);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void submit(View view) {
        ((CampNewFragment)adapter.getItem(0)).submit(view);
    }

    public void createSuccessful(){
        viewPager.setCurrentItem(1,true);
        adapter.createSuccessful();
    }
}
