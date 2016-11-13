package com.mahoucoder.misakagate.widgets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.activities.AnimeDetailActivity;
import com.mahoucoder.misakagate.api.models.Favorite;
import com.mahoucoder.misakagate.api.models.Favorite_Table;
import com.mahoucoder.misakagate.api.models.Thread;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.Picasso;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.mahoucoder.misakagate.R.string.favorite;

/**
 * Created by jamesji on 8/11/2016.
 */

public class AnimeView extends LinearLayout implements View.OnClickListener {
    private ImageView mImageView, mFavoriteAnimeLayoutIcon;
    private TextView mTitle, mSubtitleGroup, mResolution, mYearAndSeason, mLanguage, mFavoriteAnimeLayoutTV;
    private Thread mAnime;
    private boolean shouldJump;

    public AnimeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.view_anime, this);

        ViewGroup infoGroup = (ViewGroup) findViewById(R.id.anime_info_group);
        mImageView = (ImageView) infoGroup.findViewById(R.id.anime_cover_imageview);
        mTitle = (TextView) infoGroup.findViewById(R.id.anime_title_textview);
        mSubtitleGroup = (TextView) infoGroup.findViewById(R.id.anime_subtitle1);
        mResolution = (TextView) infoGroup.findViewById(R.id.anime_resolution_textview);
        mYearAndSeason = (TextView) infoGroup.findViewById(R.id.anime_subtitle2);
        mLanguage = (TextView) infoGroup.findViewById(R.id.anime_charset_textview);
        ViewGroup mFavoriteAnimeLayout = (ViewGroup) findViewById(R.id.anime_favorite_group);
        mFavoriteAnimeLayoutIcon = (ImageView) mFavoriteAnimeLayout.findViewById(R.id.anime_favorite_group_icon);
        mFavoriteAnimeLayoutTV = (TextView) mFavoriteAnimeLayout.findViewById(R.id.anime_favorite_group_text);

        infoGroup.setOnClickListener(AnimeView.this);
        mFavoriteAnimeLayout.setOnClickListener(AnimeView.this);
    }

    public void bind(Thread anime) {
        this.mAnime = anime;
        Picasso.with(GateApplication.getGlobalContext())
                .load(anime.pic)
                .into(mImageView);
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

        updateFavLayout();
    }

    private void updateFavLayout() {
        if (isAnimeFavorited()) {
            mFavoriteAnimeLayoutTV.setText(GateApplication.getGlobalContext().getString(R.string.unfavorite));
            mFavoriteAnimeLayoutIcon.setImageResource(R.drawable.ic_favorited_red);
        } else {
            mFavoriteAnimeLayoutTV.setText(GateApplication.getGlobalContext().getString(favorite));
            mFavoriteAnimeLayoutIcon.setImageResource(R.drawable.ic_not_favorited_red);
        }
    }

    public void setShouldJumpToAnimeDetailsOnClick(boolean jump) {
        this.shouldJump = jump;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.anime_favorite_group) {
            final String tid = mAnime.tid;
            Observable.create(new Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> subscriber) {
                    final Favorite favorite = new Favorite();
                    favorite.setTid(mAnime.tid);
                    if (isAnimeFavorited()) {
                        SQLite.delete().from(Favorite.class)
                                .where(Favorite_Table.tid.eq(mAnime.tid))
                                .execute();
                        subscriber.onNext(false);
                    } else {
                        favorite.save();
                        subscriber.onNext(true);
                    }

                }
            }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Boolean>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }

                @Override
                public void onNext(Boolean aBoolean) {
                    updateFavLayout();
                    if (mListener != null) {
                        mListener.onSubscriptionStatusChanged(aBoolean, tid);
                    }
                }
            });
        } else if (v.getId() == R.id.anime_info_group && shouldJump) {
            Intent intent = AnimeDetailActivity.buildLaunchIntent(GateApplication.getGlobalContext(), mAnime);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            GateApplication.getGlobalContext().startActivity(intent);
        }
    }

    private boolean isAnimeFavorited() {
        return SQLite.selectCountOf().from(Favorite.class)
                .where(Favorite_Table.tid.eq(mAnime.tid)).count() == 1;
    }

    private SubscriptionStatusChangeListener mListener;
    public interface SubscriptionStatusChangeListener {
        void onSubscriptionStatusChanged(boolean nowSubscribed, String tid);
    }
    public void setSubscriptionStatusChangeListener(SubscriptionStatusChangeListener listener) {
        this.mListener = listener;
    }
}
