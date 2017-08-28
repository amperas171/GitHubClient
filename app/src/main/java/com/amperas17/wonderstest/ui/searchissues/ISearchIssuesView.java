package com.amperas17.wonderstest.ui.searchissues;


import com.amperas17.wonderstest.data.model.pojo.Issue;
import com.amperas17.wonderstest.data.model.realm.RealmIssue;

import io.realm.RealmResults;

public interface ISearchIssuesView {
    void openNoteActivity(Issue issueItem);

    void setDataToAdapter(RealmResults<RealmIssue> issues);
}
