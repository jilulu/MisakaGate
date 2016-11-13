package com.mahoucoder.misakagate.utils;

import android.net.Uri;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jamesji on 10/11/2016.
 */

public class PicHostUtil {
    private static final char SMALL_SQUARE = 's'; // (90×90)
    private static final char BIG_SQUARE = 'b'; // (160×160)
    private static final char SMALL_THUMBNAIL = 't'; // (160×160)
    private static final char MEDIUM_THUMBNAIL = 'm'; // (320×320)
    private static final char LARGE_THUMBNAIL = 'l'; // (640×640)
    private static final char HUGE_THUMBNAIL = 'h'; // (1024×1024)

    private static Pattern pattern = Pattern.compile("(?:https?://)?(?:i\\.)?imgur\\.com/(\\w+)\\.(?:jpg|jpeg|png|gif|apng|JPG|JPEG|PNG|GIF|APNG).*");
    private static final String GATE_THUMB_BASE_URL = "thumbnail.2d-gate.org";
    private static final String GATE_THUMB_SCHEME = "https";
    private static final String GATE_THUMB_QUERY_SRC = "src";

    private static String convertImgur(String path, char format) {
        Matcher matcher = pattern.matcher(path);
        if (matcher.matches()) {
            String group = matcher.group(1);
            return path.replace(group, group + format);
        }
        GateUtils.logd(PicHostUtil.class.getSimpleName(), "Conversion failed for " + path);
        return path;
    }

    public static String convert2DDelegate(String path, int width, int height) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(GATE_THUMB_SCHEME)
                .authority(GATE_THUMB_BASE_URL)
                .appendQueryParameter(GATE_THUMB_QUERY_SRC, path)
                .appendQueryParameter("w", Integer.toString(width))
                .appendQueryParameter("h", Integer.toString(height));
        return builder.build().toString();
    }

    public static String convertSmallSquare(String path) {
        String s = convertImgur(path, SMALL_SQUARE);
        if (s.contains("pbs.twimg.com")) {
            s += ":thumb";
        }
        return s;
    }

    public static String convertBigSquare(String path) {
        return convertImgur(path, BIG_SQUARE);
    }

    public static String convertSmallThumbnail(String path) {
        return convertImgur(path, SMALL_THUMBNAIL);
    }

    public static String convertMediumThumbnail(String path) {
        return convertImgur(path, MEDIUM_THUMBNAIL);
    }

    public static String convertLargeThumbnail(String path) {
        return convertImgur(path, LARGE_THUMBNAIL);
    }

    public static String convertHugeThumbnail(String path) {
        return convertImgur(path, HUGE_THUMBNAIL);
    }
}
