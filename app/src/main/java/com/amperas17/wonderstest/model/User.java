package com.amperas17.wonderstest.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {
    @SerializedName("login")
    private String login;
    @SerializedName("avatar_url")
    private String avatarUrl;
    @SerializedName("url")
    private String url;
    @SerializedName("html_url")
    private String html_url;
    @SerializedName("repos_url")
    private String reposUrl;
    @SerializedName("type")
    private String type;
    @SerializedName("company")
    private String company;


    protected User(Parcel in) {
        login = in.readString();
        avatarUrl = in.readString();
        url = in.readString();
        html_url = in.readString();
        reposUrl = in.readString();
        type = in.readString();
        company = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(login);
        dest.writeString(avatarUrl);
        dest.writeString(url);
        dest.writeString(html_url);
        dest.writeString(reposUrl);
        dest.writeString(type);
        dest.writeString(company);
    }
}
