package com.mahoucoder.misakagate.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.mahoucoder.misakagate.BuildConfig;
import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;

import java.util.ArrayList;
import java.util.Locale;

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

    public static float px2dp(Context context, float pxValue) {
        Resources r = context.getResources();
        DisplayMetrics metrics = r.getDisplayMetrics();
        return pxValue / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
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

    public static String calculateTimeDiffInDays(long time) {
        long abs = Math.abs(time - System.currentTimeMillis() / 1000L);
        abs /= (3600L * 24L);
        String s = abs == 0L ? GateApplication.getGlobalContext().getString(R.string.today) :
                String.format(Locale.ENGLISH, "%d%s", abs, GateApplication.getGlobalContext().getString(R.string.days_ago));
        if (abs == 1) {
            s = s.replace("s", "");
        }
        return s;
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

    public static boolean checkAllPermissions() {
        return getMissingPermissionAsArray().length == 0;
    }

    @NonNull
    public static String[] getMissingPermissionAsArray() {
        int phone = ContextCompat.checkSelfPermission(GateApplication.getGlobalContext(), Manifest.permission.READ_PHONE_STATE);
        int storage = ContextCompat.checkSelfPermission(GateApplication.getGlobalContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        ArrayList<String> permReqList = new ArrayList<String>();
        if (phone != PackageManager.PERMISSION_GRANTED) {
            permReqList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            permReqList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        return permReqList.toArray(new String[0]);
    }
}
