package com.mahoucoder.misakagate.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jamesji on 12/11/2016.
 */

public class GateServiceGenerator {
    private static final String ANIME_BASE_URL = "http://anime.2d-gate.org/";
    private static final String EMBED_BASE_URL = "http://embed.2d-gate.org/";

    private static volatile Retrofit mRetrofit, vRetrofit;
    private static volatile GsonConverterFactory gsonConverterFactory;

    private static GsonConverterFactory getGsonConverterFactory() {
        if (gsonConverterFactory == null) {
            synchronized (GateAPI.class) {
                if (gsonConverterFactory == null) {
                    gsonConverterFactory = GsonConverterFactory.create();
                }
            }
        }
        return gsonConverterFactory;
    }

    static Retrofit getRetrofit() {
        if (mRetrofit == null) {
            synchronized (GateAPI.class) {
                if (mRetrofit == null) {
                    mRetrofit = new Retrofit.Builder()
                            .baseUrl(ANIME_BASE_URL)
                            .addConverterFactory(getGsonConverterFactory())
                            .build();
                }
            }
        }
        return mRetrofit;
    }

    static Retrofit getVideoInfoRetrofit() {
        if (vRetrofit == null) {
            synchronized (GateAPI.class) {
                if (vRetrofit == null) {
                    vRetrofit = new Retrofit.Builder()
                            .baseUrl(EMBED_BASE_URL)
                            .addConverterFactory(getGsonConverterFactory())
                            .build();
                }
            }
        }
        return vRetrofit;
    }
}
