package com.amperas17.wonderstest.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.model.realm.RealmUser;
import com.amperas17.wonderstest.model.User;
import com.amperas17.wonderstest.ui.userinfo.UserInfoActivity;
import com.amperas17.wonderstest.ui.auth.AuthActivity;

import io.realm.Realm;


public class SplashActivity extends AppCompatActivity {
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        realm = Realm.getDefaultInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUserAndOpenActivity();
            }
        }, 2000);
    }

    private void checkUserAndOpenActivity() {
        if (!realm.isClosed())
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmUser realmUser = realm.where(RealmUser.class).findFirst();
                    if (realmUser != null) openUserInfoActivity(realmUser.toUser());
                    else openAuthActivity();
                }
            });
    }

    private void openAuthActivity() {
        startActivity(new Intent(SplashActivity.this, AuthActivity.class));
        SplashActivity.this.finish();
    }

    private void openUserInfoActivity(User user) {
        startActivity(UserInfoActivity.newIntent(SplashActivity.this, user));
        SplashActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
