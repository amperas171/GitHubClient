package com.amperas17.wonderstest.ui.repositories;


import com.amperas17.wonderstest.data.model.pojo.Repository;
import com.amperas17.wonderstest.data.model.pojo.User;

public interface IRepositoryPresenter {
    void onRepositoryItemClick(Repository repositoryItem);
    void onRepositoryItemLongClick(Repository repositoryItem);
    void onSearchIconSelected();
    void onExitIconSelected();
    String getActionBarTitle(User user);
    void getRepositories(User user);
    void getRepositoriesAndUpdate(User user);
    void exit();
    void cancelLoading();
    void onDestroy();
}
