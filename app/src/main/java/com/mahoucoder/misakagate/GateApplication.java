package com.mahoucoder.misakagate;

import android.app.Application;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;

/**
 * Created by jamesji on 28/10/2016.
 */

public class GateApplication extends Application {
    private static WeakReference<Application> appRef;
    protected String userAgent;

    @Override
    public void onCreate() {
        super.onCreate();
        appRef = new WeakReference<Application>(GateApplication.this);

        configurePicasso();
        userAgent = Util.getUserAgent(this, getClass().getSimpleName());
        initStats();
    }

    private void configurePicasso() {
        Picasso.Builder builder = new Picasso.Builder(GateApplication.this);
        builder.downloader(new OkHttpDownloader(GateApplication.this, Integer.MAX_VALUE));
        Picasso build = builder.build();
        build.setIndicatorsEnabled(BuildConfig.DEBUG);
        build.setLoggingEnabled(false);
        Picasso.setSingletonInstance(build);
    }

    private void initStats() {
        MobclickAgent.startWithConfigure(
                new MobclickAgent.UMAnalyticsConfig(
                        GateApplication.this,
                        getString(R.string.umeng_app_key),
                        getString(R.string.umeng_channel)
                )
        );
        MobclickAgent.setDebugMode(BuildConfig.DEBUG);
    }

    public static Application getGlobalContext() {
        return appRef.get();
    }

    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(this, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
    }
}
