package com.song.redcord.map;

import android.content.Context;
import android.util.Log;

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
import com.song.redcord.bean.Lover;
import com.song.redcord.bean.Me;
import com.song.redcord.interfaces.LoverRefresh;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 地图控制器
 */
public class MapController extends Controller implements AMapLocationListener {
    private static final String TAG = "MapController";
    private final AMap aMap;
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationOption;

    // 已经缩放地图
    private final AtomicBoolean hasScale = new AtomicBoolean(false);
    private Context context;

    public MapController(Context context, AMap aMap, LoverRefresh loverRefresh) {
        super(loverRefresh);
        this.context = context;
        this.aMap = aMap;
        Me.getInstance().setLoverRefresh(this);
    }

    public void init() {
        aMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                Log.i("songhang", "+++++++++++ activate ");

                if (locationClient == null) {
                    locationClient = new AMapLocationClient(context);
                    locationOption = new AMapLocationClientOption();
                    //设置定位监听
                    locationClient.setLocationListener(MapController.this);
                    //设置为高精度定位模式
                    locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
                    //设置定位参数
                    locationClient.setLocationOption(locationOption);
                    // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
                    // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
                    // 在定位结束后，在合适的生命周期调用onDestroy()方法
                    // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
                    locationClient.startLocation();
                }
            }

            @Override
            public void deactivate() {
                Log.i("songhang", "-------- deactivate ");
                if (locationClient != null) {
                    locationClient.stopLocation();
                    locationClient.onDestroy();
                }
                locationClient = null;
            }
        });
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        aMap.setMyLocationEnabled(true);
    }

    @Override
    public void refresh() {
        Me me = Me.getInstance();
        Log.i(TAG, "me : " + me.location.getLatitude() + "    " + me.location.getLongitude());
        Log.i(TAG, "you : " + me.you.location.getLatitude() + "    " + me.you.location.getLongitude());
        if (loverRefresh != null) {
            loverRefresh.refresh();
        }
        markUs(me, me.you);
        navigation(me, me.you);
        if (!hasScale.getAndSet(true)) {
            scaleMap(me, me.you);
        }

    }

    /**
     * 标记我俩
     */
    private void markUs(Lover me, Lover you) {
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
    private void navigation(Lover me, Lover you) {
        RouteSearch routeSearch = new RouteSearch(context);
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                new LatLonPoint(me.location.getLatitude(), me.location.getLongitude()),
                new LatLonPoint(you.location.getLatitude(), you.location.getLongitude()));

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
                            context, aMap, drivePath,
                            result.getStartPos(),
                            result.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);
                    drivingRouteOverlay.setIsColorfulline(true);
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    Me.getInstance().you.setDriveInfo(AMapUtil.getFriendlyLength(dis) + " | " + AMapUtil.getFriendlyTime(dur));
                } else {
                    Me.getInstance().you.setDriveInfo("不知哪里出了问题...");
                }
                Me.getInstance().you.notifyChange();
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
                    Me.getInstance().you.setWorkInfo(AMapUtil.getFriendlyLength(dis) + " | " + AMapUtil.getFriendlyTime(dur));
                } else {
                    Me.getInstance().you.setWorkInfo("可能太远了, 要不换个交通工具?");
                }
                Me.getInstance().you.notifyChange();
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
                    Me.getInstance().you.setRideInfo(AMapUtil.getFriendlyLength(dis) + " | " + AMapUtil.getFriendlyTime(dur));
                } else {
                    Me.getInstance().you.setRideInfo("也许不适合骑车,算了吧...");
                }
                Me.getInstance().you.notifyChange();
            }
        });
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation == null) {
            return;
        }
        if (aMapLocation.getErrorCode() != 0) {
            Log.e(TAG, "定位失败, 错误码 " + aMapLocation.getErrorCode());
            return;
        }
        Me.getInstance().update(aMapLocation);

        Log.i("songhang", "address " + aMapLocation.getAddress());
        Log.i("songhang", "lat " + aMapLocation.getLatitude());
        Log.i("songhang", "long " + aMapLocation.getLongitude());
    }
}
