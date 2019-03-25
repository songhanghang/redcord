package com.song.redcord.bean;

import android.location.Location;

import com.amap.api.location.AMapLocation;
import com.song.redcord.interfaces.LoverRefresh;
import com.song.redcord.interfaces.RequestCallback;

/**
 * 我的位置只上传不下载
 * 你的位置只下载不上传
 */
public class Me extends Lover {
    private static class Holder {
        private static Me instance = new Me();
    }

    public final You you = new You();

    private LoverRefresh loverRefresh;

    private Me() {
        setName("我");
        this.loveId = "lover";
    }

    public static Me getInstance() {
        return Holder.instance;
    }

    public void setLoverRefresh(LoverRefresh loverRefresh) {
        this.loverRefresh = loverRefresh;
    }

    @Override
    public boolean ablePullLocation() {
        return false;
    }

    @Override
    public boolean ablePushLocation() {
        return true;
    }

    @Override
    public void pull(final RequestCallback callback) {
        RequestCallback wrap = new RequestCallback() {
            @Override
            public void onCall() {
                you.id = loveId;
                pullYou();

                if (callback != null)
                    callback.onCall();
            }
        };
        super.pull(wrap);
    }

    public void update(final AMapLocation location) {
        setLocation(location.getLatitude(), location.getLongitude());
        setAddress(location.getAddress());
        pull(new RequestCallback() {
            @Override
            public void onCall() {
                push(null);
            }
        });
    }

    private void pullYou() {
        if (isSingle()) {
            return;
        }

        you.pull(new RequestCallback() {
            @Override
            public void onCall() {
                if (isSingle()) {
                    return;
                }

                if (loverRefresh != null) {
                    loverRefresh.refresh();
                }
            }
        });
    }

}
