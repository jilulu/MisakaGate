package com.mahoucoder.misakagate;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mahoucoder.misakagate.activities.AnimeDetailActivity;
import com.mahoucoder.misakagate.api.GateAPI;
import com.mahoucoder.misakagate.api.models.AnimeSeason;
import com.mahoucoder.misakagate.api.models.Favorite;
import com.mahoucoder.misakagate.api.models.Thread;
import com.mahoucoder.misakagate.api.models.Thread_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SubscriptionService extends Service {
    public static final String TAG = SubscriptionService.class.getSimpleName();
    public static final long DEFAULT_CHECK_INTERVAL = 60L * 60L * 1000L;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getUpdatedThread();
        AlarmManager manager = (AlarmManager) GateApplication.getGlobalContext().getSystemService(ALARM_SERVICE);
        long triggerTime = SystemClock.elapsedRealtime() + DEFAULT_CHECK_INTERVAL;
        Intent receiverIntent = new Intent(GateApplication.getGlobalContext(), CheckSubscriptionReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(GateApplication.getGlobalContext(), 0, receiverIntent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
        } else {
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void getUpdatedThread() {
        Observable.create(new Observable.OnSubscribe<Favorite>() {
            @Override
            public void call(Subscriber<? super Favorite> subscriber) {
                try {
                    List<Favorite> favorites = SQLite.select().from(Favorite.class).queryList();
                    for (Favorite favorite : favorites) {
                        subscriber.onNext(favorite);
                    }
                    subscriber.onCompleted();
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }
            }
        }).filter(new Func1<Favorite, Boolean>() {
            @Override
            public Boolean call(Favorite favorite) {
                List<AnimeSeason> animeSeasonSync = null, animeSeasonLocal;
                try {
                    animeSeasonSync = GateAPI.getAnimeSeasonsSync(favorite.tid);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                animeSeasonLocal = sourceAnimeSeasonsFromJson(GateApplication.getGlobalContext(), favorite.tid);
                if (animeSeasonSync == null) {
                    return false;
                } else if (animeSeasonLocal == null) {
                    sinkAnimeSeasonsToJson(GateApplication.getGlobalContext(), animeSeasonSync, favorite.tid);
                    return false;
                } else {
                    // Online version has more seasons
                    if (animeSeasonSync.size() > animeSeasonLocal.size()) {
                        sinkAnimeSeasonsToJson(GateApplication.getGlobalContext(), animeSeasonSync, favorite.tid);
                        return true;
                    }
                    for (int i = 0; i < animeSeasonSync.size(); i++) {
                        AnimeSeason seasonSync = animeSeasonSync.get(i);
                        AnimeSeason seasonLocal = animeSeasonLocal.get(i);
                        // Online version has more episodes
                        if (seasonSync.playableAnimeList.size() > seasonLocal.playableAnimeList.size()) {
                            sinkAnimeSeasonsToJson(GateApplication.getGlobalContext(), animeSeasonSync, favorite.tid);
                            return true;
                        }
                    }
                    return false;
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).subscribe(new Observer<Favorite>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Favorite favorite) {
                Thread thread = SQLite.select().from(Thread.class)
                        .where(Thread_Table.tid.eq(favorite.tid))
                        .querySingle();
                if (thread == null) {
                    return;
                }
                NotificationCompat.Builder builder = new NotificationCompat.Builder(GateApplication.getGlobalContext());
                builder.setContentTitle(thread.getTitle() + getString(R.string.is_updated));
                builder.setContentText(thread.getTitle() + getString(R.string.anime_update_text));
                builder.setSmallIcon(R.drawable.ic_video_library_white);
                builder.setAutoCancel(true);

                Intent intent = AnimeDetailActivity.buildLaunchIntent(GateApplication.getGlobalContext(), thread);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(GateApplication.getGlobalContext());
                stackBuilder.addNextIntent(intent);
                PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                builder.setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(1, builder.build());
            }
        });
    }

    private static void sinkAnimeSeasonsToJson(Context context, List<AnimeSeason> animeSeasons, String tid) {
        Gson gson = new Gson();
        String serialized = gson.toJson(animeSeasons);
        File jsonStoreDir = new File(context.getFilesDir(), "favorites_anime_json_store/");
        if (!jsonStoreDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            jsonStoreDir.mkdirs();
        }
        try {
            FileOutputStream fos = new FileOutputStream(new File(jsonStoreDir, tid + ".json"));
            fos.write(serialized.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private static List<AnimeSeason> sourceAnimeSeasonsFromJson(Context context, String tid) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        File jsonStore = new File(context.getFilesDir(), "favorites_anime_json_store/" + tid + ".json");
        JsonArray jsonArray = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(jsonStore));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
            jsonArray = parser.parse(sb.toString()).getAsJsonArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonArray != null) {
            List<AnimeSeason> animeSeasonList = new ArrayList<>(jsonArray.size());
            for (JsonElement jsonElement : jsonArray) {
                animeSeasonList.add(gson.fromJson(jsonElement, AnimeSeason.class));
            }
            return animeSeasonList;
        } else {
            return null;
        }
    }
}
