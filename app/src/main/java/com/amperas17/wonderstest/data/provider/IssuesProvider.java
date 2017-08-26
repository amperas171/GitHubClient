package com.amperas17.wonderstest.data.provider;


import com.amperas17.wonderstest.App;
import com.amperas17.wonderstest.model.pojo.Issue;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IssuesProvider {

    private WeakReference<IIssuesCaller> callerRef;
    private Call<ArrayList<Issue>> call;

    public IssuesProvider(IIssuesCaller authCaller) {
        this.callerRef = new WeakReference<>(authCaller);
    }

    public void getData(String login, String repositoryName) {
        call = App.getGitHubApi().getIssues(login, repositoryName);
        call.enqueue(new Callback<ArrayList<Issue>>() {
            @Override
            public void onResponse(Call<ArrayList<Issue>> call, Response<ArrayList<Issue>> response) {
                if (callerRef.get() != null)
                    callerRef.get().onGetIssues(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Issue>> call, Throwable t) {
                if (!call.isCanceled()) {
                    if (callerRef.get() != null)
                        callerRef.get().onError(t);
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

    public interface IIssuesCaller {
        void onGetIssues(ArrayList<Issue> issues);

        void onError(Throwable th);
    }
}
