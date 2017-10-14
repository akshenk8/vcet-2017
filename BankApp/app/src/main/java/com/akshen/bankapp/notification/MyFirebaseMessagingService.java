package com.akshen.bankapp.notification;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.akshen.bankapp.emergency.ConfirmUserActivity;
import com.akshen.bankapp.misc.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;
        //Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
        // Check if message contains a notification payload.
        /*if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            //handleNotification(remoteMessage.getNotification().getBody());
        }*/
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
//            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    /*private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }*/

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            if (json.has("data")) {
                JSONObject data = json.getJSONObject("data");

                String title = "Request from Bloodbank: " + data.getString("name");

                Intent resultIntent = new Intent(getApplicationContext(), ConfirmUserActivity.class);
                resultIntent.putExtra("data", data.toString());
                //resultIntent.putExtra("location", location);

                showNotificationMessage(getApplicationContext(), "Blood Request", title, resultIntent);
            } else if (json.has("msg")) {
                Log.e("confirm", json.getJSONObject("msg").toString());
                json = json.getJSONObject("msg");

                SharedPreferences sp = new Utils().getApplicationPreference(this);
                try {
                    if (sp.contains("type1")) {
                        JSONObject type1 = new JSONObject(sp.getString("type1", ""));
                        if (type1.getString("msg").equalsIgnoreCase("success")) {
                            JSONArray jsonArray = type1.getJSONArray("banks");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if(jsonObject.getString("bid").equals(json.getString("bid"))){
                                    if(json.getString("ans").equalsIgnoreCase("yes")) {
                                        jsonObject.put("status", "2");
                                    }else{
                                        jsonObject.put("status", "1");
                                    }
                                }
                            }
                        }
                        sp.edit().putString("type1" , type1.toString()).commit();
                    }

                } catch (Exception e) {

                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationUtils.showNotificationMessage(title, message, intent);
    }
}