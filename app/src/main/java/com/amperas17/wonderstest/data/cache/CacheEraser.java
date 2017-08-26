package com.amperas17.wonderstest.data.cache;


import java.lang.ref.WeakReference;

import io.realm.Realm;

public class CacheEraser {
    private Realm realm;
    private WeakReference<IEraseCaller> callerRef;

    public CacheEraser() {
        realm = Realm.getDefaultInstance();
    }

    public CacheEraser(IEraseCaller caller) {
        this.callerRef = new WeakReference<>(caller);
        realm = Realm.getDefaultInstance();
    }

    public void erase() {
        if (!realm.isClosed()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.deleteAll();
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    if (callerRef != null)
                        callerRef.get().onDeleted();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    if (callerRef != null)
                        callerRef.get().onDeletedError(error);
                }
            });
        }
    }

    public void close(){
        if (realm != null) {
            realm.close();
        }
    }

    public interface IEraseCaller{
        void onDeleted();
        void onDeletedError(Throwable error);
    }
}