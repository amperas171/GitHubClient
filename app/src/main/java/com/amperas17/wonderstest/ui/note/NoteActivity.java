package com.amperas17.wonderstest.ui.note;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.data.model.pojo.Note;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;


public class NoteActivity extends AppCompatActivity implements INoteView {

    public static final String ITEM_KEY = "itemKey";

    private INotePresenter presenter;

    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.etText)
    EditText etText;

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
        presenter.onCreate(getKeyArg());

        initActionBar();
    }

    @OnTextChanged(R.id.etTitle)
    protected void onTitleChanged(CharSequence text) {
        saveNote();
    }

    @OnTextChanged(R.id.etText)
    protected void onTextChanged(CharSequence text) {
        saveNote();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.note_to) + " " + presenter.getItemName(getKeyArg()));
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    private Parcelable getKeyArg() {
        return getIntent().getParcelableExtra(ITEM_KEY);
    }

    private void saveNote() {
        presenter.saveNote(getKeyArg(), etTitle.getText().toString(), etText.getText().toString());
    }
}
