package com.mahoucoder.misakagate.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.mahoucoder.misakagate.api.models.Thread;
import com.mahoucoder.misakagate.api.models.Thread_Table;
import com.mahoucoder.misakagate.data.Favorite;
import com.mahoucoder.misakagate.data.Favorite_Table;
import com.mahoucoder.misakagate.widgets.AnimeView;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * Created by jamesji on 13/11/2016.
 */

public class FavoriteAnimeAdapter extends RecyclerView.Adapter<FavoriteAnimeAdapter.ViewHolder> implements AnimeView.SubscriptionStatusChangeListener {
    private List<Thread> mData;

    public FavoriteAnimeAdapter() {
        mData = SQLite.select().from(Thread.class)
                .innerJoin(Favorite.class)
                .on(Thread_Table.tid.withTable().eq(Favorite_Table.tid.withTable()))
                .queryList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AnimeView view = new AnimeView(parent.getContext(), null);
        view.setShouldJumpToAnimeDetailsOnClick(true);
        view.setSubscriptionStatusChangeListener(this);
        CardView container = new CardView(parent.getContext());
        container.addView(view);
        return new ViewHolder(container);
    }

    @Override
    public void onBindViewHolder(FavoriteAnimeAdapter.ViewHolder holder, int position) {
        holder.animeView.bind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onSubscriptionStatusChanged(boolean nowSubscribed, String tid) {
        if (!nowSubscribed) {
            for (Thread thread : mData) {
                if (thread.tid.equals(tid)) {
                    int i = mData.indexOf(thread);
                    mData.remove(thread);
                    notifyItemRemoved(i);
                }
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        AnimeView animeView;
        ViewHolder(View itemView) {
            super(itemView);
            if (itemView instanceof ViewGroup) {
                animeView = (AnimeView) ((ViewGroup) itemView).getChildAt(0);
            }
        }
    }
}
