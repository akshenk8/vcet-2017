package com.a8080.bloodbank.bloodbankuser.auth;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.a8080.bloodbank.bloodbankuser.R;
import com.a8080.bloodbank.bloodbankuser.home.HomeActivity;
import com.a8080.bloodbank.bloodbankuser.misc.VolleyRequestQueue;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    private Button register;

    private EditText name;
    private EditText mobileno;
    private EditText bloodgroup;

    private RadioGroup radioGroup;
    String MyPREFERENCES = "BloodBankUser";

    private String TAG="ProfileActivity";
    private EditText dob;
    private String gender;
    private String URL;

    private FirebaseUser mFirebaseUser;
    VolleyRequestQueue queue;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, this.MODE_PRIVATE);

        if(sharedPreferences.getBoolean("FLAG",Boolean.FALSE) == Boolean.FALSE){
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }

        URL = getString(R.string.protocol) + "" + getString(R.string.domain) + "" + getString(R.string.baseUrl) + "signup.php";

        toolbar = findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("User Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mAuth = FirebaseAuth.getInstance();



        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        mFirebaseUser = mAuth.getCurrentUser();

        name = findViewById(R.id.userName);
        mobileno = findViewById(R.id.mobileNo);

        mobileno.setText(mAuth.getCurrentUser().getPhoneNumber());
        bloodgroup = findViewById(R.id.bloodGroup);

        radioGroup = findViewById(R.id.genderGroup);
        dob = findViewById(R.id.dob);
        register = findViewById(R.id.register);

        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.loggingIn));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.male:
                        gender = "male";
                        break;
                    case R.id.female:
                        gender = "female";
                        break;
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().trim().isEmpty()) {
                    name.setError("Required");
                }
                if (mobileno.getText().toString().trim().isEmpty()) {
                    mobileno.setError("Required");
                }
                if (bloodgroup.getText().toString().trim().isEmpty()) {
                    bloodgroup.setError("Required");
                }
                if (dob.getText().toString().trim().isEmpty()) {
                    dob.setError("Required");
                } else {

                    // Call API for accessing data
                    queue = VolleyRequestQueue.getInstance(ProfileActivity.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.e("Response", response);
                                    try {
                                        if (response.equals("true")) {
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putBoolean("FLAG", Boolean.FALSE);
                                            editor.commit();

                                            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                                            finish();
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(ProfileActivity.this, "Error occured", Toast.LENGTH_LONG).show();
                                        //Log.e("login",e.toString());
                                    } finally {
                                        pd.dismiss();
                                    }
                                }
                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.dismiss();
                            Toast.makeText(ProfileActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<>();
                            map.put("uid", mFirebaseUser.getUid());
                            map.put("name", name.getText().toString().trim());
                            map.put("mobile_no", mAuth.getCurrentUser().getPhoneNumber());
                            map.put("blood_group", bloodgroup.getText().toString().trim());
                            map.put("gender", gender);
                            map.put("dob", dob.getText().toString().trim());

                            Log.e(TAG,mAuth.getCurrentUser().getPhoneNumber());
                            return map;
                        }
                    };
                    stringRequest.setTag(TAG);
                    queue.addToRequestQueue(ProfileActivity.this, stringRequest);

                }
            }
        });

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
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("FLAG", Boolean.FALSE);
                editor.commit();

                startActivity(new Intent(this, LoginActivity.class));
                finish();
                // search action
                return true;
            default:
                return true;
        }
    }

}
