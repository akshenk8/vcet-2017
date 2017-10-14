package com.akshen.bankapp.camp;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.akshen.bankapp.R;
import com.akshen.bankapp.misc.VolleyRequestQueue;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CampNewFragment extends Fragment {

    EditText campName,volunteerText,doctorsText;
    TextView address,campEndDateText;
    ScrollView scrollView;
    DatePicker startDate, endDate;
    TimePicker startTime, endTime;
    Button locationButton;
    private LatLng location;
    private boolean callFromStartDate = false;

    public CampNewFragment() {
        // Required empty public constructor
    }

    public void placeUpdated(Place selectedPlace){
        address.setText(selectedPlace.getAddress());
        location = selectedPlace.getLatLng();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.camp_new_fragment, container, false);
        locationButton=(Button) view.findViewById(R.id.pickLocation);
        campName = (EditText) view.findViewById(R.id.camp_name);
        address = (TextView) view.findViewById(R.id.camp_address);
        //date and time
        Calendar today = Calendar.getInstance();
        Calendar std = Calendar.getInstance();
        startDate = (DatePicker) view.findViewById(R.id.camp_start_date);
        endDate = (DatePicker) view.findViewById(R.id.camp_end_date);

        std.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) + 1);
        startDate.setMinDate(std.getTimeInMillis());
        endDate.setMinDate(std.getTimeInMillis());
        std.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) + 60);
        startDate.setMaxDate(std.getTimeInMillis());
        //endDate.setMaxDate(System.currentTimeMillis() + (60 * 24 * 60 * 60 * 1000));

        startDate.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH) + 1, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                Calendar min = Calendar.getInstance();
//                min.set(year, monthOfYear, dayOfMonth);
//                callFromStartDate = true;
//                endDate.updateDate(year + 1, monthOfYear, dayOfMonth);
//                endDate.setMinDate(min.getTimeInMillis());
//                callFromStartDate = true;
//                endDate.updateDate(year, monthOfYear, dayOfMonth);
//                endDate.setMinDate(min.getTimeInMillis());
            }
        });

        endDate.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH) + 1, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                if (callFromStartDate) {
//                    callFromStartDate = false;
//                    return;
//                }
            }
        });

        startTime = (TimePicker) view.findViewById(R.id.camp_start_time);
        endTime = (TimePicker) view.findViewById(R.id.camp_end_time);

        campEndDateText=(TextView) view.findViewById(R.id.camp_end_date_text);
        volunteerText=(EditText) view.findViewById(R.id.camp_volunteers);
        doctorsText=(EditText) view.findViewById(R.id.camp_doctors);
        scrollView=(ScrollView)view.findViewById(R.id.scrollViewCampForm);
        return view;
    }

    public void clearAll(){
        campName.setText("");
        address.setText("");
        location=null;
        volunteerText.setText("");
        doctorsText.setText("");
        scrollView.scrollTo(0,0);
    }

    public void submit(View view) {
        boolean submit = true;
        final String name = campName.getEditableText().toString().trim();
        final String addr = address.getText().toString();
        if (name.length() <= 3) {
            campName.setError(getString(R.string.nameError));
            submit = false;
        }
        final String jsonLocation;
        if (location == null) {
            locationButton.setError(getString(R.string.locationError));
            jsonLocation = null;
            submit = false;
        } else {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("lat", location.latitude);
                jsonObject.put("lng", location.longitude);
            } catch (JSONException e) {
            }
            jsonLocation = jsonObject.toString();
        }
        Calendar stTmp = Calendar.getInstance();
        Calendar endTmp = Calendar.getInstance();
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            stTmp.set(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth(), startTime.getCurrentHour(), startTime.getCurrentMinute(), 0);
            endTmp.set(endDate.getYear(), endDate.getMonth(), endDate.getDayOfMonth(), endTime.getCurrentHour(), endTime.getCurrentMinute(), 0);
        } else {
            stTmp.set(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth(), startTime.getHour(), startTime.getMinute(), 0);
            endTmp.set(endDate.getYear(), endDate.getMonth(), endDate.getDayOfMonth(), endTime.getHour(), endTime.getMinute(), 0);
        }
        final Timestamp start = new Timestamp(stTmp.getTimeInMillis());
        final Timestamp end = new Timestamp(endTmp.getTimeInMillis());
        if (start.compareTo(end) != -1) {
            campEndDateText.setError(getString(R.string.endDateError));
            submit = false;
        }

        int volunteer = 0;
        try {
            volunteer = Integer.parseInt(volunteerText.getEditableText().toString().trim());
        } catch (NumberFormatException e) {
        }
        final String doctor = doctorsText.getEditableText().toString().trim();

        if (submit) {
            ((CampMainActivity)getActivity()).queue = VolleyRequestQueue.getInstance(getActivity());
            final int finalVolunteer = volunteer;
            ((CampMainActivity)getActivity()).pd.setMessage(getString(R.string.saving));
            ((CampMainActivity)getActivity()).pd.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, ((CampMainActivity)getActivity()).updateUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("Response", response);
                            try {
                                JSONObject js = new JSONObject(response);
                                String msg = js.getString("msg");
                                if (msg.equalsIgnoreCase("success")) {
                                    js.remove("msg");
                                    ((CampMainActivity)getActivity()).createSuccessful();
                                    clearAll();
                                    Toast.makeText(getActivity(), getString(R.string.successful), Toast.LENGTH_LONG).show();
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
                    map.put("name", name);
                    map.put("address", addr);
                    map.put("location", jsonLocation);
                    map.put("start", start.toString());
                    map.put("end", end.toString());
                    map.put("volunteer", "" + finalVolunteer);
                    map.put("doctors", doctor);
                    map.put("status", "" + CampMainActivity.Status.PENDING.getStatus());
                    return map;
                }
            };
            stringRequest.setTag(CampMainActivity.TAG);
            ((CampMainActivity)getActivity()).queue.addToRequestQueue(getActivity(), stringRequest);
        }
    }
}
