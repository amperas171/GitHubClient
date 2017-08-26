package com.amperas17.wonderstest.ui.auth;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.data.provider.AuthProvider;
import com.amperas17.wonderstest.data.cache.UserCache;
import com.amperas17.wonderstest.model.pojo.User;
import com.amperas17.wonderstest.model.realm.RealmUser;
import com.amperas17.wonderstest.ui.utils.LoadingDialog;
import com.amperas17.wonderstest.ui.repositories.RepositoriesActivity;

public class AuthActivity extends AppCompatActivity
        implements LoadingDialog.ILoadingDialog, AuthProvider.IAuthCaller {

    private EditText etLogin;
    private EditText etPassword;
    private Button btnNext;

    private UserCache userCache;
    private AuthProvider authProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        userCache = new UserCache();
        authProvider = new AuthProvider(this);

        initActionBar();

        etLogin = (EditText) findViewById(R.id.etLogin);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyFieldsAndAuth();
            }
        });
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setTitle(R.string.auth_title);
        }
    }

    @Override
    public void onLoadingDialogCancel() {
        authProvider.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        authProvider.cancel();
        userCache.close();
    }

    private void verifyFieldsAndAuth() {
        if (etLogin.getText().toString().isEmpty()) {
            etLogin.setError(getString(R.string.empty_login));
        } else if (etPassword.getText().toString().isEmpty()) {
            etPassword.setError(getString(R.string.empty_password));
        } else {
            auth(etLogin.getText().toString(), etPassword.getText().toString());
        }
    }

    public void auth(String login, String password) {
        String authHeader = getAuthHeader(login, password);
        authProvider.getData(authHeader);
        LoadingDialog.show(getSupportFragmentManager());
    }

    @Override
    public void onGetAuth(User user, String authHeader) {
        onAuthSuccess(user, authHeader);
    }

    @Override
    public void onError(Throwable th) {
        onAuthError(th);
    }

    private void onAuthSuccess(User user, String authHeader) {
        user.setAuthHeader(authHeader);
        saveUser(user);
        LoadingDialog.dismiss(getSupportFragmentManager());
        startActivity(RepositoriesActivity.newIntent(this, user));
        finish();
    }

    private void saveUser(User user) {
        final RealmUser realmUser = new RealmUser(user);
        userCache.setUser(realmUser);
    }

    private void onAuthError(Throwable th) {
        LoadingDialog.dismiss(getSupportFragmentManager());
        Toast.makeText(AuthActivity.this, th.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private String getAuthHeader(String login, String password) {
        return "Basic " + Base64.encodeToString((login + ":" + password).getBytes(), Base64.DEFAULT).replace("\n", "");
    }
}
