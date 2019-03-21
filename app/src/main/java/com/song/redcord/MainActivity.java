package com.song.redcord;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MyLocationStyle;

public class MainActivity extends AppCompatActivity implements AMap.OnMyLocationChangeListener{

    private MapView mMapView;
    private AMap mAMap;
    private Us mUs = new Us();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(2000);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);
        mAMap.setMyLocationStyle(myLocationStyle);
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);
        mAMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        mAMap.setMyLocationEnabled(true);
        mAMap.setOnMyLocationChangeListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMyLocationChange(Location location) {
        mUs.update(location, new Runnable() {
            @Override
            public void run() {
                mUs.limitUsBound(mAMap);
            }
        });
    }
}
