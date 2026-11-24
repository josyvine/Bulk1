package com.bulk.app.utils;

public class NativeBridge {

    static {
        System.loadLibrary("bulk_native");
    }

    public static native String getBannerAdId();
}