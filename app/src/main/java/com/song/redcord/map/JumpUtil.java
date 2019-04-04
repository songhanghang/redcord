package com.song.redcord.map;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.didikee.donate.AlipayDonate;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.song.redcord.App;
import com.song.redcord.LiveWallpaper;


public class JumpUtil {

    public static void startNavAmap(Context context, double lat, double lon) throws ActivityNotFoundException {
        try {
            String uri = String.format("amapuri://route/plan/?dlat=%s&dlon=%s&dname=你的TA&dev=0&t=0", lat, lon);
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse(uri));
            intent.setPackage("com.autonavi.minimap");
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "请下载高德地图！", Toast.LENGTH_LONG).show();
        }
    }

    public static void startNavBaiduMap(Context context, double lat, double lon) throws ActivityNotFoundException {
        try {
            LatLng latLng = gd2bd(lat, lon);
            String uri = String.format("baidumap://map/direction?destination=" + "%s,%s&mode=driving&src=com.song.redcord", latLng.latitude, latLng.longitude);
            Intent intent = new Intent();
            intent.setData(Uri.parse(uri));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "请下载百度地图！", Toast.LENGTH_LONG).show();
        }
    }

    public static void startShare(Context context, String content) {
        if (isInstallApp(context, "com.tencent.mm")) {
            Intent intent = new Intent();
            ComponentName cop = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
            intent.setComponent(cop);
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra("android.intent.extra.TEXT", content);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "您需要安装微信客户端", Toast.LENGTH_LONG).show();
        }
    }

    public static void startSetWallpaper() {
        try {
            Intent intent = new Intent();
            intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(App.get().getPackageName(), LiveWallpaper.class.getCanonicalName()));
            App.get().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startAliPay(Activity activity) {
        if (activity == null) {
            return;
        }

        boolean hasInstalledAlipayClient = AlipayDonate.hasInstalledAlipayClient(activity);
        if (hasInstalledAlipayClient) {
            AlipayDonate.startAlipayClient(activity, "FKX08327O1EEEDGRVIWIFB");
        } else {
            Toast.makeText(activity, "未安装支付宝客户端", Toast.LENGTH_SHORT).show();
        }
    }

    public static void startAbout() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://github.com/songhanghang/redcord/blob/master/README.md"));
        App.get().startActivity(intent);
    }

    // 判断是否安装指定app
    private static boolean isInstallApp(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName))
            return false;

        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static LatLng gd2bd(double latitude, double longitude) {
        double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
        double x = longitude, y = latitude;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        double bd_lng = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new LatLng(bd_lat, bd_lng);
    }
}
