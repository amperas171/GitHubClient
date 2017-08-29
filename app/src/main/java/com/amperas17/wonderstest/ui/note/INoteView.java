package com.amperas17.wonderstest.ui.note;


import android.graphics.Bitmap;
import android.os.Parcelable;

import com.amperas17.wonderstest.data.model.pojo.Note;

public interface INoteView {
    void reflectNote(Note note);
    void getPicture();
    void getCameraPhoto();
    void setImage(Bitmap bitmap);
    void setImage(String path);
    String getText();
    String getTitleText();
    Parcelable getKeyArg();
}
