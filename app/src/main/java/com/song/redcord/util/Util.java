package com.song.redcord.util;

import android.text.TextUtils;

public class Util {
    private Util() {

    }

    public static final String parseOutKey(String outKey) {
        if (TextUtils.isEmpty(outKey))
            return null;
        if (!outKey.contains("~*~*~*"))
            return null;
        return outKey.replace("~*", "");
    }

    public static final String createOutKey(String inKey) {
        if (TextUtils.isEmpty(inKey)) return null;
        return "~*~*~*" + inKey + "~*~*~*";
    }
}
