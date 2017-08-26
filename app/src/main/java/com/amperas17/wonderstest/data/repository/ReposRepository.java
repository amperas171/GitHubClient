package com.amperas17.wonderstest.data.repository;


import com.amperas17.wonderstest.model.pojo.Repo;
import com.amperas17.wonderstest.model.realm.RealmRepo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class ReposRepository {
    private WeakReference<IReposCaller> callerRef;

    private Realm realm;

    public ReposRepository(IReposCaller caller) {
        this.callerRef = new WeakReference<>(caller);
        realm = Realm.getDefaultInstance();
    }

    public ReposRepository() {
        realm = Realm.getDefaultInstance();
    }

    public RealmResults<RealmRepo> getRepos(final String login) {
        if (!realm.isClosed()) {
            return realm.where(RealmRepo.class)
                    .equalTo(RealmRepo.OWNER_LOGIN, login).findAll();
        } else {
            return null;
        }
    }

    public void setRepos(final ArrayList<Repo> repos) {
        if (!realm.isClosed())
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (Repo repo : repos) {
                        RealmRepo realmRepo = new RealmRepo(repo);
                        realm.insertOrUpdate(realmRepo);
                    }
                    if (callerRef != null)
                        callerRef.get().onSetRepos();
                }
            });
    }

    public void close() {
        if (realm != null) {
            realm.close();
        }
    }

    public interface IReposCaller {
        void onSetRepos();
    }
}