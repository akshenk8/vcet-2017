package com.akshen.bankapp.donor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.Map;

import static com.akshen.bankapp.LoginActivity.BANK_ID_TAG;

public class DonorActivity extends AppCompatActivity {

    final String TAG = "DONOR";
    VolleyRequestQueue queue;
    LinearLayout uidLayout, details;
    ProgressDialog pd;
    String checkUrl;
    String addUrl;
    String bid;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donor_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("User Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        queue = VolleyRequestQueue.getInstance(this);
        pd = new ProgressDialog(this);
        pd.setTitle(getString(R.string.loading));
        checkUrl = getString(R.string.protocol) + getString(R.string.domain)
                + getString(R.string.baseUrl) + getString(R.string.checkDonationUrl);
        addUrl = getString(R.string.protocol) + getString(R.string.domain)
                + getString(R.string.baseUrl) + getString(R.string.addDonationUrl);

        SharedPreferences sp = new Utils().getApplicationPreference(this);
        bid = sp.getString(BANK_ID_TAG, null);
    }


    public void QrScanner(View view) {
        Intent i = new Intent(this, QRView.class);
        startActivityForResult(i, QRView.REQUEST_CODE);
        uidLayout = (LinearLayout) findViewById(R.id.uid_scan);
        details = (LinearLayout) findViewById(R.id.uid_details);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QRView.REQUEST_CODE && resultCode == RESULT_OK) {
            uid = (data.getStringExtra(QRView.RESPONSE_DATA));
            //Log.e("id",id);
            uidDone();
            //((EditText)findViewById(R.id.field_phone_number)).setText(id);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void uidDone() {
        pd.show();
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
                                JSONObject user = js.getJSONObject("data");

                                if (user.getInt("el") >= 90) {
                                    ((TextView) findViewById(R.id.bname)).setText("Bank name: " + js.getString("bname"));
                                    ((TextView) findViewById(R.id.uname)).setText("Username: " + user.getString("name"));
                                    ((TextView) findViewById(R.id.ubg)).setText("BloodGroup: " + user.getString("blood_group"));

                                    uidLayout.setVisibility(View.GONE);
                                    details.setVisibility(View.VISIBLE);
                                } else {
                                    Toast.makeText(DonorActivity.this, getString(R.string.notEligible), Toast.LENGTH_LONG).show();
                                }
                            } else if (msg.equalsIgnoreCase("confirmation")) {
                                Toast.makeText(DonorActivity.this, getString(R.string.accountDeactivated), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(DonorActivity.this, getString(R.string.incorrectCredentials), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(DonorActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
                            //Log.e("login",e.toString());
                        } finally {
                            pd.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(DonorActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("bid", bid);
                map.put("uid", uid);
                return map;
            }
        };
        stringRequest.setTag(TAG);
        queue.addToRequestQueue(this, stringRequest);

    }

    public void addDonation(View v) {
        final int quantity = Integer.parseInt(((EditText) findViewById(R.id.quantity)).getText().toString());
        if (quantity > 0) {
            pd.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, addUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                            Log.e("Response", response);
                            try {
                                JSONObject js = new JSONObject(response);
                                String msg = js.getString("msg");
                                if (msg.equalsIgnoreCase("success")) {
                                    //after login
                                    Toast.makeText(DonorActivity.this, getString(R.string.successful), Toast.LENGTH_LONG).show();
                                    finish();
                                } else if (msg.equalsIgnoreCase("confirmation")) {
                                    Toast.makeText(DonorActivity.this, getString(R.string.accountDeactivated), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(DonorActivity.this, getString(R.string.incorrectCredentials), Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(DonorActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
                                //Log.e("login",e.toString());
                            } finally {
                                pd.dismiss();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    Toast.makeText(DonorActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("bid", bid);
                    map.put("uid", uid);
                    map.put("quantity", "" + quantity);
                    return map;
                }
            };
            stringRequest.setTag(TAG);
            queue.addToRequestQueue(this, stringRequest);
        }
    }

}
