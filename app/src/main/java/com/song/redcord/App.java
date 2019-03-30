package com.song.redcord;

import android.app.Application;

import com.avos.avoscloud.AVLogger;
import com.avos.avoscloud.AVOSCloud;


public class App extends Application {

    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        AVOSCloud.setLogLevel(AVLogger.LOG_LEVEL_DEBUG);
        AVOSCloud.initialize(this, "vqDtqWtz5WqxrUJ08BF8pMST-gzGzoHsz", "3vXcznVIRE4EtLl9Xtgus9lM");
    }

    public static App get() {
        return app;
    }
}
