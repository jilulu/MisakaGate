package com.mahoucoder.misakagate.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.activities.PlaybackActivity;
import com.mahoucoder.misakagate.api.models.Thread;

import java.util.List;
import java.util.Locale;

/**
 * Created by jamesji on 30/10/2016.
 */

public class EpisodeListAdapter extends RecyclerView.Adapter<EpisodeListAdapter.ViewHolder> implements View.OnClickListener {

    private List<String> episodeURLList;
    private Thread anime;

    public EpisodeListAdapter(List<String> episodeURLList) {
        this.episodeURLList = episodeURLList;
    }

    public void setData(Thread anime) {
        this.anime = anime;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.episode_card, null);
        ViewHolder viewHolder = new ViewHolder(rootView);
        viewHolder.itemView.setOnClickListener(EpisodeListAdapter.this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(String.format(Locale.ENGLISH, "%d", position + 1));
        holder.itemView.setTag(episodeURLList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.episodeURLList.size();
    }

    @Override
    public void onClick(View view) {
        Intent intent = PlaybackActivity.buildLaunchIntent(view.getContext(), anime, (String) view.getTag());
        view.getContext().startActivity(intent);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.activity_anime_detail_recycler_text);
        }
    }
}
