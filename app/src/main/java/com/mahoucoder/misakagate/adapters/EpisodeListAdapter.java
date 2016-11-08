package com.mahoucoder.misakagate.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mahoucoder.misakagate.R;

import java.util.List;

/**
 * Created by jamesji on 30/10/2016.
 */

public class EpisodeListAdapter extends RecyclerView.Adapter<EpisodeListAdapter.ViewHolder> implements View.OnClickListener {

    private List<String> episodeURLList;

    public EpisodeListAdapter(List<String> episodeURLList) {
        this.episodeURLList = episodeURLList;
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
        holder.textView.setText("" + position);
    }

    @Override
    public int getItemCount() {
        return this.episodeURLList.size();
    }

    @Override
    public void onClick(View view) {

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.activity_anime_detail_recycler_text);
        }
    }
}
