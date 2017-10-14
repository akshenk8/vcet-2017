package com.a8080.bloodbank.bloodbankuser.misc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.a8080.bloodbank.bloodbankuser.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by akshen on 14/10/17.
 */

public class LocationReciever extends BroadcastReceiver {

    final public static String ONE_TIME = "onetime";
    private static final String TAG = LocationReciever.class.getSimpleName();
    private final int PID=50046;

    NetworkOrGPSLocation networkOrGPSLocation;

    private NetworkLocationListener networkLocationListener = new NetworkLocationListener() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == NetworkLocationListener.NETWORK_LOCATION_INTENT_FILTER) {
                if (intent.hasExtra(ERROR)) {
                    Log.e("Location",intent.getStringExtra(ERROR));
                    return;
                }
                String respsonse = "Latitude:" + intent.getDoubleExtra(LAT, 0.0) + "\nLongitude"
                        + intent.getDoubleExtra(LNG, 0.0) + "\nAccuracy:" + intent.getDoubleExtra(ACC, 0.0);
                Log.e("Location",respsonse);

                final double lat=intent.getDoubleExtra(LAT, 0.0),lng=intent.getDoubleExtra(LNG, 0.0),acc=intent.getDoubleExtra(ACC, 0.0);

                String url = context.getString(R.string.protocol) + context.getString(R.string.domain)
                        + context.getString(R.string.baseUrl) + "location_update.php";

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                // [END initialize_auth]

                if (mAuth.getCurrentUser() == null) {
                    return;
                }
                final String uid = mAuth.getCurrentUser().getUid();
                if (uid == null)
                    return;

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

                                    } else if (msg.equalsIgnoreCase("confirmation")) {
                                        Log.e("location", "blocked");
                                    }
                                } catch (Exception e) {
                                    Log.e("location", e.toString());
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("location", error.toString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("uid", uid);
                        map.put("lat", ""+lat);
                        map.put("lng", ""+lng);
                        map.put("acc", ""+acc);
                        return map;
                    }
                };
                stringRequest.setTag(TAG);
                queue.addToRequestQueue(context, stringRequest);


            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();

        networkOrGPSLocation = new NetworkOrGPSLocation(context, NetworkOrGPSLocation.Mode.GSM_ONLY, true);
        networkOrGPSLocation.onStart();
        LocalBroadcastManager.getInstance(context).registerReceiver(networkLocationListener,
                new IntentFilter(NetworkLocationListener.NETWORK_LOCATION_INTENT_FILTER));

        try {
            networkOrGPSLocation.fetchLocation();
        }catch (Exception e){

        }
        LocalBroadcastManager.getInstance(context).registerReceiver(networkLocationListener,
                new IntentFilter(NetworkLocationListener.NETWORK_LOCATION_INTENT_FILTER));
        networkOrGPSLocation.onStop();
        //Release the lock
        wl.release();
    }

    public void SetAlarm(Context context)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, LocationReciever.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, PID, intent, 0);
        //After after 15 seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60*1000 * 15 , pi);
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, LocationReciever.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, PID, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void setOnetimeTimer(Context context){
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, LocationReciever.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
    }

}