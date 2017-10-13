package com.akshen.bankapp.notification;

import android.content.SharedPreferences;

import com.akshen.bankapp.misc.Utils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    public static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        storeRegIdInPref(refreshedToken);

        new Config().sendRegistrationToServer(getApplicationContext(), null);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = new Utils().getApplicationPreference(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Config.BLOOD_BANK_ADMIN_FIREBASE, token);
        editor.commit();
    }
}