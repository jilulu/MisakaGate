package com.mahoucoder.misakagate;

import android.app.Application;

import java.lang.ref.WeakReference;

/**
 * Created by jamesji on 28/10/2016.
 */

public class GateApplication extends Application {
    private static WeakReference<Application> appRef;

    @Override
    public void onCreate() {
        super.onCreate();
        appRef = new WeakReference<Application>(GateApplication.this);
    }

    public static Application getGlobalContext() {
        return appRef.get();
    }
}
