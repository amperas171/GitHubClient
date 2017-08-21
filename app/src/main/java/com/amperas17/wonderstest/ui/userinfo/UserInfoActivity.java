package com.amperas17.wonderstest.ui.userinfo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.amperas17.wonderstest.App;
import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.model.Repo;
import com.amperas17.wonderstest.model.User;
import com.amperas17.wonderstest.model.realm.RealmRepo;
import com.amperas17.wonderstest.ui.AdapterItemClickListener;
import com.amperas17.wonderstest.ui.LoadingDialog;
import com.amperas17.wonderstest.ui.issues.IssuesActivity;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserInfoActivity extends AppCompatActivity implements LoadingDialog.ILoadingDialog {

    public static final String USER_ARG = "user";

    public static final String IS_UPDATING_TAG = "isUpdating";

    private Realm realm;
    private User user;
    private Call<ArrayList<Repo>> call;
    private RepoAdapter repoAdapter;
    RealmResults<RealmRepo> repos;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoData;

    private boolean isUpdating = false;

    public static Intent newIntent(Context context, User user) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra(USER_ARG, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        realm = Realm.getDefaultInstance();

        user = getIntent().getParcelableExtra(USER_ARG);
        getSupportActionBar().setTitle(user.getLogin());

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRepos();
            }
        });

        tvNoData = (TextView) findViewById(R.id.tvNoData);

        repos = realm.where(RealmRepo.class)
                .equalTo(RealmRepo.OWNER_LOGIN, user.getLogin()).findAll();
        repos.addChangeListener(new RealmChangeListener<RealmResults<RealmRepo>>() {
            @Override
            public void onChange(RealmResults<RealmRepo> realmRepos) {
                if (!realmRepos.isEmpty()) {
                    tvNoData.setVisibility(View.GONE);
                    repoAdapter.notifyDataSetChanged();
                }
            }
        });

        repoAdapter = new RepoAdapter(new AdapterItemClickListener<Repo>() {
            @Override
            public void onItemClick(Repo repoItem) {
                startActivity(IssuesActivity.newIntent(UserInfoActivity.this, repoItem));
            }
        }, repos);

        if (repos.isEmpty()) {
            tvNoData.setVisibility(View.VISIBLE);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewRepoList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(repoAdapter);

        if (savedInstanceState != null) {
            isUpdating = savedInstanceState.getBoolean(IS_UPDATING_TAG);
            if (isUpdating) {
                getRepos();
            }
        } else {
            getRepos();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_UPDATING_TAG, isUpdating);
    }

    private void getRepos() {
        swipeRefreshLayout.setRefreshing(true);
        isUpdating = true;
        call = App.getGitHubApi().getRepos(user.getLogin());
        call.enqueue(new Callback<ArrayList<Repo>>() {
            @Override
            public void onResponse(Call<ArrayList<Repo>> call, Response<ArrayList<Repo>> response) {
                onGetReposSuccess(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Repo>> call, Throwable t) {
                if (!call.isCanceled())
                    onGetReposError();
            }
        });
    }

    private void onGetReposSuccess(final ArrayList<Repo> repos) {
        if (!repos.isEmpty()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (Repo repo : repos) {
                        RealmRepo realmRepo = new RealmRepo(repo);
                        realm.insertOrUpdate(realmRepo);
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    stopRefreshing();
                    //repoAdapter.notifyDataSetChanged();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    stopRefreshing();
                }
            });
        }
    }

    private void onGetReposError() {
        stopRefreshing();
        Toast.makeText(this, R.string.error_occured_toast, Toast.LENGTH_SHORT).show();
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
        if (call != null) {
            call.cancel();
            call = null;
        }
        repos = null;
        realm.close();
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
        if (!realm.isClosed()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.deleteAll();
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    onDeleteDataSuccess();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    onDeleteDataError();
                }
            });
        }
    }

    private void onDeleteDataSuccess() {
        LoadingDialog.dismiss(getSupportFragmentManager());
        onBackPressed();
    }

    private void onDeleteDataError() {
        LoadingDialog.dismiss(getSupportFragmentManager());
        Toast.makeText(this, R.string.error_occured_toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadingDialogCancel() {
        if (call != null) call.cancel();
        else call = null;
    }

    private void stopRefreshing() {
        swipeRefreshLayout.setRefreshing(false);
        isUpdating = false;
    }
}
