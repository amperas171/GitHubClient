package com.amperas17.wonderstest.ui.note;


import android.os.Parcelable;

import com.amperas17.wonderstest.data.cache.NoteCache;
import com.amperas17.wonderstest.data.model.pojo.Issue;
import com.amperas17.wonderstest.data.model.pojo.Repository;
import com.amperas17.wonderstest.data.model.realm.RealmNote;

import java.lang.ref.WeakReference;

public class NotePresenter implements INotePresenter {

    private WeakReference<INoteView> viewRef;
    private NoteCache noteCache;

    public NotePresenter(INoteView view) {
        this.viewRef = new WeakReference<>(view);
        this.noteCache = new NoteCache();
    }

    @Override
    public void onCreate(Parcelable item) {
        getNote(item);
    }

    private void getNote(Parcelable item){
        RealmNote realmNote = noteCache.getNote(getItemKey(item));
        if (realmNote != null && viewRef.get()!=null) {
            viewRef.get().reflectNote(realmNote.toNote());
        }
    }

    @Override
    public String getItemKey(Parcelable item) {
        if (item instanceof Repository) return ((Repository) item).getName();
        if (item instanceof Issue) return ((Issue) item).getId().toString();
        return "";
    }

    @Override
    public String getItemName(Parcelable item) {
        if (item instanceof Repository) return ((Repository) item).getName();
        if (item instanceof Issue) return ((Issue) item).getTitle();
        return "";
    }

    @Override
    public void saveNote(Parcelable keyArg, String title, String text) {
        noteCache.setNote(getItemKey(keyArg), title, text);
    }

    @Override
    public void onDestroy() {
        noteCache.close();
    }
}
