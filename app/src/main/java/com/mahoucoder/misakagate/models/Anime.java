package com.mahoucoder.misakagate.models;

/**
 * Created by jamesji on 28/10/2016.
 */

public class Anime {
    private String name;
    private String subtitleGroup;
    private String resolution;
    private String subtitleLanguate;
    private String numEpisodes;
    private int year;
    private String seasonInYear;
    private String listingTime;
    private String updatingTime;

    public Anime(String name, String subtitleGroup, String resolution, String subtitleLanguate, String numEpisodes, int year, String seasonInYear, String listingTime, String updatingTime) {
        this.name = name;
        this.subtitleGroup = subtitleGroup;
        this.resolution = resolution;
        this.subtitleLanguate = subtitleLanguate;
        this.numEpisodes = numEpisodes;
        this.year = year;
        this.seasonInYear = seasonInYear;
        this.listingTime = listingTime;
        this.updatingTime = updatingTime;
    }
}
