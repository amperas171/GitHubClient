package com.amperas17.wonderstest.data.provider;


import com.amperas17.wonderstest.data.cache.IssuesCache;
import com.amperas17.wonderstest.data.loader.IssuesLoader;
import com.amperas17.wonderstest.data.model.pojo.Issue;
import com.amperas17.wonderstest.data.model.realm.RealmIssue;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.realm.RealmResults;

public class IssuesProvider implements IssuesLoader.IIssuesLoaderCaller {

    private WeakReference<IProviderCaller> callerRef;
    private IssuesCache issuesCache;
    private IssuesLoader issuesLoader;

    private String repositoryName;

    public IssuesProvider(IProviderCaller caller) {
        callerRef = new WeakReference<>(caller);
        issuesCache = new IssuesCache();
        issuesLoader = new IssuesLoader(this);
    }

    public void getIssuesAndUpdate(String login, String repositoryName) {
        getCachedIssues(repositoryName);
        issuesLoader.getData(login, repositoryName);
        this.repositoryName = repositoryName;
    }

    public void getIssuesByRepository(String repositoryName) {
        getCachedIssues(repositoryName);
    }

    public void getSearchedIssues(String pattern) {
        RealmResults<RealmIssue> issues = issuesCache.getSearchedIssues(pattern);
        if (issues != null && callerRef.get() != null) {
            callerRef.get().onProviderCallSuccess(issues);
        }
    }

    public void getIssues() {
        RealmResults<RealmIssue> issues = issuesCache.getIssues();
        if (issues != null && callerRef.get() != null) {
            callerRef.get().onProviderCallSuccess(issues);
        }
    }

    @Override
    public void onLoadIssues(ArrayList<Issue> issues) {
        if (issues != null && !issues.isEmpty()) {
            issuesCache.setIssues(issues, repositoryName);
        }
        getCachedIssues(repositoryName);
    }

    @Override
    public void onLoadIssuesError(Throwable th) {
        if (callerRef.get() != null) {
            callerRef.get().onProviderCallError(th);
        }
    }

    private void getCachedIssues(String repositoryName) {
        RealmResults<RealmIssue> issues = issuesCache.getIssuesByRepositoryName(repositoryName);
        if (issues != null && callerRef.get() != null) {
            callerRef.get().onProviderCallSuccess(issues);
        }
    }

    public void close() {
        issuesCache.close();
        issuesLoader.cancel();
    }

    public interface IProviderCaller {
        void onProviderCallSuccess(RealmResults<RealmIssue> issues);

        void onProviderCallError(Throwable th);
    }
}