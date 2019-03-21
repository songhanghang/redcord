package com.song.redcord;

import android.location.Location;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.song.redcord.bean.Me;
import com.song.redcord.bean.You;

public class Us {
    private final You mYou;
    private final Me mMe;

    public Us() {
        mYou = new You();
        mMe = new Me();
    }

    public void update(Location location, Runnable done) {
        mMe.push(location);
        mYou.pull(done);
    }

    public void limitUsBound(AMap aMap) {
        LatLng southwestLatLng = new LatLng(mMe.getLatitude(), mMe.getLongitude());
        LatLng northeastLatLng = new LatLng(mYou.getLatitude(), mYou.getLongitude());
        LatLngBounds latLngBounds = new LatLngBounds(southwestLatLng, northeastLatLng);
        aMap.setMapStatusLimits(latLngBounds);
    }

}
