package com.cookandroid.myapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NetworkService {
    @POST("/test29")
    Call<String> post_test(@Body String test);
}
