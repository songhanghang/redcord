package com.song.redcord;

import android.Manifest;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.song.redcord.bean.Me;
import com.song.redcord.map.InfoController;
import com.song.redcord.map.MapController;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements AMap.OnMyLocationChangeListener,
        EasyPermissions.PermissionCallbacks {

    private static final int RC_LOCATION_PERM = 1;
    private MapView mapView;
    private AMap aMap;
    private Me me;
    private InfoController infoViewController;
    private MapController mapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        infoViewController = new InfoController(findViewById(R.id.info));
        mapController = new MapController(this, aMap, infoViewController);
        me = new Me();
        me.setLoverRefresh(mapController);
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
        mapController.init();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}
