package com.song.redcord.bean;

public class You extends Lover {
    {
        name = "Ta";
        address = "甘肃省张掖市甘州区";
        setLocation(33.789925, 104.838326);
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
