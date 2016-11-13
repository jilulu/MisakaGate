package com.mahoucoder.misakagate.api.models;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.data.Animations;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.jsoup.Jsoup;

import java.io.Serializable;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
@Table(database = Animations.class)
public class Thread extends BaseModel implements Serializable {

    @SerializedName("fid") @Expose @Column
    public String fid;
    @SerializedName("tid") @Column @Expose @PrimaryKey @Index
    public String tid;
    @Column @SerializedName("subject") @Expose
    public String subject;
    @Column @SerializedName("dateline") @Expose
    public String dateline;
    @Column @SerializedName("lastpost") @Expose
    public String lastpost;
    @Column @SerializedName("pic") @Expose
    public String pic;
    @Column @SerializedName("year") @Expose
    public Integer year;
    @Column @SerializedName("season") @Expose
    public String season;
    @Column @SerializedName("extra") @Expose
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
            return (year == null ? GateApplication.getGlobalContext().getString(R.string.some_year) : year.toString())
                    + (TextUtils.isEmpty(season) ? "" : " " + season);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH,
                "{\"fid\":\"%s\",\"tid\":\"%s\",\"subject\":\"%s\",\"dateline\":\"%s\",\"lastpost\":" +
                        "\"%s\",\"pic\":\"%s\",\"year\":\"%d\",\"season\":\"%s\",\"extra\":\"%s\"}",
                fid, tid, subject, dateline, lastpost, pic, year, season, extra);
    }
}