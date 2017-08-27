package com.amperas17.wonderstest.data.model.pojo;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Issue implements Parcelable {

    @SerializedName("title")
    private String title;
    @SerializedName("id")
    private Long id;
    @SerializedName("html_url")
    private String html_url;
    private String repositoryName;
    @SerializedName("user")
    private User user;
    @SerializedName("labels")
    private ArrayList<Label> labels;
    @SerializedName("state")
    private String state;
    @SerializedName("body")
    private String body;


    public Issue(String title, Long id, String html_url, String repositoryName, User user, ArrayList<Label> labels, String state, String body) {
        this.title = title;
        this.id = id;
        this.html_url = html_url;
        this.repositoryName = repositoryName;
        this.user = user;
        this.labels = labels;
        this.state = state;
        this.body = body;
    }

    protected Issue(Parcel in) {
        title = in.readString();
        id = in.readLong();
        html_url = in.readString();
        repositoryName = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        labels = in.createTypedArrayList(Label.CREATOR);
        state = in.readString();
        body = in.readString();
    }

    public static final Creator<Issue> CREATOR = new Creator<Issue>() {
        @Override
        public Issue createFromParcel(Parcel in) {
            return new Issue(in);
        }

        @Override
        public Issue[] newArray(int size) {
            return new Issue[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<Label> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<Label> labels) {
        this.labels = labels;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeLong(id);
        dest.writeString(html_url);
        dest.writeString(repositoryName);
        dest.writeParcelable(user, flags);
        dest.writeTypedList(labels);
        dest.writeString(state);
        dest.writeString(body);
    }

    @Override
    public String toString() {
        return "Issue{" +
                "title='" + title + '\'' +
                ", id=" + id +
                ", html_url='" + html_url + '\'' +
                ", repositoryName='" + repositoryName + '\'' +
                ", user=" + user +
                ", labels=" + labels +
                ", state='" + state + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
