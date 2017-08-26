package com.amperas17.wonderstest.model.realm;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmNote extends RealmObject{
    public static final String REPOSITORY_KEY = "itemKey";

    @PrimaryKey
    private String itemKey;
    private String title;
    private String text;

    public RealmNote(String itemKey, String title, String text) {
        this.itemKey = itemKey;
        this.title = title;
        this.text = text;
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
}
