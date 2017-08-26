package com.amperas17.wonderstest.data.cache;


import com.amperas17.wonderstest.model.pojo.Repository;
import com.amperas17.wonderstest.model.realm.RealmRepository;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class RepositoryCache {
    private Realm realm;

    public RepositoryCache() {
        realm = Realm.getDefaultInstance();
    }

    public RealmResults<RealmRepository> getRepositories(final String login) {
        if (!realm.isClosed()) {
            return realm.where(RealmRepository.class)
                    .equalTo(RealmRepository.OWNER_LOGIN, login).findAll();
        } else {
            return null;
        }
    }

    public void setRepositories(final ArrayList<Repository> repositories) {
        if (!realm.isClosed())
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (Repository repository : repositories) {
                        RealmRepository realmRepository = new RealmRepository(repository);
                        realm.insertOrUpdate(realmRepository);
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