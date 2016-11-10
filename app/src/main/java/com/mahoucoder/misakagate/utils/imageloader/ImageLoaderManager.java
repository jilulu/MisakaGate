package com.mahoucoder.misakagate.utils.imageloader;

/**
 * Created by jamesji on 10/11/2016.
 */

public class ImageLoaderManager {
    private static volatile IImageLoader mImageLoader;
    private static volatile ImageLoaderManager mInstance;

    private ImageLoaderManager() {
        mImageLoader = new PicassoImageLoader();
    }

    public static ImageLoaderManager getInstance() {
        if (mInstance == null) {
            synchronized (ImageLoaderManager.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoaderManager();
                }
            }
        }
        return mInstance;
    }

    public IImageLoader getLoader() {
        return mImageLoader;
    }


}
