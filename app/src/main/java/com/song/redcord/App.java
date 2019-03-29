package com.song.redcord;

import android.app.Application;

import cn.leancloud.AVLogger;
import cn.leancloud.AVOSCloud;

public class App extends Application {

    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        AVOSCloud.setLogLevel(AVLogger.Level.DEBUG);
        AVOSCloud.initialize("vqDtqWtz5WqxrUJ08BF8pMST-gzGzoHsz", "3vXcznVIRE4EtLl9Xtgus9lM");
    }

    public static App get() {
        return app;
    }
}
