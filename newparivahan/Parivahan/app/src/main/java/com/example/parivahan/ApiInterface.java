package com.example.parivahan;

import com.example.parivahan.Mode.MainProject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("place/queryautocomplete/json")
    Call<MainProject> getPlace(@Query("input") String text,
                               @Query("key") String key);
}

