package com.amperas17.wonderstest.ui.note;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.data.cache.NoteCache;
import com.amperas17.wonderstest.data.model.pojo.Issue;
import com.amperas17.wonderstest.data.model.pojo.Repository;
import com.amperas17.wonderstest.data.model.realm.RealmNote;


public class NoteActivity extends AppCompatActivity {

    public static final String ITEM_KEY = "itemKey";

    private NoteCache noteCache;

    private EditText etTitle;
    private EditText etText;

    public static Intent newIntent(Context context, Parcelable itemKey) {
        Intent intent = new Intent(context, NoteActivity.class);
        intent.putExtra(ITEM_KEY, itemKey);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        noteCache = new NoteCache();

        initActionBar();

        etTitle = (EditText) findViewById(R.id.etTitle);
        etText = (EditText) findViewById(R.id.etText);

        fillFields();

        etTitle.addTextChangedListener(noteWatcher);
        etText.addTextChangedListener(noteWatcher);
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.note_to) + " " + getItemName());
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
    protected void onDestroy() {
        super.onDestroy();
        noteCache.close();
    }


    private void fillFields() {
        RealmNote note = noteCache.getNote(getItemKey());
        if (note != null) {
            etTitle.setText(note.getTitle());
            etText.setText(note.getText());
        }
    }

    private String getItemKey() {
        Parcelable item = getIntent().getParcelableExtra(ITEM_KEY);
        if (item instanceof Repository) return ((Repository) item).getName();
        if (item instanceof Issue) return ((Issue) item).getId().toString();
        return "";
    }

    private String getItemName() {
        Parcelable item = getIntent().getParcelableExtra(ITEM_KEY);
        if (item instanceof Repository) return ((Repository) item).getName();
        if (item instanceof Issue) return ((Issue) item).getTitle();
        return "";
    }

    private TextWatcher noteWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            noteCache.setNote(getItemKey(), etTitle.getText().toString(), etText.getText().toString());
        }
    };

}
