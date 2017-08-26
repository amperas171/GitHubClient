package com.amperas17.wonderstest.model.realm;


import com.amperas17.wonderstest.model.pojo.Repository;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmRepository extends RealmObject {
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

    public RealmRepository(Repository repository) {
        this.name = repository.getName();
        this.owner = new RealmUser(repository.getOwner());
        this.isPrivate = repository.isPrivate();
        this.html_url = repository.getHtml_url();
        this.description = repository.getDescription();
        this.url = repository.getUrl();
        this.hasIssues = repository.isHasIssues();
        this.openIssues = repository.getOpenIssues();
        this.clone_url = repository.getClone_url();
        this.updated_at = repository.getUpdated_at();
    }

    public RealmRepository() {
    }

    public Repository toRepository() {
        return new Repository(name, owner.toUser(), isPrivate, html_url, description, url, hasIssues, openIssues, clone_url, updated_at);
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
