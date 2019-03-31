package com.song.redcord.util;

import android.animation.ArgbEvaluator;

import com.song.redcord.App;
import com.song.redcord.R;

import java.util.Calendar;

public class ColorUtil {

    private final static Calendar calendar = Calendar.getInstance();
    private final static ArgbEvaluator rgbEvaluator = new ArgbEvaluator();
    private final static int[][] colors = new int[][]{
            {App.get().getColor(R.color.colorBlueDark), App.get().getColor(R.color.colorPrimaryDark)},
            {App.get().getColor(R.color.colorPrimaryDark), App.get().getColor(R.color.colorPurpleDark)},
            {App.get().getColor(R.color.colorPurpleDark), App.get().getColor(R.color.colorGreyDark)}
    };

    private ColorUtil() {

    }

    public static int getColor(float fraction) {
        calendar.setTimeInMillis(System.currentTimeMillis());
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        if (h >= 7 && h <= 12) { // 上午蓝
            return (int) rgbEvaluator.evaluate(fraction, colors[0][0], colors[0][1]);
        } else if (h > 12 && h <= 20) { // 下午绿
            return (int) rgbEvaluator.evaluate(fraction, colors[1][0], colors[1][1]);
        } else { // 晚上灰
            return (int) rgbEvaluator.evaluate(fraction, colors[2][0], colors[2][1]);
        }
    }
}
