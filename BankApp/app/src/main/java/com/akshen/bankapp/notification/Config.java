package com.akshen.bankapp.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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


public class Config {
    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "myride_registrationComplete";
    public static final String PUSH_NOTIFICATION = "myride_pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;

    public static final String BLOOD_BANK_ADMIN_FIREBASE = "blood_bank_admin_firebase";
    public static final String BLOOD_BANK_ADMIN_FIREBASE_SENT = "blood_bank_admin_firebase_sent";

    public void sendRegistrationToServer(final Context context, String token) {
        final SharedPreferences pref = new Utils().getApplicationPreference(context);
        pref.edit()
                .putBoolean(BLOOD_BANK_ADMIN_FIREBASE_SENT, false).commit();
        if (token == null) {
            token = pref.getString(BLOOD_BANK_ADMIN_FIREBASE, null);
        }
        final String bankid;
        if ((bankid = pref.getString(BANK_ID_TAG, null)) != null) {

        }
//        Log.e(MyFirebaseInstanceIDService.TAG, "sendRegistrationToServer: " + token);

        if (bankid == null || token == null) {
            return;
        }
        String url = context.getString(R.string.protocol) + context.getString(R.string.domain)
                + context.getString(R.string.baseUrl) + context.getString(R.string.firebaseUrl);
        final String finalToken = token;

        VolleyRequestQueue queue = VolleyRequestQueue.getInstance(context);
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
                                pref.edit()
                                        .putBoolean(BLOOD_BANK_ADMIN_FIREBASE_SENT,true).commit();
                            }
                        } catch (Exception e) {
                            //Log.e("login",e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("bid", bankid);
                map.put("firebase", finalToken);
                return map;
            }
        };
        stringRequest.setTag(MyFirebaseInstanceIDService.TAG);
        queue.addToRequestQueue(context, stringRequest);
    }
}
