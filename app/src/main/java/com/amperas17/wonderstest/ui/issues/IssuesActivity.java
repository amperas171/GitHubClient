package com.amperas17.wonderstest.ui.issues;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.model.Repo;


public class IssuesActivity extends AppCompatActivity {
    public static final String REPO_ARG = "user";

    private Repo repo;

    public static Intent newIntent(Context context, Repo repo) {
        Intent intent = new Intent(context, IssuesActivity.class);
        intent.putExtra(REPO_ARG, repo);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues);

        repo = getIntent().getParcelableExtra(REPO_ARG);
        getSupportActionBar().setTitle(repo.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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


}
