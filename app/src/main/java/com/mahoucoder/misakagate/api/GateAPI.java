package com.mahoucoder.misakagate.api;

import com.mahoucoder.misakagate.api.models.AnimeListCache;
import com.mahoucoder.misakagate.api.models.ListAnimeService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jamesji on 28/10/2016.
 */

public class GateAPI {
    private static final String ANIME_BASE_URL = "http://anime.2d-gate.org/";
    private static final String EPISODE_LIST_POST_URL = "http://2d-gate.org/thread-%s-1-1.html";

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
                            .baseUrl(ANIME_BASE_URL)
                            .addConverterFactory(gsonConverterFactory)
                            .build();
                }
            }
        }
        return mRetrofit;
    }

    public static void getEpisodeList(final String tid, Observer<List<String>> observer) {
        Observable<List<String>> listObservable = Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                String url = String.format(EPISODE_LIST_POST_URL, tid);
                Request request = new Request.Builder().url(url).build();
                try {
                    Response response = getOKHTTP().newCall(request).execute();
                    String string = response.body().string();
                    List<String> videoUrls = new ArrayList<>();
                    Matcher matcher = Pattern.compile("(http://embed.2d-gate.org/[^\"]+)").matcher(string);
                    while (matcher.find()) {
                        videoUrls.add(matcher.group());
                    }
                    subscriber.onNext(videoUrls);
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });

        listObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public static void getAnimeList(Callback<AnimeListCache> callback) {
        Retrofit retrofit = getRetrofit();
        ListAnimeService service = retrofit.create(ListAnimeService.class);
        Call<AnimeListCache> listCall = service.getAnimeCache();

        listCall.enqueue(callback);
    }
}
