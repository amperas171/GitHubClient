package com.amperas17.wonderstest.ui.issues;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amperas17.wonderstest.App;
import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.model.Issue;
import com.amperas17.wonderstest.model.Repo;
import com.amperas17.wonderstest.model.realm.RealmIssue;
import com.amperas17.wonderstest.ui.AdapterItemClickListener;
import com.amperas17.wonderstest.ui.note.NoteActivity;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class IssuesActivity extends AppCompatActivity {
    public static final String REPO_ARG = "user";

    public static final String IS_UPDATING_TAG = "isUpdating";

    private Call<ArrayList<Issue>> call;
    private Realm realm;
    private RecyclerView recyclerView;
    private IssueAdapter issueAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoData;

    private boolean isUpdating = false;

    public static Intent newIntent(Context context, Repo repo) {
        Intent intent = new Intent(context, IssuesActivity.class);
        intent.putExtra(REPO_ARG, repo);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues);

        realm = Realm.getDefaultInstance();

        getSupportActionBar().setTitle(getRepoArg().getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvNoData = (TextView) findViewById(R.id.tvNoData);

        initSwipe();
        initRecyclerView();

        setDataToAdapter(getRepoIssues());

        if (savedInstanceState != null) {
            isUpdating = savedInstanceState.getBoolean(IS_UPDATING_TAG);
            if (isUpdating) { getIssues(); }
        } else {
            getIssues();
        }
    }

    private void initSwipe(){
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getIssues();
            }
        });
    }

    private void initRecyclerView() {
        issueAdapter = new IssueAdapter(new AdapterItemClickListener<Issue>() {
            @Override
            public void onItemClick(Issue issueItem) {
            }

            @Override
            public void onItemLongClick(Issue issueItem) {
                startActivity(NoteActivity.newIntent(IssuesActivity.this, issueItem));
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.rvIssuesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(issueAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_UPDATING_TAG, isUpdating);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
            call = null;
        }
        realm.close();
    }


    private void getIssues() {
        swipeRefreshLayout.setRefreshing(true);
        isUpdating = true;
        call = App.getGitHubApi().getIssues(getRepoArg().getOwner().getLogin(), getRepoArg().getName());
        call.enqueue(new Callback<ArrayList<Issue>>() {
            @Override
            public void onResponse(Call<ArrayList<Issue>> call, Response<ArrayList<Issue>> response) {
                onGetIssuesSuccess(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Issue>> call, Throwable t) {
                if (!call.isCanceled())
                    onGetIssuesError();
            }
        });
    }

    private void onGetIssuesSuccess(final ArrayList<Issue> issues) {
        if (issues != null && !issues.isEmpty()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (Issue issue : issues) {
                        RealmIssue realmIssue = new RealmIssue(issue);
                        realmIssue.setRepoName(getRepoArg().getName());
                        realm.insertOrUpdate(realmIssue);
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    stopRefreshing();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    stopRefreshing();
                }
            });
        } else {
            stopRefreshing();
            tvNoData.setVisibility(View.VISIBLE);
        }
    }

    private void onGetIssuesError() {
        stopRefreshing();
        Toast.makeText(this, R.string.error_occurred_toast, Toast.LENGTH_SHORT).show();
    }

    private void stopRefreshing() {
        swipeRefreshLayout.setRefreshing(false);
        isUpdating = false;
    }

    private void setDataToAdapter(RealmResults<RealmIssue> issues) {
        if (issues == null || issues.isEmpty()) {
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            tvNoData.setVisibility(View.GONE);
            issueAdapter.setIssues(issues);
            issueAdapter.notifyDataSetChanged();

            issues.addChangeListener(new RealmChangeListener<RealmResults<RealmIssue>>() {
                @Override
                public void onChange(RealmResults<RealmIssue> realmIssues) {
                    if (!realmIssues.isEmpty()) {
                        tvNoData.setVisibility(View.GONE);
                        issueAdapter.notifyDataSetChanged();
                    } else {
                        tvNoData.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private RealmResults<RealmIssue> getRepoIssues() {
        return realm.where(RealmIssue.class)
                .equalTo(RealmIssue.REPO_NAME, getRepoArg().getName())
                .findAll();

    }

    private Repo getRepoArg(){
        return getIntent().getParcelableExtra(REPO_ARG);
    }
}
