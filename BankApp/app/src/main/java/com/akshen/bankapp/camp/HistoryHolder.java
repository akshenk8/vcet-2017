/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.akshen.bankapp.camp;

import android.os.Parcel;
import android.os.Parcelable;

import com.akshen.bankapp.camp.CampMainActivity.Status;
import com.google.android.gms.maps.model.LatLng;

import java.sql.Timestamp;

public class HistoryHolder implements Parcelable {

    public static final Creator<HistoryHolder> CREATOR = new Creator<HistoryHolder>() {
        @Override
        public HistoryHolder createFromParcel(Parcel in) {
            return new HistoryHolder(in);
        }

        @Override
        public HistoryHolder[] newArray(int size) {
            return new HistoryHolder[size];
        }
    };
    private int campid, bankid;
    private LatLng location;
    private String address;
    private int volunteers;
    private String doctors;
    private String _nameofevent;
    private Timestamp _startdate;
    private Timestamp _enddate;
    private Status _status;

    protected HistoryHolder(Parcel in) {
        campid = in.readInt();
        bankid = in.readInt();
        location = in.readParcelable(LatLng.class.getClassLoader());
        address = in.readString();
        volunteers = in.readInt();
        doctors = in.readString();
        _nameofevent = in.readString();
        _startdate = Timestamp.valueOf(in.readString());
        _enddate = Timestamp.valueOf(in.readString());
        int _status = in.readInt();
        if (_status == Status.COMPLETED.getStatus()) {
            this._status = Status.COMPLETED;
        } else if (_status == Status.ONGOING.getStatus()) {
            this._status = Status.ONGOING;
        } else if (_status == Status.PENDING.getStatus()) {
            this._status = Status.PENDING;
        }
    }

    public HistoryHolder(int campid, int bankid, String _nameofevent, LatLng location, String address, int volunteers, String doctors, Timestamp _startdate, Timestamp _enddate, Status _status) {
        this.campid = campid;
        this.bankid = bankid;
        this.location = location;
        this.address = address;
        this.volunteers = volunteers;
        this.doctors = doctors;
        this._nameofevent = _nameofevent;
        this._startdate = _startdate;
        this._enddate = _enddate;
        this._status = _status;
    }

    public HistoryHolder(int campid, int bankid, String _nameofevent, LatLng location, String address, int volunteers, String doctors, String _startdate, String _enddate, int _status) {
        this.campid = campid;
        this.bankid = bankid;
        this.location = location;
        this.address = address;
        this.volunteers = volunteers;
        this.doctors = doctors;
        this._nameofevent = _nameofevent;

        this._startdate = Timestamp.valueOf(_startdate);
        this._enddate = Timestamp.valueOf(_enddate);

        if (_status == Status.COMPLETED.getStatus()) {
            this._status = Status.COMPLETED;
        } else if (_status == Status.ONGOING.getStatus()) {
            this._status = Status.ONGOING;
        } else if (_status == Status.PENDING.getStatus()) {
            this._status = Status.PENDING;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(campid);
        dest.writeInt(bankid);
        dest.writeParcelable(location, flags);
        dest.writeString(address);
        dest.writeInt(volunteers);
        dest.writeString(doctors);
        dest.writeString(_nameofevent);
        dest.writeString(_startdate.toString());
        dest.writeString(_enddate.toString());
        dest.writeInt(_status.getStatus());
    }

    public int getCampid() {
        return campid;
    }

    public int getBankid() {
        return bankid;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getAddress() {
        return address;
    }

    public int getVolunteers() {
        return volunteers;
    }

    public String getDoctors() {
        return doctors;
    }

    public String get_nameofevent() {
        return _nameofevent;
    }

    public Timestamp get_startdate() {
        return _startdate;
    }

    public Timestamp get_enddate() {
        return _enddate;
    }

    public Status get_status() {
        return _status;
    }
}