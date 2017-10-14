package com.akshen.bankapp.emergency;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akshen.bankapp.LoginActivity;
import com.akshen.bankapp.R;
import com.akshen.bankapp.home.HomeActivity;
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

public class ConfirmUserActivity extends AppCompatActivity {

    JSONObject data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_user);

        try {
            data = new JSONObject(getIntent().getStringExtra("data"));
            Log.e("firebase", data.getString("firebase"));
            String text="Requested By: "+data.getString("name")+
                    "\nAt Location: "+data.getString("location");
            ((TextView)findViewById(R.id.data)).setText(text);
        } catch (Exception e) {

        }
    }

    public void submit(View view) {
        try {
            String ans = ((Button) view).getText().toString().toLowerCase();
            final String fire=data.getString("firebase");
            SharedPreferences sp = new Utils().getApplicationPreference(this);
            String bid=sp.getString(LoginActivity.BANK_ID_TAG, null);
            final JSONObject data=new JSONObject();
            data.put("bid",bid);
            data.put("ans",ans);

            String url = getString(R.string.protocol) + getString(R.string.domain)
                    + getString(R.string.baseUrl)+"/confirm.php";
            VolleyRequestQueue queue = VolleyRequestQueue.getInstance(ConfirmUserActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("Response", response);
                            try {
                                JSONObject js = new JSONObject(response);
                                String msg = js.getString("msg");
                                if (msg.equalsIgnoreCase("success")) {
                                    //after login

                                    finish();
                                } else if (msg.equalsIgnoreCase("confirmation")) {
                                    Toast.makeText(ConfirmUserActivity.this, getString(R.string.accountDeactivated), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(ConfirmUserActivity.this, getString(R.string.incorrectCredentials), Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(ConfirmUserActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
                                //Log.e("login",e.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ConfirmUserActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("data", data.toString());
                    map.put("fire", fire);
                    return map;
                }
            };
            stringRequest.setTag("CONFIRM ACT");
            queue.addToRequestQueue(this,stringRequest);
        } catch (Exception e) {

        }
    }
}
