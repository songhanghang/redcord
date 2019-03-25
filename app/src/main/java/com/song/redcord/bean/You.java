package com.song.redcord.bean;


import android.databinding.Bindable;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.song.redcord.map.AMapUtil;

public class You extends Lover {

    {
        setName("Ta");
        setAddress("甘肃省张掖市甘州区");
        setLocation(33.789925, 104.838326);
    }

    public String getDriveInfo() {
        return driveInfo;
    }

    public void setDriveInfo(String driveInfo) {
        this.driveInfo = driveInfo;
    }

    public String getWorkInfo() {
        return workInfo;
    }

    public void setWorkInfo(String workInfo) {
        this.workInfo = workInfo;
    }

    public String getRideInfo() {
        return rideInfo;
    }

    public void setRideInfo(String rideInfo) {
        this.rideInfo = rideInfo;
    }

    private String driveInfo;
    private String workInfo;
    private String rideInfo;

    @Bindable
    public String getLineDistance() {
        LatLng latMe = new LatLng(Me.getInstance().location.getLatitude(), Me.getInstance().location.getLongitude());
        LatLng latYou = new LatLng(location.getLatitude(), location.getLongitude());
        float dis = AMapUtils.calculateLineDistance(latMe, latYou);
        return AMapUtil.getFriendlyLength((int) dis);
    }


    @Override
    public boolean ablePullLocation() {
        return true;
    }

    @Override
    public boolean ablePushLocation() {
        return false;
    }

}
