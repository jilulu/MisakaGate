package com.mahoucoder.misakagate;

import android.app.Application;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

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

        Picasso.Builder builder = new Picasso.Builder(GateApplication.this);
        builder.downloader(new OkHttpDownloader(GateApplication.this, Integer.MAX_VALUE));
        Picasso build = builder.build();
        build.setIndicatorsEnabled(BuildConfig.DEBUG);
        build.setLoggingEnabled(false);
        Picasso.setSingletonInstance(build);
    }

    public static Application getGlobalContext() {
        return appRef.get();
    }
}
