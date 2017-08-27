package com.amperas17.wonderstest.data.loader;


import com.amperas17.wonderstest.App;
import com.amperas17.wonderstest.data.model.pojo.Repository;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepositoriesLoader {
    private WeakReference<IRepositoriesLoaderCaller> callerRef;
    private Call<ArrayList<Repository>> call;

    public RepositoriesLoader(IRepositoriesLoaderCaller authCaller) {
        this.callerRef = new WeakReference<>(authCaller);
    }

    public void getData(final String authHeader, String login){
        call = App.getGitHubApi().getRepositories(authHeader, login);
        call.enqueue(new Callback<ArrayList<Repository>>() {
            @Override
            public void onResponse(Call<ArrayList<Repository>> call, Response<ArrayList<Repository>> response) {
                if (callerRef.get() != null)
                    callerRef.get().onLoadRepositories(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Repository>> call, Throwable t) {
                if (!call.isCanceled()) {
                    if (callerRef.get() != null)
                        callerRef.get().onLoadRepositoriesError(t);
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

    public interface IRepositoriesLoaderCaller {
        void onLoadRepositories(ArrayList<Repository> repositories);
        void onLoadRepositoriesError(Throwable th);
    }
}
