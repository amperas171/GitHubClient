package com.amperas17.wonderstest.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.data.model.pojo.User;
import com.amperas17.wonderstest.ui.repositories.RepositoriesActivity;
import com.amperas17.wonderstest.ui.auth.AuthActivity;


public class SplashActivity extends AppCompatActivity implements ISplashView {

    private ISplashPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        presenter = new SplashPresenter(this);
        presenter.onCreate();
    }

    @Override
    public void showError(Throwable th) {
        Toast.makeText(SplashActivity.this, th.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void openAuthActivity() {
        startActivity(new Intent(SplashActivity.this, AuthActivity.class));
        SplashActivity.this.finish();
    }

    @Override
    public void openUserInfoActivity(User user) {
        startActivity(RepositoriesActivity.newIntent(SplashActivity.this, user));
        SplashActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
