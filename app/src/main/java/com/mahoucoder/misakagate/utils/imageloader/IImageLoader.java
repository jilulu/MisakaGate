package com.mahoucoder.misakagate.utils.imageloader;

import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Target;

/**
 * Created by jamesji on 10/11/2016.
 */

public interface IImageLoader {
    void load(String path, ImageView target);

    void load(Uri uri, ImageView target);

    void loadImageAsync(String path, Target callback);

    void loadImageSync(Uri uri, Target callback);

    void load(String path, ImageView target, int targetWidth, int targetHeight);
}
