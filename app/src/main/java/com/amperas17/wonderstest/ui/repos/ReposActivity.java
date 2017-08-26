package com.amperas17.wonderstest.ui.repos;

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
import com.amperas17.wonderstest.data.provider.ReposProvider;
import com.amperas17.wonderstest.data.repository.ReposRepository;
import com.amperas17.wonderstest.data.repository.ErasingRepository;
import com.amperas17.wonderstest.model.pojo.Repo;
import com.amperas17.wonderstest.model.pojo.User;
import com.amperas17.wonderstest.model.realm.RealmRepo;
import com.amperas17.wonderstest.ui.utils.AdapterItemClicksListener;
import com.amperas17.wonderstest.ui.utils.LoadingDialog;
import com.amperas17.wonderstest.ui.auth.AuthActivity;
import com.amperas17.wonderstest.ui.issues.IssuesActivity;
import com.amperas17.wonderstest.ui.note.NoteActivity;
import com.amperas17.wonderstest.ui.searchissues.SearchIssuesActivity;

import java.util.ArrayList;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class ReposActivity extends AppCompatActivity implements LoadingDialog.ILoadingDialog,
        ReposProvider.IReposCaller, ErasingRepository.IEraseCaller {

    public static final String USER_ARG = "user";
    public static final String IS_UPDATING_TAG = "isUpdating";

    private ReposRepository reposRepository;
    private ErasingRepository erasingRepository;
    private ReposProvider provider;
    private RepoAdapter repoAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoData;

    private boolean isUpdating = false;

    public static Intent newIntent(Context context, User user) {
        Intent intent = new Intent(context, ReposActivity.class);
        intent.putExtra(USER_ARG, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        reposRepository = new ReposRepository();
        erasingRepository = new ErasingRepository(this);
        provider = new ReposProvider(this);

        initActionBar();

        tvNoData = (TextView) findViewById(R.id.tvNoData);

        initSwipe();
        initRecyclerView();

        setDataToAdapter(reposRepository.getRepos(getUserArg().getLogin()));

        if (savedInstanceState != null) {
            isUpdating = savedInstanceState.getBoolean(IS_UPDATING_TAG);
            if (isUpdating) {
                getRepos();
            }
        } else {
            getRepos();
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
                getRepos();
            }
        });
    }

    private void initRecyclerView() {
        repoAdapter = new RepoAdapter(new AdapterItemClicksListener<Repo>() {
            @Override
            public void onItemClick(Repo repoItem) {
                startActivity(IssuesActivity.newIntent(ReposActivity.this, repoItem));
            }

            @Override
            public void onItemLongClick(Repo repoItem) {
                startActivity(NoteActivity.newIntent(ReposActivity.this, repoItem));
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvRepoList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(repoAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_UPDATING_TAG, isUpdating);
    }

    private void getRepos() {
        swipeRefreshLayout.setRefreshing(true);
        isUpdating = true;
        provider.getData(getUserArg().getAuthHeader(), getUserArg().getLogin());
    }

    @Override
    public void onGetRepos(ArrayList<Repo> repos) {
        onGetReposSuccess(repos);
        stopRefreshing();
    }

    @Override
    public void onError(Throwable th) {
        onGetReposError();
    }

    private void onGetReposSuccess(final ArrayList<Repo> repos) {
        if (repos != null && !repos.isEmpty()) {
            reposRepository.setRepos(repos);
        }
    }

    private void onGetReposError() {
        stopRefreshing();
        Toast.makeText(this, R.string.error_occurred_toast, Toast.LENGTH_SHORT).show();
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
        provider.cancel();
        reposRepository.close();
        erasingRepository.close();
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
        erasingRepository.erase();
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
        repoAdapter.notifyDataSetChanged();
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }

    private void onDeleteDataError() {
        LoadingDialog.dismiss(getSupportFragmentManager());
        Toast.makeText(this, R.string.error_occurred_toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadingDialogCancel() {
        provider.cancel();
    }

    private void stopRefreshing() {
        swipeRefreshLayout.setRefreshing(false);
        isUpdating = false;
    }

    private void setDataToAdapter(RealmResults<RealmRepo> repos) {
        if (repos == null) {
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            if (repos.isEmpty()) {
                tvNoData.setVisibility(View.VISIBLE);
            } else {
                tvNoData.setVisibility(View.GONE);
            }
            repoAdapter.setRepos(repos);
            repoAdapter.notifyDataSetChanged();

            repos.addChangeListener(new RealmChangeListener<RealmResults<RealmRepo>>() {
                @Override
                public void onChange(RealmResults<RealmRepo> realmRepos) {
                    if (!realmRepos.isEmpty()) {
                        tvNoData.setVisibility(View.GONE);
                        repoAdapter.notifyDataSetChanged();
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
