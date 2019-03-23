package com.song.redcord.map;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.song.redcord.bean.Lover;
import com.song.redcord.bean.Me;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 地图控制器
 */
public class Maper {
    private static final String TAG = "Maper";
    private final AMap aMap;
    // 已经缩放地图
    private final AtomicBoolean hasScale = new AtomicBoolean(false);
    private Context context;

    public Maper(Context context, AMap aMap) {
        this.context = context;
        this.aMap = aMap;
    }

    public void init() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(10000);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);
        myLocationStyle.showMyLocation(false);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        aMap.setMyLocationEnabled(true);
    }

    public void refresh(Me me) {
        Log.i(TAG, "me : " + me.location.getLatitude() + "    " + me.location.getLongitude());
        Log.i(TAG, "you : " + me.you.location.getLatitude() + "    " + me.you.location.getLongitude());

        markUs(me, me.you);
        navigation(me.location, me.you.location);
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
                .title(me.name);
        aMap.addMarker(meOption);
        MarkerOptions youOption = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(youlatl)
                .draggable(false)
                .title(you.name);
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
    private void navigation(Location me, Location you) {
        RouteSearch routeSearch = new RouteSearch(context);
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                new LatLonPoint(me.getLatitude(), me.getLongitude()),
                new LatLonPoint(you.getLatitude(), you.getLongitude()));
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null, null, "");
        routeSearch.calculateDriveRouteAsyn(query);
        routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
                if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
                    if (result == null
                            || result.getPaths() == null
                            || result.getPaths().size() == 0)
                        return;

                    final DrivePath drivePath = result.getPaths().get(0);
                    DrivingRouteOverLay drivingRouteOverlay = new DrivingRouteOverLay(
                            context, aMap, drivePath,
                            result.getStartPos(),
                            result.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";

                    Log.i("songhang", "des " + des);
                }
            }

            @Override
            public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

            }

            @Override
            public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

            }
        });
    }

}
