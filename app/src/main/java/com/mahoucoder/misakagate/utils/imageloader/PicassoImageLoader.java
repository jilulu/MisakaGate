package com.mahoucoder.misakagate.utils.imageloader;

import android.net.Uri;
import android.widget.ImageView;

import com.mahoucoder.misakagate.GateApplication;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by jamesji on 10/11/2016.
 */

public class PicassoImageLoader implements IImageLoader {

    @Override
    public void load(String path, ImageView target) {
        Picasso.with(GateApplication.getGlobalContext())
                .load(path)
                .into(target);
    }

    @Override
    public void load(Uri uri, ImageView target) {
        Picasso.with(GateApplication.getGlobalContext())
                .load(uri)
                .into(target);
    }

    @Override
    public void loadImageAsync(String path, Target callback) {
        Picasso.with(GateApplication.getGlobalContext())
                .load(path)
                .into(callback);
    }

    @Override
    public void loadImageSync(Uri uri, Target callback) {
        Picasso.with(GateApplication.getGlobalContext())
                .load(uri)
                .into(callback);
    }

    @Override
    public void load(String path, ImageView target, int targetWidth, int targetHeight) {
        Picasso.with(GateApplication.getGlobalContext())
                .load(path)
                .resize(targetWidth, targetHeight)
                .into(target);

    }
}
