package com.mahoucoder.misakagate.utils;

import com.mahoucoder.misakagate.BuildConfig;
import com.mahoucoder.misakagate.api.models.AnimeListCache;
import com.mahoucoder.misakagate.api.models.Thread;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jamesji on 10/11/2016.
 */

public class DebugUtils {
    public static void adjustThreadOrderForDebug(AnimeListCache animeListCache) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        List<Thread> threads = animeListCache.threads;
        Collections.sort(threads, new Comparator<Thread>() {
            @Override
            public int compare(Thread o1, Thread o2) {
                int i = Integer.parseInt(o1.tid);
                int j = Integer.parseInt(o2.tid);
                return i-j;
            }
        });
    }
}
