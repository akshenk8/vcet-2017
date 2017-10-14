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
import android.widget.TextView;
import android.widget.Toast;

import com.a8080.bloodbank.bloodbankuser.R;
import com.a8080.bloodbank.bloodbankuser.auth.LoginActivity;
import com.a8080.bloodbank.bloodbankuser.misc.LocationReciever;
import com.a8080.bloodbank.bloodbankuser.misc.VolleyRequestQueue;
import com.a8080.bloodbank.bloodbankuser.qrgenerator.QRGeneratorActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    CardView imageButton;
    ImageButton img;
    VolleyRequestQueue queue;
    String MyPREFERENCES = "BloodBankUser";
    String URL;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private SharedPreferences sharedPreferences;
    private String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        mAuth = FirebaseAuth.getInstance();

        LocationReciever alarm = new LocationReciever();
        alarm.SetAlarm(this);

        URL = getString(R.string.protocol) + "" + getString(R.string.domain) + "" + getString(R.string.baseUrl) + getString(R.string.eligibleUrl);
        checkEligibilty();

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

        String[] listHeadingString = getResources().getStringArray(R.array.homeListHeadingStrings);
        String[] listContentString = getResources().getStringArray(R.array.homeListDescriptionStrings);
        TypedArray listImg = getResources().obtainTypedArray(R.array.homeListDrawable);
        ArrayList<HomeItemHolder> homeItems = new ArrayList<>();

        for (int i = 0; i < listHeadingString.length; i++)
            homeItems.add(new HomeItemHolder(listHeadingString[i], listContentString[i], listImg.getResourceId(i, HomeItemHolder.NO_IMAGE_PROVIDED)));

        HomeAdapter adapter = new HomeAdapter(this, homeItems, R.color.listItemBackgroundColor);

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
                LocationReciever alarm=new LocationReciever();
                alarm.CancelAlarm(this);
                Log.e("logout", "logout");
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                // search action
                return true;
            default:
                return true;
        }
    }

    public void checkEligibilty() {
        queue = VolleyRequestQueue.getInstance(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response);
                        try {
                            JSONObject res=new JSONObject(response);
                            if (res.getString("msg").equalsIgnoreCase("success")) {
                                int diff=res.getInt("diff");
                                if(diff>=90){
                                    ((TextView)findViewById(R.id.eligibilityText)).setText(getString(R.string.eligibleToDonate));
                                }else{
                                    ((TextView)findViewById(R.id.eligibilityText)).setText(getString(R.string.willBeEligibleIn1)+" "+diff+" "+getString(R.string.willBeEligibleIn2));
                                }
                            }else{
                                ((TextView)findViewById(R.id.eligibilityText)).setText(getString(R.string.QRCode));
                            }
                        } catch (Exception e) {
                            Toast.makeText(HomeActivity.this, "Error occured", Toast.LENGTH_LONG).show();
                            //Log.e("login",e.toString());
                        } finally {
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("uid", mAuth.getCurrentUser().getUid());
                return map;
            }
        };
        stringRequest.setTag(TAG);
        queue.addToRequestQueue(HomeActivity.this, stringRequest);
    }
}
