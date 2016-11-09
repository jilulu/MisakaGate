package com.mahoucoder.misakagate.utils;

import android.text.TextUtils;

import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.api.models.AnimeSeason;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jamesji on 10/11/2016.
 */

public class GateParser {

    // Magic number, if at top level there are more than this number of elements then consider all
    // elements to be episodes, and seasons otherwise.
    public static final int ANIME_SEASON_THRESHOLD = 5;

    public static List<AnimeSeason> parseNodeIntoAnimeSeasonList(Element rootNode) {
        Elements elements = rootNode.select("ul > li > a");

        List<AnimeSeason> animeSeasonList;
        if (rootNode.select("ul").get(0).select("li > a").size() < ANIME_SEASON_THRESHOLD) {
            animeSeasonList = parseWithMultipleSeasonStrategy(rootNode, elements);
        } else {
            String id = rootNode.id();
            id = id.replace("tabs", "tab");
            AnimeSeason season = new AnimeSeason();
            season.setSeasonClassified(false);
            season.setSeasonTitle(GateApplication.getGlobalContext().getString(R.string.season_not_classified));
            for (Element element : elements) {
                try {
                    //noinspection ResultOfMethodCallIgnored
                    Integer.parseInt(element.text());
                } catch (NumberFormatException e) {
                    continue;
                }
                String href = element.attr("href");
                if (!TextUtils.isEmpty(href) && href.contains(id)) {
                    AnimeSeason.PlayableAnime anime = new AnimeSeason.PlayableAnime(element.text());
                    String episodeId = href.substring(href.indexOf("#") + 1);
                    Element addressDiv = rootNode.getElementById(episodeId);
                    if (addressDiv == null) {
                        continue;
                    }
                    String address = addressDiv.select("[href*=\"embed.2d-gate.org\"]").get(0).attr("href");
                    anime.setPlaybackAddress(address);
                    season.playableAnimeList.add(anime);
                }
            }
            animeSeasonList = Collections.singletonList(season);
        }
        return animeSeasonList;
    }

    private static List<AnimeSeason> parseWithMultipleSeasonStrategy(Element rootNode, Elements elements) {
        String id = rootNode.id();
        id = id.replace("tabs", "tab");
        List<AnimeSeason> seasonList = new ArrayList<>();
        for (Element element : elements) {
            String href = element.attr("href");
            if (!TextUtils.isEmpty(href) && href.contains(id)) {
                AnimeSeason animeSeason = new AnimeSeason();
                animeSeason.setSeasonTitle(element.text());
                animeSeason.setTabId(href.substring(href.indexOf(id)));
                animeSeason.setSeasonClassified(true);
                seasonList.add(animeSeason);
            }
        }
        if (seasonList.size() == 0) {
            AnimeSeason season = new AnimeSeason();
            season.setSeasonTitle(GateApplication.getGlobalContext().getString(R.string.season_not_classified));
            season.setSeasonClassified(false);
            seasonList.add(season);
        }

        for (AnimeSeason season : seasonList) {
            Element episodeDetailNode = rootNode.getElementById(season.getTabId());
            if (episodeDetailNode == null) {
                continue;
            }
            Elements episodeTitles = episodeDetailNode.select("ul > li > a");
            if (episodeTitles == null || episodeTitles.size() == 0) {
                Elements image = episodeDetailNode.select("a > img");
                if (image != null && image.size() > 0) {
                    season.coverURL = image.get(0).attr("src");
                }
                continue;
            }
            for (Element episodeTitle : episodeTitles) {
                AnimeSeason.PlayableAnime playableAnime = new AnimeSeason.PlayableAnime(episodeTitle.text());
                String href = episodeTitle.attr("href");
                playableAnime.tabId = href.substring(href.indexOf("#") + 1);
                season.playableAnimeList.add(playableAnime);
            }
            for (AnimeSeason.PlayableAnime playableAnime : season.playableAnimeList) {
                Element episodeAddressNode = episodeDetailNode.getElementById(playableAnime.tabId);
                String href = episodeAddressNode.child(0).attr("href");
                playableAnime.setPlaybackAddress(href);
            }
        }

        return seasonList;
    }
}
