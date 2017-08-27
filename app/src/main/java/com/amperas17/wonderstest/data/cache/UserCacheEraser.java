package com.amperas17.wonderstest.data.cache;


import com.amperas17.wonderstest.model.realm.RealmUser;

import java.lang.ref.WeakReference;

import io.realm.Realm;

public class UserCacheEraser {
    private Realm realm;
    private WeakReference<IUserCacheEraseCaller> callerRef;

    public UserCacheEraser() {
        realm = Realm.getDefaultInstance();
    }

    public UserCacheEraser(IUserCacheEraseCaller caller) {
        this.callerRef = new WeakReference<>(caller);
        realm = Realm.getDefaultInstance();
    }

    public void erase() {
        if (!realm.isClosed()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmUser user = realm.where(RealmUser.class).findFirst();
                    user.deleteFromRealm();
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    if (callerRef != null)
                        callerRef.get().onUserCacheErased();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    if (callerRef != null)
                        callerRef.get().onUserCacheErasedError(error);
                }
            });
        }
    }

    public void close(){
        if (realm != null) {
            realm.close();
        }
    }

    public interface IUserCacheEraseCaller {
        void onUserCacheErased();
        void onUserCacheErasedError(Throwable error);
    }
}
