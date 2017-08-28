package com.amperas17.wonderstest.ui.searchissues;


import com.amperas17.wonderstest.data.model.pojo.Issue;
import com.amperas17.wonderstest.data.model.realm.RealmIssue;
import com.amperas17.wonderstest.data.provider.IProviderCaller;
import com.amperas17.wonderstest.data.provider.IssuesProvider;

import java.lang.ref.WeakReference;

import io.realm.RealmResults;

public class SearchIssuesPresenter implements ISearchIssuesPresenter, IProviderCaller<RealmResults<RealmIssue>> {

    WeakReference<ISearchIssuesView> viewRef;
    private IssuesProvider issuesProvider;

    public SearchIssuesPresenter(ISearchIssuesView view) {
        this.viewRef = new WeakReference<>(view);
        issuesProvider = new IssuesProvider(this);
    }

    @Override
    public void onIssueLongItemClick(Issue issueItem) {
        ISearchIssuesView view = viewRef.get();
        if (view != null) {
            view.openNoteActivity(issueItem);
        }
    }

    @Override
    public void onQueryTextChange(String query) {
        if (!query.isEmpty()) {
            issuesProvider.getSearchedIssues(query);
        } else {
            issuesProvider.getIssues();
        }
    }

    @Override
    public void getIssues() {
        issuesProvider.getIssues();
    }

    @Override
    public void onProviderCallSuccess(RealmResults<RealmIssue> issues) {
        ISearchIssuesView view = viewRef.get();
        if (view != null) {
            view.setDataToAdapter(issues);
        }
    }

    @Override
    public void onProviderCallError(Throwable th) {

    }

    @Override
    public void onDestroy() {
        issuesProvider.close();
    }
}
