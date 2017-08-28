package com.amperas17.wonderstest.ui.issues;


import com.amperas17.wonderstest.data.model.pojo.Issue;
import com.amperas17.wonderstest.data.model.pojo.Repository;

public interface IIssuesPresenter {
    void onIssueLongItemClick(Issue issueItem);

    String getActionBarTitle(Repository repository);

    void getIssuesByRepository(Repository repository);

    void getIssuesAndUpdate(Repository repository);

    void onDestroy();
}
