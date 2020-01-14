package com.song.redcord.bean;
import com.amap.api.location.AMapLocation;

public class Me extends Lover<Her> {

    public Me(String id) {
        super(id, false, true);
        setName("我");
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
