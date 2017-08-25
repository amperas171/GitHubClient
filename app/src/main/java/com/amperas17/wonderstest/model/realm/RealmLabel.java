package com.amperas17.wonderstest.model.realm;

import com.amperas17.wonderstest.model.pojo.Label;

import io.realm.RealmObject;


public class RealmLabel extends RealmObject {
    private long id;
    private String url;
    private String name;
    private String color;

    public RealmLabel(Label label) {
        this.id = label.getId();
        this.url = label.getUrl();
        this.name = label.getName();
        this.color = label.getColor();
    }

    public RealmLabel() {
    }

    public Label toLabel(){
        return new Label(id, url, name, color);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
