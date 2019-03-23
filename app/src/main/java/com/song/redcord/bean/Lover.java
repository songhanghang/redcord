package com.song.redcord.bean;

import android.location.Location;
import android.text.TextUtils;

public abstract class Lover implements DataServer {

    protected String id;
    protected String loveId;

    final Location location = new Location("");

    @Override
    public void pull(Callback callback) {
        callback.onCall();
    }


    @Override
    public void push(Callback callback) {

    }



    public boolean isSingle() {
        return TextUtils.isEmpty(loveId);
    }

    public void setLocation(double lat, double lon) {
        location.setLatitude(lat);
        location.setLongitude(lon);
    }
}
