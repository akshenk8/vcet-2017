package com.akshen.bankapp.camp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.akshen.bankapp.R;
import com.akshen.bankapp.misc.VolleyRequestQueue;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CampHistoryFragment extends Fragment {

    TextView noCamps;
    ListView listView;

    public CampHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.camp_history_fragment, container, false);
        noCamps=(TextView)view.findViewById(R.id.noCampsText);
        listView = (ListView) view.findViewById(R.id.campHistoryList);

        ((CampMainActivity)getActivity()).pd.setMessage(getString(R.string.loading));
        ((CampMainActivity)getActivity()).pd.show();
        loadData();
        return view;
    }

    public void dataRec(JSONObject jsonObject) throws JSONException {
        if(jsonObject.has("camps")){
            noCamps.setVisibility(View.GONE);
            JSONArray camps=jsonObject.getJSONArray("camps");

            ArrayList<HistoryHolder> events = new ArrayList<>();

            for(int i=0;i<camps.length();i++) {
                JSONObject obj=camps.getJSONObject(i);
                JSONObject loc=new JSONObject(obj.getString("location"));
                LatLng location=new LatLng(loc.getDouble("lat"),loc.getDouble("lng"));
                events.add(new HistoryHolder(obj.getInt("cid"),obj.getInt("bid"),obj.getString("name"),location,
                        obj.getString("address"),obj.getInt("volunteer"),obj.getString("doctors"),obj.getString("start"),obj.getString("end"),obj.getInt("status")));
            }

            HistoryListAdapter adapter = new HistoryListAdapter(getActivity(), events,((CampMainActivity)getActivity()));
            listView.setAdapter(adapter);
        }else{
            noCamps.setVisibility(View.VISIBLE);
        }
    }

    public void refresh(){
        ((CampMainActivity)getActivity()).createSuccessful();
    }

    public void loadData(){
        ((CampMainActivity)getActivity()).queue = VolleyRequestQueue.getInstance(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ((CampMainActivity)getActivity()).getUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response);
                        try {
                            JSONObject js = new JSONObject(response);
                            String msg = js.getString("msg");
                            if (msg.equalsIgnoreCase("success")) {
                                js.remove("msg");
                                dataRec(js);
                                //Toast.makeText(getActivity(), getString(R.string.successful), Toast.LENGTH_LONG).show();
                            } else if (msg.equalsIgnoreCase("confirmation")) {
                                Toast.makeText(getActivity(), getString(R.string.accountDeactivated), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
                            Log.e("error", e.toString());
                        } finally {
                            ((CampMainActivity)getActivity()).pd.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((CampMainActivity)getActivity()).pd.dismiss();
                Toast.makeText(getActivity(), getString(R.string.errorOccurred), Toast.LENGTH_LONG).show();
                Log.e("error", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("bid", ((CampMainActivity)getActivity()).getBankId());
                return map;
            }
        };
        stringRequest.setTag(CampMainActivity.TAG);
        ((CampMainActivity)getActivity()).queue.addToRequestQueue(getActivity(), stringRequest);
    }

}
