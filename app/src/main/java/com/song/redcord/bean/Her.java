package com.song.redcord.bean;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.Bindable;
import android.text.TextUtils;
import android.view.View;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.song.redcord.map.AMapUtil;
import com.song.redcord.map.JumpUtil;

public class Her extends Lover {
    private String driveInfo;
    private String workInfo;
    private String rideInfo;

    public Her(String id) {
        super(id);
        setName("TA");
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

    public boolean isNull() {
        return TextUtils.isEmpty(id);
    }

    public void onNavClick(View view) {
        final Context context = view.getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(new String[]{"高德地图", "百度地图"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    JumpUtil.startNavAmap(context, location.getLatitude(), location.getLongitude());
                } else {
                    JumpUtil.startNavBaiduMap(context, location.getLatitude(), location.getLongitude());
                }
            }
        });
        builder.create().show();
    }

    public void onForbackClick(View view) {
        String str = "若有一天\n你与我失联\n通过这条信息找回我\n这是你的ID:\n\n"
                + id
                + "\n\n通过ID可以重新登录"
                + "\n这是我的ID:\n\n"
                + getLoverId()
                + "\n\n帮我记下，我怕忘了...\n\n「 来自: RedCrod App 」";
        JumpUtil.startShare(view.getContext(), str);
    }

    public void onSetWallpaperClick(View view) {
        JumpUtil.startSetWallpaper();
    }

    public void onPayClick(View view) {
        JumpUtil.startAliPay((Activity) view.getContext());
    }

    public void onAboutClick(View view) {
        JumpUtil.startAbout();
    }

}
