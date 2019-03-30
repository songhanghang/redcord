package com.song.redcord.map;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.List;

public class JumpUtil {
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

    public static void shareWechatFriend(Context context, String content) {
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

    // 判断是否安装指定app
    private static boolean isInstallApp(Context context, String app_package){
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
        if (pInfo != null) {
            for (int i = 0; i < pInfo.size(); i++) {
                String pn = pInfo.get(i).packageName;
                if (app_package.equals(pn)) {
                    return true;
                }
            }
        }
        return false;
    }
}
