package com.mahoucoder.misakagate.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.adapters.AnimeSeasonAdapter;
import com.mahoucoder.misakagate.api.GateAPI;
import com.mahoucoder.misakagate.api.models.AnimeSeason;
import com.mahoucoder.misakagate.api.models.Thread;
import com.mahoucoder.misakagate.widgets.AnimeView;

import java.util.List;

import rx.Observer;

public class AnimeDetailActivity extends BaseActivity {

    private static final String EXTRA_KEY = "EXTRA_KEY";
    private Thread mAnime;
    private RecyclerView episodeRecycler;
    private RecyclerView.Adapter adapter;
    AnimeView animeView;
    private ProgressBar progressBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_detail);

        initView();

        bindViewWithData();

        fetchAdapterDataAndBindToAdapter();
    }

    private void initView() {
        animeView = (AnimeView) findViewById(R.id.anime_view);
        initRecyclerView();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        progressBar = (ProgressBar) findViewById(R.id.episode_list_progress_bar);
        Toolbar actionBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void bindViewWithData() {
        mAnime = (Thread) getIntent().getSerializableExtra(EXTRA_KEY);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mAnime.getTitle());
        }

        animeView.bind(mAnime);
    }

    private void fetchAdapterDataAndBindToAdapter() {
        GateAPI.getAnimeSeasons(mAnime.tid, new Observer<List<AnimeSeason>>() {
            @Override
            public void onNext(List<AnimeSeason> animeSeasons) {
                adapter = new AnimeSeasonAdapter(animeSeasons);
                episodeRecycler.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCompleted() {

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

        episodeRecycler.setLayoutManager(new LinearLayoutManager(AnimeDetailActivity.this));
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
}
