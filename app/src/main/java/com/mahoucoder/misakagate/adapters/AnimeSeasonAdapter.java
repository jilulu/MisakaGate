package com.mahoucoder.misakagate.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.activities.GatePlaybackActivity;
import com.mahoucoder.misakagate.api.GateAPI;
import com.mahoucoder.misakagate.api.models.AnimeSeason;
import com.mahoucoder.misakagate.api.models.PlaybackInfo;
import com.mahoucoder.misakagate.api.models.PlaybackSource;
import com.mahoucoder.misakagate.utils.GateUtils;
import com.mahoucoder.misakagate.widgets.ChooseResolutionDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jamesji on 11/11/2016.
 */

public class AnimeSeasonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<AnimeSeason> seasons;
    public static final int TYPE_DIVIDER = 0;
    public static final int TYPE_ITEM = 1;
    public static final int ITEMS_PER_ROW = 4;
    private static final String FRAGMENT_TAG = "resolution_choose_fragment_tag";

    private List<List<AnimeSeason.PlayableAnime>> reshapedList;
    private ProgressDialog progressDialog;

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

    private View.OnClickListener episodeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            if (!(v.getTag() instanceof String)) {
                return;
            }
            String url = GateUtils.convertJsonFeed((String) v.getTag());
            progressDialog = ProgressDialog.show(v.getContext(),
                        GateApplication.getGlobalContext().getString(R.string.loading),
                        GateApplication.getGlobalContext().getString(R.string.loading_res_list));

            GateAPI.getPlaybackInfo(url, new Callback<PlaybackInfo[]>() {
                @Override
                public void onResponse(Call<PlaybackInfo[]> call, Response<PlaybackInfo[]> response) {
                    PlaybackInfo[] body = response.body();

                    final List<PlaybackSource> sources = body[0].sources;

                    CharSequence[] titles = new String[sources.size()];
                    for (int i = 0; i < sources.size(); i++) {
                        titles[i] = sources.get(i).label;
                    }

                    ChooseResolutionDialogFragment.ChooseResolutionDialogClickListener listener = new ChooseResolutionDialogFragment.ChooseResolutionDialogClickListener() {
                        public void itemClicked(int index) {
                            Intent intent = new Intent(GateApplication.getGlobalContext(), GatePlaybackActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(GatePlaybackActivity.PREFER_EXTENSION_DECODERS, false);
                            intent.setData(Uri.parse(sources.get(index).file));
                            intent.setAction(GatePlaybackActivity.ACTION_VIEW);
                            GateApplication.getGlobalContext().startActivity(intent);
                        }
                    };

                    showResolutionChooseDialog(titles, listener, v.getContext());

                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<PlaybackInfo[]> call, Throwable t) {
                    t.printStackTrace();
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(GateApplication.getGlobalContext(), GateApplication
                            .getGlobalContext().getString(R.string.loading_res_list_failed), Toast.LENGTH_SHORT).show();
                }
            });

        }
    };

    private void showResolutionChooseDialog(CharSequence[] titles, ChooseResolutionDialogFragment.ChooseResolutionDialogClickListener listener, Context context) {
        ChooseResolutionDialogFragment resolutionChooseFragment = new ChooseResolutionDialogFragment();
        resolutionChooseFragment.setData(titles, listener);
        if (context instanceof AppCompatActivity) {
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(resolutionChooseFragment, FRAGMENT_TAG)
                    .commitAllowingStateLoss();
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.episode_row, parent, false);
            return new EpisodeRowViewHolder(rootView, episodeClickListener);
        } else {
            TextView textView = new TextView(parent.getContext());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setTextColor(ContextCompat.getColor(GateApplication.getGlobalContext(), R.color.textSecondary));
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

        public EpisodeRowViewHolder(View itemView, View.OnClickListener listener) {
            super(itemView);
            View sub0 = itemView.findViewById(R.id.episode_row_0);
            View sub1 = itemView.findViewById(R.id.episode_row_1);
            View sub2 = itemView.findViewById(R.id.episode_row_2);
            View sub3 = itemView.findViewById(R.id.episode_row_3);
            views = Arrays.asList(sub0, sub1, sub2, sub3);
            for (View view : views) {
                view.setOnClickListener(listener);
            }
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
