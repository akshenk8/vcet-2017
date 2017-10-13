package com.akshen.bankapp.emergency;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.akshen.bankapp.R;
import com.akshen.bankapp.misc.Utils;

import org.json.JSONObject;

public class PreviousEmerActivity extends AppCompatActivity {

    JSONObject type1,type2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previous_emer_activity);
        SharedPreferences sp = new Utils().getApplicationPreference(this);
        try {
            if (sp.contains("type1")) {
                type1 = new JSONObject(sp.getString("type1", ""));
            }
            if (sp.contains("type2")) {
                type2 = new JSONObject(sp.getString("type2", ""));
            }
            Log.e("type1",type1.toString());
            Log.e("type2",type2.toString());
        }catch (Exception e){}
    }
}
