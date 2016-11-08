package com.mahoucoder.misakagate.utils;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.TypedValue;

import com.mahoucoder.misakagate.BuildConfig;
import com.mahoucoder.misakagate.GateApplication;

/**
 * Created by jamesji on 29/10/2016.
 */

public class GateUtils {
    private GateUtils() {
        // Disable object construction with a private constructor
    }

    public static float dp2px(Context context, float dpValue) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, r.getDisplayMetrics());
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    public static String calculateTimeDiff(long time) {
        long currentTime = System.currentTimeMillis() / 1000L;
        long abs = Math.abs(currentTime - time);
        String unit = "s";
        if (abs > 60) {
            abs /= 60;
            unit = "m";
        }
        if (abs > 60 && unit.equals("m")) {
            abs /= 60;
            unit = "h";
        }
        if (abs > 24 && unit.equals("h")) {
            abs /= 24;
            unit = "d";
        }
        return abs + unit;
    }

    public static int ordinalIndexOf(String str, String s, int n) {
        int pos = str.indexOf(s, 0);
        while (n-- > 0 && pos != -1)
            pos = str.indexOf(s, pos + 1);
        return pos;
    }

    public static void logd(String tag, String message) {
        if (BuildConfig.DEBUG) Log.d(tag, message);
    }

    public static void logd(String message) {
        logd(GateApplication.class.getSimpleName(), message);
    }

    public static String convertJsonFeed(String url) {
        return url.replace("/embed/", "/json-feed/");
    }
}
