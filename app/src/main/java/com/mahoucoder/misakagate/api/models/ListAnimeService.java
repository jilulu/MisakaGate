package com.mahoucoder.misakagate.api.models;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by jamesji on 28/10/2016.
 */

public interface ListAnimeService {
    @GET("/list_v1.json")
    Call<List<Anime>> listAnimes();
}
