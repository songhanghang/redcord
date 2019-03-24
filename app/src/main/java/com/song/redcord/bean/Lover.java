package com.song.redcord.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.location.Location;
import android.text.TextUtils;

import com.song.redcord.interfaces.RequestCallback;

public abstract class Lover extends BaseObservable implements DataServer {

    public String id;
    public String loveId;
    public String name;

    public String address;

    @Bindable
    public String getName() {
        return name;
    }

    @Bindable
    public void setName(String name) {
        this.name = name;
    }

    @Bindable
    public String getAddress() {
        return address;
    }

    @Bindable
    public void setAddress(String address) {
        this.address = address;
    }

    public final Location location = new Location("");

    public abstract boolean ablePullLocation();

    public abstract boolean ablePushLocation();

    @Override
    public void pull(RequestCallback callback) {
        callback.onCall();
    }

    @Override
    public void push(RequestCallback callback) {

    }

    boolean isSingle() {
        return TextUtils.isEmpty(loveId);
    }

    void setLocation(double lat, double lon) {
        location.setLatitude(lat);
        location.setLongitude(lon);
    }


}
