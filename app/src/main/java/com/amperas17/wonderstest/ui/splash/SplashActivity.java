package com.amperas17.wonderstest.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.data.cache.UserCache;
import com.amperas17.wonderstest.data.model.pojo.User;
import com.amperas17.wonderstest.ui.repositories.RepositoriesActivity;
import com.amperas17.wonderstest.ui.auth.AuthActivity;


public class SplashActivity extends AppCompatActivity implements UserCache.ICachedUserCaller {

    public static final int DELAY = 2;

    private UserCache userCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        userCache = new UserCache(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUserAndOpenActivity();
            }
        }, DELAY);
    }

    private void checkUserAndOpenActivity() {
        userCache.getUser();
    }

    @Override
    public void onGetUser(User user) {
        if (user != null) openUserInfoActivity(user);
        else openAuthActivity();
    }

    private void openAuthActivity() {
        startActivity(new Intent(SplashActivity.this, AuthActivity.class));
        SplashActivity.this.finish();
    }

    private void openUserInfoActivity(User user) {
        startActivity(RepositoriesActivity.newIntent(SplashActivity.this, user));
        SplashActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userCache.close();
    }
}
