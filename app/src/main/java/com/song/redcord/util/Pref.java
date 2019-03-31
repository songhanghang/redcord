package com.song.redcord.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.song.redcord.App;

public class Pref {
    private static final String ID = "id";
    private static final String FIEST_SET_WALLPAPER = "first_set_wallpaper";
    private SharedPreferences preferences = App.get().getSharedPreferences("me", Context.MODE_PRIVATE);

    private Pref() {

    }

    private static class Holder {
        private static Pref instance = new Pref();
    }

    public static Pref get() {
        return Holder.instance;
    }


    public void saveId(String id) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(ID, id);
        edit.apply();
    }

    public String getId() {
        return preferences.getString(ID, "");
    }

    public void setWallpaper() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(FIEST_SET_WALLPAPER, false);
        edit.apply();
    }

    public boolean isFirstSetWallpaper() {
        return preferences.getBoolean(FIEST_SET_WALLPAPER, true);
    }

}
