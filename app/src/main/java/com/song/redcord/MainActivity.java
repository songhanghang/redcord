package com.song.redcord;

import android.Manifest;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.song.redcord.bean.Me;
import com.song.redcord.interfaces.OnDataUpdateListener;
import com.song.redcord.map.Maper;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements AMap.OnMyLocationChangeListener,
        EasyPermissions.PermissionCallbacks,
        OnDataUpdateListener {

    private static final int RC_LOCATION_PERM = 1;
    private MapView mapView;
    private InfoViewSetter infoViewSetter;
    private AMap aMap;
    private Maper maper;
    private Me me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        infoViewSetter = new InfoViewSetter(findViewById(R.id.info));
        maper = new Maper(this, aMap);
        me = new Me();
        me.setOnDataUpdateListener(this);
        aMap.setOnMyLocationChangeListener(this);

        EasyPermissions.requestPermissions(this,
                getString(R.string.app_need_location),
                RC_LOCATION_PERM,
                Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMyLocationChange(Location location) {
        me.update(location);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        maper.init();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onUpdate() {
        maper.refresh(me);
        infoViewSetter.refresh(me.you);
    }
}
