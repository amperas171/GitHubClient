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
import com.amperas17.wonderstest.data.model.pojo.Repository;
import com.amperas17.wonderstest.data.model.pojo.User;
import com.amperas17.wonderstest.data.model.realm.RealmRepository;
import com.amperas17.wonderstest.ui.utils.AdapterItemClicksListener;
import com.amperas17.wonderstest.ui.utils.LoadingDialog;
import com.amperas17.wonderstest.ui.auth.AuthActivity;
import com.amperas17.wonderstest.ui.issues.IssuesActivity;
import com.amperas17.wonderstest.ui.note.NoteActivity;
import com.amperas17.wonderstest.ui.searchissues.SearchIssuesActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class RepositoriesActivity extends AppCompatActivity implements IRepositoryView, LoadingDialog.ILoadingDialog {
    static final String USER_ARG = "user";
    public static final String IS_UPDATING_TAG = "isRefreshing";

    private IRepositoryPresenter presenter;

    private RepositoryAdapter repositoryAdapter;

    @BindView(R.id.tvNoData)
    TextView tvNoData;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private boolean isRefreshing = false;

    public static Intent newIntent(Context context, User user) {
        Intent intent = new Intent(context, RepositoriesActivity.class);
        intent.putExtra(USER_ARG, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);

        presenter = new RepositoryPresenter(this);

        initActionBar();
        initSwipe();
        initRecyclerView();

        if (savedInstanceState != null) {
            isRefreshing = savedInstanceState.getBoolean(IS_UPDATING_TAG);
            if (isRefreshing) {
                refresh();
            }
        } else {
            refresh();
        }
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(presenter.getActionBarTitle(getUserArg()));
            actionBar.setLogo(R.drawable.ic_git_small);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayUseLogoEnabled(true);
        }
    }

    private void initSwipe() {
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void initRecyclerView() {
        repositoryAdapter = new RepositoryAdapter(new AdapterItemClicksListener<Repository>() {
            @Override
            public void onItemClick(Repository repositoryItem) {
                presenter.onRepositoryItemClick(repositoryItem);
            }

            @Override
            public void onItemLongClick(Repository repositoryItem) {
                presenter.onRepositoryItemLongClick(repositoryItem);
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvRepositoryList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(repositoryAdapter);
    }

    @Override
    public void openIssuesActivity(Repository repositoryItem) {
        startActivity(IssuesActivity.newIntent(RepositoriesActivity.this, repositoryItem));
    }

    @Override
    public void openNoteActivity(Repository repositoryItem) {
        startActivity(NoteActivity.newIntent(RepositoriesActivity.this, repositoryItem));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_UPDATING_TAG, isRefreshing);
    }

    private void refresh() {
        presenter.getRepositoriesAndUpdate(getUserArg());
    }

    @Override
    public void showError(Throwable th) {
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
                presenter.onSearchIconSelected();
                return true;
            case R.id.exit:
                presenter.onExitIconSelected();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void openSearchIssuesActivity() {
        startActivity(new Intent(this, SearchIssuesActivity.class));
    }

    @Override
    public void showExitDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(R.string.exit_alert_message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.exit();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    @Override
    public void openAuthActivity() {
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }

    @Override
    public void updateAdapter() {
        repositoryAdapter.notifyDataSetChanged();
    }


    @Override
    public void onLoadingDialogCancel() {
        presenter.cancelLoading();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void showLoading() {
        LoadingDialog.show(getSupportFragmentManager());
    }

    @Override
    public void hideLoading() {
        LoadingDialog.dismiss(getSupportFragmentManager());
    }

    @Override
    public void hideRefreshing() {
        swipeRefreshLayout.setRefreshing(false);
        isRefreshing = false;
    }

    @Override
    public void showRefreshing() {
        swipeRefreshLayout.setRefreshing(true);
        isRefreshing = true;
    }

    @Override
    public void setDataToAdapter(RealmResults<RealmRepository> repositories) {
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

    private User getUserArg() {
        return getIntent().getParcelableExtra(USER_ARG);
    }

}
