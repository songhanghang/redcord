package com.song.redcord.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.song.redcord.R;
import com.song.redcord.bean.Her;
import com.song.redcord.bean.Lover;
import com.song.redcord.bean.Me;
import com.song.redcord.databinding.ActivityMainBinding;
import com.song.redcord.interfaces.RequestCallback;
import com.song.redcord.util.Pref;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 地图控制器
 */
public class MapController implements AMapLocationListener {
    private static final String TAG = "MapController";
    private final AMap aMap;
    private final AtomicBoolean hasScale = new AtomicBoolean(false);
    private Me me;
    private AMapLocationClient locationClient;
    private AMapLocation aMapLocation;
    private AMapLocationClientOption locationOption;
    private ActivityMainBinding binding;
    // 已经缩放地图
    private Activity context;

    public MapController(Activity context, AMap aMap, ActivityMainBinding binding) {
        this.context = context;
        this.aMap = aMap;
        this.binding = binding;
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
                    locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
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
                Log.i("songhang", "-------- deactivate -------");
                if (locationClient != null) {
                    locationClient.stopLocation();
                    locationClient.onDestroy();
                }
                locationClient = null;
            }
        });
        aMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        aMap.setMyLocationEnabled(true);

        check();
    }

    private void check() {
        String meId = Pref.getInstance().getId();
        if (!TextUtils.isEmpty(meId)) {
            me = new Me(meId);
            binding.setMe(me);
            me.pull(new RequestCallback() {
                @Override
                public void onSuccess() {
                    if (me.isSingle()) {
                        showBindHerView();
                    } else {
                        final Her her = new Her(me.loverId);
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
        final View view = LayoutInflater.from(context).inflate(R.layout.edit_dialog, null);
        final EditText editText = view.findViewById(R.id.edit);
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("新用户请直接创建，如果已经建立过连接, 从TA那里获取你的ID输入")
                .setView(view)
                .setCancelable(false)
                .setPositiveButton("创建", null)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.finish();
                    }
                })
                .create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editText.getText().toString())) {
                    createMe(dialog);
                } else {
                    pullMe(editText);
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = TextUtils.isEmpty(s.toString()) ? "创建" : "获取";
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(str);
            }
        });
    }

    private void showBindHerView() {
        final View view = LayoutInflater.from(context).inflate(R.layout.edit_dialog, null);
        final EditText editText = view.findViewById(R.id.edit);
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("你的id是 " + me.id + "\n输入TA的ID,或者复制自己分享给她")
                .setView(view)
                .setCancelable(false)
                .setPositiveButton("分享", null)
                .setNegativeButton("取消", null)
                .create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                Toast.makeText(context, "已绑定，等待数据刷新", Toast.LENGTH_LONG).show();
                                refreshView(me, her);
                                dialog.dismiss();
                            } else {
                                Toast.makeText(context, "人家已经有爱人", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFail() {
                            Toast.makeText(context, "绑定失败", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    JumpUtil.shareWechatFriend(context, me.id);
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = TextUtils.isEmpty(s.toString()) ? "分享" : "绑定";
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(str);
            }
        });

    }

    private void createMe(final AlertDialog dialog) {
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
                    Pref.getInstance().saveId(me.id);
                    dialog.dismiss();
                    showBindHerView();
                } else {
                    Toast.makeText(context, "创建失败", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void pullMe(EditText editText) {
        me = new Me(editText.getText().toString());
        me.pull(new RequestCallback() {
            @Override
            public void onSuccess() {
                Pref.getInstance().saveId(me.id);
                Her her = new Her(me.loverId);
                me.setLover(her);
                binding.setMe(me);
                binding.setHer(her);
            }

            @Override
            public void onFail() {
                Toast.makeText(context, "获取失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void refreshView(@NonNull Me me, @NonNull Her her) {
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
        RouteSearch routeSearch = new RouteSearch(context);
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
                            context, aMap, drivePath,
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
                    her.setDriveInfo("不知哪里出了问题...");
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
                    her.setWorkInfo("可能太远了, 要不换个交通工具?");
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
                    her.setRideInfo("也许不适合骑车,算了吧...");
                }
                her.notifyChange();
            }
        });
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        Log.i("songhang", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 定位定位  ~~~~~~~~~~~~~~~~~~~~~~~~~~ ");

        if (me == null) {
            return;
        }

        // 位置改变
        if (location != null && location.getErrorCode() == 0) {
            this.aMapLocation = location;
            me.setLocation(location.getLatitude(), location.getLongitude());
            me.setAddress(location.getAddress());
            me.push();
        }

        final Lover lover = me.getLover();
        if (lover == null) {
            return;
        }

        lover.pull(new RequestCallback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "you : " + lover.location.getLatitude() + "    " + lover.location.getLongitude());
                refreshView(me, (Her) lover);
            }

            @Override
            public void onFail() {

            }
        });

    }
}
