package com.amperas17.wonderstest.model.realm;

import com.amperas17.wonderstest.model.Issue;
import com.amperas17.wonderstest.model.Label;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class RealmIssue extends RealmObject {
    public static final String REPO_NAME = "repoName";
    public static final String TITLE = "title";

    private String title;
    @PrimaryKey
    private Long id;
    private String html_url;
    private String repoName;
    private RealmUser user;
    private RealmList<RealmLabel> labels;
    private String state;
    private String body;

    public RealmIssue(Issue issue) {
        this.title = issue.getTitle();
        this.id = issue.getId();
        this.html_url = issue.getHtml_url();
        this.repoName = issue.getRepoName();
        this.user = new RealmUser(issue.getUser());
        this.state = issue.getState();
        this.body = issue.getBody();
        this.labels = getRealmLabels(issue.getLabels());
    }

    public RealmIssue() {
    }

    public Issue toIssue() {
        return new Issue(title, id, html_url, repoName, user.toUser(), getPlainLabels(), state, body);
    }

    public RealmList<RealmLabel> getRealmLabels(ArrayList<Label> plainLabels){
        RealmList<RealmLabel> realmLabels = new RealmList<>();
        for (Label label : plainLabels) {
            realmLabels.add(new RealmLabel(label));
        }
        return realmLabels;
    }

    public ArrayList<Label> getPlainLabels(){
        ArrayList<Label> labels = new ArrayList<>();
        RealmList<RealmLabel> realmLabels = this.labels;
        for (RealmLabel label : realmLabels) {
            labels.add(label.toLabel());
        }
        return labels;
    }

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

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public RealmUser getUser() {
        return user;
    }

    public void setUser(RealmUser user) {
        this.user = user;
    }

    public RealmList<RealmLabel> getLabels() {
        return labels;
    }

    public void setLabels(RealmList<RealmLabel> labels) {
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
}
