package com.amperas17.wonderstest.ui.auth;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.data.model.pojo.User;
import com.amperas17.wonderstest.data.provider.UserProvider;
import com.amperas17.wonderstest.ui.utils.LoadingDialog;
import com.amperas17.wonderstest.ui.repositories.RepositoriesActivity;

public class AuthActivity extends AppCompatActivity
        implements LoadingDialog.ILoadingDialog, UserProvider.IProviderCaller  {

    private EditText etLogin;
    private EditText etPassword;
    private Button btnNext;

    private UserProvider userProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        userProvider = new UserProvider(this);

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
            actionBar.setTitle(R.string.auth_title);
        }
    }

    @Override
    public void onProviderCallSuccess(User user) {
        onAuthSuccess(user);
    }

    @Override
    public void onProviderCallError(Throwable th) {
        onAuthError(th);
    }

    @Override
    public void onLoadingDialogCancel() {
        userProvider.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userProvider.close();
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
        showLoadingDialog();
        userProvider.getUser(login, password);
    }

    private void onAuthSuccess(User user) {
        hideLoadingDialog();
        startActivity(RepositoriesActivity.newIntent(this, user));
        finish();
    }

    private void onAuthError(Throwable th) {
        hideLoadingDialog();
        Toast.makeText(AuthActivity.this, th.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void showLoadingDialog(){
        LoadingDialog.show(getSupportFragmentManager());
    }

    private void hideLoadingDialog(){
        LoadingDialog.show(getSupportFragmentManager());
    }
}
