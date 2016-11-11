package com.mahoucoder.misakagate.utils;

import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.api.models.AnimeSeason;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jamesji on 10/11/2016.
 */

public class GateParser {

    public static final String ONE_SEASON_TEXT = GateApplication.getGlobalContext().getString(R.string.one_season_in_total);

    public static List<AnimeSeason> parseNodeIntoAnimeSeasonList(Element rootNode) {
        try {
            return doParse(rootNode);
        } catch (Throwable t) {
            return doNaiveParse(rootNode);
        }
    }

    private static List<AnimeSeason> doNaiveParse(Element rootNode) {
        String rawText = rootNode.html();
        Pattern pattern = Pattern.compile("(https?://embed\\.2d-gate\\.org/[^\"]+)");
        Matcher matcher = pattern.matcher(rawText);
        AnimeSeason onlySeason = new AnimeSeason();
        onlySeason.setSeasonTitle(GateApplication.getGlobalContext().getString(R.string.one_season_in_total));
        int index = 0;
        while (matcher.find()) {
            String url = matcher.group();
            AnimeSeason.PlayableAnime anime = new AnimeSeason.PlayableAnime(Integer.toString(++index));
            anime.setPlaybackAddress(url);
            onlySeason.playableAnimeList.add(anime);
        }
        return Collections.singletonList(onlySeason);
    }

    private static List<AnimeSeason> doParse(Element rootNode) {
        Elements divs = rootNode.select("div[style=\"display:none\"]");
        boolean multipleSeasons = divs.size() > 1;
        Elements topLevelIndexingAnchors = rootNode.select("ul").get(0).select("li > a");
        if (!multipleSeasons) {
            AnimeSeason season = parseSingleSeason(rootNode, ONE_SEASON_TEXT);
            return Collections.singletonList(season);
        } else {
            List<AnimeSeason> seasons = new ArrayList<>();
            for (Element seasonLink : topLevelIndexingAnchors) {
                try {
                    String href = seasonLink.attr("href");
                    String divId = href.substring(href.indexOf("#") + 1);
                    Element innerRootNode = rootNode.getElementById(divId).child(0);
                    AnimeSeason season = parseSingleSeason(innerRootNode, seasonLink.text());
                    seasons.add(season);
                } catch (Throwable t) {
                    // ignored
                }
            }
            return seasons;
        }
    }

    public static AnimeSeason parseSingleSeason(Element rootNode, String seasonTitle) {
        Elements links = rootNode.select("ul").get(0).select("li > a");
        AnimeSeason season = new AnimeSeason();
        season.setSeasonTitle(seasonTitle);
        for (Element link : links) {
            AnimeSeason.PlayableAnime anime = new AnimeSeason.PlayableAnime(link.text());
            String href = link.attr("href");
            String divId = href.substring(href.indexOf("#") + 1);
            Element linkDiv = rootNode.getElementById(divId);
            try {
                String url;
                if (linkDiv.child(0).hasAttr("href")) {
                    url = linkDiv.child(0).attr("href");
                } else {
                    url = linkDiv.select("[href*=\"embed.2d-gate.org\"]").get(0).attr("href");
                }
                anime.setPlaybackAddress(url);
                season.playableAnimeList.add(anime);
            } catch (Throwable t) {
                // ignored
            }
        }
        return season;
    }


}
