package com.amperas17.wonderstest.ui.auth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amperas17.wonderstest.App;
import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.model.pojo.User;
import com.amperas17.wonderstest.model.realm.RealmUser;
import com.amperas17.wonderstest.ui.utils.LoadingDialog;
import com.amperas17.wonderstest.ui.userinfo.UserInfoActivity;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity
        implements LoadingDialog.ILoadingDialog {

    EditText etLogin;
    EditText etPassword;
    Button btnNext;

    Realm realm;
    Call<User> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        etLogin = (EditText) findViewById(R.id.etLogin);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnNext = (Button) findViewById(R.id.btnNext);

        getSupportActionBar().setTitle(R.string.auth_title);

        realm = Realm.getDefaultInstance();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyFieldsAndAuth();
            }
        });
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
        final String authHeader = getAuthHeader(login, password);
        call = App.getGitHubApi().getUser(authHeader);
        LoadingDialog.show(getSupportFragmentManager());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                onAuthSuccess(response.body(), authHeader);
            }

            @Override
            public void onFailure(Call<User> call, Throwable th) {
                onAuthError(th);
            }
        });
    }

    private void onAuthSuccess(User user, String authHeader) {
        user.setAuthHeader(authHeader);
        saveUser(user);
        LoadingDialog.dismiss(getSupportFragmentManager());
        startActivity(UserInfoActivity.newIntent(this, user));
        finish();
    }

    private void saveUser(User user) {
        final RealmUser realmUser = new RealmUser(user);
        if (!realm.isClosed())
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.insertOrUpdate(realmUser);
                }
            });
    }

    private void onAuthError(Throwable th) {
        LoadingDialog.dismiss(getSupportFragmentManager());
        Toast.makeText(AuthActivity.this, th.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private String getAuthHeader(String login, String password) {
        return "Basic " + Base64.encodeToString((login + ":" + password).getBytes(), Base64.DEFAULT).replace("\n", "");
    }

    @Override
    public void onLoadingDialogCancel() {
        if (call != null)
            call.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
            call = null;
        }
        realm.close();
    }
}
