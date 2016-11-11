package com.mahoucoder.misakagate.activities;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.adapters.ThreadListAdapter;
import com.mahoucoder.misakagate.api.GateAPI;
import com.mahoucoder.misakagate.api.models.AnimeListCache;
import com.mahoucoder.misakagate.api.models.Thread;
import com.mahoucoder.misakagate.api.models.Thread_Table;
import com.mahoucoder.misakagate.data.Animations;
import com.mahoucoder.misakagate.utils.GateUtils;
import com.mahoucoder.misakagate.widgets.DividerItemDecoration;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AnimeListActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    private RecyclerView animeListRecyclerView;
    private RecyclerView.Adapter animeListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private View fetchingDataIndicator, connectionOfflineIndicator;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_list);

        initView();

        initRecyclerView();

        fetchAdapterDataAndBindToAdapter();
    }

    private void initView() {
        fetchingDataIndicator = findViewById(R.id.anime_listc_connection_indicator);
        connectionOfflineIndicator = findViewById(R.id.anime_list_offline_indicator);
        fetchingDataIndicator.setVisibility(VISIBLE);
        connectionOfflineIndicator.setVisibility(GONE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void fetchAdapterDataAndBindToAdapter() {
        GateAPI.getAnimeList(new Callback<AnimeListCache>() {
            @Override
            public void onResponse(Call<AnimeListCache> call, Response<AnimeListCache> response) {
                AnimeListCache animeListCache = response.body();
                GateUtils.logd(AnimeListActivity.class.getSimpleName(), String.format(Locale.ENGLISH, "Got %d threads from server. ", animeListCache.threads.size()));
                List<Thread> threads = animeListCache.threads;
                saveDataAsync(threads);
                animeListAdapter = new ThreadListAdapter(threads);
                animeListRecyclerView.setAdapter(animeListAdapter);
                animeListRecyclerView.addItemDecoration(new DividerItemDecoration(AnimeListActivity.this));
                fetchingDataIndicator.setVisibility(GONE);
                connectionOfflineIndicator.setVisibility(GONE);
            }

            @Override
            public void onFailure(Call<AnimeListCache> call, Throwable t) {
                t.printStackTrace();
                fetchingDataIndicator.setVisibility(GONE);
                connectionOfflineIndicator.setVisibility(VISIBLE);
            }
        });
    }

    private static void saveDataAsync(final List<Thread> threads) {
        Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                FlowManager.getDatabase(Animations.class)
                        .getTransactionManager()
                        .getSaveQueue()
                        .addAll2(threads);
                return null;
            }
        }).subscribeOn(Schedulers.computation()).subscribe();
    }

    @Override
    public boolean onQueryTextSubmit(final String query) {
        Observable.create(new Observable.OnSubscribe<List<Thread>>() {
            @Override
            public void call(Subscriber<? super List<Thread>> subscriber) {
                List<Thread> matchingThreads = SQLite.select().from(Thread.class)
                        .where(Thread_Table.subject.like("%" + query + "%"))
                        .queryList();
                subscriber.onNext(matchingThreads);
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Thread>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<Thread> threads) {
                        if (animeListAdapter != null && animeListAdapter instanceof ThreadListAdapter) {
                            ((ThreadListAdapter) animeListAdapter).setData(threads);
                            animeListAdapter.notifyDataSetChanged();
                        }
                    }
                });
        return true;
    }

    private void initRecyclerView() {
        animeListRecyclerView = (RecyclerView) findViewById(R.id.anime_list_recycler);
        animeListRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(GateApplication.getGlobalContext());
        animeListRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_anime_list_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(AnimeListActivity.this);
        searchView.setQueryHint(GateApplication.getGlobalContext().getString(R.string.search_hint));

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}