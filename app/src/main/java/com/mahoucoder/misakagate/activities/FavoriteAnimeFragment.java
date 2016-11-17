package com.mahoucoder.misakagate.activities;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.adapters.FavoriteAnimeAdapter;
import com.mahoucoder.misakagate.api.models.Favorite;
import com.mahoucoder.misakagate.utils.GateUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * Created by jamesji on 13/11/2016.
 */

public class FavoriteAnimeFragment extends Fragment {
    private RecyclerView favAnimeRecycler;
    private ViewGroup noFavHint;
    private RecyclerView.Adapter favAnimeAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
        favAnimeRecycler = (RecyclerView) rootView.findViewById(R.id.fragment_favorite_recycler);
        favAnimeAdapter = new FavoriteAnimeAdapter();
        favAnimeRecycler.setLayoutManager(new LinearLayoutManager(container.getContext()));
        favAnimeRecycler.setAdapter(favAnimeAdapter);
        favAnimeRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int margin = (int) GateUtils.dp2px(GateApplication.getGlobalContext(), 10);
                outRect.top = outRect.left = outRect.right = margin;
                if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
                    outRect.bottom = margin;
                }
            }
        });
        noFavHint = (ViewGroup) rootView.findViewById(R.id.fragment_favorite_hint);
        noFavHint.setVisibility(View.GONE);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            long count = SQLite.selectCountOf().from(Favorite.class).count();
            if (count == 0L) {
                noFavHint.setVisibility(View.VISIBLE);
                favAnimeRecycler.setVisibility(View.INVISIBLE);
            } else {
                noFavHint.setVisibility(View.INVISIBLE);
                favAnimeRecycler.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            // ignored
        }
    }
}
