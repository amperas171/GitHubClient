package com.amperas17.wonderstest.data.cache;


import com.amperas17.wonderstest.model.pojo.Issue;
import com.amperas17.wonderstest.model.realm.RealmIssue;

import java.util.ArrayList;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

public class IssuesCache {

    private Realm realm;

    public IssuesCache() {
        realm = Realm.getDefaultInstance();
    }

    public RealmResults<RealmIssue> getIssuesByRepositoryName(final String repositoryName) {
        return realm.where(RealmIssue.class)
                .equalTo(RealmIssue.REPOSITORY_NAME, repositoryName)
                .findAll();
    }

    public RealmResults<RealmIssue> getAllIssues() {
        return realm.where(RealmIssue.class).findAll();
    }

    public RealmResults<RealmIssue> getSearchedIssues(String pattern) {
        return realm.where(RealmIssue.class)
                .contains(RealmIssue.TITLE, pattern, Case.INSENSITIVE)
                .findAllSorted(RealmIssue.TITLE);
    }

    public void setIssues(final ArrayList<Issue> issues, final String repositoryName) {
        if (!realm.isClosed())
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (Issue issue : issues) {
                        RealmIssue realmIssue = new RealmIssue(issue);
                        realmIssue.setRepositoryName(repositoryName);
                        realm.insertOrUpdate(realmIssue);
                    }
                }
            });
    }

    public void close() {
        if (realm != null) {
            realm.close();
        }
    }
}
