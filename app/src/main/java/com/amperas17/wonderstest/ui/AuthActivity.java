package com.amperas17.wonderstest.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amperas17.wonderstest.App;
import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity
        implements LoadingDialog.ILoadingDialog {

    EditText etLogin;
    EditText etPassword;
    Button btnNext;

    Call<User> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etLogin = (EditText) findViewById(R.id.etLogin);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnNext = (Button) findViewById(R.id.btnNext);

        getSupportActionBar().setTitle("Authorization");

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyFieldsAndAuth();
            }
        });
    }

    private void verifyFieldsAndAuth() {
        if (etLogin.getText().toString().isEmpty()) {
            etLogin.setError("Empty login");
        } else if (etPassword.getText().toString().isEmpty()) {
            etPassword.setError("Empty password");
        } else {
            auth(etLogin.getText().toString(), etPassword.getText().toString());
        }
    }

    public void auth(String login, String password) {
        String authHeader = getAuthHeader(login, password);
        call = App.getGitHubApi().getUser(authHeader);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(AuthActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getAuthHeader(String login, String password) {
        return "Basic " + Base64.encodeToString((login + ":" + password).getBytes(), Base64.DEFAULT).replace("\n", "");
    }

    @Override
    public void onLoadingDialogCancel() {
        call.cancel();
    }
}
