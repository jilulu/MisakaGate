package com.mahoucoder.misakagate.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.adapters.EpisodeListAdapter;
import com.mahoucoder.misakagate.api.GateAPI;
import com.mahoucoder.misakagate.api.models.AnimeSeason;
import com.mahoucoder.misakagate.api.models.Thread;
import com.mahoucoder.misakagate.utils.GateUtils;
import com.mahoucoder.misakagate.widgets.AnimeView;

import java.util.List;

import rx.Observer;

public class AnimeDetailActivity extends BaseActivity {

    private static final String EXTRA_KEY = "EXTRA_KEY";
    private Thread mAnime;
    private RecyclerView episodeRecycler;
    private RecyclerView.Adapter episodeAdapter;
    AnimeView animeView;
    public static final int SPAN_COUNT = 4;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_detail);

        initView();

        bindViewWithData();

        fetchAdapterDataAndBindToAdapter();

        GateAPI.getEpisodeStructure(mAnime.tid, new Observer<List<AnimeSeason>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(List<AnimeSeason> animeSeasons) {
                System.out.println(animeSeasons);
            }
        });
    }

    private void initView() {
        animeView = (AnimeView) findViewById(R.id.anime_view);
        initRecyclerView();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void bindViewWithData() {
        mAnime = (Thread) getIntent().getSerializableExtra(EXTRA_KEY);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mAnime.getTitle());
        } else if (getActionBar() != null) {
            getActionBar().setTitle(mAnime.getTitle());
        }

        animeView.bind(mAnime);
    }

    private void fetchAdapterDataAndBindToAdapter() {
        GateAPI.getEpisodeList(mAnime.tid, new Observer<List<String>>() {
            @Override
            public void onNext(List<String> strings) {
                episodeAdapter = new EpisodeListAdapter(strings);
                episodeRecycler.setAdapter(episodeAdapter);
                if (episodeAdapter instanceof EpisodeListAdapter) {
                    ((EpisodeListAdapter) episodeAdapter).setData(mAnime);
                }
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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(AnimeDetailActivity.this, SPAN_COUNT);

        episodeRecycler.setLayoutManager(gridLayoutManager);

        float distanceInPx = GateUtils.dp2px(GateApplication.getGlobalContext(), 3);
        SimpleItemDecoration simpleItemDecoration = new SimpleItemDecoration((int) distanceInPx);
        episodeRecycler.addItemDecoration(simpleItemDecoration);
    }

    public static Intent buildLaunchIntent(Context context, Thread thread) {
        Intent intent = new Intent(context, AnimeDetailActivity.class);
        intent.putExtra(EXTRA_KEY, thread);
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class SimpleItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        SimpleItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = parent.getChildAdapterPosition(view) % SPAN_COUNT == 0 ? space : 0;
            outRect.right = space;
//            outRect.bottom = space;
            outRect.top = space;
        }
    }
}
