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
import com.amperas17.wonderstest.model.Issue;
import com.amperas17.wonderstest.model.realm.RealmIssue;
import com.amperas17.wonderstest.ui.AdapterItemClickListener;
import com.amperas17.wonderstest.ui.issues.IssueAdapter;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

public class SearchIssuesActivity extends AppCompatActivity {

    public static final String SEARCH_QUERY = "query";
    public static final String IS_SEARCHING_TAG = "isSearching";

    private Realm realm;
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

        realm = Realm.getDefaultInstance();
        getSupportActionBar().setTitle(getString(R.string.search));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvNoData = (TextView) findViewById(R.id.tvNoData);

        initRecyclerView();

        setDataToAdapter(getAllIssues());

        if (savedInstanceState != null) {
            isSearching = savedInstanceState.getBoolean(IS_SEARCHING_TAG, false);
            if (isSearching) {
                query = savedInstanceState.getString(SEARCH_QUERY);
            }
        }
    }

    private void initRecyclerView() {
        issueAdapter = new IssueAdapter(new AdapterItemClickListener<Issue>() {
            @Override
            public void onItemClick(Issue issueItem) {
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
            setDataToAdapter(getSearchedIssues(pattern));
        } else {
            setDataToAdapter(getAllIssues());
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
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }


    private void setDataToAdapter(RealmResults<RealmIssue> issues) {
        if (issues == null || issues.isEmpty()) {
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            tvNoData.setVisibility(View.GONE);
            issueAdapter.setIssues(issues);
            issueAdapter.notifyDataSetChanged();
        }
    }

    private RealmResults<RealmIssue> getAllIssues() {
        return realm.where(RealmIssue.class).findAll();
    }

    private RealmResults<RealmIssue> getSearchedIssues(String pattern) {
        return realm.where(RealmIssue.class)
                .contains(RealmIssue.TITLE, pattern, Case.INSENSITIVE)
                .findAllSorted(RealmIssue.TITLE);
    }

}
