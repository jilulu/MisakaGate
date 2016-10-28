package com.mahoucoder.misakagate.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.adapters.AnimeListAdapter;
import com.mahoucoder.misakagate.api.GateAPI;
import com.mahoucoder.misakagate.api.models.Anime;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView animeListRecyclerView;
    private RecyclerView.Adapter animeListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animeListRecyclerView = (RecyclerView) findViewById(R.id.anime_list_recycler);
        animeListRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(GateApplication.getGlobalContext());
        animeListRecyclerView.setLayoutManager(mLayoutManager);

        GateAPI.getAnimeList(new Callback<List<Anime>>() {
            @Override
            public void onResponse(Call<List<Anime>> call, Response<List<Anime>> response) {
                List<Anime> animeList = response.body();
                System.out.println(animeList.size());

                AnimeListAdapter animeListAdapter = new AnimeListAdapter(animeList);
                animeListRecyclerView.setAdapter(animeListAdapter);
            }

            @Override
            public void onFailure(Call<List<Anime>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}