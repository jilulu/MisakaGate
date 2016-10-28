package com.mahoucoder.misakagate.api;

import com.mahoucoder.misakagate.api.models.Anime;
import com.mahoucoder.misakagate.api.models.ListAnimeService;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jamesji on 28/10/2016.
 */

public class GateAPI {
    private static final String ANIME_BASE_URL = "http://anime.2d-gate.org";
    public static final String ANIME_CACHE_URL = ANIME_BASE_URL + "/__cache.html";

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
                            .baseUrl("http://mahoucoder.com:8000/")
                            .addConverterFactory(gsonConverterFactory)
                            .build();
                }
            }
        }
        return mRetrofit;
    }

    public static Response getAnimeListDOM() throws IOException {
        OkHttpClient client = getOKHTTP();
        Request request = new Request.Builder().url(ANIME_CACHE_URL)
                .header("User-Agent", "Android Application by MahouCoder")
                .build();
        return client.newCall(request).execute();
    }

    public static void getAnimeList(Callback<List<Anime>> callback) {
        Retrofit retrofit = getRetrofit();
        ListAnimeService service = retrofit.create(ListAnimeService.class);
        Call<List<Anime>> listCall = service.listAnimes();

        listCall.enqueue(callback);
    }
}
