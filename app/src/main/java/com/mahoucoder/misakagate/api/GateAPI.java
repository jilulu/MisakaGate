package com.mahoucoder.misakagate.api;

import com.mahoucoder.misakagate.api.models.AnimeListCache;
import com.mahoucoder.misakagate.api.models.AnimeSeason;
import com.mahoucoder.misakagate.api.models.ListAnimeService;
import com.mahoucoder.misakagate.api.models.PlaybackInfo;
import com.mahoucoder.misakagate.api.models.PlaybackInfoService;
import com.mahoucoder.misakagate.utils.GateParser;
import com.mahoucoder.misakagate.utils.GateUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSource;
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
    private static final String EMBED_BASE_URL = "http://embed.2d-gate.org/";
    private static final String EPISODE_LIST_POST_URL = "http://2d-gate.org/thread-%s-1-1.html";

    private static volatile Retrofit mRetrofit, vRetrofit;
    private static volatile GsonConverterFactory gsonConverterFactory;
    private static volatile OkHttpClient mClient;

    public static GsonConverterFactory getGsonConverterFactory() {
        if (gsonConverterFactory == null) {
            synchronized (GateAPI.class) {
                if (gsonConverterFactory == null) {
                    gsonConverterFactory = GsonConverterFactory.create();
                }
            }
        }
        return gsonConverterFactory;
    }

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
                    mRetrofit = new Retrofit.Builder()
                            .baseUrl(ANIME_BASE_URL)
                            .addConverterFactory(getGsonConverterFactory())
                            .build();
                }
            }
        }
        return mRetrofit;
    }

    public static Retrofit getVideoInfoRetrofit() {
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

    public static void getEpisodeDiv(final String tid, final Observer<Element> observer) {
        Observable<Element> listObservable = Observable.create(new Observable.OnSubscribe<Element>() {
            @Override
            public void call(Subscriber<? super Element> subscriber) {
                String url = String.format(EPISODE_LIST_POST_URL, tid);
                Request request = new Request.Builder().url(url).build();
                try {
                    Response response = getOKHTTP().newCall(request).execute();
                    BufferedSource source = response.body().source();
                    StringBuilder domBuilder = new StringBuilder();
                    String addressDiv = null;
                    int findsInDiv = 0;
                    Pattern p = Pattern.compile("http://embed.2d-gate.org/");
                    while (!source.exhausted()) {
                        int numFinds = 0;
                        String s = source.readUtf8Line();
                        Matcher matcher = p.matcher(s);
                        while (matcher.find()) {
                            numFinds += 1;
                        }
                        if (numFinds > findsInDiv) {
                            findsInDiv = numFinds;
                            addressDiv = s;
                        }
                        domBuilder.append(s);
                    }
                    String id = Jsoup.parse(addressDiv).body().child(0).id();
                    subscriber.onNext(Jsoup.parse(domBuilder.toString()).body().getElementById(id));
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });

        listObservable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(observer);
    }

    public static void getEpisodeStructure(String tid, final Observer<List<AnimeSeason>> observer) {
        Observer<Element> divisionObserver = new Observer<Element>() {
            @Override
            public void onNext(Element rootNode) {
                List<AnimeSeason> seasonList = GateParser.parseNodeIntoAnimeSeasonList(rootNode);
                GateUtils.logd("TagParsing", seasonList.size() + " seasons parsed. ");
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                observer.onError(e);
            }
        };

        getEpisodeDiv(tid, divisionObserver);
    }

    public static void getAnimeList(Callback<AnimeListCache> callback) {
        Retrofit retrofit = getRetrofit();
        ListAnimeService service = retrofit.create(ListAnimeService.class);
        Call<AnimeListCache> listCall = service.getAnimeCache();

        listCall.enqueue(callback);
    }

    public static void getPlaybackInfo(String url, Callback<PlaybackInfo[]> callback) {
        Pattern pattern = Pattern.compile("http://embed.2d-gate.org/json-feed/([^/]+)/\\?hash=(.+)");
        Matcher matcher = pattern.matcher(url);
        if (!matcher.matches()) {
            GateUtils.logd("Pattern match failed for " + url);
            return;
        }
        Retrofit retrofit = getVideoInfoRetrofit();
        PlaybackInfoService service = retrofit.create(PlaybackInfoService.class);
        Call<PlaybackInfo[]> infoCall = service.getPlayBackInfo(matcher.group(1), matcher.group(2));
        infoCall.enqueue(callback);
    }
}
