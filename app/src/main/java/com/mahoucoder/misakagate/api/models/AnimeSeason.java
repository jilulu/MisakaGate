package com.mahoucoder.misakagate.api.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jamesji on 9/11/2016.
 */

public class AnimeSeason implements Serializable {

    private String seasonTitle;

    public List<PlayableAnime> playableAnimeList = new ArrayList<>();

    public String getSeasonTitle() {
        return seasonTitle;
    }

    public void setSeasonTitle(String seasonTitle) {
        this.seasonTitle = seasonTitle;
    }

    @Override
    public String toString() {
        return String.format("{\"seasonTitle\":\"%s\",\"playableAnimeList\":", seasonTitle) + playableAnimeList.toString() + "}";
    }

    public static class PlayableAnime {
        private String title;
        private String playbackAddress;

        public PlayableAnime(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public String getPlaybackAddress() {
            return playbackAddress;
        }

        public void setPlaybackAddress(String playbackAddress) {
            this.playbackAddress = playbackAddress;
        }

        @Override
        public String toString() {
            return String.format("{\"title\":\"%s\",\"playbackAddress\":\"%s\"}", title, playbackAddress);
        }
    }
}