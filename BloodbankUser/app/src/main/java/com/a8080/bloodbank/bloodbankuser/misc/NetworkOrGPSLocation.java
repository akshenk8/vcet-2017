package com.a8080.bloodbank.bloodbankuser.misc;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.a8080.bloodbank.bloodbankuser.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akshen Kadakia.
 */

public class NetworkOrGPSLocation {

    private final String[] needsPermission = {Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_PHONE_STATE};

    public enum Mode {
        GPS_ONLY(1), GSM_ONLY(2), BOTH(3);
        private int modeType;

        Mode(int val) {
            modeType = val;
        }

        public int getModeType() {
            return modeType;
        }
    }

    private Context context;
    private JSONObject parameters;
    private List<Cell> cellList;
    private LatLng latLng;
    private double accuracy;
    private boolean single;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private LocationListener locationListener;
    private Mode mode;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager tel;

    public NetworkOrGPSLocation(Context context) throws SecurityException {
        this(context, Mode.BOTH);
    }

    public NetworkOrGPSLocation(Context context, Mode mode) throws SecurityException {
        this(context, mode, true);
    }

    public NetworkOrGPSLocation(Context context1, Mode mode1,boolean single1) throws SecurityException {
        this.context = context1;
        for (String per : needsPermission) {
            if (ContextCompat.checkSelfPermission(context, per) == PackageManager.PERMISSION_DENIED) {
                throw new SecurityException("Permission not available:" + per);
            }
        }
        tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        this.mode = mode1;
        this.single = single1;
        if (mode == Mode.GPS_ONLY) {
            if (!isGpsEnabled()) {
                Intent intent = new Intent(NetworkLocationListener.NETWORK_LOCATION_INTENT_FILTER);
                intent.putExtra(NetworkLocationListener.ERROR, "GPS is OFF");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        } else if (mode == Mode.GSM_ONLY) {
            if (!isInternetEnabled()) {
                Intent intent = new Intent(NetworkLocationListener.NETWORK_LOCATION_INTENT_FILTER);
                intent.putExtra(NetworkLocationListener.ERROR, "Internet is OFF");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        } else if (mode == Mode.BOTH) {
            if (!isGpsEnabled() && !isInternetEnabled()) {
                Intent intent = new Intent(NetworkLocationListener.NETWORK_LOCATION_INTENT_FILTER);
                intent.putExtra(NetworkLocationListener.ERROR, "GPS & Internet is OFF");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        }
        if (mode == Mode.GPS_ONLY || mode == Mode.BOTH) {
            this.mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            locationRequest = LocationRequest.create();
                            locationRequest.setInterval(1000);
                            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                            locationListener = new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    if (location != null) {
                                        latLng = new LatLng(location.getLatitude(), location.getLatitude());
                                        accuracy = location.getAccuracy();
                                        Intent intent = new Intent(NetworkLocationListener.NETWORK_LOCATION_INTENT_FILTER);
                                        intent.putExtra(NetworkLocationListener.LAT, latLng.latitude);
                                        intent.putExtra(NetworkLocationListener.LNG, latLng.longitude);
                                        intent.putExtra(NetworkLocationListener.ACC, accuracy);
                                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                                        if (single) {
                                            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
                                        }
                                    }
                                }

                            };
                            if (!single) {
                                try {
                                    //location updates moves blue marker
                                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, locationListener);
                                } catch (SecurityException e) {
                                    Log.e("ex:", e.toString());
                                }
                            }
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            mGoogleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            if (mode == Mode.BOTH) {
                                Intent intent = new Intent(NetworkLocationListener.NETWORK_LOCATION_INTENT_FILTER);
                                intent.putExtra(NetworkLocationListener.ERROR, "GPS service not available");
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                                mode = Mode.GSM_ONLY;
                            } else {
                                Intent intent = new Intent(NetworkLocationListener.NETWORK_LOCATION_INTENT_FILTER);
                                intent.putExtra(NetworkLocationListener.ERROR, "Connection Failed");
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            }
                        }
                    })
                    .addApi(LocationServices.API)
                    .build();
        }
        if (!single) {
            if (mode == Mode.GSM_ONLY || mode == Mode.BOTH) {
                phoneStateListener = new PhoneStateListener() {
                    @Override
                    public void onCellInfoChanged(List<CellInfo> cellInfo) {
                        super.onCellInfoChanged(cellInfo);
                        try {
                            fetchLocation();
                        } catch (JSONException | ClassCastException e) {
                            Log.e("gsm fetch", e.toString());
                        }
                    }
                };
            }
            setContinuous();
        }
    }

    public void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    private void setContinuous() throws SecurityException {
        if (!single) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mode == Mode.GSM_ONLY) {
                tel.listen(phoneStateListener, PhoneStateListener.LISTEN_CELL_INFO);
            }
            if (mode == Mode.GPS_ONLY || mode == Mode.BOTH) {
                //done in onConnected
            }
        } else {
            if (mode == Mode.GSM_ONLY && phoneStateListener != null)
                tel.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
            if ((mode == Mode.GPS_ONLY || mode == Mode.BOTH) && locationRequest != null && mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
            }
        }
    }

    /**
     * @return
     * @throws Exception if any occurs
     */
    private boolean generateParameters() throws JSONException, ClassCastException, SecurityException {
        String networkOperator = tel.getNetworkOperator();
        Integer mnc = null, mcc = null;
        if (!TextUtils.isEmpty(networkOperator)) {
            mcc = Integer.parseInt(networkOperator.substring(0, 3));
            mnc = Integer.parseInt(networkOperator.substring(3));
        }
        parameters = new JSONObject();
//        Cell.RadioType radioType = getRadioType(tel.getDataNetworkType());

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        Cell.RadioType radioType = null;
        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            radioType = getRadioType(info.getSubtype());
        }

        if (radioType != null)
            parameters.put("radioType", radioType.getRadioType());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            JSONArray cellTowers = new JSONArray();
            List<CellInfo> neighCells = tel.getAllCellInfo();
            cellList = new ArrayList<>();
            if (neighCells == null) {
//                List<NeighboringCellInfo> cells = tel.getNeighboringCellInfo();
//                if(cells==null) {
//                    for (NeighboringCellInfo nci : cells) {
//                        Cell cell = null;
//                        if (getRadioType(nci.getNetworkType()) == Cell.RadioType.GSM) {
//                            cell = Cell.cellValidatorAndInflator(Cell.RadioType.GSM, mcc, mnc, nci.getLac()
//                                    , nci.getCid(), nci.getPsc(), nci.getRssi());
//                        }
//                        if(getRadioType(nci.getNetworkType()) == Cell.RadioType.WCDMA) {
//                            cell = Cell.cellValidatorAndInflator(Cell.RadioType.WCDMA, mcc, mnc, nci.getLac()
//                                    , nci.getCid(), nci.getPsc(), nci.getRssi());
//                        }
//
//                        if (cell != null) {
//                            cellList.add(cell);
//                            cellTowers.put(cell.toJSONObject());
//                        }
//                    }
//                }

//                CellLocation cellLocation=tel.getCellLocation();
//                cellLocation.requestLocationUpdate();
//                Cell cell=Cell.cellValidatorAndInflator(,mcc,mnc);
            } else {
                for (int i = 0; i < neighCells.size(); i++) {
                    Cell cell = null;
                    CellInfo cellWrapper = neighCells.get(i);
                    if (cellWrapper instanceof CellInfoGsm) {
                        CellIdentityGsm cellGsm = ((CellInfoGsm) cellWrapper).getCellIdentity();
                        CellSignalStrengthGsm strength = ((CellInfoGsm) cellWrapper).getCellSignalStrength();
                        cell = Cell.cellValidatorAndInflator(Cell.RadioType.GSM, cellGsm.getMcc(), cellGsm.getMnc(),
                                cellGsm.getLac(), cellGsm.getCid(), -1, strength.getDbm());

                    }
                    if (cellWrapper instanceof CellInfoCdma) {
                        CellIdentityCdma cellCdma = ((CellInfoCdma) cellWrapper).getCellIdentity();
                        CellSignalStrengthCdma strength = ((CellInfoCdma) cellWrapper).getCellSignalStrength();
                        cell = Cell.cellValidatorAndInflator(Cell.RadioType.CDMA, mcc, cellCdma.getSystemId(),
                                cellCdma.getNetworkId(), cellCdma.getBasestationId(), -1, strength.getDbm());
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        if (cellWrapper instanceof CellInfoWcdma) {
                            CellIdentityWcdma cellWcdma = ((CellInfoWcdma) cellWrapper).getCellIdentity();
                            CellSignalStrengthWcdma strength = ((CellInfoWcdma) cellWrapper).getCellSignalStrength();
                            cell = Cell.cellValidatorAndInflator(Cell.RadioType.WCDMA, cellWcdma.getMcc(), cellWcdma.getMnc(),
                                    cellWcdma.getLac(), cellWcdma.getCid(), cellWcdma.getPsc(), strength.getDbm());
                        }
                        if (cellWrapper instanceof CellInfoLte) {
                            CellIdentityLte cellLte = ((CellInfoLte) cellWrapper).getCellIdentity();
                            CellSignalStrengthLte strength = ((CellInfoLte) cellWrapper).getCellSignalStrength();
                            cell = Cell.cellValidatorAndInflator(Cell.RadioType.LTE, cellLte.getMcc(), cellLte.getMnc(),
                                    cellLte.getTac(), cellLte.getCi(), cellLte.getPci(), strength.getDbm());
                        }
                    }
                    if (cell != null) {
                        cellList.add(cell);
                        cellTowers.put(cell.toJSONObject());
                    }
                }
            }
            if (cellTowers.length() > 0) {
                parameters.put("cellTowers", cellTowers);
            }
        }
//        parameters.put("considerIp","false");
        Log.i("paramters", parameters.toString());

        return true;
    }

    public void fetchLocation() throws JSONException, ClassCastException, SecurityException {
        if (mode == Mode.GPS_ONLY && single) {
            if (!isGpsEnabled()) {
                Intent intent = new Intent(NetworkLocationListener.NETWORK_LOCATION_INTENT_FILTER);
                intent.putExtra(NetworkLocationListener.ERROR, "GPS is OFF");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, locationListener);
        } else if (mode == Mode.BOTH && single && isGpsEnabled()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, locationListener);
        } else if (mode == Mode.GSM_ONLY || (mode == Mode.BOTH && !isGpsEnabled())) {
            if (!isInternetEnabled()) {
                Intent intent = new Intent(NetworkLocationListener.NETWORK_LOCATION_INTENT_FILTER);
                intent.putExtra(NetworkLocationListener.ERROR, "Internet is OFF");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                return;
            }
            generateParameters();
            VolleyRequestQueue queue = VolleyRequestQueue.getInstance(context);
            String url = context.getString(R.string.geo_location_url) + context.getString(R.string.api_key);
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Intent intent = new Intent(NetworkLocationListener.NETWORK_LOCATION_INTENT_FILTER);
                    try {
                        if (response.has("error")) {
                            latLng = null;
                            accuracy = 0.0;
                            intent.putExtra(NetworkLocationListener.ERROR, "geolocation error code:" + response.getInt("code") + "&msg:" + response.getString("message"));
                            Log.e("NetworkOrGPSLocation", "code:" + response.getInt("code") + "&msg:" + response.getString("message"));
                            return;
                        }
                        JSONObject loc = response.getJSONObject("location");
                        latLng = new LatLng(loc.getDouble("lat"), loc.getDouble("lng"));
                        accuracy = response.getDouble("accuracy");
                        intent.putExtra(NetworkLocationListener.LAT, latLng.latitude);
                        intent.putExtra(NetworkLocationListener.LNG, latLng.longitude);
                        intent.putExtra(NetworkLocationListener.ACC, accuracy);
                    } catch (Exception e) {
                        latLng = null;
                        accuracy = 0.0;
                        intent.putExtra(NetworkLocationListener.ERROR, e.toString());
                        Log.e("NetworkOrGPSLocation", e.toString());
                    }
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Intent intent = new Intent(NetworkLocationListener.NETWORK_LOCATION_INTENT_FILTER);
                    intent.putExtra(NetworkLocationListener.ERROR, error.toString());
                    Log.e("NetworkOrGPSLocation", error.toString());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            });
            queue.addToRequestQueue(context,req);
        }
    }

    public boolean isInternetEnabled() {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isGpsEnabled() {
//        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        try {
            int locationMode = 0;
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Cell.RadioType getRadioType(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return Cell.RadioType.GSM;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return Cell.RadioType.WCDMA;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return Cell.RadioType.LTE;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return Cell.RadioType.CDMA;
        }
        return null;
    }
}

class Cell {

    final String JSON_CID = "cellId", JSON_LAC = "locationAreaCode",
            JSON_MCC = "mobileCountryCode", JSON_MNC = "mobileNetworkCode",
            JSON_AGE = "age", JSON_SIGNAL = "signalStrength", JSON_TIME_ADVANCE = "timingAdvance";

    private RadioType type;
    private int mcc;
    private int mnc;
    private int lac;
    private long cid;
    private int psc;
    private int signal;

    public static Cell cellValidatorAndInflator(RadioType type, int mcc, int mnc, int lac, long cid, int psc, int signal) {
        if (type == null)
            return null;
        boolean cdma = type == RadioType.CDMA;
        if (mcc < 0 || mcc > 999)
            return null;
        if (cdma ? (mnc < 1 || mnc > 32767) : (mnc < 0 || mnc > 999))
            return null;
        if (lac < 1 || lac > (cdma ? 65534 : 65533))
            return null;
        if (cid < 0)
            return null;
        return new Cell(type, mcc, mnc, lac, cid, psc, signal);
    }

    private Cell(RadioType type, int mcc, int mnc, int lac, long cid, int psc, int signal) {
        this.type = type;
        this.mcc = mcc;
        this.mnc = mnc;
        this.lac = lac;
        this.cid = cid;
        this.psc = psc;
        this.signal = signal;
    }

    /**
     * @return RSCP for WCDMA, RSRP for LTE, RSSI for GSM and CDMA
     */
    public int getSignal() {
        return signal;
    }

    public RadioType getType() {
        return type;
    }

    public int getMcc() {
        return mcc;
    }

    public int getMnc() {
        return mnc;
    }

    public int getLac() {
        return lac;
    }

    public long getCid() {
        return cid;
    }

    public int getPsc() {
        return psc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cell cell = (Cell) o;

        if (cid != cell.cid) return false;
        if (lac != cell.lac) return false;
        if (mcc != cell.mcc) return false;
        if (mnc != cell.mnc) return false;
        if (psc != cell.psc) return false;
        if (signal != cell.signal) return false;
        if (type != cell.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + mcc;
        result = 31 * result + mnc;
        result = 31 * result + lac;
        result = 31 * result + (int) (cid ^ (cid >>> 32));
        result = 31 * result + psc;
        result = 31 * result + signal;
        return result;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "type=" + type +
                ", mcc=" + mcc +
                ", mnc=" + mnc +
                ", lac=" + lac +
                ", cid=" + cid +
                (psc != -1 ? (", psc=" + psc) : "") +
                ", signal=" + signal +
                '}';
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        try {
            json.put(JSON_CID, "" + cid);
            json.put(JSON_LAC, lac);
            json.put(JSON_MCC, mcc);
            json.put(JSON_MNC, mnc);
            json.put(JSON_SIGNAL, signal);
        } catch (JSONException e) {
            return null;
        }
        return json;
    }

    public enum RadioType {
        GSM("gsm"), LTE("lte"), CDMA("cdma"), WCDMA("wcdma");
        private String radioType;

        private RadioType(String val) {
            radioType = val;
        }

        public String getRadioType() {
            return radioType;
        }
    }
}


abstract class NetworkLocationListener extends BroadcastReceiver {
    public static final String NETWORK_LOCATION_INTENT_FILTER = "NETWORK_LOCATION_INTENT_FILTER";
    public static final String ERROR = "error";
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String ACC = "acc";

    @Override
    public abstract void onReceive(Context context, Intent intent);
}
