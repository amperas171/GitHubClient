package com.amperas17.wonderstest.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.data.model.pojo.User;
import com.amperas17.wonderstest.data.provider.UserProvider;
import com.amperas17.wonderstest.ui.repositories.RepositoriesActivity;
import com.amperas17.wonderstest.ui.auth.AuthActivity;


public class SplashActivity extends AppCompatActivity implements UserProvider.IProviderCaller {

    public static final int DELAY = 2000;

    private UserProvider userProvider;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        userProvider = new UserProvider(this);

        handler = new Handler();
        handler.postDelayed(checkUserRunnable, DELAY);
    }

    private void checkUserAndOpenActivity() {
        userProvider.checkIfUserExist();
    }

    @Override
    public void onProviderCallSuccess(User user) {
        doOnSuccess(user);
    }

    @Override
    public void onProviderCallError(Throwable th) {
        Toast.makeText(SplashActivity.this, th.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userProvider.close();
        handler.removeCallbacks(checkUserRunnable);
    }

    private void doOnSuccess(User user){
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

    private Runnable checkUserRunnable = new Runnable() {
        @Override
        public void run() {
            checkUserAndOpenActivity();
        }
    };

}
