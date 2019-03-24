package com.song.redcord.bean;

import android.location.Location;

import com.song.redcord.interfaces.OnDataUpdateListener;
import com.song.redcord.interfaces.RequestCallback;

/**
 * 我的位置只上传不下载
 * 你的位置只下载不上传
 */
public class Me extends Lover {

    public final Lover you = new You();

    private OnDataUpdateListener onDataUpdateListener;

    public Me() {
        this.name = "ME";
        this.loveId = "lover";
    }

    public void setOnDataUpdateListener(OnDataUpdateListener onDataUpdateListener) {
        this.onDataUpdateListener = onDataUpdateListener;
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

    public void update(final Location location) {
        setLocation(location.getLatitude(), location.getLongitude());
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

                if (onDataUpdateListener != null) {
                    onDataUpdateListener.onUpdate();
                }
            }
        });
    }

}
