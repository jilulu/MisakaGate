package com.mahoucoder.misakagate.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.api.models.Anime;
import com.mahoucoder.misakagate.utils.GateUtils;
import com.squareup.picasso.Picasso;

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
        Anime anime = animeList.get(position);
        holder.titleText.setText(anime.name);
        holder.subtitleGroupText.setText(String.format("%s%s", GateApplication.getGlobalContext().getString(R.string.subtitle_group), anime.subtitleGroup));
        holder.resText.setText(String.format("%s%s", GateApplication.getGlobalContext().getString(R.string.resolution), anime.resolution));
        holder.yearText.setText(String.format("%s%s", anime.year.toString(), GateApplication.getGlobalContext().getString(R.string.year)));
        holder.charsetText.setText(String.format("%s%s", GateApplication.getGlobalContext().getString(R.string.language), anime.subtitleLanguage));

        int v = (int) GateUtils.dp2px(GateApplication.getGlobalContext(), 80);
        Picasso.with(GateApplication.getGlobalContext())
                .load(anime.pic)
                .centerCrop()
                .resize(v, v)
                .into(holder.coverImageView);
    }

    @Override
    public int getItemCount() {
        return animeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleText, subtitleGroupText, resText, yearText, charsetText;
        ImageView coverImageView;

        public ViewHolder(ViewGroup itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.anime_title_textview);
            subtitleGroupText = (TextView) itemView.findViewById(R.id.anime_subtitle_group_textview);
            resText = (TextView) itemView.findViewById(R.id.anime_resolution_textview);
            yearText = (TextView) itemView.findViewById(R.id.anime_year_textview);
            charsetText = (TextView) itemView.findViewById(R.id.anime_charset_textview);
            coverImageView = (ImageView) itemView.findViewById(R.id.anime_cover_imageview);
        }
    }
}
