package com.amperas17.wonderstest.ui.issues;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.data.provider.IssuesProvider;
import com.amperas17.wonderstest.data.repository.IssuesRepository;
import com.amperas17.wonderstest.model.pojo.Issue;
import com.amperas17.wonderstest.model.pojo.Repo;
import com.amperas17.wonderstest.model.realm.RealmIssue;
import com.amperas17.wonderstest.ui.note.NoteActivity;
import com.amperas17.wonderstest.ui.utils.AdapterItemLongClickListener;

import java.util.ArrayList;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class IssuesActivity extends AppCompatActivity implements IssuesProvider.IIssuesCaller {

    public static final String REPO_ARG = "user";
    public static final String IS_UPDATING_TAG = "isUpdating";

    private IssuesProvider provider;
    private IssuesRepository repository;

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

        provider = new IssuesProvider(this);
        repository = new IssuesRepository();

        initActionBar();

        tvNoData = (TextView) findViewById(R.id.tvNoData);

        initSwipe();
        initRecyclerView();

        setDataToAdapter(repository.getIssuesByRepoName(getRepoArg().getName()));

        if (savedInstanceState != null) {
            isUpdating = savedInstanceState.getBoolean(IS_UPDATING_TAG);
            if (isUpdating) {
                getIssues();
            }
        } else {
            getIssues();
        }
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setTitle(getRepoArg().getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initSwipe() {
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
        issueAdapter = new IssueAdapter(new AdapterItemLongClickListener<Issue>() {
            @Override
            public void onItemLongClick(Issue issueItem) {
                startActivity(NoteActivity.newIntent(IssuesActivity.this, issueItem));
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvIssuesList);
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
        provider.cancel();
        repository.close();
    }

    private void getIssues() {
        swipeRefreshLayout.setRefreshing(true);
        isUpdating = true;
        provider.getData(getRepoArg().getOwner().getLogin(), getRepoArg().getName());
    }

    @Override
    public void onGetIssues(ArrayList<Issue> issues) {
        onGetIssuesSuccess(issues);
    }

    @Override
    public void onError(Throwable th) {
        onGetIssuesError(th);
    }

    private void onGetIssuesSuccess(final ArrayList<Issue> issues) {
        stopRefreshing();
        if (issues != null && !issues.isEmpty()) {
            repository.setIssues(issues, getRepoArg().getName());
        } else {
            tvNoData.setVisibility(View.VISIBLE);
        }
    }

    private void onGetIssuesError(Throwable th) {
        stopRefreshing();
        Toast.makeText(this, th.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void stopRefreshing() {
        swipeRefreshLayout.setRefreshing(false);
        isUpdating = false;
    }

    private void setDataToAdapter(RealmResults<RealmIssue> issues) {
        if (issues == null) {
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            if (issues.isEmpty()) {
                tvNoData.setVisibility(View.VISIBLE);
            } else {
                tvNoData.setVisibility(View.GONE);
            }
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

    private Repo getRepoArg() {
        return getIntent().getParcelableExtra(REPO_ARG);
    }
}
