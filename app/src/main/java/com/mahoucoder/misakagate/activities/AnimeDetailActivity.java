package com.mahoucoder.misakagate.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.adapters.EpisodeListAdapter;
import com.mahoucoder.misakagate.api.GateAPI;
import com.mahoucoder.misakagate.api.models.Thread;
import com.mahoucoder.misakagate.utils.GateUtils;
import com.mahoucoder.misakagate.widgets.AnimeView;

import java.util.List;

import rx.Observer;

public class AnimeDetailActivity extends AppCompatActivity {

    private static final String EXTRA_KEY = "EXTRA_KEY";
    private Thread mAnime;
    private RecyclerView episodeRecycler;
    private RecyclerView.Adapter episodeAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_detail);

        mAnime = (Thread) getIntent().getSerializableExtra(EXTRA_KEY);

        AnimeView animeView = (AnimeView) findViewById(R.id.anime_view);
        animeView.bind(mAnime);

        initRecyclerView();

        fetchAdapterDataAndBindToAdapter();
    }

    private void fetchAdapterDataAndBindToAdapter() {
        GateAPI.getEpisodeList(mAnime.tid, new Observer<List<String>>() {
            @Override
            public void onNext(List<String> strings) {
                episodeAdapter = new EpisodeListAdapter(strings);
                episodeRecycler.setAdapter(episodeAdapter);
            }

            @Override
            public void onCompleted() {
                // do nothing
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
        });
    }

    private void initRecyclerView() {
        episodeRecycler = (RecyclerView) findViewById(R.id.episode_list_recycler);

        episodeRecycler.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(AnimeDetailActivity.this, 4);

        episodeRecycler.setLayoutManager(gridLayoutManager);

        float distanceInPx = GateUtils.dp2px(GateApplication.getGlobalContext(), 5);
        SimpleItemDecoration simpleItemDecoration = new SimpleItemDecoration((int) distanceInPx);
        episodeRecycler.addItemDecoration(simpleItemDecoration);
    }

    public static Intent buildLaunchIntent(Context context, Thread thread) {
        Intent intent = new Intent(context, AnimeDetailActivity.class);
        intent.putExtra(EXTRA_KEY, thread);
        return intent;
    }

    private class SimpleItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SimpleItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }
}
