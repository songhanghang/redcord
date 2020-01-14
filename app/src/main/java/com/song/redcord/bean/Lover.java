package com.song.redcord.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.maps.model.AMapGLOverlay;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.GetCallback;
import com.song.redcord.interfaces.RequestCallback;
import com.song.redcord.util.DateUtil;
import com.song.redcord.util.TAG;

/**
 * 我的位置只上传不下载
 * Ta的位置只下载不上传
 */
public abstract class Lover<T extends Lover> extends BaseObservable implements DataServer {
    public static final String AV_CLASS = "LOVE";
    public static final String AV_KEY_ADDRESS = "address";
    public static final String AV_KEY_LOVE_ID = "love_id";
    public static final String AV_KEY_LAT = "lat";
    public static final String AV_KEY_LON = "lon";
    public final String id;
    public final Location location = new Location("");
    private T lover;
    private String loverId;
    private String name;
    private String address;
    private String updateTime;
    private boolean allowPullLocation;
    private boolean allowPushLocation;


    public Lover(String id, boolean allowPullLocation, boolean allowPushLocation) {
        this.id = id;
        this.allowPullLocation = allowPullLocation;
        this.allowPushLocation = allowPushLocation;
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

    public String getUpdateTime() {
        return updateTime;
    }

    public void setLover(@NonNull Lover lover) {
        // 我中有你，你中有我
        this.lover = (T) lover;
        this.loverId = lover.id;
        lover.lover = this;
        lover.loverId = this.id;
    }

    public String getLoverId() {
        return loverId;
    }

    public T getLover() {
        return lover;
    }

    @Override
    public void pull(final RequestCallback callback) {
        AVObject love = AVObject.createWithoutData(AV_CLASS, id);
        love.fetchInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                if (e == null) {
                    address = object.getString(AV_KEY_ADDRESS);
                    updateTime = DateUtil.getUpdateDateString(object.getUpdatedAt());
                    loverId = object.getString(AV_KEY_LOVE_ID);
                    Log.i(TAG.V, Lover.this.getClass() + " +++++ pull id "+  id + " loveid " + loverId);

                    if (allowPullLocation) {
                        location.setLatitude(object.getDouble(AV_KEY_LAT));
                        location.setLongitude(object.getDouble(AV_KEY_LON));
                    }

                    notifyChange();
                    if (callback != null)
                        callback.onSuccess();
                } else {
                    if (callback != null)
                        callback.onFail();
                }
            }
        });
    }

    @Override
    public void push() {
        AVObject love = AVObject.createWithoutData(AV_CLASS, id);
        if (!TextUtils.isEmpty(address)) {
            love.put(AV_KEY_ADDRESS, address);
        }
        if (!TextUtils.isEmpty(loverId)) {
            love.put(AV_KEY_LOVE_ID, loverId);
        }
        if (allowPushLocation) {
            love.put(AV_KEY_LAT, location.getLatitude());
            love.put(AV_KEY_LON, location.getLongitude());
        }
        Log.i(TAG.V, Lover.this.getClass() + " ------- push id "+  id + " loveid " + loverId);
        love.saveInBackground();
    }


    public boolean isSingle() {
        return TextUtils.isEmpty(loverId);
    }

}
