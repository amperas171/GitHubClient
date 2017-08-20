package com.amperas17.wonderstest.ui.userinfo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.model.User;
import com.amperas17.wonderstest.ui.LoadingDialog;

import io.realm.Realm;


public class UserInfoActivity extends AppCompatActivity implements LoadingDialog.ILoadingDialog {

    public static final String USER_TAG = "user";

    Realm realm;
    User user;

    public static Intent newIntent(Context context, User user) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra(USER_TAG, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        user = getIntent().getParcelableExtra(USER_TAG);
        getSupportActionBar().setTitle(user.getLogin());

        realm = Realm.getDefaultInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit:
                showExitDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void showExitDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(R.string.exit_alert_message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exit();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }


    private void exit() {
        LoadingDialog.show(getSupportFragmentManager());
        if (!realm.isClosed()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.deleteAll();
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    onDeleteDataSuccess();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    onDeleteDataError();
                }
            });
        }
    }

    private void onDeleteDataSuccess() {
        LoadingDialog.dismiss(getSupportFragmentManager());
        onBackPressed();
    }

    private void onDeleteDataError() {
        LoadingDialog.dismiss(getSupportFragmentManager());
        Toast.makeText(this, R.string.error_occured_toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadingDialogCancel() {

    }
}
