package com.song.redcord.bean;

import android.location.Location;

public class Me extends Lover{

    public void push(Location location) {
        mLocation.setLatitude(location.getLatitude());
        mLocation.setLongitude(location.getLongitude());


    }
}
