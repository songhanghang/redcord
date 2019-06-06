package com.song.redcord.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final SimpleDateFormat sFormatter = new SimpleDateFormat("yy年MM月dd HH:mm:ss");

    public static String getUpdateDateString(Date date) {
        return sFormatter.format(date);
    }
}
