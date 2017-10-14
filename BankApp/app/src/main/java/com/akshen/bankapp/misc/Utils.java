package com.akshen.bankapp.misc;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

/**
 * Created by Akshen Kadakia.
 */

public class Utils {

    private final String PREF="bb_admin_preference";
    public SharedPreferences getApplicationPreference(Context context){
        return context.getSharedPreferences(PREF,context.MODE_PRIVATE);
    }

    public boolean isInternetEnabled(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isGpsEnabled(Context context) {
//        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        try {
            int locationMode = 0;
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void start(){

    }
}
