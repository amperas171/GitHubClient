package com.amperas17.wonderstest.ui.note;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;

import com.amperas17.wonderstest.data.cache.NoteCache;
import com.amperas17.wonderstest.data.model.pojo.Issue;
import com.amperas17.wonderstest.data.model.pojo.Repository;
import com.amperas17.wonderstest.data.model.realm.RealmNote;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotePresenter implements INotePresenter {

    private WeakReference<INoteView> viewRef;
    private NoteCache noteCache;
    private String filePath;

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
        if (view != null) {
            filePath = getOutputFilePath();
            view.getCameraPhoto(filePath);
        }
    }

    @Override
    public void onGetImageFromGallery(Uri uri) {
        String path = null;
        if (uri.getScheme().equals("file")) {
            path = uri.getPath();
        } else if (uri.getScheme().equals("content"))
            path = getContentPath(uri);
        setImagePath(path);
    }

    private void setImagePath(String path) {
        INoteView view = viewRef.get();
        if (view != null) {
            view.setImage(path);
            noteCache.setNoteImagePath(getItemKey(view.getKeyArg()), path);
        }
    }

    @Override
    public void onGetImageFromCamera() {
        setImagePath(filePath);
    }

    private String getContentPath(Uri uri) {
        if (viewRef.get() != null && viewRef.get() instanceof Activity) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = ((Activity) viewRef.get()).managedQuery(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                filePath = cursor.getString(column_index);
                return filePath;
            }
        }
        return null;
    }

    private String getOutputFilePath() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Camera");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        filePath = mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg";
        return filePath;
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
