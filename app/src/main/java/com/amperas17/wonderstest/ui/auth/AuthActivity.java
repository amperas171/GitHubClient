package com.amperas17.wonderstest.ui.auth;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.data.model.pojo.User;
import com.amperas17.wonderstest.ui.utils.LoadingDialog;
import com.amperas17.wonderstest.ui.repositories.RepositoriesActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthActivity extends AppCompatActivity
        implements LoadingDialog.ILoadingDialog, IAuthView {

    private IAuthPresenter presenter;

    @BindView(R.id.etLogin)
    EditText etLogin;
    @BindView(R.id.etPassword)
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);

        presenter = new AuthPresenter(this);

        initActionBar();
    }

    @OnClick(R.id.btnNext)
    public void onNextClick() {
        presenter.verifyFieldsAndAuth(etLogin.getText().toString(), etPassword.getText().toString());
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.auth_title);
        }
    }

    @Override
    public void openRepositoriesActivity(User user) {
        startActivity(RepositoriesActivity.newIntent(this, user));
        finish();
    }

    @Override
    public void showLoginError() {
        etLogin.setError(getString(R.string.empty_login));
    }

    @Override
    public void showPasswordError() {
        etPassword.setError(getString(R.string.empty_password));
    }

    @Override
    public void showAuthError(Throwable th) {
        Toast.makeText(AuthActivity.this, th.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void showLoader() {
        LoadingDialog.show(getSupportFragmentManager());
    }

    @Override
    public void hideLoader() {
        LoadingDialog.show(getSupportFragmentManager());
    }
}
