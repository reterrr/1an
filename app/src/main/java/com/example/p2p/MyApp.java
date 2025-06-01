package com.example.p2p;

import android.app.Application;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        NetworkResourceManager.init(this);
    }
}