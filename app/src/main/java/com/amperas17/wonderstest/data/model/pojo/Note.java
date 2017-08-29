package com.amperas17.wonderstest.data.model.pojo;


public class Note {
    private String itemKey;
    private String title;
    private String text;
    private String imagePath;

    public Note(String itemKey, String title, String text, String imagePath) {
        this.itemKey = itemKey;
        this.title = title;
        this.text = text;
        this.imagePath = imagePath;
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
