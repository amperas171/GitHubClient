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

    public User(String login, String avatarUrl, String url, String html_url, String reposUrl, String type, String company) {
        this.login = login;
        this.avatarUrl = avatarUrl;
        this.url = url;
        this.html_url = html_url;
        this.reposUrl = reposUrl;
        this.type = type;
        this.company = company;
    }

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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }

    public String getReposUrl() {
        return reposUrl;
    }

    public void setReposUrl(String reposUrl) {
        this.reposUrl = reposUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
