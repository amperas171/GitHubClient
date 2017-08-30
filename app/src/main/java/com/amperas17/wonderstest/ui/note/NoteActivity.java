package com.amperas17.wonderstest.ui.note;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTextChanged;

public class NoteActivity extends AppCompatActivity implements INoteView {

    public static final int GET_CONTENT_TAG = 101;
    public static final int IMAGE_CAPTURE_TAG = 102;
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

    @OnTextChanged(R.id.etTitle)
    protected void onTitleChanged(CharSequence text) {
        presenter.saveNoteTitle(text.toString());
    }

    @OnTextChanged(R.id.etText)
    protected void onTextChanged(CharSequence text) {
        presenter.saveNoteText(text.toString());
    }

    @OnClick(R.id.image)
    protected void onImageClick() {
        presenter.onImageClick();
    }

    @OnLongClick(R.id.image)
    protected boolean onLongImageClick() {
        presenter.onImageLongClick();
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

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.note_to) + " " + presenter.getItemName());
            actionBar.setDisplayHomeAsUpEnabled(true);
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
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
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
}
