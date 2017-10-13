package com.akshen.bankapp.inventory;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.akshen.bankapp.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class BloodDataFragment extends Fragment {
    private ListItemAdapter adapter;
    private String displayText;
    private InventoryType inventoryType;
    private ArrayList<BloodGroupListItem> items;

    public enum InventoryType {
        WBC("WBC"), RBC("RBC"), FFP("FFP"), PC("PC"), CRY("CRY");
        private String inventoryType;

        InventoryType(String inventoryType) {
            this.inventoryType = inventoryType;
        }

        public String getInventoryType() {
            return inventoryType;
        }
    }

    public InventoryType getInventoryType() {
        return inventoryType;
    }

    public BloodDataFragment() {
        // Required empty public constructor
        items = new ArrayList<>();
    }

    public void setMetaData(String pageTitle, InventoryType inventoryType, JSONObject data, Context ctx) throws JSONException {
        this.displayText = pageTitle;
        this.inventoryType = inventoryType;
        if (data == null) {
            items.add(new BloodGroupListItem(BloodGroupListItem.BloodGroup.OP, ctx.getString(R.string.op), 0));
            items.add(new BloodGroupListItem(BloodGroupListItem.BloodGroup.ON, ctx.getString(R.string.on), 0));

            items.add(new BloodGroupListItem(BloodGroupListItem.BloodGroup.AP, ctx.getString(R.string.ap), 0));
            items.add(new BloodGroupListItem(BloodGroupListItem.BloodGroup.AN, ctx.getString(R.string.an), 0));

            items.add(new BloodGroupListItem(BloodGroupListItem.BloodGroup.BP, ctx.getString(R.string.bp), 0));
            items.add(new BloodGroupListItem(BloodGroupListItem.BloodGroup.BN, ctx.getString(R.string.bn), 0));

            items.add(new BloodGroupListItem(BloodGroupListItem.BloodGroup.ABP, ctx.getString(R.string.abp), 0));
            items.add(new BloodGroupListItem(BloodGroupListItem.BloodGroup.ABN, ctx.getString(R.string.abn), 0));
        } else {
            items.add(new BloodGroupListItem(BloodGroupListItem.BloodGroup.OP, ctx.getString(R.string.op),
                    data.getInt(BloodGroupListItem.BloodGroup.OP.getBloodGroup())));
            items.add(new BloodGroupListItem(BloodGroupListItem.BloodGroup.ON, ctx.getString(R.string.on),
                    data.getInt(BloodGroupListItem.BloodGroup.ON.getBloodGroup())));

            items.add(new BloodGroupListItem(BloodGroupListItem.BloodGroup.AP, ctx.getString(R.string.ap),
                    data.getInt(BloodGroupListItem.BloodGroup.AP.getBloodGroup())));
            items.add(new BloodGroupListItem(BloodGroupListItem.BloodGroup.AN, ctx.getString(R.string.an),
                    data.getInt(BloodGroupListItem.BloodGroup.AN.getBloodGroup())));

            items.add(new BloodGroupListItem(BloodGroupListItem.BloodGroup.BP, ctx.getString(R.string.bp),
                    data.getInt(BloodGroupListItem.BloodGroup.BP.getBloodGroup())));
            items.add(new BloodGroupListItem(BloodGroupListItem.BloodGroup.BN, ctx.getString(R.string.bn),
                    data.getInt(BloodGroupListItem.BloodGroup.BN.getBloodGroup())));

            items.add(new BloodGroupListItem(BloodGroupListItem.BloodGroup.ABP, ctx.getString(R.string.abp),
                    data.getInt(BloodGroupListItem.BloodGroup.ABP.getBloodGroup())));
            items.add(new BloodGroupListItem(BloodGroupListItem.BloodGroup.ABN, ctx.getString(R.string.abn),
                    data.getInt(BloodGroupListItem.BloodGroup.ABN.getBloodGroup())));
        }
    }

    public String getPageTitle() {
        return displayText;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.inventory_fragment, container, false);

        adapter = new ListItemAdapter(getActivity(), items);
        ListView listView = (ListView) rootView.findViewById(R.id.campHistoryList);
        listView.setAdapter(adapter);

        rootView.findViewById(R.id.savebutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSave();
            }
        });
        rootView.findViewById(R.id.cancelbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel();
            }
        });

        return rootView;
    }

    public void onSave() {
        if (isChanged()) {
            try {
                ((InventoryActivity) getActivity()).pd.setMessage(getString(R.string.saving));
                ((InventoryActivity) getActivity()).pd.show();


                final JSONObject bloodGroups = new JSONObject();
                for (int i = 0; i < adapter.getCount(); i++) {
                    BloodGroupListItem x = adapter.getItem(i);
                    bloodGroups.put(x.getBloodGroup().getBloodGroup(), x.getQuantity());
                }
                String time = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
                bloodGroups.put("last_updated", time);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, ((InventoryActivity) getActivity()).urlUpdate,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //Log.e("Response", response);
                                try {
                                    JSONObject js = new JSONObject(response);
                                    String msg = js.getString("msg");
                                    if (msg.equalsIgnoreCase("success")) {
                                        //after login
                                        for (int i = 0; i < adapter.getCount(); i++) {
                                            BloodGroupListItem x = adapter.getItem(i);
                                            x.saved();
                                        }
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(getActivity(), getString(R.string.saved), Toast.LENGTH_LONG).show();

                                    } else if (msg.equalsIgnoreCase("confirmation")) {
                                        Toast.makeText(getActivity(), getString(R.string.accountDeactivated), Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getActivity(), getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
                                    //Log.e("login",e.toString());
                                } finally {
                                    ((InventoryActivity) getActivity()).pd.dismiss();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ((InventoryActivity) getActivity()).pd.dismiss();
                        Toast.makeText(getActivity(), getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("bid", ((InventoryActivity) getActivity()).getBankId());
                        map.put("type", inventoryType.getInventoryType());
                        map.put("data", bloodGroups.toString());
                        return map;
                    }
                };
                stringRequest.setTag(InventoryActivity.TAG);
                ((InventoryActivity) getActivity()).queue.addToRequestQueue(getActivity(), stringRequest);

            } catch (Exception e) {
                Log.e("error",e.toString());
            }
        }
    }

    public void onCancel() {
        for (int i = 0; i < adapter.getCount(); i++) {
            BloodGroupListItem x = adapter.getItem(i);
            x.revertChanges();
        }
        adapter.notifyDataSetChanged();
    }

    public boolean isChanged() {
        for (int i = 0; i < adapter.getCount(); i++) {
            BloodGroupListItem x = adapter.getItem(i);
            if (x.isChanged())
                return true;
        }
        return false;
    }

    /*public void setDownloadedData(JSONObject res) throws JSONException{
        if(adapter==null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                BloodGroupListItem x = adapter.getItem(i);
                String type = x.getBloodGroup().getBloodGroup();
                if (res.has(type)) {
                    x.setQuantity(res.getInt(type));
                }
            }
        }
    }*/
}
