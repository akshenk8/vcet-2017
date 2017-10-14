package com.a8080.bloodbank.smsreceiver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

    }
    public void onStartService(View view){
        Intent smsServiceInstance=new Intent(this,SmsProcessService.class);
        startService(smsServiceInstance);
    }
    public void onStopService(View view){
        stopService(new Intent(this, SmsProcessService.class));
    }
}
