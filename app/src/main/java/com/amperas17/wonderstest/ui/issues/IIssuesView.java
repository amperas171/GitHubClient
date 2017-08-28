package com.amperas17.wonderstest.ui.issues;


import com.amperas17.wonderstest.data.model.pojo.Issue;
import com.amperas17.wonderstest.data.model.realm.RealmIssue;

import io.realm.RealmResults;

public interface IIssuesView {
    void openNoteActivity(Issue issueItem);

    void showRefreshing();

    void hideRefreshing();

    void showError(Throwable th);

    void setDataToAdapter(RealmResults<RealmIssue> issues);
}
