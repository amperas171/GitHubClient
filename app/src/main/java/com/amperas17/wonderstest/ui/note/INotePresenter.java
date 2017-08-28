package com.amperas17.wonderstest.ui.note;


import android.os.Parcelable;

public interface INotePresenter {
    void onCreate(Parcelable item);

    String getItemKey(Parcelable item);

    String getItemName(Parcelable item);

    void saveNote(Parcelable keyArg, String title, String text);

    void onDestroy();
}
