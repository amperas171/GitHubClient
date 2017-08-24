package com.amperas17.wonderstest.model.realm;


import com.amperas17.wonderstest.model.Repo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmRepo extends RealmObject {
    public static final String OWNER_LOGIN = "owner.login";

    @PrimaryKey
    private String name;
    private RealmUser owner;
    private boolean isPrivate;
    private String html_url;
    private String description;
    private String url;
    private boolean hasIssues;
    private int openIssues;
    private String clone_url;
    private String updated_at;

    public RealmRepo(Repo repo) {
        this.name = repo.getName();
        this.owner = new RealmUser(repo.getOwner());
        this.isPrivate = repo.isPrivate();
        this.html_url = repo.getHtml_url();
        this.description = repo.getDescription();
        this.url = repo.getUrl();
        this.hasIssues = repo.isHasIssues();
        this.openIssues = repo.getOpenIssues();
        this.clone_url = repo.getClone_url();
        this.updated_at = repo.getUpdated_at();
    }

    public RealmRepo() {
    }

    public Repo toRepo() {
        return new Repo(name, owner.toUser(), isPrivate, html_url, description, url, hasIssues, openIssues, clone_url, updated_at);
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmUser getOwner() {
        return owner;
    }

    public void setOwner(RealmUser owner) {
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
}
