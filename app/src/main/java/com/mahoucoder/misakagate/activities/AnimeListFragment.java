package com.mahoucoder.misakagate.activities;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by jamesji on 13/11/2016.
 */

public class AnimeListFragment extends Fragment implements SearchView.OnQueryTextListener {
    private RecyclerView animeListRecyclerView;
    private RecyclerView.Adapter animeListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private View fetchingDataIndicator, connectionOfflineIndicator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_anime_list, container, false);
        initView(rootView);

        initRecyclerView(rootView);

        fetchAdapterDataAndBindToAdapter();
        return rootView;
    }

    private void initView(View rootView) {
        fetchingDataIndicator = rootView.findViewById(R.id.anime_listc_connection_indicator);
        connectionOfflineIndicator = rootView.findViewById(R.id.anime_list_offline_indicator);
        fetchingDataIndicator.setVisibility(VISIBLE);
        connectionOfflineIndicator.setVisibility(GONE);
    }

    private void fetchAdapterDataAndBindToAdapter() {
        GateAPI.getAnimeList(new Callback<AnimeListCache>() {
            @Override
            public void onResponse(Call<AnimeListCache> call, Response<AnimeListCache> response) {
                AnimeListCache animeListCache = response.body();
                GateUtils.logd(MainActivity.class.getSimpleName(), String.format(Locale.ENGLISH, "Got %d threads from server. ", animeListCache.threads.size()));
                List<Thread> threads = animeListCache.threads;
                saveDataAsync(threads);
            }

            @Override
            public void onFailure(Call<AnimeListCache> call, Throwable t) {
                t.printStackTrace();
                fetchingDataIndicator.setVisibility(GONE);
                connectionOfflineIndicator.setVisibility(VISIBLE);
            }
        });
    }

    private void saveDataAsync(final List<Thread> threads) {
        Observable.fromCallable(new Callable<List<Thread>>() {
            @Override
            public List<Thread> call() throws Exception {
                FlowManager.getDatabase(Animations.class)
                        .getTransactionManager()
                        .getSaveQueue()
                        .addAll2(threads);
                return SQLite.select()
                        .from(Thread.class)
                        .orderBy(Thread_Table.lastpost, false)
                        .queryList();
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<Thread>>() {
            @Override
            public void call(List<Thread> threads) {
                animeListAdapter = new ThreadListAdapter(threads);
                animeListRecyclerView.setAdapter(animeListAdapter);
                animeListRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), threads));
                fetchingDataIndicator.setVisibility(GONE);
                connectionOfflineIndicator.setVisibility(GONE);
            }
        });
    }

    private void initRecyclerView(View rootView) {
        animeListRecyclerView = (RecyclerView) rootView.findViewById(R.id.anime_list_recycler);
        animeListRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(GateApplication.getGlobalContext());
        animeListRecyclerView.setLayoutManager(mLayoutManager);
    }

    public void doQueryAndChangeData(final String query) {
        Observable.fromCallable(new Callable<List<Thread>>() {
            @Override
            public List<Thread> call() throws Exception {
                From<Thread> from = SQLite.select()
                        .from(Thread.class);
                String[] split = query.trim().split("\\s");
                Where<Thread> where = from.where(Thread_Table.subject.like("%" + split[0] + "%"));
                for (int i = 1; i < split.length; i += 1) {
                    where = where.and(Thread_Table.subject.like("%" + split[i] + "%"));
                }
                return where.orderBy(Thread_Table.lastpost, false).queryList();
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<Thread>>() {
            @Override
            public void call(List<Thread> threads) {
                if (animeListAdapter != null && animeListAdapter instanceof ThreadListAdapter) {
                    ((ThreadListAdapter) animeListAdapter).setData(threads);
                    animeListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_anime_list_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(AnimeListFragment.this);
        searchView.setQueryHint(GateApplication.getGlobalContext().getString(R.string.search_hint));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_refresh) {
            fetchingDataIndicator.setVisibility(VISIBLE);
            connectionOfflineIndicator.setVisibility(GONE);
            fetchAdapterDataAndBindToAdapter();
            return true;
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        doQueryAndChangeData(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        doQueryAndChangeData(newText);
        return true;
    }
}
