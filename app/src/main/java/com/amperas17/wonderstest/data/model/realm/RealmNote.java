package com.amperas17.wonderstest.data.model.realm;


import com.amperas17.wonderstest.data.model.pojo.Note;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmNote extends RealmObject {
    public static final String ITEM_KEY = "itemKey";

    @PrimaryKey
    private String itemKey;
    private String title;
    private String text;
    private String imagePath;

    public RealmNote(String itemKey, String title, String text, String imagePath) {
        this.itemKey = itemKey;
        this.title = title;
        this.text = text;
        this.imagePath = imagePath;
    }

    public RealmNote(Note note) {
        this.itemKey = note.getItemKey();
        this.title = note.getTitle();
        this.text = note.getText();
        this.imagePath = note.getImagePath();
    }

    public Note toNote() {
        return new Note(itemKey, title, text, imagePath);
    }

    public RealmNote() {
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
