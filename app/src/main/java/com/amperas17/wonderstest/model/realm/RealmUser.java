package com.amperas17.wonderstest.model.realm;


import com.amperas17.wonderstest.model.pojo.User;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmUser extends RealmObject {

    @PrimaryKey
    private String login;
    private String avatarUrl;
    private String url;
    private String html_url;
    private String reposUrl;
    private String type;
    private String company;
    private String authHeader;


    public RealmUser() {
    }

    public RealmUser(User user) {
        this.login = user.getLogin();
        this.avatarUrl = user.getAvatarUrl();
        this.url = user.getUrl();
        this.html_url = user.getHtml_url();
        this.reposUrl = user.getReposUrl();
        this.type = user.getType();
        this.company = user.getCompany();
        this.authHeader = user.getAuthHeader();
    }

    public User toUser() {
        return new User(login, avatarUrl, url, html_url, reposUrl, type, company, authHeader);
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

    public String getAuthHeader() {
        return authHeader;
    }

    public void setAuthHeader(String authHeader) {
        this.authHeader = authHeader;
    }

}
