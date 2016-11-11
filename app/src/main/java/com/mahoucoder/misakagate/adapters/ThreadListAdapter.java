package com.mahoucoder.misakagate.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.activities.AnimeDetailActivity;
import com.mahoucoder.misakagate.api.models.Thread;
import com.mahoucoder.misakagate.utils.GateUtils;
import com.mahoucoder.misakagate.utils.imageloader.ImageLoaderManager;
import com.mahoucoder.misakagate.utils.imageloader.PicHostUtil;

import java.util.List;

/**
 * Created by jamesji on 28/10/2016.
 */

public class ThreadListAdapter extends RecyclerView.Adapter<ThreadListAdapter.ViewHolder> implements View.OnClickListener {

    private List<Thread> threadList;

    public ThreadListAdapter(List<Thread> threadList) {
        this.threadList = threadList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.anime_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(viewGroup);
        viewHolder.rootView.setOnClickListener(ThreadListAdapter.this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Thread thread = threadList.get(position);
        holder.titleText.setText(thread.getTitle());
        holder.subtitleGroupText.setText(String.format("%s%s", GateApplication.getGlobalContext().getString(R.string.subtitle_group), thread.getSubtitleGroup()));
        holder.resText.setText(String.format("%s%s", GateApplication.getGlobalContext().getString(R.string.resolution), thread.getResolution()));
        holder.yearText.setText(String.format("%s", thread.getYearAndSeason()));
        holder.charsetText.setText(String.format("%s%s", GateApplication.getGlobalContext().getString(R.string.language), thread.getSubtitleLanguage()));

        holder.rootView.setTag(thread);

        int v = (int) GateUtils.dp2px(GateApplication.getGlobalContext(), 80);
        ImageLoaderManager.getInstance().getLoader().load(
                PicHostUtil.convert2DDelegate(thread.pic, v, v),
                holder.coverImageView,
                v, v
        );
    }

    @Override
    public int getItemCount() {
        return threadList.size();
    }

    @Override
    public void onClick(View view) {
        Thread anime = (Thread) view.getTag();
        Intent intent = AnimeDetailActivity.buildLaunchIntent(view.getContext(), anime);
        view.getContext().startActivity(intent);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ViewGroup rootView;
        TextView titleText, subtitleGroupText, resText, yearText, charsetText;
        ImageView coverImageView;

        ViewHolder(ViewGroup itemView) {
            super(itemView);
            rootView = (ViewGroup) itemView.findViewById(R.id.anime_root_view);
            titleText = (TextView) itemView.findViewById(R.id.anime_title_textview);
            subtitleGroupText = (TextView) itemView.findViewById(R.id.anime_subtitle_group_textview);
            resText = (TextView) itemView.findViewById(R.id.anime_resolution_textview);
            yearText = (TextView) itemView.findViewById(R.id.anime_year_textview);
            charsetText = (TextView) itemView.findViewById(R.id.anime_charset_textview);
            coverImageView = (ImageView) itemView.findViewById(R.id.anime_cover_imageview);
        }
    }
}
