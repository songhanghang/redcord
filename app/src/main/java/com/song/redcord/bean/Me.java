package com.song.redcord.bean;
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

    public void setLocation(AMapLocation aMapLocation) {
        if (aMapLocation == null) {
            return;
        }
        location.setLatitude(aMapLocation.getLatitude());
        location.setLongitude(aMapLocation.getLongitude());
        setAddress(aMapLocation.getAddress());
    }
}
