package com.a8080.bloodbank.bloodbankuser.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.a8080.bloodbank.bloodbankuser.R;
import com.a8080.bloodbank.bloodbankuser.auth.LoginActivity;
import com.a8080.bloodbank.bloodbankuser.misc.VolleyRequestQueue;
import com.a8080.bloodbank.bloodbankuser.qrgenerator.QRGeneratorActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth mAuth;

    CardView imageButton;

    ImageButton img;

    private FirebaseUser mFirebaseUser;
    VolleyRequestQueue queue;

    private SharedPreferences sharedPreferences;
    String MyPREFERENCES = "BloodBankUser";
    private String TAG="ProfileActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        mAuth = FirebaseAuth.getInstance();

        imageButton = findViewById(R.id.img);
        toolbar = findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("User Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        img = findViewById(R.id.imgButton);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, QRGeneratorActivity.class));
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, QRGeneratorActivity.class));
            }
        });

        String[] listHeadingString=getResources().getStringArray(R.array.homeListHeadingStrings);
        String[] listContentString=getResources().getStringArray(R.array.homeListDescriptionStrings);
        TypedArray listImg=getResources().obtainTypedArray(R.array.homeListDrawable);
        ArrayList<HomeItemHolder> homeItems = new ArrayList<>();

        for(int i=0;i<listHeadingString.length;i++)
            homeItems.add(new HomeItemHolder(listHeadingString[i], listContentString[i], listImg.getResourceId(i,HomeItemHolder.NO_IMAGE_PROVIDED)));

        HomeAdapter adapter = new HomeAdapter(this, homeItems,R.color.listItemBackgroundColor);

        ListView listView = (ListView) findViewById(R.id.campHistoryList);

        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                Log.e("logout", "logout");
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                // search action
                return true;
            default:
                return true;
        }
    }

}
