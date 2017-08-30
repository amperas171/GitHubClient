package com.amperas17.wonderstest.ui.note;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.data.model.pojo.Note;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTextChanged;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class NoteActivity extends AppCompatActivity implements INoteView, EasyPermissions.PermissionCallbacks {

    public static final int GET_CONTENT_TAG = 101;
    public static final int IMAGE_CAPTURE_TAG = 102;
    private static final int CAMERA_PERM = 123;
    private static final int READ_EXTERNAL_STORAGE_PERM = 124;
    private static final int WRITE_EXTERNAL_STORAGE_PERM = 125;
    private static final int CAMERA_AND_WRITE_EXTERNAL_STORAGE_PERMS = 126;
    public static final String ITEM_KEY = "itemKey";

    private INotePresenter presenter;

    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.etText)
    EditText etText;
    @BindView(R.id.image)
    PhotoView image;

    public static Intent newIntent(Context context, Parcelable itemKey) {
        Intent intent = new Intent(context, NoteActivity.class);
        intent.putExtra(ITEM_KEY, itemKey);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        ButterKnife.bind(this);

        presenter = new NotePresenter(this);
        presenter.onCreate();

        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.note_to) + " " + presenter.getItemName());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @OnTextChanged(R.id.etTitle)
    protected void onTitleChanged(CharSequence text) {
        presenter.saveNoteTitle(text.toString());
    }

    @OnTextChanged(R.id.etText)
    protected void onTextChanged(CharSequence text) {
        presenter.saveNoteText(text.toString());
    }

    @AfterPermissionGranted(READ_EXTERNAL_STORAGE_PERM)
    @OnClick(R.id.image)
    protected void onImageClick() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            presenter.onImageClick();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_read_storage),
                    READ_EXTERNAL_STORAGE_PERM, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @AfterPermissionGranted(WRITE_EXTERNAL_STORAGE_PERM | CAMERA_PERM)
    @OnLongClick(R.id.image)
    protected boolean onLongImageClick() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            presenter.onImageLongClick();
        } else {
            String rationale = getString(R.string.rationale_camera) + " and " +
                    getString(R.string.rationale_write_storage);
            EasyPermissions.requestPermissions(this, rationale, CAMERA_AND_WRITE_EXTERNAL_STORAGE_PERMS,
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GET_CONTENT_TAG:
                if (resultCode == RESULT_OK) {
                    onGetImageFromGallery(data.getData());
                }
                break;
            case IMAGE_CAPTURE_TAG:
                if (resultCode == RESULT_OK) {
                    onGetImageFromCamera();
                }
                break;
        }
    }

    private void onGetImageFromGallery(Uri uri) {
        presenter.onGetImageFromGallery(uri);
    }

    private void onGetImageFromCamera() {
        presenter.onGetImageFromCamera();
    }

    @Override
    public void setImage(String path) {
        if (path != null && !path.isEmpty()) {
            File file = new File(path);
            Uri uri = Uri.fromFile(file);
            Glide.with(this).load(uri)
                    .placeholder(R.drawable.img_git_folder)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(image);
        } else {
            image.setImageDrawable(getResources().getDrawable(R.drawable.img_git_folder));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void reflectNote(Note note) {
        etTitle.setText(note.getTitle());
        etText.setText(note.getText());
        setImage(note.getImagePath());
    }

    @Override
    public void getPicture() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GET_CONTENT_TAG);
    }

    @Override
    public void getCameraPhoto(String filePath) {
        File file = new File(filePath);
        Uri fileUri = Uri.fromFile(file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, IMAGE_CAPTURE_TAG);
    }

    @Override
    public Parcelable getKeyArg() {
        return getIntent().getParcelableExtra(ITEM_KEY);
    }

    @Override
    public String getText() {
        return etText.getText().toString();
    }

    @Override
    public String getTitleText() {
        return etTitle.getText().toString();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
