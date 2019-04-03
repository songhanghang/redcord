package com.song.redcord.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.song.redcord.App;
import com.song.redcord.R;
import com.song.redcord.bean.Her;
import com.song.redcord.bean.Lover;
import com.song.redcord.bean.Me;
import com.song.redcord.databinding.ActivityMainBinding;
import com.song.redcord.interfaces.RequestCallback;
import com.song.redcord.util.Pref;
import com.song.redcord.util.TAG;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 地图控制器
 */
public class MapController implements AMapLocationListener, Application.ActivityLifecycleCallbacks {
    private static final long LOCATION_INTERVAL = 10000;
    private final AMap aMap;
    private final AtomicBoolean hasScale = new AtomicBoolean(false);
    private Me me;
    private AMapLocationClient locationClient;
    private AMapLocation aMapLocation;
    private AMapLocationClientOption locationOption;
    private ActivityMainBinding binding;
    private Activity activity;

    public MapController(Activity activity, AMap aMap, ActivityMainBinding binding) {
        this.activity = activity;
        this.aMap = aMap;
        this.binding = binding;
    }

    public void init() {
        aMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                if (locationClient == null) {
                    locationClient = new AMapLocationClient(activity);
                    locationOption = new AMapLocationClientOption();
                    locationOption.setInterval(LOCATION_INTERVAL);
                    locationClient.setLocationListener(MapController.this);
                    locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                    locationClient.setLocationOption(locationOption);
                    locationClient.startLocation();
                }
            }

            @Override
            public void deactivate() {
                if (locationClient != null) {
                    locationClient.stopLocation();
                    locationClient.onDestroy();
                }
                locationClient = null;
            }
        });
        aMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        aMap.setMyLocationEnabled(true);

        App.get().registerActivityLifecycleCallbacks(this);
        check();
    }

    private void check() {
        String meId = Pref.get().getId();
        if (!TextUtils.isEmpty(meId)) {
            me = new Me(meId);
            binding.setMe(me);
            me.pull(new RequestCallback() {
                @Override
                public void onSuccess() {
                    if (me.isSingle()) {
                        showBindHerView();
                    } else {
                        final Her her = new Her(me.getLoverId());
                        me.setLover(her);
                        binding.setHer(her);
                        her.pull(new RequestCallback() {
                            @Override
                            public void onSuccess() {
                                refreshView(me, her);
                            }

                            @Override
                            public void onFail() {

                            }
                        });
                    }
                }

                @Override
                public void onFail() {

                }
            });
        } else {
            showFirstStartView();
        }
    }

    private void showFirstStartView() {
        final View view = LayoutInflater.from(activity).inflate(R.layout.edit_dialog, null);
        final EditText editText = view.findViewById(R.id.edit);
        editText.setHint(R.string.app_input_you_id);
        new AlertDialog.Builder(activity)
                .setTitle(R.string.app_login)
                .setMessage(R.string.app_input_you_id)
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(R.string.app_login, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(editText.getText().toString())) {
                            login(dialog, editText);
                        } else {
                            Toast.makeText(activity, R.string.app_input_right_id, Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(R.string.app_register, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        register(dialog);
                    }
                })
                .create()
                .show();
    }

    private void showBindHerView() {
        final View view = LayoutInflater.from(activity).inflate(R.layout.edit_dialog, null);
        final EditText editText = view.findViewById(R.id.edit);
        editText.setHint(R.string.app_input_her_id);
        new AlertDialog.Builder(activity)
                .setTitle(R.string.app_link_her)
                .setMessage(activity.getString(R.string.app_link_tips, me.id))
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(R.string.app_link, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        String id = editText.getText().toString();
                        if (!TextUtils.isEmpty(id)) {
                            final Her her = new Her(id);
                            binding.setHer(her);
                            her.pull(new RequestCallback() {
                                @Override
                                public void onSuccess() {
                                    if (her.isSingle()) {
                                        me.setLover(her);
                                        me.push();
                                        her.push();
                                        refreshView(me, her);
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(activity, R.string.app_her_not_alone, Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFail() {
                                    Toast.makeText(activity, R.string.app_link_err, Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(activity, R.string.app_input_right_id, Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(R.string.app_send_to_her, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JumpUtil.startShare(activity, me.id);
                    }
                })
                .create()
                .show();

    }

    private void register(final DialogInterface dialog) {
        final AVObject love = new AVObject(Lover.AV_CLASS);
        if (aMapLocation != null) {
            love.put(Lover.AV_KEY_LAT, aMapLocation.getLatitude());
            love.put(Lover.AV_KEY_LON, aMapLocation.getLongitude());
            love.put(Lover.AV_KEY_ADDRESS, aMapLocation.getAddress());
        }
        love.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    me = new Me(love.getObjectId());
                    binding.setMe(me);
                    Pref.get().saveId(me.id);
                    dialog.dismiss();
                    showBindHerView();
                    Toast.makeText(activity, R.string.app_register_success, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, R.string.app_register_err, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void login(final DialogInterface dialog, EditText editText) {
        me = new Me(editText.getText().toString());
        me.pull(new RequestCallback() {
            @Override
            public void onSuccess() {
                Pref.get().saveId(me.id);
                final Her her = new Her(me.getLoverId());
                me.setLover(her);
                binding.setMe(me);
                binding.setHer(her);
                her.pull(new RequestCallback() {
                    @Override
                    public void onSuccess() {
                        refreshView(me, her);
                        dialog.dismiss();
                    }

                    @Override
                    public void onFail() {
                        Toast.makeText(activity, R.string.app_pull_err, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFail() {
                Toast.makeText(activity, R.string.app_login_err, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void refreshView(@NonNull Me me, @NonNull Her her) {
        if (Pref.get().isFirstSetWallpaper()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    JumpUtil.startSetWallpaper();
                }
            }, 500);
            Pref.get().setWallpaper();
            Toast.makeText(activity, R.string.app_link_success, Toast.LENGTH_LONG).show();
        }

        me.setLocation(aMapLocation);
        // 经纬度 0|0 视为尚未定位成功
        if (me.location.getLongitude() == 0 || me.location.getLatitude() == 0) {
            return;
        }

        markUs(me, her);
        navigation(me, her);
        if (!hasScale.getAndSet(true)) {
            scaleMap(me, her);
        }
    }

    /**
     * 标记我俩
     */
    private void markUs(Lover me, Lover you) {
        aMap.clear();
        LatLng melatl = new LatLng(me.location.getLatitude(), me.location.getLongitude());
        LatLng youlatl = new LatLng(you.location.getLatitude(), you.location.getLongitude());
        MarkerOptions meOption = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .position(melatl)
                .draggable(false)
                .title(me.getName());
        aMap.addMarker(meOption);
        MarkerOptions youOption = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(youlatl)
                .draggable(false)
                .title(you.getName());
        aMap.addMarker(youOption).showInfoWindow();
    }

    /**
     * 显示我俩到地图中
     */
    private void scaleMap(Lover me, Lover you) {
        LatLng melatl = new LatLng(me.location.getLatitude(), me.location.getLongitude());
        LatLng youlatl = new LatLng(you.location.getLatitude(), you.location.getLongitude());
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        boundsBuilder.include(melatl);
        boundsBuilder.include(youlatl);
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 300));
    }

    /**
     * 实现导航信息
     */
    private void navigation(final Lover me, final Her her) {
        RouteSearch routeSearch = new RouteSearch(activity);
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                new LatLonPoint(me.location.getLatitude(), me.location.getLongitude()),
                new LatLonPoint(her.location.getLatitude(), her.location.getLongitude()));

        RouteSearch.DriveRouteQuery driveQuery = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_SINGLE_DEFAULT, null, null, "");
        routeSearch.calculateDriveRouteAsyn(driveQuery);

        RouteSearch.WalkRouteQuery workQuery = new RouteSearch.WalkRouteQuery(fromAndTo);
        routeSearch.calculateWalkRouteAsyn(workQuery);

        RouteSearch.RideRouteQuery rideQuery = new RouteSearch.RideRouteQuery(fromAndTo);
        routeSearch.calculateRideRouteAsyn(rideQuery);

        routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
                if (errorCode == AMapException.CODE_AMAP_SUCCESS
                        && result != null
                        && result.getPaths() != null
                        && result.getPaths().size() > 0) {
                    final DrivePath drivePath = result.getPaths().get(0);
                    DrivingRouteOverLay drivingRouteOverlay = new DrivingRouteOverLay(
                            activity, aMap, drivePath,
                            result.getStartPos(),
                            result.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);
                    drivingRouteOverlay.setIsColorfulline(true);
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    her.setDriveInfo(AMapUtil.getFriendlyLength(dis) + " | " + AMapUtil.getFriendlyTime(dur));
                } else {
                    her.setDriveInfo(activity.getString(R.string.app_nav_drive_err));
                }
                her.notifyChange();
            }

            @Override
            public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
                if (errorCode == AMapException.CODE_AMAP_SUCCESS
                        && result != null
                        && result.getPaths() != null
                        && result.getPaths().size() > 0) {
                    WalkPath walkPath = result.getPaths().get(0);
                    int dis = (int) walkPath.getDistance();
                    int dur = (int) walkPath.getDuration();
                    her.setWorkInfo(AMapUtil.getFriendlyLength(dis) + " | " + AMapUtil.getFriendlyTime(dur));
                } else {
                    her.setWorkInfo(activity.getString(R.string.app_nav_work_err));
                }
                her.notifyChange();
            }

            @Override
            public void onRideRouteSearched(RideRouteResult result, int errorCode) {
                if (errorCode == AMapException.CODE_AMAP_SUCCESS
                        && result != null
                        && result.getPaths() != null
                        && result.getPaths().size() > 0) {
                    RidePath ridePath = result.getPaths().get(0);
                    int dis = (int) ridePath.getDistance();
                    int dur = (int) ridePath.getDuration();
                    her.setRideInfo(AMapUtil.getFriendlyLength(dis) + " | " + AMapUtil.getFriendlyTime(dur));
                } else {
                    her.setRideInfo(activity.getString(R.string.app_nav_ride_err));
                }
                her.notifyChange();
            }
        });
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        Log.i(TAG.V, "~~~~~~~~~~~~~~ 地图界面触发定位  ~~~~~~~~~~~~~ " + location);

        // 位置改变
        if (location != null && location.getErrorCode() == 0) {
            this.aMapLocation = location;
            if (me == null) {
                return;
            }

            me.setLocation(aMapLocation);
            me.push();
        }

        if (me == null) {
            return;
        }

        final Lover lover = me.getLover();
        if (lover == null) {
            return;
        }

        lover.pull(new RequestCallback() {
            @Override
            public void onSuccess() {
                refreshView(me, (Her) lover);
            }

            @Override
            public void onFail() {

            }
        });

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (locationClient != null && !locationClient.isStarted()) {
            locationClient.startLocation();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (locationClient != null) {
            locationClient.stopLocation();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
