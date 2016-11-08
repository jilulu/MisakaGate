package com.mahoucoder.misakagate.api.models;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by jamesji on 28/10/2016.
 */

public interface ListAnimeService {
    @GET("/__cache.json")
    Call<AnimeListCache> getAnimeCache();
}
