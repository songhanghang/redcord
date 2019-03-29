package com.song.redcord.bean;


import android.databinding.Bindable;
import android.view.View;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.song.redcord.map.AMapUtil;
import com.song.redcord.map.NavgationUtil;

public class You extends Lover {
    private String driveInfo;
    private String workInfo;
    private String rideInfo;

    public You(String id) {
        super(id);
        setName("你");
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

    @Bindable
    public String getLineDistance() {
        LatLng latMe = new LatLng(getLover().location.getLatitude(), getLover().location.getLongitude());
        LatLng latYou = new LatLng(location.getLatitude(), location.getLongitude());
        float dis = AMapUtils.calculateLineDistance(latMe, latYou);
        return AMapUtil.getFriendlyLength((int) dis);
    }

    @Override
    public boolean allowPullLocation() {
        return true;
    }

    @Override
    public boolean allowPushLocation() {
        return false;
    }

    public void onNavClick(View view) {
        NavgationUtil.nav(view.getContext(), location.getLatitude(), location.getLongitude());
    }

}
