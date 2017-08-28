package com.amperas17.wonderstest.ui.repositories;

import com.amperas17.wonderstest.data.model.pojo.Repository;
import com.amperas17.wonderstest.data.model.realm.RealmRepository;

import io.realm.RealmResults;

public interface IRepositoryView {
    void setDataToAdapter(RealmResults<RealmRepository> repositories);

    void showError(Throwable th);

    void showExitDialog();

    void openSearchIssuesActivity();

    void openIssuesActivity(Repository repositoryItem);

    void openNoteActivity(Repository repositoryItem);

    void openAuthActivity();

    void updateAdapter();

    void showLoading();

    void hideLoading();

    void showRefreshing();

    void hideRefreshing();
}
