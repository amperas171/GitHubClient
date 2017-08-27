package com.amperas17.wonderstest.data.loader;


import com.amperas17.wonderstest.App;
import com.amperas17.wonderstest.data.model.pojo.Issue;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IssuesLoader {

    private WeakReference<IIssuesLoaderCaller> callerRef;
    private Call<ArrayList<Issue>> call;

    public IssuesLoader(IIssuesLoaderCaller authCaller) {
        this.callerRef = new WeakReference<>(authCaller);
    }

    public void getData(String login, String repositoryName) {
        call = App.getGitHubApi().getIssues(login, repositoryName);
        call.enqueue(new Callback<ArrayList<Issue>>() {
            @Override
            public void onResponse(Call<ArrayList<Issue>> call, Response<ArrayList<Issue>> response) {
                if (callerRef.get() != null)
                    callerRef.get().onLoadIssues(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Issue>> call, Throwable t) {
                if (!call.isCanceled()) {
                    if (callerRef.get() != null)
                        callerRef.get().onLoadIssuesError(t);
                }
            }
        });
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
            call = null;
        }
    }

    public interface IIssuesLoaderCaller {
        void onLoadIssues(ArrayList<Issue> issues);

        void onLoadIssuesError(Throwable th);
    }
}
