package com.amperas17.wonderstest.ui.issues;


import com.amperas17.wonderstest.data.model.pojo.Issue;
import com.amperas17.wonderstest.data.model.pojo.Repository;
import com.amperas17.wonderstest.data.model.realm.RealmIssue;
import com.amperas17.wonderstest.data.provider.IProviderCaller;
import com.amperas17.wonderstest.data.provider.IssuesProvider;

import java.lang.ref.WeakReference;

import io.realm.RealmResults;

public class IssuesPresenter implements IIssuesPresenter, IProviderCaller<RealmResults<RealmIssue>> {

    WeakReference<IIssuesView> viewRef;
    private IssuesProvider issuesProvider;

    public IssuesPresenter(IIssuesView view) {
        this.viewRef = new WeakReference<>(view);
        issuesProvider = new IssuesProvider(this);
    }

    @Override
    public String getActionBarTitle(Repository repository) {
        return repository.getName();
    }

    @Override
    public void onIssueLongItemClick(Issue issueItem) {
        IIssuesView view = viewRef.get();
        if (view != null) {
            view.openNoteActivity(issueItem);
        }
    }

    @Override
    public void getIssuesByRepository(Repository repository) {
        issuesProvider.getIssuesByRepository(repository.getName());
    }

    @Override
    public void getIssuesAndUpdate(Repository repository) {
        IIssuesView view = viewRef.get();
        if (view != null) {
            view.showRefreshing();
        }
        issuesProvider.getIssuesAndUpdate(repository.getOwner().getLogin(), repository.getName());
    }

    @Override
    public void onProviderCallSuccess(RealmResults<RealmIssue> issues) {
        IIssuesView view = viewRef.get();
        if (view != null) {
            view.hideRefreshing();
            view.setDataToAdapter(issues);
        }
    }

    @Override
    public void onProviderCallError(Throwable th) {
        IIssuesView view = viewRef.get();
        if (view != null) {
            view.hideRefreshing();
            view.showError(th);
        }
    }

    @Override
    public void onDestroy() {
        issuesProvider.close();
    }
}
