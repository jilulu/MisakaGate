package com.mahoucoder.misakagate.api;

import android.util.Log;

import com.mahoucoder.misakagate.BuildConfig;
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

    public static void getNewestVersion(Observer<String> observer) {
        final String RELEASE_PAGE_URL = "https://github.com/jilulu/MisakaGate/releases/latest";
        final Request request = new Request.Builder().url(RELEASE_PAGE_URL).build();
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Response response = null;
                try {
                    // Get GitHub release page DOM and let Jsoup parse it
                    response = getOKHTTP().newCall(request).execute();
                    Document dom = Jsoup.parse(response.body().byteStream(), "utf-8", RELEASE_PAGE_URL);
                    // Get the title
                    String title = dom.head().getElementsByTag("title").text();
                    // Get the version name of the latest build using specific text locations, without the 'v'
                    String version = title.substring(title.indexOf("v") + 1);
                    version = version.substring(0, version.indexOf(" "));
                    // Get current version name, without the 'v'
                    String currentVersion = BuildConfig.VERSION_NAME.substring(BuildConfig.VERSION_NAME.indexOf("v") + 1);
                    // Split both version names into parts, divided by spaces
                    String[] versionParts = version.split("\\.");
                    String[] currentVersionParts = currentVersion.split("\\.");
                    // Compare version name parts
                    for (int i = 0; i < Math.min(versionParts.length, currentVersionParts.length); i++) {
                        if (Integer.parseInt(versionParts[i]) > Integer.parseInt(currentVersionParts[i])) {
                            subscriber.onNext(String.format("https://github.com/jilulu/MisakaGate/releases/download/v%s/app-release.apk", version));
                            return;
                        } else if (Integer.parseInt(versionParts[i]) < Integer.parseInt(currentVersionParts[i])) {
                            break;
                        }
                    }
                    subscriber.onNext("");
                } catch (IOException e) {
                    subscriber.onError(e);
                } finally {
                    if (response != null) {
                        response.close();
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }
}
