package com.song.redcord.bean;
/**
 * 我的位置只上传不下载
 * 你的位置只下载不上传
 */
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
