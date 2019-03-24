package com.song.redcord.bean;

import android.databinding.Bindable;

public class You extends Lover {

    {
        name = "Ta";
        address = "甘肃省张掖市甘州区";
        setLocation(33.789925, 104.838326);
    }

    public String driveInfo;

    public String getDriveInfo() {
        return driveInfo;
    }

    public void setDriveInfo(String driveInfo) {
        this.driveInfo = driveInfo;
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
