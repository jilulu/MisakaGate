package com.mahoucoder.misakagate.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AnimeListCache {

    @SerializedName("timestamp")
    @Expose
    public Integer timestamp;
    @SerializedName("threads")
    @Expose
    public List<Thread> threads = new ArrayList<Thread>();

}
