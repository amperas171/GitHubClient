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
        if (viewRef.get() != null) {
            viewRef.get().showRefreshing();
        }
        repositoriesProvider.getRepositories(user.getLogin());
    }

    @Override
    public void onProviderCallSuccess(RealmResults<RealmRepository> realmRepositories) {
        if (viewRef.get() != null) {
            viewRef.get().hideRefreshing();
            viewRef.get().setDataToAdapter(realmRepositories);
        }
    }

    @Override
    public void onProviderCallError(Throwable th) {
        if (viewRef.get() != null) {
            viewRef.get().hideRefreshing();
            viewRef.get().showError(th);
        }
    }

    @Override
    public void getRepositoriesAndUpdate(User user) {
        if (viewRef.get() != null) {
            viewRef.get().showRefreshing();
        }
        repositoriesProvider.getRepositoriesAndUpdate(user.getAuthHeader(), user.getLogin());
    }

    @Override
    public void onRepositoryItemClick(Repository repositoryItem) {
        if (viewRef.get() != null) {
            viewRef.get().openIssuesActivity(repositoryItem);
        }
    }

    @Override
    public void onRepositoryItemLongClick(Repository repositoryItem) {
        if (viewRef.get() != null) {
            viewRef.get().openNoteActivity(repositoryItem);
        }
    }

    @Override
    public void onSearchIconSelected() {
        if (viewRef.get() != null) {
            viewRef.get().openSearchIssuesActivity();
        }
    }

    @Override
    public void onExitIconSelected() {
        if (viewRef.get() != null) {
            viewRef.get().showExitDialog();
        }
    }

    @Override
    public void exit() {
        if (viewRef.get() != null) {
            viewRef.get().showLoading();
        }
        userCacheEraser.erase();
    }

    @Override
    public void onUserCacheErased() {
        if (viewRef.get() != null) {
            viewRef.get().hideLoading();
            viewRef.get().updateAdapter();
            viewRef.get().openAuthActivity();
        }
    }

    @Override
    public void onUserCacheErasedError(Throwable th) {
        if (viewRef.get() != null) {
            viewRef.get().hideLoading();
            viewRef.get().showError(th);
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
