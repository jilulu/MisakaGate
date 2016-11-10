package com.mahoucoder.misakagate.widgets;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.api.models.Thread;
import com.mahoucoder.misakagate.utils.imageloader.ImageLoaderManager;

/**
 * Created by jamesji on 8/11/2016.
 */

public class AnimeView extends LinearLayout {
    private ImageView mImageView;
    private TextView mTitle, mSubtitleGroup, mResolution, mYearAndSeason, mLanguage;

    public AnimeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.view_anime, this);

        mImageView = (ImageView) findViewById(R.id.anime_cover_imageview);
        mTitle = (TextView) findViewById(R.id.anime_title_textview);
        mSubtitleGroup = (TextView) findViewById(R.id.anime_subtitle_group_textview);
        mResolution = (TextView) findViewById(R.id.anime_resolution_textview);
        mYearAndSeason = (TextView) findViewById(R.id.anime_year_textview);
        mLanguage = (TextView) findViewById(R.id.anime_charset_textview);
    }

    public void bind(Thread anime) {
        ImageLoaderManager.getInstance().getLoader().load(anime.pic, mImageView);
        mTitle.setText(anime.getTitle());

        ForegroundColorSpan spanColor = new ForegroundColorSpan(Color.DKGRAY);

        String subtitleGroupLocaledName = GateApplication.getGlobalContext().getString(R.string.subtitle_group);
        SpannableString subtitleGroupSpannable = new SpannableString(subtitleGroupLocaledName + anime.getSubtitleGroup());
        subtitleGroupSpannable.setSpan(spanColor, 0, subtitleGroupLocaledName.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mSubtitleGroup.setText(subtitleGroupSpannable);

        String resolutionLocaledName = GateApplication.getGlobalContext().getString(R.string.resolution);
        SpannableString resolutionSpannable = new SpannableString(resolutionLocaledName + anime.getResolution());
        resolutionSpannable.setSpan(spanColor, 0, resolutionLocaledName.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mResolution.setText(resolutionSpannable);

        String yearLocaledName = GateApplication.getGlobalContext().getString(R.string.year);
        SpannableString yearSpannable = new SpannableString(yearLocaledName + anime.year);
        yearSpannable.setSpan(spanColor, 0, yearLocaledName.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mYearAndSeason.setText(yearSpannable);

        String languageLocaledName = GateApplication.getGlobalContext().getString(R.string.language);
        SpannableString languageSpannableString = new SpannableString(languageLocaledName + anime.getSubtitleLanguage());
        languageSpannableString.setSpan(spanColor, 0, languageLocaledName.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mLanguage.setText(languageSpannableString);
    }
}
