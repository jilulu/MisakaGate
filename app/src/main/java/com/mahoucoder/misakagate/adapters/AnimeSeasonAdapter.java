package com.mahoucoder.misakagate.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.api.models.AnimeSeason;
import com.mahoucoder.misakagate.utils.GateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jamesji on 11/11/2016.
 */

public class AnimeSeasonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<AnimeSeason> seasons;
    public static final int TYPE_DIVIDER = 0;
    public static final int TYPE_ITEM = 1;
    public static final int ITEMS_PER_ROW = 4;
    private List<List<AnimeSeason.PlayableAnime>> reshapedList;

    public AnimeSeasonAdapter(List<AnimeSeason> seasons) {
        this.seasons = seasons;
        reshapeList(seasons);
    }

    private void reshapeList(List<AnimeSeason> seasons) {
        reshapedList = new ArrayList<>();
        for (AnimeSeason season : seasons) {
            List<List<AnimeSeason.PlayableAnime>> listOfList = new ArrayList<>();
            for (int i = 0; i < season.playableAnimeList.size() / ITEMS_PER_ROW + 1; i++) {
                int remainingItems = season.playableAnimeList.size() - i * ITEMS_PER_ROW > 4 ? 4 : season.playableAnimeList.size() - i * ITEMS_PER_ROW;
                List<AnimeSeason.PlayableAnime> list = new ArrayList<>();
                for (int j = 0; j < remainingItems; j++) {
                    list.add(season.playableAnimeList.get(i * ITEMS_PER_ROW + j));
                }
                listOfList.add(list);
            }
            reshapedList.add(null);
            reshapedList.addAll(listOfList);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.episode_row, parent, false);
            return new EpisodeRowViewHolder(rootView);
        } else {
            TextView textView = new TextView(parent.getContext());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setTextColor(GateApplication.getGlobalContext().getResources().getColor(R.color.textSecondary));
            int padding = (int) GateUtils.dp2px(GateApplication.getGlobalContext(), 10);
            textView.setPadding(padding * 2, padding, padding, padding);
            return new TextViewHolder(textView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_DIVIDER) {
            TextViewHolder viewHolder = (TextViewHolder) holder;
            int index = 0;
            for (int i = 0; i < position; i++) {
                if (reshapedList.get(i) == null) {
                    index += 1;
                }
            }
            String seasonTitle = seasons.get(index).getSeasonTitle();
            viewHolder.textView.setText(seasonTitle);
        } else {
            EpisodeRowViewHolder viewHolder = (EpisodeRowViewHolder) holder;
            List<AnimeSeason.PlayableAnime> playableAnimes = reshapedList.get(position);
            for (int i = 0; i < ITEMS_PER_ROW; i++) {
                View view = viewHolder.views.get(i);
                TextView textView = viewHolder.textViews.get(i);
                if (i < playableAnimes.size()) {
                    AnimeSeason.PlayableAnime playableAnime = playableAnimes.get(i);
                    view.setTag(playableAnime.getPlaybackAddress());
                    textView.setText(playableAnime.getTitle());
                    view.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return reshapedList.get(position) == null ? TYPE_DIVIDER : TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return reshapedList.size();
    }

    public static class EpisodeRowViewHolder extends RecyclerView.ViewHolder {
        List<View> views;
        List<TextView> textViews;

        public EpisodeRowViewHolder(View itemView) {
            super(itemView);
            View sub0 = itemView.findViewById(R.id.episode_row_0);
            View sub1 = itemView.findViewById(R.id.episode_row_1);
            View sub2 = itemView.findViewById(R.id.episode_row_2);
            View sub3 = itemView.findViewById(R.id.episode_row_3);
            views = Arrays.asList(sub0, sub1, sub2, sub3);
            TextView sub0Text = (TextView) sub0.findViewById(R.id.activity_anime_detail_recycler_text);
            TextView sub1Text = (TextView) sub1.findViewById(R.id.activity_anime_detail_recycler_text);
            TextView sub2Text = (TextView) sub2.findViewById(R.id.activity_anime_detail_recycler_text);
            TextView sub3Text = (TextView) sub3.findViewById(R.id.activity_anime_detail_recycler_text);
            textViews = Arrays.asList(sub0Text, sub1Text, sub2Text, sub3Text);
        }
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public TextViewHolder(TextView itemView) {
            super(itemView);
            textView = itemView;
        }
    }

}
