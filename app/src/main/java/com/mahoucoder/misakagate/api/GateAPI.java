package com.mahoucoder.misakagate.api;

import com.mahoucoder.misakagate.api.models.AnimeListCache;
import com.mahoucoder.misakagate.api.models.ListAnimeService;

import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jamesji on 28/10/2016.
 */

public class GateAPI {
    private static final String ANIME_BASE_URL = "http://anime.2d-gate.org";
    public static final String API_BASE = "http://anime.2d-gate.org/";
    public static final String EPISODE_LIST_POST_URL = "http://2d-gate.org/thread-%d-1-1.html";

    private static volatile Retrofit mRetrofit;

    private static volatile OkHttpClient mClient;

    public static OkHttpClient getOKHTTP() {
        if (mClient == null) {
            synchronized (GateAPI.class) {
                if (mClient == null) {
                    mClient = new OkHttpClient.Builder().followRedirects(true).build();
                }
            }
        }
        return mClient;
    }

    public static Retrofit getRetrofit() {
        if (mRetrofit == null) {
            synchronized (GateAPI.class) {
                if (mRetrofit == null) {
                    GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create();
                    mRetrofit = new Retrofit.Builder()
                            .baseUrl(API_BASE)
                            .addConverterFactory(gsonConverterFactory)
                            .build();
                }
            }
        }
        return mRetrofit;
    }

    public static void getEpisodeList(int tid, okhttp3.Callback callback) {
        String url = String.format(Locale.ENGLISH, EPISODE_LIST_POST_URL, tid);
        Request request = new Request.Builder().url(url).build();
        getOKHTTP().newCall(request).enqueue(callback);
    }

    public static void getAnimeList(Callback<AnimeListCache> callback) {
        Retrofit retrofit = getRetrofit();
        ListAnimeService service = retrofit.create(ListAnimeService.class);
        Call<AnimeListCache> listCall = service.getAnimeCache();

        listCall.enqueue(callback);
    }
}
