package com.amperas17.wonderstest.data.provider;


import com.amperas17.wonderstest.App;
import com.amperas17.wonderstest.model.pojo.Repository;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepositoriesProvider {
    private WeakReference<IRepositoriesCaller> callerRef;
    private Call<ArrayList<Repository>> call;

    public RepositoriesProvider(IRepositoriesCaller authCaller) {
        this.callerRef = new WeakReference<>(authCaller);
    }

    public void getData(final String authHeader, String login){
        call = App.getGitHubApi().getRepositories(authHeader, login);
        call.enqueue(new Callback<ArrayList<Repository>>() {
            @Override
            public void onResponse(Call<ArrayList<Repository>> call, Response<ArrayList<Repository>> response) {
                if (callerRef.get() != null)
                    callerRef.get().onGetRepositories(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Repository>> call, Throwable t) {
                if (!call.isCanceled()) {
                    if (callerRef.get() != null)
                        callerRef.get().onGetRepositoriesError(t);
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

    public interface IRepositoriesCaller {
        void onGetRepositories(ArrayList<Repository> repositories);
        void onGetRepositoriesError(Throwable th);
    }
}
