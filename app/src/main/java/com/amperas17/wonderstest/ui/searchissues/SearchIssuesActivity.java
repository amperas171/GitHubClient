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
import com.amperas17.wonderstest.data.cache.IssuesCache;
import com.amperas17.wonderstest.data.model.pojo.Issue;
import com.amperas17.wonderstest.data.model.realm.RealmIssue;
import com.amperas17.wonderstest.ui.issues.IssueAdapter;
import com.amperas17.wonderstest.ui.note.NoteActivity;
import com.amperas17.wonderstest.ui.utils.AdapterItemLongClickListener;

import io.realm.RealmResults;

public class SearchIssuesActivity extends AppCompatActivity {

    public static final String SEARCH_QUERY = "query";
    public static final String IS_SEARCHING_TAG = "isSearching";

    private IssuesCache issuesCache;

    private IssueAdapter issueAdapter;

    private TextView tvNoData;
    private SearchView searchView;

    private boolean isSearching = false;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_issues);

        issuesCache = new IssuesCache();

        initActionBar();

        tvNoData = (TextView) findViewById(R.id.tvNoData);

        initRecyclerView();

        setDataToAdapter(issuesCache.getAllIssues());

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
            getSupportActionBar().setTitle(getString(R.string.search));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    private void onQueryTextChanged(String pattern) {
        query = pattern;
        if (!pattern.isEmpty()) {
            setDataToAdapter(issuesCache.getSearchedIssues(pattern));
        } else {
            setDataToAdapter(issuesCache.getAllIssues());
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
            setDataToAdapter(issuesCache.getAllIssues());
        } else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        issuesCache.close();
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
