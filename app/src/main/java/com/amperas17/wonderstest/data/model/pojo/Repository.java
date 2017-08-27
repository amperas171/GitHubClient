package com.amperas17.wonderstest.data.model.pojo;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Repository implements Parcelable {
    @SerializedName("name")
    private String name;
    @SerializedName("owner")
    private User owner;
    @SerializedName("private")
    private boolean isPrivate;
    @SerializedName("html_url")
    private String html_url;
    @SerializedName("description")
    private String description;
    @SerializedName("url")
    private String url;
    @SerializedName("has_issues")
    private boolean hasIssues;
    @SerializedName("open_issues")
    private int openIssues;
    @SerializedName("clone_url")
    private String clone_url;
    @SerializedName("updated_at")
    private String updated_at;

    protected Repository(Parcel in) {
        name = in.readString();
        owner = in.readParcelable(User.class.getClassLoader());
        isPrivate = in.readByte() != 0;
        html_url = in.readString();
        description = in.readString();
        url = in.readString();
        hasIssues = in.readByte() != 0;
        openIssues = in.readInt();
        clone_url = in.readString();
        updated_at = in.readString();
    }

    public static final Creator<Repository> CREATOR = new Creator<Repository>() {
        @Override
        public Repository createFromParcel(Parcel in) {
            return new Repository(in);
        }

        @Override
        public Repository[] newArray(int size) {
            return new Repository[size];
        }
    };

    public Repository(String name, User owner, boolean isPrivate, String html_url, String description, String url, boolean hasIssues, int openIssues, String clone_url, String updated_at) {
        this.name = name;
        this.owner = owner;
        this.isPrivate = isPrivate;
        this.html_url = html_url;
        this.description = description;
        this.url = url;
        this.hasIssues = hasIssues;
        this.openIssues = openIssues;
        this.clone_url = clone_url;
        this.updated_at = updated_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isHasIssues() {
        return hasIssues;
    }

    public void setHasIssues(boolean hasIssues) {
        this.hasIssues = hasIssues;
    }

    public int getOpenIssues() {
        return openIssues;
    }

    public void setOpenIssues(int openIssues) {
        this.openIssues = openIssues;
    }

    public String getClone_url() {
        return clone_url;
    }

    public void setClone_url(String clone_url) {
        this.clone_url = clone_url;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        return "Repository{" +
                "name='" + name + '\'' +
                ", owner=" + owner +
                ", isPrivate=" + isPrivate +
                ", html_url='" + html_url + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", hasIssues=" + hasIssues +
                ", openIssues=" + openIssues +
                ", clone_url='" + clone_url + '\'' +
                ", updated_at='" + updated_at + '\'' +
                "}\n";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(owner, flags);
        dest.writeByte((byte) (isPrivate ? 1 : 0));
        dest.writeString(html_url);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeByte((byte) (hasIssues ? 1 : 0));
        dest.writeInt(openIssues);
        dest.writeString(clone_url);
        dest.writeString(updated_at);
    }
}
