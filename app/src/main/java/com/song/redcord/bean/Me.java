package com.song.redcord.bean;

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
}
