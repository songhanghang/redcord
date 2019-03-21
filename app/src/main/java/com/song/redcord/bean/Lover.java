package com.song.redcord.bean;

import android.location.Location;

public class Lover {
    public final Location mLocation = new Location("");

    public final double getLatitude() {
        return mLocation.getLatitude();
    }

    public final double getLongitude() {
        return mLocation.getLongitude();
    }
}
