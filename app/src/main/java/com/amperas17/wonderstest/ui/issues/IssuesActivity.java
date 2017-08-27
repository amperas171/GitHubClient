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
import com.amperas17.wonderstest.data.model.pojo.Issue;
import com.amperas17.wonderstest.data.model.pojo.Repository;
import com.amperas17.wonderstest.data.model.realm.RealmIssue;
import com.amperas17.wonderstest.data.provider.IssuesProvider;
import com.amperas17.wonderstest.ui.note.NoteActivity;
import com.amperas17.wonderstest.ui.utils.AdapterItemLongClickListener;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class IssuesActivity extends AppCompatActivity implements IssuesProvider.IProviderCaller /*IssuesLoader.IIssuesLoaderCaller*/  {

    public static final String REPOSITORY_ARG = "user";
    public static final String IS_UPDATING_TAG = "isUpdating";

    private IssuesProvider issuesProvider;

    private IssueAdapter issueAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoData;

    private boolean isUpdating = false;

    public static Intent newIntent(Context context, Repository repository) {
        Intent intent = new Intent(context, IssuesActivity.class);
        intent.putExtra(REPOSITORY_ARG, repository);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues);

        issuesProvider = new IssuesProvider(this);

        initActionBar();

        tvNoData = (TextView) findViewById(R.id.tvNoData);

        initSwipe();
        initRecyclerView();

        issuesProvider.getIssuesByRepository(getRepositoryArg().getName());

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
            actionBar.setTitle(getRepositoryArg().getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
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
        issuesProvider.close();
    }

    private void getIssues() {
        swipeRefreshLayout.setRefreshing(true);
        isUpdating = true;
        issuesProvider.getIssuesAndUpdate(getRepositoryArg().getOwner().getLogin(), getRepositoryArg().getName());
    }

    @Override
    public void onProviderCallSuccess(RealmResults<RealmIssue> issues) {
        stopRefreshing();
        setDataToAdapter(issues);
    }

    @Override
    public void onProviderCallError(Throwable th) {
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

    private Repository getRepositoryArg() {
        return getIntent().getParcelableExtra(REPOSITORY_ARG);
    }
}
