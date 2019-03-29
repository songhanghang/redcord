package com.song.redcord.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.song.redcord.interfaces.RequestCallback;

import cn.leancloud.AVObject;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class Lover extends BaseObservable implements DataServer {
    public static final String AV_CLASS = "LOVE";
    public static final String AV_KEY_ADDRESS = "address";
    public static final String AV_KEY_LOVE_ID = "love_id";
    public static final String AV_KEY_LAT = "lat";
    public static final String AV_KEY_LON = "lon";
    // 我中有你，你中有我
    private Lover lover;
    public final String id;
    public final Location location = new Location("");
    public String loveId;
    private String name;
    private String address;

    public Lover(String id) {
        this.id = id;
    }

    @Bindable
    public String getName() {
        return name;
    }

    @Bindable
    public void setName(String name) {
        this.name = name;
    }

    @Bindable
    public String getAddress() {
        return address;
    }

    @Bindable
    public void setAddress(String address) {
        this.address = address;
    }

    public void setLover(@NonNull Lover lover) {
        // 我中有你，你中有我
        this.lover = lover;
        lover.lover = this;
    }

    public Lover getLover() {
        return lover;
    }

    public abstract boolean allowPullLocation();

    public abstract boolean allowPushLocation();

    @Override
    public void pull(final RequestCallback callback) {
        AVObject love = AVObject.createWithoutData(AV_CLASS, id);
        love.fetchInBackground().subscribe(new Observer<AVObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(AVObject avObject) {
                address = avObject.getString(AV_KEY_ADDRESS);
                loveId = avObject.getString(AV_KEY_LOVE_ID);
                if (allowPullLocation()) {
                    location.setLatitude(avObject.getLong(AV_KEY_LAT));
                    location.setLongitude(avObject.getLong(AV_KEY_LON));
                }
                notifyChange();
                callback.onCall();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void push() {
        AVObject love = AVObject.createWithoutData(AV_CLASS, id);
        love.put(AV_KEY_ADDRESS, address);
        if (allowPushLocation()) {
            love.put(AV_KEY_LAT, location.getLatitude());
            love.put(AV_KEY_LON, location.getLongitude());
        }
        love.put(AV_KEY_LOVE_ID, loveId);
        love.saveInBackground();
    }

    public boolean isSingle() {
        return TextUtils.isEmpty(loveId);
    }

    public void setLocation(double lat, double lon) {
        location.setLatitude(lat);
        location.setLongitude(lon);
    }


}
