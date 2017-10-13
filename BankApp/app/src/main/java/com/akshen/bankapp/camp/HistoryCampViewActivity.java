package com.akshen.bankapp.camp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.akshen.bankapp.R;
import com.akshen.bankapp.misc.VolleyRequestQueue;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class HistoryCampViewActivity extends AppCompatActivity {

    HistoryHolder data;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camp_single_history_view_activity);
        data = getIntent().getParcelableExtra("data");

        url = getString(R.string.protocol) + getString(R.string.domain)
                + getString(R.string.baseUrl) + getString(R.string.campDeleteUrl);

        ((TextView)findViewById(R.id.camp_name)).setText("Name:"+data.get_nameofevent());
        ((TextView)findViewById(R.id.camp_address)).setText("Address:"+data.getAddress());
        ((TextView)findViewById(R.id.start_date)).setText("Start Date:"+data.get_startdate().toString());
        ((TextView)findViewById(R.id.end_date)).setText("End Date:"+data.get_enddate().toString());
        ((TextView)findViewById(R.id.camp_volunteers)).setText("Volunteers:"+data.getVolunteers());
        ((TextView)findViewById(R.id.camp_doctors)).setText("Doctors:"+data.getDoctors());
    }

    public void delete(View view) {
        VolleyRequestQueue queue=VolleyRequestQueue.getInstance(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response);
                        try {
                            JSONObject js = new JSONObject(response);
                            String msg = js.getString("msg");
                            if (msg.equalsIgnoreCase("success")) {
                                js.remove("msg");
                                Toast.makeText(HistoryCampViewActivity.this, getString(R.string.successful), Toast.LENGTH_LONG).show();

                                finish();
                            } else if (msg.equalsIgnoreCase("confirmation")) {
                                Toast.makeText(HistoryCampViewActivity.this, getString(R.string.accountDeactivated), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(HistoryCampViewActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(HistoryCampViewActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
                            Log.e("error", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HistoryCampViewActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
                Log.e("error", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("cid", ""+data.getCampid());
                map.put("bid",""+data.getBankid());
                return map;
            }
        };
        stringRequest.setTag(CampMainActivity.TAG);
        queue.addToRequestQueue(this, stringRequest);
    }
}
