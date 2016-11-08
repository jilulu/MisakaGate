package com.mahoucoder.misakagate.api.models;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by jamesji on 9/11/2016.
 */

public interface PlaybackInfoService {
    @GET("/json-feed/{param_0}/")
    Call<PlaybackInfo[]> getPlayBackInfo(@Path("param_0") String param, @Query("hash") String hash);
}
