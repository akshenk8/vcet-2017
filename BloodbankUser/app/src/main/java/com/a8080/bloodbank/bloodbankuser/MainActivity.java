package com.a8080.bloodbank.bloodbankuser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.a8080.bloodbank.bloodbankuser.auth.LoginActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
