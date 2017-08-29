package com.amperas17.wonderstest.data.cache;


import com.amperas17.wonderstest.data.model.realm.RealmNote;

import io.realm.Realm;

public class NoteCache {

    private Realm realm;

    public NoteCache() {
        realm = Realm.getDefaultInstance();
    }

    public RealmNote getNote(String itemKey) {
        return realm.where(RealmNote.class).equalTo(RealmNote.ITEM_KEY, itemKey).findFirst();
    }

    public void setNote(String itemKey, String title, String text, String imagePath) {
        if (!realm.isClosed()) {
            final RealmNote note = new RealmNote(itemKey, title, text, imagePath);
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.insertOrUpdate(note);
                }
            });
        }
    }

    public void setNoteText(final String itemKey, final String text){
        if (!realm.isClosed()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmNote note = realm.where(RealmNote.class).equalTo(RealmNote.ITEM_KEY, itemKey).findFirst();
                    if (note == null){
                        note = new RealmNote(itemKey, "", text, "");
                    } else {
                        note.setText(text);
                    }
                    realm.insertOrUpdate(note);
                }
            });
        }
    }

    public void setNoteTitle(final String itemKey, final String title){
        if (!realm.isClosed()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmNote note = realm.where(RealmNote.class).equalTo(RealmNote.ITEM_KEY, itemKey).findFirst();
                    if (note == null){
                        note = new RealmNote(itemKey, title, "", "");
                    } else {
                        note.setTitle(title);
                    }
                    realm.insertOrUpdate(note);
                }
            });
        }
    }

    public void setNoteImagePath(final String itemKey, final String imagePath){
        if (!realm.isClosed()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmNote note = realm.where(RealmNote.class).equalTo(RealmNote.ITEM_KEY, itemKey).findFirst();
                    if (note == null){
                        note = new RealmNote(itemKey, "", "", imagePath);
                    } else {
                        note.setImagePath(imagePath);
                    }
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
