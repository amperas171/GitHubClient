package com.amperas17.wonderstest.ui.repositories;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.data.provider.RepositoriesProvider;
import com.amperas17.wonderstest.data.cache.RepositoryCache;
import com.amperas17.wonderstest.data.cache.CacheEraser;
import com.amperas17.wonderstest.model.pojo.Repository;
import com.amperas17.wonderstest.model.pojo.User;
import com.amperas17.wonderstest.model.realm.RealmRepository;
import com.amperas17.wonderstest.ui.utils.AdapterItemClicksListener;
import com.amperas17.wonderstest.ui.utils.LoadingDialog;
import com.amperas17.wonderstest.ui.auth.AuthActivity;
import com.amperas17.wonderstest.ui.issues.IssuesActivity;
import com.amperas17.wonderstest.ui.note.NoteActivity;
import com.amperas17.wonderstest.ui.searchissues.SearchIssuesActivity;

import java.util.ArrayList;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class RepositoriesActivity extends AppCompatActivity implements LoadingDialog.ILoadingDialog,
        RepositoriesProvider.IRepositoriesCaller, CacheEraser.IEraseCaller {

    public static final String USER_ARG = "user";
    public static final String IS_UPDATING_TAG = "isUpdating";

    private RepositoryCache repositoryCache;
    private CacheEraser cacheEraser;
    private RepositoriesProvider repositoriesProvider;
    private RepositoryAdapter repositoryAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoData;

    private boolean isUpdating = false;

    public static Intent newIntent(Context context, User user) {
        Intent intent = new Intent(context, RepositoriesActivity.class);
        intent.putExtra(USER_ARG, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        repositoryCache = new RepositoryCache();
        cacheEraser = new CacheEraser(this);
        repositoriesProvider = new RepositoriesProvider(this);

        initActionBar();

        tvNoData = (TextView) findViewById(R.id.tvNoData);

        initSwipe();
        initRecyclerView();

        setDataToAdapter(repositoryCache.getRepositories(getUserArg().getLogin()));

        if (savedInstanceState != null) {
            isUpdating = savedInstanceState.getBoolean(IS_UPDATING_TAG);
            if (isUpdating) {
                getRepositories();
            }
        } else {
            getRepositories();
        }
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setTitle(getUserArg().getLogin());
        }
    }

    private void initSwipe(){
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRepositories();
            }
        });
    }

    private void initRecyclerView() {
        repositoryAdapter = new RepositoryAdapter(new AdapterItemClicksListener<Repository>() {
            @Override
            public void onItemClick(Repository repositoryItem) {
                startActivity(IssuesActivity.newIntent(RepositoriesActivity.this, repositoryItem));
            }

            @Override
            public void onItemLongClick(Repository repositoryItem) {
                startActivity(NoteActivity.newIntent(RepositoriesActivity.this, repositoryItem));
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvRepositoryList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(repositoryAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_UPDATING_TAG, isUpdating);
    }

    private void getRepositories() {
        swipeRefreshLayout.setRefreshing(true);
        isUpdating = true;
        repositoriesProvider.getData(getUserArg().getAuthHeader(), getUserArg().getLogin());
    }

    @Override
    public void onGetRepositories(ArrayList<Repository> repositories) {
        doOnGetRepositories(repositories);
    }

    @Override
    public void onGetRepositoriesError(Throwable th) {
        doOnGetRepositoriesError(th);
    }

    private void doOnGetRepositories(final ArrayList<Repository> repositories) {
        stopRefreshing();
        if (repositories != null && !repositories.isEmpty()) {
            repositoryCache.setRepositories(repositories);
        }
    }

    private void doOnGetRepositoriesError(Throwable th) {
        stopRefreshing();
        Toast.makeText(this, th.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                startActivity(new Intent(this, SearchIssuesActivity.class));
                return true;
            case R.id.exit:
                showExitDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        repositoriesProvider.cancel();
        repositoryCache.close();
        cacheEraser.close();
    }

    private void showExitDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(R.string.exit_alert_message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exit();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void exit() {
        LoadingDialog.show(getSupportFragmentManager());
        cacheEraser.erase();
    }

    @Override
    public void onDeleted() {
        onDeleteDataSuccess();
    }

    @Override
    public void onDeletedError(Throwable error) {
        onDeleteDataError();
    }

    private void onDeleteDataSuccess() {
        LoadingDialog.dismiss(getSupportFragmentManager());
        repositoryAdapter.notifyDataSetChanged();
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }

    private void onDeleteDataError() {
        LoadingDialog.dismiss(getSupportFragmentManager());
        Toast.makeText(this, R.string.error_occurred_toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadingDialogCancel() {
        repositoriesProvider.cancel();
    }

    private void stopRefreshing() {
        swipeRefreshLayout.setRefreshing(false);
        isUpdating = false;
    }

    private void setDataToAdapter(RealmResults<RealmRepository> repositories) {
        if (repositories == null) {
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            if (repositories.isEmpty()) {
                tvNoData.setVisibility(View.VISIBLE);
            } else {
                tvNoData.setVisibility(View.GONE);
            }
            repositoryAdapter.setRepositories(repositories);
            repositoryAdapter.notifyDataSetChanged();

            repositories.addChangeListener(new RealmChangeListener<RealmResults<RealmRepository>>() {
                @Override
                public void onChange(RealmResults<RealmRepository> realmRepositories) {
                    if (!realmRepositories.isEmpty()) {
                        tvNoData.setVisibility(View.GONE);
                        repositoryAdapter.notifyDataSetChanged();
                    } else {
                        tvNoData.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private User getUserArg(){
        return getIntent().getParcelableExtra(USER_ARG);
    }
}
