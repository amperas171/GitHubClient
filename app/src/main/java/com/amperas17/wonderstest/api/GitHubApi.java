package com.amperas17.wonderstest.api;


import com.amperas17.wonderstest.model.pojo.Issue;
import com.amperas17.wonderstest.model.pojo.Repo;
import com.amperas17.wonderstest.model.pojo.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface GitHubApi {

    @GET("user")
    Call<User> getUser(@Header("Authorization") String authorization);

    @GET("users/{user}/repos")
    Call<ArrayList<Repo>> getRepos(@Header("Authorization") String authorization, @Path("user") String userLogin);

    @GET("repos/{user}/{repo}/issues")
    Call<ArrayList<Issue>> getIssues(
            @Path("user") String userLogin,
            @Path("repo") String repoName
    );
}
