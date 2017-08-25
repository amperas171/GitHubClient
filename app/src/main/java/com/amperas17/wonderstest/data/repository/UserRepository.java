package com.amperas17.wonderstest.data.repository;


import com.amperas17.wonderstest.model.pojo.User;
import com.amperas17.wonderstest.model.realm.RealmUser;

import java.lang.ref.WeakReference;

import io.realm.Realm;

public class UserRepository {
    private WeakReference<IGetUser> callerRef;
    private Realm realm;

    public UserRepository(IGetUser caller) {
        this.callerRef = new WeakReference<>(caller);
        realm = Realm.getDefaultInstance();
    }

    public UserRepository() {
        realm = Realm.getDefaultInstance();
    }

    public void getUser(){
        if (!realm.isClosed())
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmUser realmUser = realm.where(RealmUser.class).findFirst();
                    if (callerRef != null)
                        callerRef.get().onGetUser(realmUser.toUser());
                }
            });
    }

    public void setUser(final RealmUser realmUser){
        if (!realm.isClosed())
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.insertOrUpdate(realmUser);
                }
            });
    }

    public void close(){
        if (realm != null) {
            realm.close();
        }
    }

    public interface IGetUser {
        void onGetUser(User user);
    }
}
