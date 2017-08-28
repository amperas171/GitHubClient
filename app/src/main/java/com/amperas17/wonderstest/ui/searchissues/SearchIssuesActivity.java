package com.amperas17.wonderstest.ui.searchissues;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.data.model.pojo.Issue;
import com.amperas17.wonderstest.data.model.realm.RealmIssue;
import com.amperas17.wonderstest.data.provider.IssuesProvider;
import com.amperas17.wonderstest.ui.issues.IssueAdapter;
import com.amperas17.wonderstest.ui.note.NoteActivity;
import com.amperas17.wonderstest.ui.utils.AdapterItemLongClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

public class SearchIssuesActivity extends AppCompatActivity implements IssuesProvider.IProviderCaller {

    public static final String SEARCH_QUERY = "query";
    public static final String IS_SEARCHING_TAG = "isSearching";

    private IssuesProvider issuesProvider;
    private IssueAdapter issueAdapter;

    @BindView(R.id.tvNoData) TextView tvNoData;
    private SearchView searchView;

    private boolean isSearching = false;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_issues);
        ButterKnife.bind(this);

        issuesProvider = new IssuesProvider(this);

        initActionBar();
        initRecyclerView();

        issuesProvider.getIssues();

        if (savedInstanceState != null) {
            isSearching = savedInstanceState.getBoolean(IS_SEARCHING_TAG, false);
            if (isSearching) {
                query = savedInstanceState.getString(SEARCH_QUERY);
            }
        }
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.search));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initRecyclerView() {
        issueAdapter = new IssueAdapter(new AdapterItemLongClickListener<Issue>() {
            @Override
            public void onItemLongClick(Issue issueItem) {
                startActivity(NoteActivity.newIntent(SearchIssuesActivity.this, issueItem));
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvIssuesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(issueAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEARCH_QUERY, query);
        outState.putBoolean(IS_SEARCHING_TAG, !searchView.isIconified());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.issues_search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                onQueryTextChanged(newText);
                return true;
            }
        });

        if (isSearching) {
            menuItem.expandActionView();
            searchView.setQuery(query, true);
            searchView.setIconified(false);
        }

        return true;
    }

    @Override
    public void onProviderCallSuccess(RealmResults<RealmIssue> issues) {
        setDataToAdapter(issues);
    }

    @Override
    public void onProviderCallError(Throwable th) {

    }

    private void onQueryTextChanged(String pattern) {
        query = pattern;
        if (!pattern.isEmpty()) {
            issuesProvider.getSearchedIssues(pattern);
        } else {
            issuesProvider.getIssues();
        }
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
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
            issuesProvider.getIssues();
        } else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        issuesProvider.close();
    }

    private void setDataToAdapter(RealmResults<RealmIssue> issues) {
        if (issues == null || issues.isEmpty()) {
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            tvNoData.setVisibility(View.GONE);
        }
        issueAdapter.setIssues(issues);
        issueAdapter.notifyDataSetChanged();
    }
}
