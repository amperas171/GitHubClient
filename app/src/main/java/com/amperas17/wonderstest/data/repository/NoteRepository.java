package com.amperas17.wonderstest.data.repository;


import com.amperas17.wonderstest.model.realm.RealmNote;

import io.realm.Realm;

public class NoteRepository {

    private Realm realm;

    public NoteRepository() {
        realm = Realm.getDefaultInstance();
    }

    public RealmNote getNote(String itemKey) {
        return realm.where(RealmNote.class).equalTo(RealmNote.REPO_KEY, itemKey).findFirst();
    }

    public void setNote(String itemKey, String title, String text) {
        if (!realm.isClosed()) {
            final RealmNote note = new RealmNote(itemKey, title, text);
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.insertOrUpdate(note);
                }
            });
        }
    }

    public void close() {
        if (realm != null) {
            realm.close();
        }
    }
}
