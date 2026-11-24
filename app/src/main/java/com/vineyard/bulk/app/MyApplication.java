package com.vineyard.bulk.app;

import android.app.Application;
import com.google.android.gms.ads.MobileAds;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            MobileAds.initialize(this, initializationStatus -> {});
        } catch (Exception ignored) {
        }
    }
}