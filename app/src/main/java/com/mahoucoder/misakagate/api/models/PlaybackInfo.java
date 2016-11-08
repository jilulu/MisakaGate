package com.mahoucoder.misakagate.api.models;

/**
 * Created by jamesji on 9/11/2016.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PlaybackInfo {

    @SerializedName("errors")
    @Expose
    public List<Object> errors = new ArrayList<Object>();
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("sources")
    @Expose
    public List<PlaybackSource> sources = new ArrayList<PlaybackSource>();
//    @SerializedName("tracks")
//    @Expose
//    public List<Object> tracks = new ArrayList<Object>();
    @SerializedName("cid")
    @Expose
    public String cid;

}