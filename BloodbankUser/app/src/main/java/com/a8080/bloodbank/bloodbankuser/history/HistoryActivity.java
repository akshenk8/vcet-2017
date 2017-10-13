package com.a8080.bloodbank.bloodbankuser.history;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.a8080.bloodbank.bloodbankuser.R;
import com.a8080.bloodbank.bloodbankuser.auth.LoginActivity;
import com.a8080.bloodbank.bloodbankuser.auth.ProfileActivity;
import com.a8080.bloodbank.bloodbankuser.home.HomeActivity;
import com.a8080.bloodbank.bloodbankuser.misc.VolleyRequestQueue;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.github.vipulasri.timelineview.TimelineView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {
    Toolbar toolbar;
    private String URL;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    VolleyRequestQueue queue;
    private String TAG="HistoryActivity";
    private ListView listView;
    private ArrayList<Item> arrayList;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);

        toolbar = findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("User Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        arrayList = new ArrayList<>();
        listView=findViewById(R.id.list_hisory);
         final HistoryAdaptor historyAdaptor=new HistoryAdaptor(this,arrayList);


        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.loggingIn));

        URL = getString(R.string.protocol) + "" + getString(R.string.domain) + "" + getString(R.string.baseUrl) + "getHistory.php";

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        queue = VolleyRequestQueue.getInstance(HistoryActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response);

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            System.out.println(jsonArray.length());
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject mJsonObject = jsonArray.getJSONObject(i);
                                arrayList.add(new Item(mJsonObject.getString("name"),mJsonObject.getString("timestamp"),mJsonObject.getString("quantity")));

                            }
                            historyAdaptor.notifyDataSetChanged();
                            listView.setAdapter(historyAdaptor);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {

                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(HistoryActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("uid", mFirebaseUser.getUid());
                return map;
            }
        };
        stringRequest.setTag(TAG);
        queue.addToRequestQueue(HistoryActivity.this, stringRequest);




    }
    class HistoryHolder{
        AppCompatTextView appCompatTextViewQuantity;
        AppCompatTextView appCompatTextViewDate;
        AppCompatTextView appCompatTextViewTitle;
    }
    class HistoryAdaptor extends BaseAdapter{

        private Context context; //context
        private ArrayList<Item> items; //data source of the list adapter

        //public constructor
        public HistoryAdaptor(Context context, ArrayList<Item> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HistoryHolder historyHolder;
            if(convertView==null){
                LayoutInflater li=getLayoutInflater();
                convertView=li.inflate(R.layout.history_list,null);
                historyHolder=new HistoryHolder();
                historyHolder.appCompatTextViewDate=convertView.findViewById(R.id.text_timeline_date);
                historyHolder.appCompatTextViewTitle=convertView.findViewById(R.id.text_timeline_title);
                historyHolder.appCompatTextViewQuantity=convertView.findViewById(R.id.text_timeline_quantity);
                convertView.setTag(historyHolder);
            }
            else{
                historyHolder= (HistoryHolder) convertView.getTag();
            }
            Item i= (Item) getItem(position);
            historyHolder.appCompatTextViewTitle.setText(i.getName());
            historyHolder.appCompatTextViewDate.setText(i.getTimestamp());
            historyHolder.appCompatTextViewQuantity.setText(i.getQuantity());
            return convertView;
        }
    }
}
