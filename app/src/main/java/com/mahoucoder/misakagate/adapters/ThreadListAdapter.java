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
import com.mahoucoder.misakagate.utils.PicHostUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jamesji on 28/10/2016.
 */

public class ThreadListAdapter extends RecyclerView.Adapter<ThreadListAdapter.ViewHolder> implements View.OnClickListener {

    private List<Thread> threadList;

    public ThreadListAdapter(List<Thread> threadList) {
        this.threadList = threadList;
    }

    public void setData(List<Thread> threadList) {
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Thread thread = threadList.get(position);
        holder.titleText.setText(thread.getTitle());
        holder.subtitle1.setText(String.format("%s%s%s%s%s", GateApplication.getGlobalContext().getString(R.string.subtitle_group),
                thread.getSubtitleGroup(), GateApplication.getGlobalContext().getString(R.string.middle_dot),
                GateApplication.getGlobalContext().getString(R.string.resolution), thread.getResolution()));
        holder.subtitle2.setText(String.format("%s%s%s%s", thread.getYearAndSeason(),GateApplication.getGlobalContext().getString(R.string.middle_dot),
                GateApplication.getGlobalContext().getString(R.string.language), thread.getSubtitleLanguage()));

        holder.rootView.setTag(thread);

        Picasso.with(GateApplication.getGlobalContext())
                .load(PicHostUtil.convertSmallSquare(thread.pic))
                .placeholder(R.drawable.img_downloading)
                .error(R.drawable.img_offline)
                .into(holder.coverImageView);
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
        TextView titleText, subtitle1, resText, subtitle2, charsetText;
        ImageView coverImageView;

        ViewHolder(ViewGroup itemView) {
            super(itemView);
            rootView = (ViewGroup) itemView.findViewById(R.id.anime_root_view);
            titleText = (TextView) itemView.findViewById(R.id.anime_title_textview);
            subtitle1 = (TextView) itemView.findViewById(R.id.anime_subtitle1);
            resText = (TextView) itemView.findViewById(R.id.anime_resolution_textview);
            subtitle2 = (TextView) itemView.findViewById(R.id.anime_subtitle2);
            charsetText = (TextView) itemView.findViewById(R.id.anime_charset_textview);
            coverImageView = (ImageView) itemView.findViewById(R.id.anime_cover_imageview);
        }
    }
}
