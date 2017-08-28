package com.amperas17.wonderstest.ui.searchissues;


import com.amperas17.wonderstest.data.model.pojo.Issue;

public interface ISearchIssuesPresenter {
    void onIssueLongItemClick(Issue issueItem);

    void onQueryTextChange(String query);

    void getIssues();

    void onDestroy();
}
