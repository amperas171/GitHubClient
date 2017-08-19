package com.amperas17.wonderstest;

import android.app.Application;

import com.amperas17.wonderstest.api.GitHubApi;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class App extends Application {
    private static GitHubApi gitHubApi;
    private Retrofit retrofit;

    public static GitHubApi getGitHubApi() {
        return gitHubApi;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        configureRetrofit();
    }

    private void configureRetrofit() {
        //Logging
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://gitHubApi.github.com/") //Base address part
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()) //transform json into objects
                .build();

        gitHubApi = retrofit.create(GitHubApi.class); //gitHubApi object that makes responses
    }
}
