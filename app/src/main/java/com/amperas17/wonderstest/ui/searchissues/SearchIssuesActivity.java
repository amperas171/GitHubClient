package com.amperas17.wonderstest.ui.searchissues;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.data.repository.IssuesRepository;
import com.amperas17.wonderstest.model.pojo.Issue;
import com.amperas17.wonderstest.model.realm.RealmIssue;
import com.amperas17.wonderstest.ui.issues.IssueAdapter;
import com.amperas17.wonderstest.ui.note.NoteActivity;
import com.amperas17.wonderstest.ui.utils.AdapterItemLongClickListener;

import io.realm.RealmResults;

public class SearchIssuesActivity extends AppCompatActivity {

    public static final String SEARCH_QUERY = "query";
    public static final String IS_SEARCHING_TAG = "isSearching";

    private IssuesRepository repository;
    private RecyclerView recyclerView;

    private IssueAdapter issueAdapter;

    private TextView tvNoData;
    private SearchView searchView;

    private boolean isSearching = false;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_issues);

        repository = new IssuesRepository();

        getSupportActionBar().setTitle(getString(R.string.search));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvNoData = (TextView) findViewById(R.id.tvNoData);

        initRecyclerView();

        setDataToAdapter(repository.getAllIssues());

        if (savedInstanceState != null) {
            isSearching = savedInstanceState.getBoolean(IS_SEARCHING_TAG, false);
            if (isSearching) {
                query = savedInstanceState.getString(SEARCH_QUERY);
            }
        }
    }

    private void initRecyclerView() {
        issueAdapter = new IssueAdapter(new AdapterItemLongClickListener<Issue>() {
            @Override
            public void onItemLongClick(Issue issueItem) {
                startActivity(NoteActivity.newIntent(SearchIssuesActivity.this, issueItem));
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.rvIssuesList);
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
            setDataToAdapter(repository.getSearchedIssues(pattern));
        } else {
            setDataToAdapter(repository.getAllIssues());
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
            searchView.setIconified(true);
            setDataToAdapter(repository.getAllIssues());
        } else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        repository.close();
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
