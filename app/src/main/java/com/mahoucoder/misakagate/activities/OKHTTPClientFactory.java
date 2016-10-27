package com.mahoucoder.misakagate.activities;

import okhttp3.OkHttpClient;

/**
 * Created by jamesji on 28/10/2016.
 */

public class OKHTTPClientFactory {
    private static volatile OkHttpClient mClient;

    public static OkHttpClient getClient() {
        if (mClient == null) {
            synchronized (OKHTTPClientFactory.class) {
                if (mClient == null) {
                    mClient = new OkHttpClient.Builder().followRedirects(true).build();
                }
            }
        }
        return mClient;
    }
}
