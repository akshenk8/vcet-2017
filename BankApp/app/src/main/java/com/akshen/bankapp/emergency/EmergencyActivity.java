package com.akshen.bankapp.emergency;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akshen.bankapp.R;
import com.akshen.bankapp.misc.Utils;
import com.akshen.bankapp.misc.VolleyRequestQueue;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.akshen.bankapp.LoginActivity.BANK_ID_TAG;

public class EmergencyActivity extends AppCompatActivity {

    final String TAG = "EMERGENCY";
    Spinner group;
    String bid;
    VolleyRequestQueue queue;
    ProgressDialog pd;
    String checkUrl;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("User Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        queue = VolleyRequestQueue.getInstance(this);
        pd = new ProgressDialog(this);
        pd.setTitle(getString(R.string.loading));

        checkUrl = getString(R.string.protocol) + getString(R.string.domain)
                + getString(R.string.baseUrl) + getString(R.string.algoUrl);

        List<String> list = new ArrayList<String>();
        String[] arr = getResources().getStringArray(R.array.blood_groups);

        Log.e("e", arr[0]);
        for (String x : arr) {
            list.add(x);
        }
        group = (Spinner) findViewById(R.id.blood_groups);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        group.setAdapter(adapter);

        sp = new Utils().getApplicationPreference(this);
        bid = sp.getString(BANK_ID_TAG, null);

        checkPrev();
    }

    public void checkPrev(){
        if(sp.contains("type1")||sp.contains("type2")){
            ((Button)findViewById(R.id.previous)).setVisibility(View.VISIBLE);
        }else{
            ((Button)findViewById(R.id.previous)).setVisibility(View.GONE);
        }
    }
    public void previous(View v){
        Intent i=new Intent(EmergencyActivity.this,PreviousEmerActivity.class);
        startActivity(i);
    }

    public void submit(View v) {
        String bg = group.getSelectedItem().toString();
        if (getString(R.string.op).equalsIgnoreCase(bg)) {
            bg = "OP";
        } else if (getString(R.string.on).equalsIgnoreCase(bg)) {
            bg = "ON";
        } else if (getString(R.string.ap).equalsIgnoreCase(bg)) {
            bg = "AP";
        } else if (getString(R.string.an).equalsIgnoreCase(bg)) {
            bg = "AN";
        } else if (getString(R.string.bp).equalsIgnoreCase(bg)) {
            bg = "BP";
        } else if (getString(R.string.bn).equalsIgnoreCase(bg)) {
            bg = "BN";
        } else if (getString(R.string.abp).equalsIgnoreCase(bg)) {
            bg = "ABP";
        } else if (getString(R.string.abn).equalsIgnoreCase(bg)) {
            bg = "ABN";
        }
        final int quantity = Integer.parseInt(((EditText) findViewById(R.id.quantity)).getText().toString());
        if (quantity > 0) {
            pd.show();
            final String finalBg = bg;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, checkUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                            Log.e("Response", response);
                            try {
                                JSONObject js = new JSONObject(response);
                                String msg = js.getString("msg");
                                if (msg.equalsIgnoreCase("success")) {
                                    //after login
                                    sp.edit().putString("type" + js.getString("type"), response).commit();
                                    submit2(finalBg, quantity);
                                } else if (msg.equalsIgnoreCase("confirmation")) {
                                    Toast.makeText(EmergencyActivity.this, getString(R.string.accountDeactivated), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(EmergencyActivity.this, getString(R.string.incorrectCredentials), Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(EmergencyActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
                                //Log.e("login",e.toString());
                            } finally {
//                                pd.dismiss();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    Toast.makeText(EmergencyActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("bid", bid);
                    map.put("bg", finalBg);
                    map.put("quantity", "" + quantity);
                    map.put("type", "1");
                    return map;
                }
            };
            stringRequest.setTag(TAG);
            queue.addToRequestQueue(this, stringRequest);
        }
    }

    public void submit2(final String bg, final int quantity) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, checkUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Log.e("Response", response);
                        try {
                            JSONObject js = new JSONObject(response);
                            String msg = js.getString("msg");
                            if (msg.equalsIgnoreCase("success")) {
                                //after login
                                sp.edit().putString("type" + js.getString("type"), response).commit();
//                                checkPrev();
                                previous(new View(EmergencyActivity.this));
                            } else if (msg.equalsIgnoreCase("confirmation")) {
                                Toast.makeText(EmergencyActivity.this, getString(R.string.accountDeactivated), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(EmergencyActivity.this, getString(R.string.incorrectCredentials), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(EmergencyActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
                            //Log.e("login",e.toString());
                        } finally {
                                pd.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(EmergencyActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("bid", bid);
                map.put("bg", bg);
                map.put("quantity", "" + quantity);
                map.put("type", "2");
                return map;
            }
        };
        stringRequest.setTag(TAG);
        queue.addToRequestQueue(this, stringRequest);
    }
}