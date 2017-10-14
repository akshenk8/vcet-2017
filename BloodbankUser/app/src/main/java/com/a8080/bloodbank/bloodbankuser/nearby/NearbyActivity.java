package com.a8080.bloodbank.bloodbankuser.nearby;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a8080.bloodbank.bloodbankuser.R;
import com.a8080.bloodbank.bloodbankuser.home.HomeActivity;
import com.a8080.bloodbank.bloodbankuser.misc.VolleyRequestQueue;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NearbyActivity extends FragmentActivity implements OnMapReadyCallback {

    final String TAG = "Nearby";
    ArrayList<Marker> camps = new ArrayList<>();
    String URL;
    ProgressDialog pd;
    FirebaseAuth mAuth;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_activity);

        mAuth = FirebaseAuth.getInstance();

        URL = getString(R.string.protocol) + "" + getString(R.string.domain) + "" + getString(R.string.baseUrl) + "get_camp.php";
        getAllCamps();

        pd = new ProgressDialog(this);
        pd.setTitle(getString(R.string.loading));
        pd.show();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(NearbyActivity.this);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(NearbyActivity.this);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(NearbyActivity.this);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }

    public void getAllCamps() {
        VolleyRequestQueue queue = VolleyRequestQueue.getInstance(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response);
                        try {
                            JSONObject res = new JSONObject(response);
                            if (res.getString("msg").equalsIgnoreCase("success")) {
                                JSONArray ca = res.getJSONArray("camps");
                                for (int i = 0; i < ca.length(); i++) {
                                    JSONObject camp = ca.getJSONObject(i);
                                    JSONObject loc = new JSONObject(camp.getString("location"));
                                    LatLng zz = new LatLng(loc.getDouble("lat"), loc.getDouble("lng"));
                                    Marker m = mMap.addMarker(new MarkerOptions()
                                            .position(zz).snippet("Starts:" + camp.getString("start")
                                                    + "\nEnds:" + camp.getString("end"))
                                            .title(camp.getString("name")));
                                    camps.add(m);
                                }
                                if (ca.length() > 0)
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camps.get(0).getPosition(), 15));
                                else
                                    Toast.makeText(NearbyActivity.this, "Nothing Nearby", Toast.LENGTH_LONG).show();
                            } else {
                                ((TextView) findViewById(R.id.eligibilityText)).setText(getString(R.string.QRCode));
                            }
                        } catch (Exception e) {
                            Toast.makeText(NearbyActivity.this, "Error occured", Toast.LENGTH_LONG).show();
                            Log.e("login", e.toString());
                        } finally {
                            pd.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NearbyActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
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
        queue.addToRequestQueue(NearbyActivity.this, stringRequest);
    }
}
