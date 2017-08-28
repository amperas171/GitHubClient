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
import com.amperas17.wonderstest.ui.note.NoteActivity;
import com.amperas17.wonderstest.ui.utils.AdapterItemLongClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class IssuesActivity extends AppCompatActivity implements IIssuesView {

    public static final String REPOSITORY_ARG = "user";
    public static final String IS_UPDATING_TAG = "isUpdating";

    private IssuesPresenter presenter;
    private IssueAdapter issueAdapter;

    @BindView(R.id.tvNoData)
    TextView tvNoData;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

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
        ButterKnife.bind(this);

        presenter = new IssuesPresenter(this);

        initActionBar();
        initSwipe();
        initRecyclerView();

        if (savedInstanceState != null) {
            isUpdating = savedInstanceState.getBoolean(IS_UPDATING_TAG);
            if (isUpdating) {
                getIssues();
            } else {
                presenter.getIssuesByRepository(getRepositoryArg());
            }
        } else {
            getIssues();
        }
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(presenter.getActionBarTitle(getRepositoryArg()));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initSwipe() {
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getIssuesAndUpdate(getRepositoryArg());
            }
        });
    }

    private void initRecyclerView() {
        issueAdapter = new IssueAdapter(new AdapterItemLongClickListener<Issue>() {
            @Override
            public void onItemLongClick(Issue issueItem) {
                presenter.onIssueLongItemClick(issueItem);
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvIssuesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(issueAdapter);
    }

    @Override
    public void openNoteActivity(Issue issueItem) {
        startActivity(NoteActivity.newIntent(IssuesActivity.this, issueItem));
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
        presenter.onDestroy();
    }

    private void getIssues() {
        presenter.getIssuesAndUpdate(getRepositoryArg());
    }

    @Override
    public void showError(Throwable th) {
        Toast.makeText(this, th.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showRefreshing() {
        swipeRefreshLayout.setRefreshing(true);
        isUpdating = true;
    }

    @Override
    public void hideRefreshing() {
        swipeRefreshLayout.setRefreshing(false);
        isUpdating = false;
    }

    @Override
    public void setDataToAdapter(RealmResults<RealmIssue> issues) {
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
