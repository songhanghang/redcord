package com.song.redcord.map;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class NavgationUtil {
    public static void nav(Context context, double lat, double lon) {
        try {
            navAmap(context, lat, lon);
        } catch (ActivityNotFoundException e) {
            try {
                navBaiduMap(context, lat, lon);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(context, "请下载高德活百度地图！", Toast.LENGTH_LONG).show();
            }
        }
    }

    private static void navAmap(Context context, double lat, double lon) throws ActivityNotFoundException {
        String uri = String.format("amapuri://route/plan/?dlat=%s&dlon=%s&dname=你的TA&dev=0&t=0", lat, lon);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse(uri));
        intent.setPackage("com.autonavi.minimap");
        context.startActivity(intent);
    }

    private static void navBaiduMap(Context context, double lat, double lon) throws ActivityNotFoundException {
        String uri = String.format("baidumap://map/direction?destination=" + "%s,%s&mode=driving&src=com.song.redcord", lat, lon);
        Intent intent = new Intent();
        intent.setData(Uri.parse(uri));
        context.startActivity(intent);
    }
}
