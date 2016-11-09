package com.mahoucoder.misakagate.api.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jamesji on 9/11/2016.
 */

public class AnimeSeason implements Serializable {

    private String seasonTitle;
    private String tabId;
    private boolean isSeasonClassified;
    public String coverURL;

    public List<PlayableAnime> playableAnimeList = new ArrayList<>();

    public String getSeasonTitle() {
        return seasonTitle;
    }

    public void setSeasonTitle(String seasonTitle) {
        this.seasonTitle = seasonTitle;
    }

    public void setSeasonClassified(boolean seasonClassified) {
        isSeasonClassified = seasonClassified;
    }

    public boolean isSeasonClassified() {
        return isSeasonClassified;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public static class PlayableAnime {
        private String title;
        private String playbackAddress;
        public String tabId;

        public PlayableAnime(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPlaybackAddress() {
            return playbackAddress;
        }

        public void setPlaybackAddress(String playbackAddress) {
            this.playbackAddress = playbackAddress;
        }
    }
}
