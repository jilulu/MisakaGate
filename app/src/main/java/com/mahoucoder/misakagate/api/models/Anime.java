package com.mahoucoder.misakagate.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by jamesji on 28/10/2016.
 */

public class Anime {
    @SerializedName("tid")
    @Expose
    public Integer tid;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("subtitle_group")
    @Expose
    public String subtitleGroup;
    @SerializedName("resolution")
    @Expose
    public String resolution;
    @SerializedName("subtitle_language")
    @Expose
    public String subtitleLanguage;
    @SerializedName("num_episodes")
    @Expose
    public String numEpisodes;
    @SerializedName("year")
    @Expose
    public Integer year;
    @SerializedName("season")
    @Expose
    public String season;
    @SerializedName("posted")
    @Expose
    public String posted;
    @SerializedName("updated")
    @Expose
    public String updated;

}
