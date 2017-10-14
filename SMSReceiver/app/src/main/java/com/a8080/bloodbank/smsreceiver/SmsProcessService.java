package com.a8080.bloodbank.smsreceiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Network;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.constraint.solver.Cache;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.x;
import static android.R.id.message;

public class SmsProcessService extends Service {
    SmsReceiver smsReceiver = new SmsReceiver();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        System.out.println("On start command method");
        Toast.makeText(this, "onStart command", Toast.LENGTH_LONG).show();

        return START_STICKY;
    }

    private class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle myBundle = intent.getExtras();
            SmsMessage[] messages = null;
            String strMessage = "";
            String res = "";
            System.out.println("On receive method");
            Toast.makeText(context, "onreceive", Toast.LENGTH_LONG).show();

            if (myBundle != null) {
                Object[] pdus = (Object[]) myBundle.get("pdus");
                messages = new SmsMessage[pdus.length];

                for (int i = 0; i < messages.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    strMessage += "SMS From: " + messages[i].getOriginatingAddress();
                    strMessage += " : ";
                    strMessage += messages[i].getMessageBody();
                    strMessage += "\n";
                }

                final String mess = messages[0].getDisplayMessageBody();
                final String sender = messages[0].getDisplayOriginatingAddress();
                final String x[] = mess.split(" ");
                boolean flag = true;
                if (!(x[0].equalsIgnoreCase("blood")))
                    return;
                Toast.makeText(context, strMessage, Toast.LENGTH_SHORT).show();
                System.out.println(sender);
                System.out.println(mess);

                if (x.length < 4) {
                    flag = false;
                } else if (x[3].matches("-?\\d+(\\.\\d+)?")) {
                    flag = false;
                }
                if (flag == false) {
                    /*Intent i = new Intent(Intent.ACTION_SEND);
                    i.setData(Uri.parse("smsto:"+sender));  // This ensures only SMS apps respond
                    i.putExtra("sms_body", "Please send message in the format: BLOOD <blood_group> <quantity> <location>");
                    if (i.resolveActivity(getPackageManager()) != null) {
                        startActivity(i);
                    }*/
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage("" + sender, null, "Please send message in the format: BLOOD <blood_group> <quantity> <location>", null, null);
                    return;
                }
                x[1] = x[1].replace("+", "P");
                x[1] = x[1].replace("-", "N");
                VolleyRequestQueue volleyRequestQueue = VolleyRequestQueue.getInstance(SmsProcessService.this);

//for POST requests, only the following line should be changed to

                StringRequest sr = new StringRequest(Request.Method.POST, "http://192.168.137.88/bloodbank/admin/algo.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //Log.e("HttpClient", "success! response: " + response.toString());
                                System.out.println("response=" + response);

                                try {
                                    JSONObject js = new JSONObject(response);
                                    if (js.getString("msg").equalsIgnoreCase("success")) {
                                        System.out.println(1);
                                        JSONObject jo = js.getJSONObject("bank");
                                        String bname = jo.getString("name");
                                        String city = jo.getString("city");
                                        String url = js.getString("url");
                                        String lat = jo.getString("lat");
                                        String lng = jo.getString("lng");
                                        String message = bname + "\n" + city + "\n" + "http://192.168.137.88/bloodbank/admin/" + url;
                                        message += "\n" + "http://www.google.com/maps/place/" + lat + "," + lng;
                                        SmsManager sms = SmsManager.getDefault();
                                        sms.sendTextMessage("" + sender, null, message, null, null);
                                    } else {

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
//                                "http://192.168.137.88/bloodbank/admin/"
                                /*Intent i = new Intent(Intent.ACTION_SEND);
                                i.setData(Uri.parse("smsto:"+sender));  // This ensures only SMS apps respond
                                i.putExtra("sms_body", response);
                                if (i.resolveActivity(getPackageManager()) != null) {
                                    startActivity(i);
                                }*/
//                                SmsManager sms = SmsManager.getDefault();
//                                sms.sendTextMessage(""+sender, null, "", null, null);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("HttpClient", "error: " + error.toString());
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("bg", x[1].toUpperCase());
                        params.put("type", "3");
                        params.put("quantity", x[2]);
                        params.put("city", x[3].toLowerCase());
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/x-www-form-urlencoded");
                        return params;
                    }
                };
                volleyRequestQueue.addToRequestQueue(SmsProcessService.this, sr);
            }


        }


    }
}