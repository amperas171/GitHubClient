package com.amperas17.wonderstest.ui.note;


import android.os.Parcelable;

public interface INotePresenter {
    void onCreate();

    String getItemName();

    void saveNote(String title, String text, String imagePath);

    void saveNoteTitle(String title);

    void saveNoteText(String text);

    void saveNoteImagePath(String imagePath);

    void onImageClick();

    void onImageLongClick();

    void onGetImagePathFromGallery(String path);

    void onDestroy();
}
