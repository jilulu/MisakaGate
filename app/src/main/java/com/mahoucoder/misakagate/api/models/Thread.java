package com.mahoucoder.misakagate.api.models;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jsoup.Jsoup;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Thread implements Serializable {

    @SerializedName("fid")
    @Expose
    public String fid;
    @SerializedName("tid")
    @Expose
    public String tid;
    @SerializedName("subject")
    @Expose
    public String subject;
    @SerializedName("dateline")
    @Expose
    public String dateline;
    @SerializedName("lastpost")
    @Expose
    public String lastpost;
    @SerializedName("pic")
    @Expose
    public String pic;
    @SerializedName("year")
    @Expose
    public Integer year;
    @SerializedName("season")
    @Expose
    public Object season;
    @SerializedName("extra")
    @Expose
    public String extra;

    public static final Pattern SUBJECT_MATCHER = Pattern.compile("([^\\[]+)(?:\\[([^\\]]+)\\])?(?:\\[([^\\]]+)\\])?.*");

    private String title;
    private String subtitleGroup;
    private String resolution;
    private String subtitleLanguage;
    private String episodeRange;

    private boolean subjectParsed() {
        return !(TextUtils.isEmpty(subtitleGroup) || TextUtils.isEmpty(resolution) || TextUtils.isEmpty(subtitleLanguage));
    }

    private void parseSubject() {
        Matcher matcher = SUBJECT_MATCHER.matcher(subject);
        if (matcher.matches()) {
            title = matcher.group(1);
            if (!TextUtils.isEmpty(matcher.group(2))) {
                String richInfoString = Jsoup.parse(matcher.group(2)).text();
                String[] split = richInfoString.split("@");
                if (split.length >= 3) {
                    subtitleGroup = split[0];
                    resolution = split[1];
                    subtitleLanguage = split[2];
                }
            }
            episodeRange = matcher.group(3);
        } else {
            Log.e("SubjectMatcher", "Subject not matched: \"" + subject + "\"");
            title = subject;
        }
    }

    public String getSubtitleGroup() {
        if (!subjectParsed()) {
            parseSubject();
        }
        return subtitleGroup;
    }

    public String getResolution() {
        if (!subjectParsed()) {
            parseSubject();
        }
        return resolution;
    }

    public String getSubtitleLanguage() {
        if (!subjectParsed()) {
            parseSubject();
        }
        return subtitleLanguage;
    }

    public String getTitle() {
        if (!subjectParsed()) {
            parseSubject();
        }
        return title;
    }

    public String getEpisodeRange() {
        if (!subjectParsed()) {
            parseSubject();
        }
        return episodeRange;
    }

    public String getYearAndSeason() {
        try {
            return year + " " + season;
        } catch (Exception e) {
            return "";
        }
    }
}