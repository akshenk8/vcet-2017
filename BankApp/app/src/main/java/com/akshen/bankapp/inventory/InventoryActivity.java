package com.akshen.bankapp.inventory;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.akshen.bankapp.LoginActivity;
import com.akshen.bankapp.R;
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

public class InventoryActivity extends AppCompatActivity {
    String urlUpdate = "";
    String urlDownload = "";
    static String TAG ="INVENTORY";
    private ViewPager viewPager;
    private TabFragmentAdapter adapter;
    ProgressDialog pd;
    VolleyRequestQueue queue;

    String bankid;

    public String getBankId(){
        return bankid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_activity);

        urlUpdate = getString(R.string.protocol) + getString(R.string.domain)
                + getString(R.string.baseUrl) + getString(R.string.inventoryUpdateUrl);

        urlDownload= getString(R.string.protocol) + getString(R.string.domain)
                + getString(R.string.baseUrl) + getString(R.string.inventoryGetUrl);

        viewPager = (ViewPager) findViewById(R.id.inventory_viewpager);
        /*viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    try {
                        BloodDataFragment bloodDataFragment = (BloodDataFragment) adapter.getItem(viewPager.getCurrentItem());
                        if (bloodDataFragment.isChanged()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(InventoryActivity.this);
                            builder.setTitle(getString(R.string.saveChanges));
                            builder.setMessage(getString(R.string.saveChangesMadeTo)+" " + adapter.getPageTitle(viewPager.getCurrentItem()));
                            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((BloodDataFragment) adapter.getItem(viewPager.getCurrentItem())).onCancel();
                                }
                            });
                            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((BloodDataFragment) adapter.getItem(viewPager.getCurrentItem())).onSave();
                                }
                            });
                            builder.setCancelable(false);
                            AlertDialog alert1 = builder.create();
                            alert1.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });*/

        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage(getString(R.string.loading));
        pd.show();

        SharedPreferences sp =new Utils().getApplicationPreference(this);
        bankid = sp.getString(LoginActivity.BANK_ID_TAG, null);

        fetchData();
    }

    private void fetchData(){
        queue = VolleyRequestQueue.getInstance(InventoryActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlDownload,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Log.e("Response", response);
                        try {
                            JSONObject js = new JSONObject(response);
                            String msg = js.getString("msg");
                            if (msg.equalsIgnoreCase("success")) {
                                js.remove("msg");

                                adapter = new TabFragmentAdapter(InventoryActivity.this, getSupportFragmentManager(),js);
                                viewPager.setAdapter(adapter);
                                TabLayout tabLayout = (TabLayout) findViewById(R.id.inventory_tabs);
                                tabLayout.setupWithViewPager(viewPager);

//                                adapter.setDownloadedData(js);
                            } else if (msg.equalsIgnoreCase("confirmation")) {
                                Toast.makeText(InventoryActivity.this, getString(R.string.accountDeactivated), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(InventoryActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(InventoryActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
//                            Log.e("error",e.toString());
                        } finally {
                            pd.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(InventoryActivity.this, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
//                Log.e("error",error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("bid", getBankId());
                return map;
            }
        };
        stringRequest.setTag(TAG);
        queue.addToRequestQueue(this,stringRequest);
    }
}
