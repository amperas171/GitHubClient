package com.amperas17.wonderstest.ui.repositories;

import com.amperas17.wonderstest.data.cache.UserCacheEraser;
import com.amperas17.wonderstest.data.model.pojo.Repository;
import com.amperas17.wonderstest.data.model.pojo.User;
import com.amperas17.wonderstest.data.model.realm.RealmRepository;
import com.amperas17.wonderstest.data.provider.IProviderCaller;
import com.amperas17.wonderstest.data.provider.RepositoriesProvider;

import java.lang.ref.WeakReference;

import io.realm.RealmResults;

public class RepositoryPresenter implements IRepositoryPresenter,
        UserCacheEraser.IUserCacheEraseCaller, IProviderCaller<RealmResults<RealmRepository>> {

    private WeakReference<IRepositoryView> viewRef;
    private RepositoriesProvider repositoriesProvider;
    private UserCacheEraser userCacheEraser;

    public RepositoryPresenter(IRepositoryView view) {
        this.viewRef = new WeakReference<>(view);
        userCacheEraser = new UserCacheEraser(this);
        repositoriesProvider = new RepositoriesProvider(this);
    }

    @Override
    public String getActionBarTitle(User user) {
        return user.getLogin();
    }

    @Override
    public void getRepositories(User user) {
        IRepositoryView view = viewRef.get();
        if (view != null) {
            view.showRefreshing();
        }
        repositoriesProvider.getRepositories(user.getLogin());
    }

    @Override
    public void onProviderCallSuccess(RealmResults<RealmRepository> realmRepositories) {
        IRepositoryView view = viewRef.get();
        if (view != null) {
            view.hideRefreshing();
            view.setDataToAdapter(realmRepositories);
        }
    }

    @Override
    public void onProviderCallError(Throwable th) {
        IRepositoryView view = viewRef.get();
        if (view != null) {
            view.hideRefreshing();
            view.showError(th);
        }
    }

    @Override
    public void getRepositoriesAndUpdate(User user) {
        IRepositoryView view = viewRef.get();
        if (view != null) {
            view.showRefreshing();
        }
        repositoriesProvider.getRepositoriesAndUpdate(user.getAuthHeader(), user.getLogin());
    }

    @Override
    public void onRepositoryItemClick(Repository repositoryItem) {
        IRepositoryView view = viewRef.get();
        if (view != null) {
            view.openIssuesActivity(repositoryItem);
        }
    }

    @Override
    public void onRepositoryItemLongClick(Repository repositoryItem) {
        IRepositoryView view = viewRef.get();
        if (view != null) {
            view.openNoteActivity(repositoryItem);
        }
    }

    @Override
    public void onSearchIconSelected() {
        IRepositoryView view = viewRef.get();
        if (view != null) {
            view.openSearchIssuesActivity();
        }
    }

    @Override
    public void onExitIconSelected() {
        IRepositoryView view = viewRef.get();
        if (view != null) {
            view.showExitDialog();
        }
    }

    @Override
    public void exit() {
        IRepositoryView view = viewRef.get();
        if (view != null) {
            view.showLoading();
        }
        userCacheEraser.erase();
    }

    @Override
    public void onUserCacheErased() {
        IRepositoryView view = viewRef.get();
        if (view != null) {
            view.hideLoading();
            view.updateAdapter();
            view.openAuthActivity();
        }
    }

    @Override
    public void onUserCacheErasedError(Throwable th) {
        IRepositoryView view = viewRef.get();
        if (view != null) {
            view.hideLoading();
            view.showError(th);
        }
    }

    @Override
    public void cancelLoading() {
        repositoriesProvider.close();
    }

    @Override
    public void onDestroy() {
        repositoriesProvider.close();
        userCacheEraser.close();
    }
}
