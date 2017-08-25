package com.amperas17.wonderstest.data.provider;


import com.amperas17.wonderstest.App;
import com.amperas17.wonderstest.model.pojo.Repo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReposProvider {
    private WeakReference<IReposCaller> callerRef;
    private Call<ArrayList<Repo>> call;

    public ReposProvider(IReposCaller authCaller) {
        this.callerRef = new WeakReference<>(authCaller);
    }

    public void getData(final String authHeader, String login){
        call = App.getGitHubApi().getRepos(authHeader, login);
        call.enqueue(new Callback<ArrayList<Repo>>() {
            @Override
            public void onResponse(Call<ArrayList<Repo>> call, Response<ArrayList<Repo>> response) {
                if (callerRef.get() != null)
                    callerRef.get().onGetRepos(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Repo>> call, Throwable t) {
                if (!call.isCanceled()) {
                    if (callerRef.get() != null)
                        callerRef.get().onError(t);
                }
            }
        });
    }

    public void cancel(){
        if (call != null) {
            call.cancel();
            call = null;
        }
    }

    public interface IReposCaller{
        void onGetRepos(ArrayList<Repo> repos);
        void onError(Throwable th);
    }
}
