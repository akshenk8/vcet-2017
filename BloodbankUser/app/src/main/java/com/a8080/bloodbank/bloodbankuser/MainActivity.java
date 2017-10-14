package com.a8080.bloodbank.bloodbankuser;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.a8080.bloodbank.bloodbankuser.auth.LoginActivity;
import com.a8080.bloodbank.bloodbankuser.misc.LocationReciever;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    final int MY_PERMISSIONS_REQUEST = 2222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        checkAndReq();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void checkAndReq(){
        final String[] needsPermission = {android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_WIFI_STATE, android.Manifest.permission.CHANGE_WIFI_STATE,
                android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WAKE_LOCK};

        ArrayList<String> arr = new ArrayList<>();
        for (String per : needsPermission) {
            if (ContextCompat.checkSelfPermission(this, per) == PackageManager.PERMISSION_DENIED) {
                arr.add(per);
            }
        }
        if(arr.size()>0) {
            String[] req = new String[arr.size()];
            arr.toArray(req);

            ActivityCompat.requestPermissions(this, req, MY_PERMISSIONS_REQUEST);
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                boolean req=false;
                for(int x:grantResults){
                    if(x!=PackageManager.PERMISSION_GRANTED){
                        req=true;
                    }
                }
                if (req) {
                    checkAndReq();
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
}
