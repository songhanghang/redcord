package com.song.redcord.bean;

public class You extends Lover {
    public int
    {
        name = "YOU";
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
