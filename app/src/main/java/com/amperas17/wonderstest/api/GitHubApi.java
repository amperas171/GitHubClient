package com.amperas17.wonderstest.api;


import com.amperas17.wonderstest.model.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface GitHubApi {

    @GET("user")
    Call<User> getUser(@Header("Authorization") String authorization);
}
