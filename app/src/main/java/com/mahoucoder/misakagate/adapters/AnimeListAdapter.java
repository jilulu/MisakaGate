package com.mahoucoder.misakagate.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.api.models.Anime;

import java.util.List;

/**
 * Created by jamesji on 28/10/2016.
 */

public class AnimeListAdapter extends RecyclerView.Adapter<AnimeListAdapter.ViewHolder> {

    private List<Anime> animeList;

    public AnimeListAdapter(List<Anime> animeList) {
        this.animeList = animeList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.anime_layout, null);
        return new ViewHolder(viewGroup);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.titleText.setText(animeList.get(position).name);
    }

    @Override
    public int getItemCount() {
        return animeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleText;

        public ViewHolder(ViewGroup itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.anime_title_textview);
        }
    }
}
