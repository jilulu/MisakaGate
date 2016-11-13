package com.mahoucoder.misakagate.api;

import android.util.Log;

import com.mahoucoder.misakagate.api.models.AnimeListCache;
import com.mahoucoder.misakagate.api.models.AnimeSeason;
import com.mahoucoder.misakagate.api.models.ListAnimeService;
import com.mahoucoder.misakagate.api.models.PlaybackInfo;
import com.mahoucoder.misakagate.api.models.PlaybackInfoService;
import com.mahoucoder.misakagate.utils.GateParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.mahoucoder.misakagate.api.GateServiceGenerator.getRetrofit;
import static com.mahoucoder.misakagate.api.GateServiceGenerator.getVideoInfoRetrofit;

/**
 * Created by jamesji on 28/10/2016.
 */

public class GateAPI {
    private static final String EPISODE_LIST_POST_URL = "http://2d-gate.org/thread-%s-1-1.html";
    private static volatile OkHttpClient mClient;

    private static OkHttpClient getOKHTTP() {
        if (mClient == null) {
            synchronized (GateAPI.class) {
                if (mClient == null) {
                    mClient = new OkHttpClient.Builder().followRedirects(true).build();
                }
            }
        }
        return mClient;
    }

    public static void getAnimeSeasons(final String tid, final Observer<List<AnimeSeason>> observer) {
        Observable<List<AnimeSeason>> listObservable = Observable.create(new Observable.OnSubscribe<List<AnimeSeason>>() {
            @Override
            public void call(Subscriber<? super List<AnimeSeason>> subscriber) {
                String url = String.format(EPISODE_LIST_POST_URL, tid);
                Request request = new Request.Builder().url(url).build();
                try {
                    Response response = getOKHTTP().newCall(request).execute();
                    Document dom = Jsoup.parse(response.body().byteStream(), "utf-8", url);
                    Element interestingNode = dom.select("div[style=\"display:none\"]").get(0);
                    List<AnimeSeason> animeSeasons = GateParser.parseNodeIntoAnimeSeasonList(interestingNode);
                    subscriber.onNext(animeSeasons);
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

    public static void getPlaybackInfo(String url, Callback<PlaybackInfo[]> callback) throws IllegalArgumentException {
        Pattern pattern = Pattern.compile("http://embed.2d-gate.org/json-feed/([^/]+)/\\?hash=(.+)");
        Matcher matcher = pattern.matcher(url);
        if (!matcher.matches()) {
            Log.e("GateAPI.getPlaybackInfo", "Pattern match failed for " + url);
        }
        Retrofit retrofit = getVideoInfoRetrofit();
        PlaybackInfoService service = retrofit.create(PlaybackInfoService.class);
        Call<PlaybackInfo[]> infoCall = service.getPlayBackInfo(matcher.group(1), matcher.group(2));
        infoCall.enqueue(callback);
    }
}
