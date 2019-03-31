package com.song.redcord.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;


/**
 * @author by songhang on 2018/3/2
 */

public class ScreenUtil {
    private volatile static int sHeight;
    private volatile static int sWidth;

    public static int getHeight(Context context) {
        if (sHeight > 0) {
            return sHeight;
        }
        calcRealScreenSize(context);
        return sHeight;
    }

    public static int getWidth(Context context) {
        if (sWidth > 0) {
            return sWidth;
        }
        calcRealScreenSize(context);
        return sWidth;
    }

    private static void calcRealScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getRealMetrics(dm);
        if (dm.widthPixels > dm.heightPixels) {
            sWidth = dm.heightPixels;
            sHeight = dm.widthPixels;
        } else {
            sWidth = dm.widthPixels;
            sHeight = dm.heightPixels;
        }
    }
}
