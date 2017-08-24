package com.amperas17.wonderstest.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Label implements Parcelable {

    @SerializedName("id")
    private long id;
    @SerializedName("url")
    private String url;
    @SerializedName("name")
    private String name;
    @SerializedName("color")
    private String color;

    protected Label(Parcel in) {
        id = in.readLong();
        url = in.readString();
        name = in.readString();
        color = in.readString();
    }

    public static final Creator<Label> CREATOR = new Creator<Label>() {
        @Override
        public Label createFromParcel(Parcel in) {
            return new Label(in);
        }

        @Override
        public Label[] newArray(int size) {
            return new Label[size];
        }
    };

    public Label(long id, String url, String name, String color) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.color = color;
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

    @Override
    public String toString() {
        return "Label{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(url);
        dest.writeString(name);
        dest.writeString(color);
    }
}
