package com.akshen.bankapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.akshen.bankapp.home.HomeActivity;
import com.akshen.bankapp.misc.Utils;
import com.akshen.bankapp.misc.VolleyRequestQueue;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    static final int MY_REQUEST=10;
    private String url = "";
    String TAG = "LOGIN", NAME_TAG = "blood_bank_app_admin_email", PASS_TAG = "blood_bank_app_admin_pass";
    public static String BANK_ID_TAG = "blood_bank_app_admin_id";
    EditText name, pass;
    Button login;
    ProgressDialog pd;
    VolleyRequestQueue queue;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);


        sp = new Utils().getApplicationPreference(this);

        if (sp.getString(BANK_ID_TAG, null) != null) {
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
            finish();
        }


        url = getString(R.string.protocol) + getString(R.string.domain)
                + getString(R.string.baseUrl) + getString(R.string.loginUrl);

        name = (EditText) findViewById(R.id.login_userid);
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkName();
                }
            }
        });

        pass = (EditText) findViewById(R.id.userpass);
        pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkPass();
                }
            }
        });
        pass.setLongClickable(false);
        pass.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        pass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (pass.getCompoundDrawables()[DRAWABLE_RIGHT] != null && event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (pass.getRight() - pass.getCompoundDrawables()
                            [DRAWABLE_RIGHT].getBounds().width())) {

                        int type = pass.getInputType();
                        if (type == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                            pass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_pass_invisible, 0);
                        } else if (type == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                            pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_pass_visible, 0);
                        }


                        return true;
                    }
                }
                return false;
            }
        });

        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.loggingIn));

//        name.setText(sp.getString(NAME_TAG, ""));
//        pass.setText(sp.getString(PASS_TAG, ""));

        login = (Button) findViewById(R.id.login_user);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogin();
            }
        });
    }

    public boolean checkName() {
        String username = getName();
        if (username.isEmpty() || username.equals("")) {
            name.setError(getString(R.string.cantBeEmptyError));
            return false;
        } else if (!username.matches("[a-zA-Z0-9]{5,25}")) {
            name.setError(getString(R.string.userCustomError));
            return false;
        }
        return true;
    }

    public boolean checkPass() {
        String password = getPass();
        if (password.isEmpty() || password.equals("")) {
            pass.setError(getString(R.string.cantBeEmptyError));
        } else if (!password.matches(".{8,20}")) {
            pass.setError(getString(R.string.passwordCustomError));
        }
        return true;
    }

    public String getName() {
        return name.getText().toString().toLowerCase().trim();
    }

    public String getPass() {
        return pass.getText().toString();
    }

    public void onLogin() {
        final String username = getName();
        final String password = getPass();
        if (!new Utils().isInternetEnabled(this)) {
            Toast.makeText(LoginActivity.this, getString(R.string.noInternetConnection), Toast.LENGTH_LONG).show();
            return;
        }
        if (checkName() && checkPass()) {

            pd.show();
            queue = VolleyRequestQueue.getInstance(LoginActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                            Log.e("Response", response);
                            try {
                                JSONObject js = new JSONObject(response);
                                String msg = js.getString("msg");
                                if (msg.equalsIgnoreCase("success")) {
                                    //after login

                                    //Toast.makeText(Login_User.this, "Success", Toast.LENGTH_LONG).show();
                                    //intent code
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                                            |Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
                                    String bankid = js.getString("bid");
                                    sp.edit().putString(BANK_ID_TAG, bankid)
                                            .commit();
//                                    intent.putExtra("bankid", bankid);
                                    startActivity(intent);
                                    finish();
                                } else if (msg.equalsIgnoreCase("confirmation")) {
                                    Toast.makeText(LoginActivity.this, getString(R.string.accountDeactivated), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, getString(R.string.incorrectCredentials), Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(LoginActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
                                //Log.e("login",e.toString());
                            } finally {
                                pd.dismiss();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", username);
                    map.put("pass", password);
                    return map;
                }
            };
            stringRequest.setTag(TAG);
            queue.addToRequestQueue(this,stringRequest);

        }
    }
}