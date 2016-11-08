package com.mahoucoder.misakagate.api.models;

/**
 * Created by jamesji on 9/11/2016.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaybackSource {

    @SerializedName("file")
    @Expose
    public String file;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("label")
    @Expose
    public String label;
    @SerializedName("default")
    @Expose
    public Boolean _default;

}
