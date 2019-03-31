package com.song.redcord.bean;

import android.support.annotation.NonNull;

import com.amap.api.location.AMapLocation;

public class Me extends Lover {

    public Me(String id) {
        super(id);
        setName("我");
    }

    @Override
    public boolean allowPullLocation() {
        return false;
    }

    @Override
    public boolean allowPushLocation() {
        return true;
    }

    public void setLocation(@NonNull AMapLocation aMapLocation) {
        location.setLatitude(aMapLocation.getLatitude());
        location.setLongitude(aMapLocation.getLongitude());
        setAddress(aMapLocation.getAddress());
    }
}
