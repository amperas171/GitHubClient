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
    public void onCreate() {
        INoteView view = viewRef.get();
        if (view != null) {
            getNote(view.getKeyArg());
        }
    }

    private void getNote(Parcelable item) {
        RealmNote realmNote = noteCache.getNote(getItemKey(item));
        if (realmNote != null) {
            INoteView view = viewRef.get();
            if (view != null)
                view.reflectNote(realmNote.toNote());
        }
    }

    private String getItemKey(Parcelable item) {
        if (item instanceof Repository) return ((Repository) item).getName();
        if (item instanceof Issue) return ((Issue) item).getId().toString();
        return "";
    }

    @Override
    public String getItemName() {
        INoteView view = viewRef.get();
        if (view != null) {
            Parcelable item = view.getKeyArg();
            if (item instanceof Repository) return ((Repository) item).getName();
            if (item instanceof Issue) return ((Issue) item).getTitle();
        }
        return "";
    }

    @Override
    public void onImageClick() {
        INoteView view = viewRef.get();
        if (view != null)
            view.getPicture();
    }

    @Override
    public void onImageLongClick() {
        INoteView view = viewRef.get();
        if (view != null)
            view.getCameraPhoto();
    }

    @Override
    public void onGetImagePathFromGallery(String path) {
        INoteView view = viewRef.get();
        if (view != null) {
            view.setImage(path);
            noteCache.setNoteImagePath(getItemKey(view.getKeyArg()), path);
        }
    }

    @Override
    public void saveNote(String title, String text, String imagePath) {
        INoteView view = viewRef.get();
        if (view != null) {
            noteCache.setNote(getItemKey(view.getKeyArg()), title, text, imagePath);
        }
    }

    @Override
    public void saveNoteTitle(String title) {
        INoteView view = viewRef.get();
        if (view != null) {
            noteCache.setNoteTitle(getItemKey(view.getKeyArg()), title);
        }
    }

    @Override
    public void saveNoteText(String text) {
        INoteView view = viewRef.get();
        if (view != null) {
            noteCache.setNoteText(getItemKey(view.getKeyArg()), text);
        }
    }

    @Override
    public void saveNoteImagePath(String imagePath) {
        INoteView view = viewRef.get();
        if (view != null) {
            noteCache.setNoteImagePath(getItemKey(view.getKeyArg()), imagePath);
        }
    }

    @Override
    public void onDestroy() {
        noteCache.close();
    }
}
