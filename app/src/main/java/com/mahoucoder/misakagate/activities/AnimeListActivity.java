package com.mahoucoder.misakagate.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.adapters.ThreadListAdapter;
import com.mahoucoder.misakagate.api.GateAPI;
import com.mahoucoder.misakagate.api.models.AnimeListCache;
import com.mahoucoder.misakagate.utils.GateUtils;
import com.mahoucoder.misakagate.widgets.DividerItemDecoration;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimeListActivity extends BaseActivity {

    private RecyclerView animeListRecyclerView;
    private RecyclerView.Adapter animeListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecyclerView();

        fetchAdapterDataAndBindToAdapter();
    }

    private void fetchAdapterDataAndBindToAdapter() {
        GateAPI.getAnimeList(new Callback<AnimeListCache>() {
            @Override
            public void onResponse(Call<AnimeListCache> call, Response<AnimeListCache> response) {
                AnimeListCache animeListCache = response.body();
                GateUtils.logd(AnimeListActivity.class.getSimpleName(), String.format(Locale.ENGLISH, "Got %d threads from server. ", animeListCache.threads.size()));
                animeListAdapter = new ThreadListAdapter(animeListCache.threads);
                animeListRecyclerView.setAdapter(animeListAdapter);
                animeListRecyclerView.addItemDecoration(new DividerItemDecoration(AnimeListActivity.this));
            }

            @Override
            public void onFailure(Call<AnimeListCache> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void initRecyclerView() {
        animeListRecyclerView = (RecyclerView) findViewById(R.id.anime_list_recycler);
        animeListRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(GateApplication.getGlobalContext());
        animeListRecyclerView.setLayoutManager(mLayoutManager);
    }
}